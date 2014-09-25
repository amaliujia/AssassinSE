/**
 *  This class implements the SCORE operator for all retrieval models.
 *  The single argument to a score operator is a query operator that
 *  produces an inverted list.  The SCORE operator uses this
 *  information to produce a score list that contains document ids and
 *  scores.
 */
package SearchEngine.Assassin;
import java.io.*;
import java.util.*;

public class QryopSlScore extends QryopSl {

  /**
   *  Construct a new SCORE operator.  The SCORE operator accepts just
   *  one argument.
   *  @param q The query operator argument.
   *  @return @link{QryopSlScore}
   */
  public QryopSlScore(Qryop q) {
    this.args.add(q);
  }

  /**
   *  Construct a new SCORE operator.  Allow a SCORE operator to be
   *  created with no arguments.  This simplifies the design of some
   *  query parsing architectures.
   *  @return @link{QryopSlScore}
   */
  public QryopSlScore() {
  }

  /**
   *  Appends an argument to the list of query operator arguments.  This
   *  simplifies the design of some query parsing architectures.
   *  @param a The query argument to append.
   */
  public void add (Qryop a) {
    this.args.add(a);
  }

  /**
   *  Evaluate the query operator.
   *  @param r A retrieval model that controls how the operator behaves.
   *  @return The result of evaluating the query.
   *  @throws IOException
   */
  public QryResult evaluate(RetrievalModel r) throws IOException {

    if (r instanceof RetrievalModelUnrankedBoolean)
      return (evaluateBoolean (r));
    else if(r instanceof RetrievalModelRankedBoolean){
      return (evaluateRankedBoolean(r));
    }else if(r instanceof RetrievalModelBM25){
       return (evaluateBM25(r));
    }
    return null;
  }

    /**
     * @param r
     * @return
     * @throws IOException
     */
    public QryResult evaluateBM25(RetrievalModel r) throws IOException{
       DataCenter dataCenter = DataCenter.sharedDataCenter();
       QryResult result = args.get(0).evaluate(r);
        int N = dataCenter.numDocs;
        int df =  result.invertedList.df;
        String field = result.invertedList.field;
        double k1 = dataCenter.k1;
        double k3 = dataCenter.k3;
        double b = dataCenter.b;
        int g = 0;
        double avgLen = QryEval.READER.getSumTotalTermFreq(field) / QryEval.READER.getDocCount(field);
       for(int i = 0; i < result.invertedList.df; i++){
           int docid = result.invertedList.getDocid(i);
           int tf = result.invertedList.getTf(i);
           long docLen = dataCenter.docLengthStore.getDocLength(field, docid);
           double docScore = (double)Math.log(((double)N - df + 0.5)/(df + 0.5)) *
                   ((tf)/(tf + k1 * ((1.0-b)+b*((double)docLen/avgLen))))*
                   ((k1 + 1.0) * 1.0)/(k3 + 1.0);
//                   (double)Math.log(((double)N - df + 0.5)/(df + 0.5)) *
//                   ((tf)/(tf + k1 * ((1.0-b)+b*((double)docLen/(int)avgLen)))) *
//                   (((k1 + 1.0) * 1.0)/(k3 + 1.0));
         //  if(g < 20) {System.out.println(result.invertedList.getDocid(i) + "  " + docScore);g++;}
           //if(g == 0) {System.out.println(N + "  " + df + "  " + field + "  " + docLen + "  " + avgLen + " " + tf); g++;}
           result.docScores.add(docid, docScore);
       }

        if (result.invertedList.df > 0)
            result.invertedList = new InvList();

        return result;
    }



    /**
     *  Evaluate the query operator for boolean retrieval models.
     *  @param r A retrieval model that controls how the operator behaves.
     *  @return The result of evaluating the query.
     *  @throws IOException
     */
    public QryResult evaluateRankedBoolean(RetrievalModel r) throws IOException {

        // Evaluate the query argument.

        QryResult result = args.get(0).evaluate(r);

        // Each pass of the loop computes a score for one document. Note:
        // If the evaluate operation above returned a score list (which is
        // very possible), this loop gets skipped.

        for (int i = 0; i < result.invertedList.df; i++) {

            // DIFFERENT RETRIEVAL MODELS IMPLEMENT THIS DIFFERENTLY.

            result.docScores.add(result.invertedList.postings.get(i).docid,
                    result.invertedList.postings.get(i).tf);
        }

        // The SCORE operator should not return a populated inverted list.
        // If there is one, replace it with an empty inverted list.

        if (result.invertedList.df > 0)
            result.invertedList = new InvList();

        return result;
    }

 /**
   *  Evaluate the query operator for boolean retrieval models.
   *  @param r A retrieval model that controls how the operator behaves.
   *  @return The result of evaluating the query.
   *  @throws IOException
   */
  public QryResult evaluateBoolean(RetrievalModel r) throws IOException {

    // Evaluate the query argument.

    QryResult result = args.get(0).evaluate(r);

    // Each pass of the loop computes a score for one document. Note:
    // If the evaluate operation above returned a score list (which is
    // very possible), this loop gets skipped.

    for (int i = 0; i < result.invertedList.df; i++) {

      // DIFFERENT RETRIEVAL MODELS IMPLEMENT THIS DIFFERENTLY. 
      // Unranked Boolean. All matching documents get a score of 1.0.

      result.docScores.add(result.invertedList.postings.get(i).docid,
			   (float) 1.0);
    }

    // The SCORE operator should not return a populated inverted list.
    // If there is one, replace it with an empty inverted list.

    if (result.invertedList.df > 0)
	result.invertedList = new InvList();

    return result;
  }

  /*
   *  Calculate the default score for a document that does not match
   *  the query argument.  This score is 0 for many retrieval models,
   *  but not all retrieval models.
   *  @param r A retrieval model that controls how the operator behaves.
   *  @param docid The internal id of the document that needs a default score.
   *  @return The default score.
   */
  public double getDefaultScore (RetrievalModel r, long docid) throws IOException {

    if (r instanceof RetrievalModelUnrankedBoolean)
      return (0.0);

    return 0.0;
  }

  /**
   *  Return a string version of this query operator.  
   *  @return The string version of this query operator.
   */
  public String toString(){
    
    String result = new String ();

    for (Iterator<Qryop> i = this.args.iterator(); i.hasNext(); )
      result += (i.next().toString() + " ");

    return ("#SCORE( " + result + ")");
  }
}
