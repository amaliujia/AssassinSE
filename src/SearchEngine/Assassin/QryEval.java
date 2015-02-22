package SearchEngine.Assassin;

import SearchEngine.Assassin.DataStructure.SortEntity;
import SearchEngine.Assassin.LearningToRank.SDLearningToRankPool;
import SearchEngine.Assassin.Lucene.DocLengthStore;
import SearchEngine.Assassin.Operators.*;
import SearchEngine.Assassin.PesudoFeedback.SDPesudoFeedBackEngine;
import SearchEngine.Assassin.RetrievalModel.*;
import SearchEngine.Assassin.Util.DataCenter;
import SearchEngine.Assassin.Util.Util;
import TextMiningEngine.Witch.Clustering.BipartiteClustering;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.*;

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
    public static BufferedWriter featuresWriter;

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
        String line;
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


        String modelType = params.get("retrievalAlgorithm");
        if(modelType.equals("UnrankedBoolean"))
            model = new RetrievalModelUnrankedBoolean();
        else if(modelType.equals("RankedBoolean"))
            model = new RetrievalModelRankedBoolean();
        else if(modelType.equals("BM25")) {
            model = new RetrievalModelBM25();
        } else if(modelType.equals("Indri")) {
            model = new RetrievalModelIndri();
        } else if(modelType.equals("letor")) {
            model = new RetrievalModelLearningToRank();
        } else if(modelType.equals("Bipart")){
           model = new BipartiteClusteringModel();
        } else {
            return;
        }

        if(model instanceof RetrievalModelBM25) {
            DataCenter.k1 = Double.parseDouble(params.get("BM25:k_1"));
            DataCenter.b = Double.parseDouble(params.get("BM25:b"));
            DataCenter.k3 = Double.parseDouble(params.get("BM25:k_3"));
            System.out.println("BM25 parameters  " + DataCenter.k1 + "  " + DataCenter.b + "  " + DataCenter.k3);
        } else if(model instanceof RetrievalModelIndri) {
            model.setParameter("mu", Double.parseDouble(params.get("Indri:mu")));
            model.setParameter("lambda",1 - Double.parseDouble(params.get("Indri:lambda")));
            model.setParameter("smoothing", params.get("Indri:smoothing"));
        } else if(model instanceof RetrievalModelLearningToRank) {
            model.setParameter("mu", Double.parseDouble(params.get("Indri:mu")));
            model.setParameter("lambda",Double.parseDouble(params.get("Indri:lambda")));
            model.setParameter("smoothing", params.get("Indri:smoothing"));
            model.setParameter("k1", Double.parseDouble(params.get("BM25:k_1")));
            model.setParameter("k3", Double.parseDouble(params.get("BM25:k_3")));
            model.setParameter("b", Double.parseDouble(params.get("BM25:b")));
            model.setParameter("numDocs", READER.numDocs());
            ((RetrievalModelLearningToRank)model).docLengthStore = docLengthStore;
        }

        if(model == null) {
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
        try {
            String str;
            f = new FileInputStream(params.get("queryFilePath"));
            fileReader = new InputStreamReader(f);
            bufferReader = new BufferedReader(fileReader);
            while((str = bufferReader.readLine()) != null) {
                String[] queryPair=  str.split(":");
                keys.add(queryPair[0]);
                queries.add(queryPair[1]);
            }
            f.close();
        } catch(Exception e) {
            System.out.println("Error on dealing with queries file!");
        }

        Qryop qTree;
        String outputPath = params.get("trecEvalOutputPath");
        writer = new BufferedWriter(new FileWriter(new File(outputPath)));

        //In the part, decide which path will AssassinSE use, normal pattern, pesudo feedback or
        //learning to rank
        if(model instanceof RetrievalModelLearningToRank) { // learning to rank model
            //initialization
            String traningFilePath = params.get("letor:trainingFeatureVectorsFile");
            String learningModelPath = params.get("letor:svmRankLearnPath");
            String FEAT_GEN = params.get("letor:svmRankParamC");
            String modelOutputFile = params.get("letor:svmRankModelFile");
            featuresWriter = new BufferedWriter(new FileWriter(new File(traningFilePath)));

            //preprocessing relevant documents
            HashMap<String, ArrayList<String>> revelanceMap = new HashMap<String, ArrayList<String>>();
            File relevanceFiles = new File(params.get("letor:trainingQrelsFile"));
            Scanner s;
            try {
                s= new Scanner(relevanceFiles);
            } catch (FileNotFoundException e) {
                System.out.println("No relevance judgement file");
                e.printStackTrace();
                return;
            }

            while(s.hasNext()) {
                String currLine = s.nextLine();
                String currKey = currLine.split(" ")[0];
                if(revelanceMap.containsKey(currKey)) {
                    ArrayList<String> queryDocs = revelanceMap.get(currKey);
                    queryDocs.add(currLine);
                    revelanceMap.put(currKey, queryDocs);
                } else {
                    ArrayList<String> queryDocs = new ArrayList<String>();
                    queryDocs.add(currLine);
                    revelanceMap.put(currKey, queryDocs);
                }
            }

            // get disable setting
            String[] settings = new String[0];
            if(params.containsKey("letor:featureDisable")) {
                String disableSetting = params.get("letor:featureDisable");
                settings = disableSetting.split(",");
            }

            //get page rank score
            //HashMap<Integer, Double> pageRank = new HashMap<Integer, Double>();
            HashMap<String, Double> pageRank = new HashMap<String, Double>();
            String pageRankFile = params.get("letor:pageRankFile");
            if(pageRankFile == null) {
                throw new Exception("PageRank file doesn't exist!");
            }
            Scanner scanner = new Scanner(new File(pageRankFile));

            while(scanner.hasNext()) {
                String cur = scanner.nextLine();
                String[] fileScore = cur.split("\t");
                // int id = getInternalDocid(fileScore[0]);
                pageRank.put(fileScore[0], Double.parseDouble(fileScore[1]));
            }

            //read training queies
            FileInputStream trainingFile = new FileInputStream(params.get("letor:trainingQueryFile"));
            InputStreamReader traningFileReader = new InputStreamReader(trainingFile);
            BufferedReader traningBufferReader = new BufferedReader(traningFileReader);
            ArrayList<String> traningQueries = new ArrayList<String>();
            ArrayList<String> traningKeys = new ArrayList<String>();

            // read traning queries
            String q;
            while((q = traningBufferReader.readLine()) != null) {
                String[] pair = q.split(":");
                traningQueries.add(pair[1]);
                traningKeys.add(pair[0]);
            }
            trainingFile.close();

            //start to build features vectors for documents of each query
            SDLearningToRankPool pool = new SDLearningToRankPool();
            pool.setPageRank(pageRank);
            pool.setDisableSetting(settings);

            for (int i = 0; i < traningKeys.size(); i++) {
                String key = traningKeys.get(i);
                String query = traningQueries.get(i);

                pool.addQuery(query);

                if(!revelanceMap.containsKey(key)) {
                    throw new NoSuchElementException("Not relevance judgement files for query " + key + " " + query);
                }

                ArrayList<String> docs = revelanceMap.get(key);
                ArrayList<Integer> docsInternalIds = new ArrayList<Integer>();
                ArrayList<Integer> docsRelevance = new ArrayList<Integer>();
                for(int j = 0; j < docs.size(); j++) {
                    String[] docinfo = docs.get(j).split(" ");
                    int docid = getInternalDocid(docinfo[2]);
                    docsInternalIds.add(docid);
                    int relevance = Integer.parseInt(docinfo[3]);
                    docsRelevance.add(relevance);
                }

                ArrayList<String> vs = pool.produceNormalizedFeatureVector(model, docsInternalIds);

                //write feature vector into target file
                for(int j = 0; j < docsInternalIds.size(); j++) {
                    featuresWriter.write(docsRelevance.get(j) + " qid:" + key + vs.get(j)
                                         + " # " + docs.get(j).split(" ")[2] + "\n");
                }
            }
            try {
                featuresWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // train SVM model
            trainSVM(learningModelPath, FEAT_GEN, traningFilePath, modelOutputFile);

            //Read test queries
            RetrievalModel modelBM25 = new RetrievalModelBM25();
            DataCenter.k1 = Double.parseDouble(params.get("BM25:k_1"));
            DataCenter.b = Double.parseDouble(params.get("BM25:b"));
            DataCenter.k3 = Double.parseDouble(params.get("BM25:k_3"));

            HashMap<String, ArrayList<String>> rerankPool = new HashMap<String, ArrayList<String>>();
            String testFeatureVectors = params.get("letor:testingFeatureVectorsFile");
            featuresWriter = new BufferedWriter(new FileWriter(new File(testFeatureVectors)));


            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String que = queries.get(i);
                qTree = parseQuery(que, modelBM25);
                printFeatureVectors(key, qTree.evaluate(modelBM25), model, pool, que, rerankPool);
            }

            try {
                featuresWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // call SVM classifier to produce new score
            String execPath = params.get("letor:svmRankClassifyPath");
            String testDataPath = params.get("letor:testingFeatureVectorsFile");
            String modelPath = params.get("letor:svmRankModelFile");
            String predictionPath = params.get("letor:testingDocumentScores");

            svmClassifier(execPath, testDataPath, modelPath, predictionPath);

            //print rerank result
            printRerankResult(rerankPool, predictionPath, keys);

        }else if(model instanceof  BipartiteClusteringModel){
            BipartiteClustering bipartiteClustering = new BipartiteClustering(params, false);

        }else if(!params.containsKey("fb") || params.get("fb").equals("false")) { //normal search engine model

            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                String que = queries.get(i);
                qTree = parseQuery(que, model);
                printResults(key, qTree.evaluate(model), qTree);
            }
        } else { // code following is about pesudo feedback
            double mu = Double.parseDouble(params.get("fbMu"));
            double w = Double.parseDouble( params.get("fbOrigWeight"));

            if(params.containsKey("fbInitialRankingFile")) {
                String initialRankingFilePath = params.get("fbInitialRankingFile");
                String outputRankingFilePath = params.get("fbExpansionQueryFile");
                int docNum = Integer.parseInt(params.get("fbDocs"));
                int termNum = Integer.parseInt(params.get("fbTerms"));
                BufferedWriter queryWriter = new BufferedWriter(new FileWriter(new File(outputRankingFilePath)));

                // for exp
                BufferedWriter queryWriter2 = new BufferedWriter(new FileWriter(new File("output/dummy")));

                // read reference document
                File referenceFile = new File(initialRankingFilePath);
                Scanner scanner;
                try {
                    scanner = new Scanner(referenceFile);
                } catch (FileNotFoundException e) {
                    System.out.println("No reference file");
                    e.printStackTrace();
                    return;
                }

                for(int q = 0; q < keys.size(); q++) {
                    String docRecord;
                    int counter = docNum;
                    ArrayList<SortEntity> currentQuery = new ArrayList<SortEntity>();
                    while (scanner.hasNext() && counter != 0) {
                        docRecord = scanner.nextLine();

                        String[] infos = docRecord.split(" ");
                        if(!infos[0].equals(keys.get(q)) &&
                                Integer.parseInt(infos[0]) < Integer.parseInt(keys.get(q))) {
                            continue;
                        } else if(!infos[0].equals(keys.get(q)) &&
                                  Integer.parseInt(infos[0]) > Integer.parseInt(keys.get(q))) {
                            System.out.println("Never should be here");
                            break;
                        } else {
                            counter--;
                            int docid = getInternalDocid(infos[2]);
                            SortEntity entity = new SortEntity(docid, infos[2], Double.parseDouble(infos[4]));
                            currentQuery.add(entity);
                        }
                    }
                    SDPesudoFeedBackEngine feedBackEngine = new SDPesudoFeedBackEngine(termNum,
                            currentQuery, keys.get(q), mu);
                    String queryExpasion = feedBackEngine.SDFeedback();
                    String newQuery = "#WAND ( " + w + "  #AND ( " + queries.get(q) + " )  " +
                                      (1 - w) + " " + queryExpasion +  " )";
                    System.out.println(newQuery);
                    queryWriter.write(keys.get(q) + ":" + queryExpasion + "\n");

                    //for exp
                    queryWriter2.write(keys.get(q) + ":" + newQuery+ "\n");

                    qTree = parseQuery(newQuery, model);
                    printResults(keys.get(q), qTree.evaluate(model), qTree);
                }
                try {
                    queryWriter.close();
                    queryWriter2.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                int docNum = Integer.parseInt(params.get("fbDocs"));
                int termNum = Integer.parseInt(params.get("fbTerms"));
                String outputRankingFilePath = params.get("fbExpansionQueryFile");
                BufferedWriter queryWriter = new BufferedWriter(new FileWriter(new File(outputRankingFilePath)));

                // for exp
                BufferedWriter queryWriter2 = new BufferedWriter(new FileWriter(new File("output/dummy")));

                for (int i = 0; i < keys.size(); i++) {
                    String key = keys.get(i);
                    String que = queries.get(i);
                    qTree = parseQuery(que, model);
                    String queryExpasion  = ResultsAfterFeedback(key, qTree.evaluate(model), qTree, docNum, termNum, mu);

                    String newQuery = "#WAND ( " + w + "  #AND ( " + queries.get(i) + " )  " +
                                      (1 - w) + " " + queryExpasion +  " )";
                    System.out.println(newQuery);
                    queryWriter.write(keys.get(i) + ":" + queryExpasion + "\n");

                    //for exp
                    queryWriter2.write(keys.get(i) + ":" + newQuery+ "\n");


                    qTree = parseQuery(newQuery, model);
                    printResults(keys.get(i), qTree.evaluate(model), qTree);
                }
                try {
                    queryWriter.close();
                    queryWriter2.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            writer.close();
        } catch (Exception e) {
            System.exit(1);
        }
        Util.printMemoryUsage(true);
    }

    /**
     *  Get the external document id for a document specified by an
     *  internal document id. If the internal id doesn't exists, returns null.
     *
     * @param iid The internal document id of the document.
     * @throws IOException
     */
    public static String getExternalDocid (int iid) throws IOException {
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
        if((model instanceof RetrievalModelUnrankedBoolean) || model instanceof  RetrievalModelRankedBoolean) // {}
            stack.push(new QryopSlOR());
        else if(model instanceof RetrievalModelBM25)
            stack.push(new QryopIlSum());
        else if(model instanceof RetrievalModelIndri) {
            stack.push(new QryopSlAnd());
        }
        // Add a default query operator to an unstructured query. This
        // is a tiny bit easier if unnecessary whitespace is removed.

        qString = qString.trim();
        if(model instanceof RetrievalModelRankedBoolean || model instanceof RetrievalModelUnrankedBoolean) {
            if (qString.charAt(0) != '#') {
                qString = "#OR(" + qString + ")";
            }
            if(qString.charAt(qString.length() - 1) != ')') {
                qString = "#OR(" + qString + ")";
            }
        } else if(model instanceof RetrievalModelBM25) {
            if (qString.charAt(0) != '#') {
                qString = "#SUM(" + qString + ")";
            }
            if(qString.charAt(qString.length() - 1) != ')') {
                qString = "#SUM(" + qString + ")";
            }
        } else if (model instanceof  RetrievalModelIndri) {
            if (qString.charAt(0) != '#') {
                qString = "#AND(" + qString + ")";
            }
            if(qString.charAt(qString.length() - 1) != ')') {
                qString = "#AND(" + qString + ")";
            }
        }
        // Tokenize the query.

        StringTokenizer tokens = new StringTokenizer(qString, "\t\n\r ,()", true);
        String token;
        boolean isWeight = true;
        // Each pass of the loop processes one token. To improve
        // efficiency and clarity, the query operator on the top of the
        // stack is also stored in currentOp.

        while (tokens.hasMoreTokens()) {

            token = tokens.nextToken();
            String [] dealNear = token.split("/");

            if (dealNear[0].matches("[ ,(\t\n\r]")) {
                continue;
                // Ignore most delimiters.
            } else if (dealNear[0].equalsIgnoreCase("#AND") || dealNear[0].equalsIgnoreCase("#And")
                       || dealNear[0].equalsIgnoreCase("#and")) {
                currentOp = new QryopSlAnd();
                stack.push(currentOp);
            } else if (dealNear[0].equalsIgnoreCase("#NEAR")) {
                currentOp = new QryopIlNear(Integer.parseInt(dealNear[1]));
                stack.push(currentOp);
            } else if(dealNear[0].equalsIgnoreCase("#OR") || dealNear[0].equalsIgnoreCase("#Or")) {
                currentOp = new QryopSlOR();
                stack.push(currentOp);
            } else if(dealNear[0].equalsIgnoreCase("#SUM")) {
                currentOp = new QryopIlSum();
                stack.push(currentOp);
            } else if(dealNear[0].equalsIgnoreCase("#SYN")) {
                currentOp = new QryopIlSyn();
                stack.push(currentOp);
            } else if(dealNear[0].equalsIgnoreCase("#WINDOW")) {
                currentOp = new QryopIlWindow(Integer.parseInt(dealNear[1]));
                stack.push(currentOp);
            } else if(dealNear[0].equalsIgnoreCase("#WAND")) {
                currentOp = new QryopSlWAND();
                stack.push(currentOp);
            } else if(dealNear[0].equalsIgnoreCase("#WSUM")) {
                currentOp = new QryopSlWSUM();
                stack.push(currentOp);
            } else if (dealNear[0].startsWith(")")) { // Finish current query operator.

                stack.pop();

                if (stack.empty())
                    break;

                Qryop arg = currentOp;
                currentOp = stack.peek();
                // if term is not noun, push into stack
                if(arg.getArgSize() != 0)
                    currentOp.add(arg);
            } else {
                if((currentOp instanceof QryopSlWAND ||
                        currentOp instanceof QryopSlWSUM) &&
                        isWeight) {
                    Double weight = Double.parseDouble(token.trim());
                    if(currentOp instanceof QryopSlWSUM) {
                        ((QryopSlWSUM)currentOp).weights.add(weight);
                    } else {
                        ((QryopSlWAND)currentOp).weights.add(weight);
                    }
                    isWeight = false;
                    continue;
                }

                String[] fieldName = token.split("\\.");
                if(fieldName.length == 1) {
                    String[] c = tokenizeQuery(fieldName[0]);
                    if(c.length != 0) {
                        currentOp.add(new QryopIlTerm(c[0]));
                    } else {
                        if (!isWeight && (currentOp instanceof QryopSlWAND ||
                                          currentOp instanceof QryopSlWSUM)) {
                            if (currentOp instanceof QryopSlWSUM) {
                                ((QryopSlWSUM) currentOp).weights.remove(((QryopSlWSUM) currentOp).weights.size() - 1);
                            } else if (currentOp instanceof QryopSlWAND) {
                                ((QryopSlWAND) currentOp).weights.remove(((QryopSlWAND) currentOp).weights.size() - 1);
                            }
                        }
                    }
                } else {
                    String[] c = tokenizeQuery(fieldName[0]);
                    if(c.length != 0) {
                        currentOp.add(new QryopIlTerm(c[0], fieldName[1]));
                    } else {
                        if (!isWeight && (currentOp instanceof QryopSlWAND ||
                                          currentOp instanceof QryopSlWSUM)) {
                            if (currentOp instanceof QryopSlWSUM) {
                                ((QryopSlWSUM) currentOp).weights.remove(((QryopSlWSUM) currentOp).weights.size() - 1);
                            } else if (currentOp instanceof QryopSlWAND) {
                                ((QryopSlWAND) currentOp).weights.remove(((QryopSlWAND) currentOp).weights.size() - 1);
                            }
                        }
                    }
                }
            }
            isWeight = true;
        }

        // A broken structured query can leave unprocessed tokens on the
        // stack, so check for that.

        if (tokens.hasMoreTokens()) {
            //System.err.println("Error:  Query syntax is incorrect.  " + qString);
            if(model instanceof RetrievalModelBM25) {
                qString = "#SUM(" + qString + ")";
                return parseQuery(qString, model);
            } else if(model instanceof RetrievalModelRankedBoolean || model instanceof RetrievalModelUnrankedBoolean) {
                qString = "#OR(" + qString + ")";
                return parseQuery(qString, model);
            } else if(model instanceof RetrievalModelIndri) {
                qString  = "#AND(" + qString + ")";
                return parseQuery(qString, model);
            }
            return null;
        }
        return currentOp;
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
        try {
            if (result.docScores.scores.size() < 1) {
                writer.write(queryID + "\tQ0\tdummy\t1\t0\trun-1\n");
            } else {
                List<Map.Entry<String, Double>> folder = Util.sortHashMap(result);
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
     *
     * @param queryID
     * @param result
     * @throws IOException
     */
    static void printFeatureVectors(String queryID, QryResult result,
                                    RetrievalModel modelLeanringToRank, SDLearningToRankPool pool, String query,
                                    HashMap<String, ArrayList<String>> rerankPool) throws IOException {
        try {
            if (result.docScores.scores.size() < 1) {
                writer.write(queryID + "\tQ0\tdummy\t1\t0\trun-1\n");
            } else {
                List<Map.Entry<String, Double>> folder = Util.sortHashMap(result);
                ArrayList<Integer> internalDocids = new ArrayList<Integer>();
                ArrayList<String> docExternIds = new ArrayList<String>();

                for (int i = 0; i < result.docScores.scores.size() && i < 100; i++) {
                    internalDocids.add(getInternalDocid(folder.get(i).getKey()));
                    docExternIds.add(folder.get(i).getKey());
                }
                rerankPool.put(queryID, docExternIds);

                pool.addQuery(query);

                ArrayList<String> features = pool.produceNormalizedFeatureVector(modelLeanringToRank, internalDocids);
                for(int j = 0; j < internalDocids.size(); j++) {
                    featuresWriter.write("0 qid:" + queryID + features.get(j)
                                         + " # " + folder.get(j).getKey() + "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Print the query results and compute feedback.
     *
     * @param result Result object generated by
     *               {@link SearchEngine.Assassin.Operators.Qryop#evaluate(SearchEngine.Assassin.RetrievalModel.RetrievalModel)}.
     * @throws IOException
     */
    static String ResultsAfterFeedback(String queryID, QryResult result, Qryop qTree,
                                       int docNum, int termNum, double mu) throws IOException {
        try {
            if (result.docScores.scores.size() < 1) {
                writer.write(queryID + "\tQ0\tdummy\t1\t0\trun-1\n");
            } else {
                ArrayList<SortEntity> folder = new ArrayList<SortEntity>();
                for(int i = 0; i < result.docScores.scores.size(); i++) {
                    folder.add(new SortEntity(result.docScores.scores.get(i).getDocid(),
                                              getExternalDocid(result.docScores.scores.get(i).getDocid()),
                                              result.docScores.getDocidScore(i)));
                }
                Collections.sort(folder);

                ArrayList<SortEntity> newFolder = new ArrayList<SortEntity>();
                for(int i = 0; i < docNum; i++) {
                    newFolder.add(folder.get(i));
                }

                SDPesudoFeedBackEngine feedBackEngine = new SDPesudoFeedBackEngine(termNum, newFolder, queryID, mu);
                String newQuery = feedBackEngine.SDFeedback();
                //System.out.println(newQuery);
                return newQuery;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     *  Given a query string, returns the terms one at a time with stopwords
     *  removed and the terms stemmed using the Krovetz stemmer. d
     *  Use this method to process raw query terms.
     *
     *  @param query String containing query
     *  @return Array of query tokens
     *  @throws IOException
     */
    public static String[] tokenizeQuery(String query) throws IOException {

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

    /**
     *
     * @param learningModelPath
     *        the SVM Rank trainer executable
     * @param FEAT_GEN
     *        argument for SVM
     * @param traningFilePath
     *        path to feature vectors file
     * @param modelOutputFile
     *        path to save trained model
     * @throws IOException
     *          throws if some files cannot be found
     * @throws InterruptedException
     *          throws if training process is interrupted
     * @throws Exception
     *          throws when SVM crash
     */
    private static void trainSVM(String learningModelPath, String FEAT_GEN, String traningFilePath,
                                 String modelOutputFile) throws Exception {
        Process cmdProc = Runtime.getRuntime().exec(
                              new String[] { learningModelPath, "-c", FEAT_GEN, traningFilePath,
                                             modelOutputFile
                                           });

        // consume stdout and print it out for debugging purposes
        BufferedReader stdoutReader = new BufferedReader(
            new InputStreamReader(cmdProc.getInputStream()));
        String l;
        while ((l = stdoutReader.readLine()) != null) {
            System.out.println(l);
        }
        // consume stderr and print it for debugging purposes
        BufferedReader stderrReader = new BufferedReader(
            new InputStreamReader(cmdProc.getErrorStream()));
        while ((l = stderrReader.readLine()) != null) {
            System.out.println(l);
        }

        // get the return value from the executable. 0 means success, non-zero
        // indicates a problem
        int retValue = cmdProc.waitFor();
        if (retValue != 0) {
            throw new Exception("SVM Rank crashed.");
        }
    }

    /**
     * Use SVM classifier to produce new scores
     * @param execPath
     *          Path to SVM classifier executable
     * @param textDataPath
     *          path to target documents' feature vector
     * @param modelPath
     *          path to tranined SVM model.
     * @param predictionPath
     *         path to result score files.
     * @throws Exception
     *      throw IOException when files do not exist
     *      throw Exception when SVM Classifier crash
     */
    private static void svmClassifier(String execPath, String textDataPath, String modelPath, String predictionPath)
    throws Exception {
        Process cmdProc = Runtime.getRuntime().exec(
                              new String[] { execPath, textDataPath, modelPath, predictionPath });

        // consume stdout and print it out for debugging purposes
        BufferedReader stdoutReader = new BufferedReader(
            new InputStreamReader(cmdProc.getInputStream()));
        String line;
        while ((line = stdoutReader.readLine()) != null) {
            System.out.println(line);
        }
        // consume stderr and print it for debugging purposes
        BufferedReader stderrReader = new BufferedReader(
            new InputStreamReader(cmdProc.getErrorStream()));
        while ((line = stderrReader.readLine()) != null) {
            System.out.println(line);
        }

        // get the return value from the executable. 0 means success, non-zero
        // indicates a problem
        int retValue = cmdProc.waitFor();
        if (retValue != 0) {
            throw new Exception("SVM Classifier crashed.");
        }
    }

    /**
     *
     * @param rerankPool
     * @param predictionPath
     * @param keys
     * @throws IOException
     */
    private static void printRerankResult(HashMap<String, ArrayList<String>> rerankPool,
                                          String predictionPath, ArrayList<String> keys) throws IOException {
        Scanner scanner = new Scanner(new File(predictionPath));
        if(scanner == null) {
            throw new FileNotFoundException("Prediction file not found");
        }


        for(int i = 0; i < keys.size(); i++) {
            String curKey = keys.get(i);
            ArrayList<String> curDocs = rerankPool.get(curKey);
            ArrayList<SortEntity> sortEntities = new ArrayList<SortEntity>();

            int j = 0;
            while(j < curDocs.size() && scanner.hasNext()) {
                String s = scanner.next();
                sortEntities.add(new SortEntity(curDocs.get(j), Double.parseDouble(s)));
                j++;
            }
            if(j < curDocs.size()) {
                System.err.println("Prediction files do not contain all scores");
                throw new NoSuchElementException("Prediction files do not contain all scores");
            }
            Collections.sort(sortEntities);

            for (int z = 0; z < sortEntities.size(); z++) {
                writer.write(curKey + "\tQ0\t"
                             + sortEntities.get(z).getExternalDocid()
                             + "\t"
                             + (z + 1)
                             + "\t"
                             + sortEntities.get(z).getScore()
                             + "\trun-1\n");
            }
        }
    }
}
