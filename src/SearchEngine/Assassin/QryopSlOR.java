package SearchEngine.Assassin;

import java.io.*;

public class QryopSlOR extends QryopSl {

  public QryopSlOR(Qryop... q){
    for(int i = 0; i < q.length; i++){
      this.args.add(q[i]);
    }
  }
  
  @Override
  public double getDefaultScore(RetrievalModel r, long docid) throws IOException {
    if(r instanceof RetrievalModelUnrankedBoolean){
      return (1.0);
    }
    return 0.0;
  }

  /**
   *  Appends an argument to the list of query operator arguments.  This
   *  simplifies the design of some query parsing architectures.
   *  @param {q} q The query argument (query operator) to append.
   *  @return void
   *  @throws IOException
   */
  public void add(Qryop q) throws IOException {
    this.args.add(q);
  }


  public QryResult evaluate(RetrievalModel r) throws IOException {
    if(r instanceof RetrievalModelUnrankedBoolean){
      return (evaluateBoolean(r));
    }else if(r instanceof RetrievalModelRankedBoolean){
        return (evaluateRankedBoolean(r));
    }else if(r instanceof RetrievalModelBM25){
        return (evaluateRankedBoolean(r));
    }
    return null;
  }

  public QryResult evaluateRankedBoolean(RetrievalModel r) throws IOException {
      allocDaaTPtrs(r);
      QryResult result = new QryResult();
      if(this.daatPtrs.size() == 1){
          result.docScores = this.daatPtrs.get(0).scoreList;
          return result;
      }
      //  Exact-match OR requires that ALL scoreLists contain a
      //  document id.  Use the first (shortest) list to control the
      //  search for matches.

      //  Named loops are a little ugly.  However, they make it easy
      //  to terminate an outer loop from within an inner loop.
      //  Otherwise it is necessary to use flags, which is also ugly.
      DaaTPtr currentPtr = null;
      int currentID = -1;
      int []a = new int[this.daatPtrs.size()];
      for(int i = 0; i < this.daatPtrs.size(); i++)  a[i] = -1;

      while(true) {
          int smallestForThisIteration = Integer.MAX_VALUE;
          currentPtr = null;
          double docScore = -1;
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
                              currentPtr = ptrj;
                              docScore = ptrj.scoreList.getDocidScore(doc);
                          } else if (ptrj.scoreList.getDocid(doc) == smallestForThisIteration) {
                              if (ptrj.scoreList.getDocidScore(doc) > docScore) {
                                  docScore = ptrj.scoreList.getDocidScore(doc);
                              }
                          }
                          break;
                      }
                  }
              }
           }

          if (currentPtr != null) {
              result.docScores.add(currentPtr.scoreList.getDocid(currentPtr.nextDoc), docScore);
              currentID = smallestForThisIteration;
              currentPtr.nextDoc++;
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
      freeDaaTPtrs ();
      return result;
  }

  public QryResult evaluateBoolean(RetrievalModel r) throws IOException{
    allocDaaTPtrs(r);
    QryResult result = new QryResult();

    //  Exact-match OR requires that ALL scoreLists contain a
    //  document id.  Use the first (shortest) list to control the
    //  search for matches.

    //  Named loops are a little ugly.  However, they make it easy
    //  to terminate an outer loop from within an inner loop.
    //  Otherwise it is necessary to use flags, which is also ugly.
    DaaTPtr currentPtr = null;
    int currentID = -1;
    int []a = new int[this.daatPtrs.size()];
    for(int i = 0; i < this.daatPtrs.size(); i++)  a[i] = -1;
        while(true) {
         int smallestForThisIteration = Integer.MAX_VALUE;
         currentPtr = null;
         double docScore = 1.0;
         for(int j = 0; j < this.daatPtrs.size(); j++){
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
                             currentPtr = ptrj;
                         }
                         break;
                     }
                 }
             }
         }
             if (currentPtr != null) {
                 result.docScores.add(currentPtr.scoreList.getDocid(currentPtr.nextDoc), docScore);
                 currentID = smallestForThisIteration;
                 currentPtr.nextDoc++;
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

      freeDaaTPtrs ();
      return result;
  }



  public String toString() {
    String result = new String();
    for(int i = 0; i < this.args.size(); i++){
      result += this.args.get(i).toString() + " ";
    }
    return ("#OR(" + result + ")");
  }
  
}
