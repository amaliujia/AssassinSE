/**
 *  QryEval illustrates the architecture for the portion of a search
 *  engine that evaluates queries.  It is a template for class
 *  homework assignments, so it emphasizes simplicity over efficiency.
 *  It implements an unranked Boolean retrieval model, however it is
 *  easily extended to other retrieval models.  For more information,
 *  see the ReadMe.txt file.
 */

package SearchEngine.Assassin;

import java.io.*;
import java.util.*;

import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import static java.lang.System.exit;

public class QryEval {

  static String usage = "Usage:  java " + System.getProperty("sun.java.command")
      + " paramFile\n\n";

  //  The index file reader is accessible via a global variable. This
  //  isn't great programming style, but the alternative is for every
  //  query operator to store or pass this value, which creates its
  //  own headaches.

  public static IndexReader READER;
  public static BufferedWriter writer;

    //  Create and configure an English analyzer that will be used for
  //  query parsing.

  public static EnglishAnalyzerConfigurable analyzer =
      new EnglishAnalyzerConfigurable (Version.LUCENE_43);
  static {
    analyzer.setLowercase(true);
    analyzer.setStopwordRemoval(true);
    analyzer.setStemmer(EnglishAnalyzerConfigurable.StemmerType.KSTEM);
  }
  public static RetrievalModel model = null;

  /**
   *  @param args The only argument is the path to the parameter file.
   *  @throws Exception
   */
  public static void main(String[] args) throws Exception {
    
    // must supply parameter file
    if (args.length < 1) {
      System.err.println(usage);
      exit(1);
    }

    // read in the parameter file; one parameter per line in format of key=value
    Map<String, String> params = new HashMap<String, String>();
    Scanner scan = new Scanner(new File(args[0]));
    String line = null;
    do {
      line = scan.nextLine();
      String[] pair = line.split("=");
      params.put(pair[0].trim(), pair[1].trim());
    } while (scan.hasNext());
    scan.close();
    
    // parameters required for this example to run
    if (!params.containsKey("indexPath")) {
      System.err.println("Error: Parameters were missing.");
      exit(1);
    }

    // open the index
    READER = DirectoryReader.open(FSDirectory.open(new File(params.get("indexPath"))));

    if (READER == null) {
      System.err.println(usage);
      exit(1);
    }

    DocLengthStore docLengthStore = new DocLengthStore(READER);
    DataCenter dataCenter = DataCenter.sharedDataCenter();
    dataCenter.docLengthStore = docLengthStore;
    dataCenter.numDocs = READER.numDocs();

    //RetrievalModel model = null;
    String modelType = params.get("retrievalAlgorithm");
    if(modelType.equals("UnrankedBoolean"))
            model = new RetrievalModelUnrankedBoolean();
    else if(modelType.equals("RankedBoolean"))
            model = new RetrievalModelRankedBoolean();
    else if(modelType.equals("BM25")) {
            model = new RetrievalModelBM25();
    }else if(modelType.equals("Indri")){
            model = new RetrievalModelIndri();
    }else{
        return;
    }

    if(model instanceof RetrievalModelBM25){
        dataCenter.k1 = Double.parseDouble(params.get("BM25:k_1"));
        dataCenter.b = Double.parseDouble(params.get("BM25:b"));
        dataCenter.k3 = Double.parseDouble(params.get("BM25:k_3"));
        System.out.println("BM25 parameters  " + dataCenter.k1 + "  " + dataCenter.b + "  " + dataCenter.k3);
    }else if(model instanceof RetrievalModelIndri){
        ((RetrievalModelIndri)model).mu = Double.parseDouble(params.get("Indri:mu"));
        ((RetrievalModelIndri) model).lambda = Double.parseDouble(params.get("Indri:lambda"));
        //((RetrievalModelIndri) model).smoothing = params.get("Indri:smoothing");
    }

    if(model == null){
        System.out.println("Model Created error!");
        return;
    }
    //  Using the example query parser.  Notice that this does no
    //  lexical processing of query terms.  Add that to the query
    //  parser.
    FileInputStream f;
    InputStreamReader fileReader;
    BufferedReader bufferReader;

    ArrayList<String> queries = new ArrayList<String>();
    ArrayList<String> keys = new ArrayList<String>();
    try{
       String str = "";
       f = new FileInputStream(params.get("queryFilePath"));
       fileReader = new InputStreamReader(f);
       bufferReader = new BufferedReader(fileReader);
       while((str = bufferReader.readLine()) != null){
  //       if(str.length() <= 2)  continue;
         String[] queryPair=  str.split(":");
         keys.add(queryPair[0]);
         queries.add(queryPair[1]);
       }
       f.close();
    }catch(Exception e){
      System.out.println("Error on dealing with queries file!");
    }
    
    Qryop qTree;
    String outputPath = params.get("trecEvalOutputPath");
    writer = new BufferedWriter(new FileWriter(new File(outputPath)));
      for(int i = 0; i < keys.size(); i++){
        String key = keys.get(i);
        String que = queries.get(i);
        qTree = parseQuery (que, model);
        printResults (key, qTree.evaluate (model), qTree);
      }

      try{
        writer.close();
      }catch (Exception e){
        System.out.println("error: close files");
      }
      printMemoryUsage(true);
  }

