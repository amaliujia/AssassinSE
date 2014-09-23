package SearchEngine.Assassin;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by amaliujia on 14-9-21.
 */
public class QryopIlSum extends QryopIl {

    @Override
    public void add(Qryop q) throws IOException {
        this.args.add(q);
    }

    public void add(Qryop ...q) throws IOException{
        for(int i = 0; i < q.length; i++){
            this.args.add(q[i]);
        }
    }

    // This function is the place where our algorithm is implemented
    @Override
    public QryResult evaluate(RetrievalModel r) throws IOException {
        allocDaaTPtrs(r);
        syntaxCheckArgResults(this.daatPtrs);
        QryResult result = new QryResult();

        DaaTPtr currentPtr = null;
        int currentID = -1;
        int []a = new int[this.daatPtrs.size()];
        for(int i = 0; i < this.daatPtrs.size(); i++)  a[i] = -1;
        DataCenter dataCenter = DataCenter.sharedDataCenter();

        while(true) {
            int smallestForThisIteration = Integer.MAX_VALUE;
            //currentPtr = null;
            double sum = 0;
            for (int j = 0; j < this.daatPtrs.size(); j++) {
                if (a[j] == -1) {
                    DaaTPtr ptrj = this.daatPtrs.get(j);
                    while (true) {
                        if (ptrj.nextDoc >= ptrj.invList.postings.size()) {
                            a[j] = 1;
                            break;
                        } else if (ptrj.invList.getDocid(ptrj.nextDoc) <= currentID) {
                            ptrj.nextDoc++;
                            continue;
                        } else if (ptrj.invList.getDocid(ptrj.nextDoc) > currentID) {
                            int doc = ptrj.nextDoc;
                            if (ptrj.invList.getDocid(doc) < smallestForThisIteration) {
                                smallestForThisIteration = ptrj.invList.getDocid(doc);
                              //  currentPtr = ptrj;
                            }
                            break;
                        }
                    }
                }
            }

            if (smallestForThisIteration != Integer.MAX_VALUE) {
                DocPosting newPost = new DocPosting();
                for(int j = 0; j < this.daatPtrs.size(); j++){
                    if(a[j] == -1) {
                        DaaTPtr tempDaat = this.daatPtrs.get(j);
                        int docID = tempDaat.invList.getDocid(tempDaat.nextDoc);
                        newPost.docid = docID;
                        if (docID == smallestForThisIteration) {
                            // calculate formule
                            DocPosting post = tempDaat.invList.postings.get(tempDaat.nextDoc);
                           // int tf = post.tf;
                            //int N = dataCenter.numDocs;
                            int df = tempDaat.invList.df;
                            int docLen = (int) dataCenter.docLengthStore.getDocLength(tempDaat.invList.field, docID);
                            // To DO: calculate term freq in query
                            float avgLen = QryEval.READER.getSumTotalTermFreq(tempDaat.invList.field) / QryEval.READER.getDocCount(tempDaat.invList.field);
                            sum += ((Math.log(dataCenter.numDocs - df + 0.5) / Math.log(df + 0.5)) * (post.tf / (post.tf + dataCenter.k1 * ((1 - dataCenter.b) + dataCenter.b * docLen / avgLen))) * ((dataCenter.k3 + 1 * 1) / dataCenter.k3 + 1));
                            tempDaat.nextDoc++;
                        }
                    }
                }
                newPost.frqBM25 = sum;
                result.invertedList.postings.add(newPost);
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
   //     result.sort();
        return result;
    }

    @Override
    public String toString() {
        String result = new String();
        for(Iterator<Qryop> i = this.args.iterator(); i.hasNext();){
            result += (i.next().toString() + " ");
        }
        return "#SUM( " + result + ")";
    }

    public Boolean syntaxCheckArgResults (List<DaaTPtr> ptrs) {

        for (int i=0; i<this.args.size(); i++) {

            if (! (this.args.get(i) instanceof QryopIl))
                QryEval.fatalError ("Error:  Invalid argument in " +
                        this.toString());
            else if ((i>0) && (! ptrs.get(i).invList.field.equals (ptrs.get(0).invList.field)))
                QryEval.fatalError ("Error:  Arguments must be in the same field:  " +
                        this.toString());
        }
        return true;
    }
}
