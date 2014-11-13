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

   public int ctf;
   String filed;
  // HashMap<Integer, Double> ids;

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
    }else if(r instanceof RetrievalModelIndri){
        return (evaluateIndri(r));
    }
    return null;
  }

    /**
     *
     * @param r this model is Indri retrieval model
     * @return  QryResult, save Indri scores
     * @throws IOException
     */
    public QryResult evaluateIndri(RetrievalModel r) throws IOException{
        HashMap<Double, Integer> map = new HashMap<Double, Integer>();
        QryResult result = args.get(0).evaluate(r);
        this.filed = result.invertedList.field;
        this.ctf = result.invertedList.ctf;
        RetrievalModelIndri indri = (RetrievalModelIndri)r;
        for(int i = 0; i < result.invertedList.postings.size(); i++){
            int docid = result.invertedList.getDocid(i);
            double tf = result.invertedList.getTf(i);
            double p = 0;
            double collectionLen = QryEval.READER.getSumTotalTermFreq(filed);
            double lenDoc = DataCenter.sharedDataCenter().docLengthStore.getDocLength(this.filed, docid);
            double pmle = ((double)this.ctf / collectionLen);
            p = (((indri.lambda * (tf + (indri.mu * pmle))) / (lenDoc + indri.mu))) +
                        ((1 - indri.lambda) * pmle);
            result.docScores.add(docid, p);
        }

        if(result.invertedList.df > 0){
            result.invertedList = new InvList();
        }
        return result;
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
       double avgLen = (double)QryEval.READER.getSumTotalTermFreq(field) / (double)QryEval.READER.getDocCount(field);
       // core part of algorithm. logic is similar with #OR, iterate all docid
       for(int i = 0; i < result.invertedList.df; i++){
           int docid = result.invertedList.getDocid(i);
           double tf = result.invertedList.getTf(i);
           double docLen = dataCenter.docLengthStore.getDocLength(field, docid);
           double a = Math.log((N - df + 0.5)/(df + 0.5));
           double c =  (tf / (tf + k1 * ((1.0 - b) + b * (docLen / avgLen))));
           double d =  ((k3 + 1.0) * 1.0) / (k3 + 1.0);
           double docScore =  a * c  * d ;
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

        for (int i = 0; i < result.invertedList.df; i++) {
            result.docScores.add(result.invertedList.postings.get(i).docid,
                    result.invertedList.postings.get(i).tf);
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
  public QryResult evaluateBoolean(RetrievalModel r) throws IOException {

    // Evaluate the query argument.

    QryResult result = args.get(0).evaluate(r);

    for (int i = 0; i < result.invertedList.df; i++) {
      result.docScores.add(result.invertedList.postings.get(i).docid,
			   (float) 1.0);
    }

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

    if (r instanceof RetrievalModelUnrankedBoolean || r instanceof  RetrievalModelRankedBoolean || r instanceof RetrievalModelBM25)
      return (0.0);
    else if(r instanceof RetrievalModelIndri){
        // apply Indri default score formula
        RetrievalModelIndri indri = (RetrievalModelIndri)r;
        double tf = 0;
        double p = 0;
        double collectionLen = QryEval.READER.getSumTotalTermFreq(filed);
        double lenDoc = DataCenter.sharedDataCenter().docLengthStore.getDocLength(this.filed, (int)docid);
        double pmle = (double)this.ctf / collectionLen;
        p = (indri.lambda * ((tf + (indri.mu * pmle)) / (lenDoc + indri.mu))) +
                    ((1 - indri.lambda) * pmle);
        return p;
    }

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