  /**
   *  Write an error message and exit.  This can be done in other
   *  ways, but I wanted something that takes just one statement so
   *  that it is easy to insert checks without cluttering the code.
   *  @param message The error message to write before exiting.
   *  @return void
   */
  static void fatalError (String message) {
    System.err.println (message);
    exit(1);
  }

  /**
   *  Get the external document id for a document specified by an
   *  internal document id. If the internal id doesn't exists, returns null.
   *  
   * @param iid The internal document id of the document.
   * @throws IOException 
   */
  static String getExternalDocid (int iid) throws IOException {
    Document d = QryEval.READER.document (iid);
    String eid = d.get ("externalId");
    return eid;
  }

  /**
   *  Finds the internal document id for a document specified by its
   *  external id, e.g. clueweb09-enwp00-88-09710.  If no such
   *  document exists, it throws an exception. 
   * 
   * @param externalId The external document id of a document.s
   * @return An internal doc id suitable for finding document vectors etc.
   * @throws Exception
   */
  static int getInternalDocid (String externalId) throws Exception {
    Query q = new TermQuery(new Term("externalId", externalId));
    
    IndexSearcher searcher = new IndexSearcher(QryEval.READER);
    TopScoreDocCollector collector = TopScoreDocCollector.create(1,false);
    searcher.search(q, collector);
    ScoreDoc[] hits = collector.topDocs().scoreDocs;
    
    if (hits.length < 1) {
      throw new Exception("External id not found.");
    } else {
      return hits[0].doc;
    }
  }

  /**
   * parseQuery converts a query string into a query tree.
   * 
   * @param qString
   *          A string containing a query.
   * @throws IOException                                                            `
   */
  static Qryop parseQuery(String qString, RetrievalModel model) throws IOException {

    Qryop currentOp = null;
    Stack<Qryop> stack = new Stack<Qryop>();
    if(!(model instanceof RetrievalModelIndri)) // {}
       stack.push(new QryopSlOR());
    // Add a default query operator to an unstructured query. This
    // is a tiny bit easier if unnecessary whitespace is removed.

    qString = qString.trim();
    if(model instanceof RetrievalModelRankedBoolean || model instanceof RetrievalModelUnrankedBoolean) {
        if (qString.charAt(0) != '#') {
            qString = "#OR(" + qString + ")";
        }
        if(qString.charAt(qString.length() - 1) != ')'){
           qString = "#OR(" + qString + ")";
        }
    } else if(model instanceof RetrievalModelBM25) {
        if (qString.charAt(0) != '#') {
            qString = "#SUM(" + qString + ")";
        }
        if(qString.charAt(qString.length() - 1) != ')'){
            qString = "#SUM(" + qString + ")";
        }
    } else if (model instanceof  RetrievalModelIndri){
        if (qString.charAt(0) != '#'){
            qString = "#AND(" + qString + ")";
        }
        if(qString.charAt(qString.length() - 1) != ')'){
            qString = "#AND(" + qString + ")";
        }
    }
    // Tokenize the query.

    StringTokenizer tokens = new StringTokenizer(qString, "\t\n\r ,()", true);
    String token = null;

    // Each pass of the loop processes one token. To improve
    // efficiency and clarity, the query operator on the top of the
    // stack is also stored in currentOp.

    while (tokens.hasMoreTokens()) {

      token = tokens.nextToken();
      String [] dealNear = token.split("/");

      if (dealNear[0].matches("[ ,(\t\n\r]")) {
        // Ignore most delimiters.
      } else if (dealNear[0].equalsIgnoreCase("#AND") || dealNear[0].equalsIgnoreCase("#And") || dealNear[0].equalsIgnoreCase("#and")) {
        currentOp = new QryopSlAnd();
        stack.push(currentOp);
      } else if (dealNear[0].equalsIgnoreCase("#NEAR")) {
        currentOp = new QryopIlNear(Integer.parseInt(dealNear[1]));
        stack.push(currentOp);
      } else if(dealNear[0].equalsIgnoreCase("#OR") || dealNear[0].equalsIgnoreCase("#Or")){
        currentOp = new QryopSlOR();
        stack.push(currentOp);
      }else if(dealNear[0].equalsIgnoreCase("#SUM")){
        currentOp = new QryopIlSum();
        stack.push(currentOp);
      }else if(dealNear[0].equalsIgnoreCase("#SYN")) {
        currentOp = new QryopIlSyn();
        stack.push(currentOp);
      }else if (dealNear[0].startsWith(")")) { // Finish current query operator.

        // If the current query operator is not an argument to
        // another query operator (i.e., the stack is empty when it
        // is removed), we're done (assuming correct syntax - see
        // below). Otherwise, add the current operator as an
        // argument to the higher-level operator, and shift
        // processing back to the higher-level operator.

        stack.pop();

        if (stack.empty())
          break;

        Qryop arg = currentOp;
        currentOp = stack.peek();
        //arg.evaluate(model);
        currentOp.add(arg);
      } else {
         String[] fieldName = token.split("\\.");
         if(fieldName.length == 1){
             String[] c = tokenizeQuery(fieldName[0]);
             if(c.length != 0) {
                 currentOp.add(new QryopIlTerm(c[0]));
             }
         }else{
             String[] c = tokenizeQuery(fieldName[0]);
             if(c.length != 0) {
                 currentOp.add(new QryopIlTerm(c[0], fieldName[1]));
             }
         }
      }
    }

    // A broken structured query can leave unprocessed tokens on the
    // stack, so check for that.

    if (tokens.hasMoreTokens()) {
      //System.err.println("Error:  Query syntax is incorrect.  " + qString);
      if(model instanceof RetrievalModelBM25){
          qString = "#SUM(" + qString + ")";
          return parseQuery(qString, model);
      }else if(model instanceof RetrievalModelRankedBoolean || model instanceof RetrievalModelUnrankedBoolean) {
          qString = "#OR(" + qString + ")";
          return parseQuery(qString, model);
      }else if(model instanceof RetrievalModelIndri){
          qString  = "#AND(" + qString + ")";
          return parseQuery(qString, model);
      }
      return null;
    }
    return currentOp;
  }

