/**
 *  This class implements the AND operator for all retrieval models.
 */
package SearchEngine.Assassin;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class QryopSlAnd extends QryopSl{

  /**
   *  It is convenient for the constructor to accept a variable number
   *  of arguments. Thus new qryopAnd (arg1, arg2, arg3, ...).
   *  @param q A query argument (a query operator).
   */
  public QryopSlAnd(Qryop... q) {
    for (int i = 0; i < q.length; i++)
      this.args.add(q[i]);
  }

  /**
   *  Appends an argument to the list of query operator arguments.  This
   *  simplifies the design of some query parsing architectures.
   *  @param {q} q The query argument (query operator) to append.
   *  @return void
   *  @throws IOException
   */
  public void add (Qryop a) {
    this.args.add(a);
  }

  /**
   *  Evaluates the query operator, including any child operators and
   *  returns the result.
   *  @param r A retrieval model that controls how the operator behaves.
   *  @return The result of evaluating the query.
   *  @throws IOException
   */
  public QryResult evaluate(RetrievalModel r) throws IOException {

    if (r instanceof RetrievalModelUnrankedBoolean)
      return (evaluateBoolean (r));
    else if(r instanceof RetrievalModelRankedBoolean)
      return (evaluateRankedBoolean(r));
    else if(r instanceof RetrievalModelIndri){
        return evaluateIndri(r);
    }
    return null;
  }

    public QryResult evaluateIndri(RetrievalModel r) throws  IOException{
        allocDaaTPtrs(r);
        QryResult result = new QryResult();
        HashMap<Double, Integer> map = new HashMap<Double, Integer>();
        DaaTPtr currentPtr = null;
        int currentID = -1;
        int []a = new int[this.daatPtrs.size()];
        int c = 0;
        for(int i = 0; i < this.daatPtrs.size(); i++)  a[i] = -1;

        while(true) {
            int smallestForThisIteration = Integer.MAX_VALUE;

            for (int j = 0; j < this.daatPtrs.size(); j++) {
                if (a[j] == -1) {
                    DaaTPtr ptrj = this.daatPtrs.get(j);
                    while (true) {
                        if (ptrj.nextDoc >= ptrj.scoreList.scores.size()) {
                            a[j] = 1;
                            break;
                        } else if (ptrj.scoreList.getDocid(ptrj.nextDoc) <= currentID) {
                            ptrj.nextDoc++;
                            continue;
                        } else if (ptrj.scoreList.getDocid(ptrj.nextDoc) > currentID) {
                            int doc = ptrj.nextDoc;
                            if (ptrj.scoreList.getDocid(doc) < smallestForThisIteration) {
                                smallestForThisIteration = ptrj.scoreList.getDocid(doc);
                            }
                            break;
                        }
                    }
                }
            }
            double docScore = 1.0;

            if(smallestForThisIteration != Integer.MAX_VALUE){
//                if(QryEval.getExternalDocid(smallestForThisIteration).equals("clueweb09-enwp00-44-01825")){
//                    System.out.println("Get you");
//                }
                for(int z = 0; z < this.daatPtrs.size(); z++){
                   if(a[z] == -1) {
                       DaaTPtr ptrz = this.daatPtrs.get(z);
                       int docid = ptrz.scoreList.getDocid(ptrz.nextDoc);
                       if (docid == smallestForThisIteration) {
                           double t = ptrz.scoreList.getDocidScore(ptrz.nextDoc);
                           double temp = (Math.pow(t, (1.0 / this.args.size())));
                           docScore *= temp;
                           ptrz.nextDoc++;
                       } else {
                           double temp = (Math.pow(((QryopSl) this.args.get(z)).getDefaultScore(r, smallestForThisIteration), (1.0 / this.args.size())));
                           docScore *= temp;
                       }
                   }else{
                       double temp = (Math.pow(((QryopSl) this.args.get(z)).getDefaultScore(r, smallestForThisIteration), (1.0 / this.args.size())));
                       docScore *= temp;
                   }
                }
                currentID = smallestForThisIteration;
                result.docScores.add(smallestForThisIteration, docScore);
            }
            int count = 0;
            for (; count < this.daatPtrs.size(); count++) {
                if (a[count] == -1) {
                    break;
                }
            }
            if (count == this.daatPtrs.size()) {
                break;
            }
        }
        freeDaaTPtrs();
        return result;
    }
    /*
        Evaluate the query by ranked boolean retrieval model
        @param r a RetrievalModel, for this one, a RankedRetrievalModel
        @return The result of the query
     */
public QryResult evaluateRankedBoolean(RetrievalModel r) throws IOException {
        allocDaaTPtrs (r);
        QryResult result = new QryResult();

        //  Sort the arguments so that the shortest lists are first.  This
        //  improves the efficiency of exact-match AND without changing
        //  the result.

        for (int i=0; i<(this.daatPtrs.size()-1); i++) {
            for (int j=i+1; j<this.daatPtrs.size(); j++) {
                if(this.daatPtrs.get(i).scoreList.scores.size() >
                        this.daatPtrs.get(j).scoreList.scores.size()){
                    ScoreList tmpScoreList = this.daatPtrs.get(i).scoreList;
                    this.daatPtrs.get(i).scoreList = this.daatPtrs.get(j).scoreList;
                    this.daatPtrs.get(j).scoreList = tmpScoreList;
                }
            }
        }
        //  Exact-match AND requires that ALL scoreLists contain a
        //  document id.  Use the first (shortest) list to control the
        //  search for matches.

        //  Named loops are a little ugly.  However, they make it easy
        //  to terminate an outer loop from within an inner loop.
        //  Otherwise it is necessary to use flags, which is also ugly.

        DaaTPtr ptr0 = this.daatPtrs.get(0);

        EVALUATEDOCUMENTS:
        for ( ; ptr0.nextDoc < ptr0.scoreList.scores.size(); ptr0.nextDoc++) {

            int ptr0Docid = ptr0.scoreList.getDocid (ptr0.nextDoc);
            double docScore = 0;

            //  Do the other query arguments have the ptr0Docid?

            for (int j=1; j<this.daatPtrs.size(); j++) {

                DaaTPtr ptrj = this.daatPtrs.get(j);

                while (true) {
                    if (ptrj.nextDoc >= ptrj.scoreList.scores.size())
                        break EVALUATEDOCUMENTS;		// No more docs can match
                    else if (ptrj.scoreList.getDocid (ptrj.nextDoc) > ptr0Docid)
                        continue EVALUATEDOCUMENTS;	// The ptr0docid can't match.
                    else if (ptrj.scoreList.getDocid (ptrj.nextDoc) < ptr0Docid)
                        ptrj.nextDoc++;			// Not yet at the right doc.
                    else
                        break;				// ptrj matches ptr0Docid
                }
            }

            //  The ptr0Docid matched all query arguments, so save it.
            double minScore = Integer.MAX_VALUE;
            for (int j=0; j<this.daatPtrs.size(); j++) {
                  DaaTPtr ptrj = this.daatPtrs.get(j);
                  if(ptrj.scoreList.getDocidScore(ptrj.nextDoc) < minScore) {
                      minScore = ptrj.scoreList.getDocidScore(ptrj.nextDoc);
                  }

            }
            docScore = minScore;
            result.docScores.add(ptr0Docid, docScore);
        }

        freeDaaTPtrs ();
        return result;
    }


  /**
   *  Evaluates the query operator for boolean retrieval models,
   *  including any child operators and returns the result.
   *  @param r A retrieval model that controls how the operator behaves.
   *  @return The result of evaluating the query.
   *  @throws IOException
   */
  public QryResult evaluateBoolean (RetrievalModel r) throws IOException {

    //  Initialization

    allocDaaTPtrs (r);
    QryResult result = new QryResult ();

    //  Sort the arguments so that the shortest lists are first.  This
    //  improves the efficiency of exact-match AND without changing
    //  the result.

      for (int i=0; i<(this.daatPtrs.size()-1); i++) {
          for (int j=i+1; j<this.daatPtrs.size(); j++) {
            if(this.daatPtrs.get(i).scoreList.scores.size() >
                this.daatPtrs.get(j).scoreList.scores.size()){
                  ScoreList tmpScoreList = this.daatPtrs.get(i).scoreList;
                  this.daatPtrs.get(i).scoreList = this.daatPtrs.get(j).scoreList;
                  this.daatPtrs.get(j).scoreList = tmpScoreList;
              }
          }
      }

    //  Exact-match AND requires that ALL scoreLists contain a
    //  document id.  Use the first (shortest) list to control the
    //  search for matches.

    //  Named loops are a little ugly.  However, they make it easy
    //  to terminate an outer loop from within an inner loop.
    //  Otherwise it is necessary to use flags, which is also ugly.

    DaaTPtr ptr0 = this.daatPtrs.get(0);

    EVALUATEDOCUMENTS:
    for ( ; ptr0.nextDoc < ptr0.scoreList.scores.size(); ptr0.nextDoc ++) {

      int ptr0Docid = ptr0.scoreList.getDocid (ptr0.nextDoc);
      double docScore = 1.0;

      //  Do the other query arguments have the ptr0Docid?

      for (int j=1; j<this.daatPtrs.size(); j++) {

             DaaTPtr ptrj = this.daatPtrs.get(j);

             while (true) {
                if (ptrj.nextDoc >= ptrj.scoreList.scores.size())
                    break EVALUATEDOCUMENTS;		// No more docs can match
                else if (ptrj.scoreList.getDocid (ptrj.nextDoc) > ptr0Docid)
                    continue EVALUATEDOCUMENTS;	// The ptr0docid can't match.
                else if (ptrj.scoreList.getDocid (ptrj.nextDoc) < ptr0Docid)
                      ptrj.nextDoc++;			// Not yet at the right doc.
                else
                      break;				// ptrj matches ptr0Docid
             }
      }

      //  The ptr0Docid matched all query arguments, so save it.
      result.docScores.add (ptr0Docid, docScore);
    }

    freeDaaTPtrs ();
   // result.sort();
    return result;
  }

  /*
   *  Calculate the default score for the specified document if it
   *  does not match the query operator.  This score is 0 for many
   *  retrieval models, but not all retrieval models.
   *  @param r A retrieval model that controls how the operator behaves.
   *  @param docid The internal id of the document that needs a default score.
   *  @return The default score.
   */
  public double getDefaultScore (RetrievalModel r, long docid) throws IOException {

      if (r instanceof RetrievalModelUnrankedBoolean || r instanceof  RetrievalModelRankedBoolean || r instanceof RetrievalModelBM25)
        return (0.0);
      else if(r instanceof RetrievalModelIndri){
        double defaultScore = 1;
        for (int i = 0; i < this.args.size(); i++){
                defaultScore *= (Math.pow(((QryopSl)this.args.get(i)).getDefaultScore(r, docid), (1.0 / this.args.size())));
        }
        return defaultScore;
      }

    return 0.0;
  }

  /*
   *  Return a string version of this query operator.  
   *  @return The string version of this query operator.
   */
  public String toString(){
    
    String result = new String ();

    for (int i=0; i<this.args.size(); i++)
      result += this.args.get(i).toString() + " ";

    return ("#AND( " + result + ")");
  }

}