  /**
   *  Print a message indicating the amount of memory used.  The
   *  caller can indicate whether garbage collection should be
   *  performed, which slows the program but reduces memory usage.
   *  @param gc If true, run the garbage collector before reporting.
   *  @return void
   */
  public static void printMemoryUsage (boolean gc) {

    Runtime runtime = Runtime.getRuntime();

    if (gc) {
      runtime.gc();
    }

    System.out.println ("Memory used:  " +
			((runtime.totalMemory() - runtime.freeMemory()) /
			 (1024L * 1024L)) + " MB");
  }
  
  /**
   * Print the query results. 
   * 
   * THIS IS NOT THE CORRECT OUTPUT FORMAT.  YOU MUST CHANGE THIS
   * METHOD SO THAT IT OUTPUTS IN THE FORMAT SPECIFIED IN THE HOMEWORK
   * PAGE, WHICH IS:
   * 
   * QueryID Q0 DocID Rank Score RunID
   *
   * @param result Result object generated by {@link Qryop#evaluate(RetrievalModel)}.
   * @throws IOException 
   */
  static void printResults(String queryID, QryResult result, Qryop qTree) throws IOException {

    /*
     *  Create the trec_eval output.  Your code should write to the
     *  file specified in the parameter file, and it should write the
     *  results that you retrieved above.  This code just allows the
     *  testing infrastructure to work on QryEval.
     */
          try {
              if (result.docScores.scores.size() < 1) {
                  writer.write(queryID + "\tQ0\tdummy\t1\t0\trun-1\n");
              } else {
                  HashMap<String, Double> map = new HashMap<String, Double>();
                  for(int i = 0; i < result.docScores.scores.size(); i++){
                      map.put(getExternalDocid(result.docScores.scores.get(i).getDocid()), result.docScores.getDocidScore(i));
                  }
                  List<Map.Entry<String, Double>> folder = new ArrayList<Map.Entry<String, Double>>(map.entrySet());
                  Collections.sort(folder, new Comparator<Map.Entry<String, Double>>() {
                      public int compare(Map.Entry<String, Double> e1,
                                         Map.Entry<String, Double> e2) {
                          if(!e1.getValue().equals(e2.getValue())) {
                              if(e2.getValue() > e1.getValue()) return 1;
                              else return -1;
                          }
                          else
                              return (e1.getKey()).toString().compareTo(e2.getKey().toString());
                      }
                  });
                  for (int i = 0; i < result.docScores.scores.size() && i < 100; i++) {
                      writer.write(queryID + "\tQ0\t"
                              + folder.get(i).getKey()
                              + "\t"
                              + (i + 1)
                              + "\t"
                              + folder.get(i).getValue()
                              + "\trun-1\n");
                  }

              }
          } catch (Exception e) {
              e.printStackTrace();
          }

  }

  /**
   *  Given a query string, returns the terms one at a time with stopwords
   *  removed and the terms stemmed using the Krovetz stemmer. 
   * 
   *  Use this method to process raw query terms. 
   * 
   *  @param query String containing query
   *  @return Array of query tokens
   *  @throws IOException
   */
  static String[] tokenizeQuery(String query) throws IOException {

    TokenStreamComponents comp = analyzer.createComponents("dummy", new StringReader(query));
    TokenStream tokenStream = comp.getTokenStream();

    CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
    tokenStream.reset();

    List<String> tokens = new ArrayList<String>();
    while (tokenStream.incrementToken()) {
      String term = charTermAttribute.toString();
      tokens.add(term);
    }
    return tokens.toArray(new String[tokens.size()]);
  }
}
