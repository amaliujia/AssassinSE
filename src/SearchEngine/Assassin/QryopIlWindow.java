package SearchEngine.Assassin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Created by amaliujia on 14-10-15.
 */
public class QryopIlWindow extends QryopIl {

    private int distance;

    public QryopIlWindow(int distance) {
        this.distance = distance;
    }
    /**
     *
     * @param q
     * @throws IOException
     */
    public void add(Qryop q) throws IOException {
        this.args.add(q);
    }

    /**
     *
     * @param q
     */
    public void QryopIlWindow(Qryop... q) {
        for(int i = 0; i < q.length; i++) {
            this.args.add(q[i]);
        }
    }

    /**
     *
     * @param r A retrieval model that controls how the operator behaves.
     * @return
     * @throws IOException
     */
    public QryResult evaluate(RetrievalModel r) throws IOException {
        if(r instanceof RetrievalModelIndri || r instanceof RetrievalModelBM25)
            return evaluateBM25AndIndri(r);
        return null;
    }

    /**
     *
     * @param r
     * @return
     * @throws IOException
     */
    public QryResult evaluateBM25AndIndri(RetrievalModel r) throws IOException {
        allocDaaTPtrs(r);
        QryResult result = new QryResult();

        if(this.daatPtrs.size() == 0) {
            return null;
        } else if(this.daatPtrs.size() == 1) {
            result.invertedList.field = this.daatPtrs.get(0).invList.field;
            result.invertedList.postings = this.daatPtrs.get(0).invList.postings;
            result.invertedList.df = this.daatPtrs.get(0).invList.df;
            result.invertedList.ctf = this.daatPtrs.get(0).invList.ctf;
            freeDaaTPtrs();
            return result;
        }

        // Sort list first, use the shortest one as base
        // This sort can improve code performance
        int minInvList = Integer.MAX_VALUE;
        DaaTPtr basePtr = null;
        int baseIndex = -1;
        for (int i=0; i<(this.daatPtrs.size()-1); i++) {
            if(this.daatPtrs.get(i).invList.postings.size() < minInvList) {
                basePtr = this.daatPtrs.get(i);
                minInvList = basePtr.invList.postings.size();
                baseIndex = i;
            }
        }

        result.invertedList.field = basePtr.invList.field;

        EVALUATEDOCUMENTS:
        for ( ; basePtr.nextDoc < basePtr.invList.postings.size(); basePtr.nextDoc++) {

            int baseDocid = basePtr.invList.getDocid(basePtr.nextDoc);

            //  Do the other query arguments have the baseDocid?
            // If so, at least this doc have all terms we try to find

            for (int j = 0; j<this.daatPtrs.size(); j++) {
                if(j != baseIndex) {
                    DaaTPtr ptrj = this.daatPtrs.get(j);

                    while (true) {
                        if (ptrj.nextDoc >= ptrj.invList.postings.size())
                            break EVALUATEDOCUMENTS;        // No more docs can match
                        else if (ptrj.invList.getDocid(ptrj.nextDoc) > baseDocid)
                            continue EVALUATEDOCUMENTS;    // The ptr0docid can't match.
                        else if (ptrj.invList.getDocid(ptrj.nextDoc) < baseDocid)
                            ptrj.nextDoc++;            // Not yet at the right doc.
                        else
                            break;                // ptrj matches ptr0Docid
                    }
                }
            }

            ArrayList<Integer> pos = new ArrayList<Integer>();
            DocPosting returnPost = null;
            int isFirst = 0;

            OK:
            while(true) {
                for(int j = 0; j < daatPtrs.size(); j++) {
                    DaaTPtr ptrj = this.daatPtrs.get(j);
                    DocPosting postj = ptrj.invList.postings.get(ptrj.nextDoc);
                    if(postj.nextPostion >= postj.positions.size()) {
                        break OK;
                    }
                    pos.add(postj.positions.get(postj.nextPostion));
                }
                Collections.sort(pos);
                int min = pos.get(0);
                int max = pos.get(pos.size() - 1);
                if(this.distance >= (max - min + 1)) {
                    if(isFirst == 0) {
                        returnPost = new DocPosting(this.daatPtrs.get(0).
                                                    invList.getDocid(this.daatPtrs.get(0).nextDoc));
                        isFirst = 1;
                    }
                    returnPost.tf++;
                    returnPost.positions.add(max);
                    for(int z = 0; z < daatPtrs.size(); z++) {
                        DaaTPtr ptrz = this.daatPtrs.get(z);
                        DocPosting posting = ptrz.invList.postings.get(ptrz.nextDoc);
                        posting.nextPostion++;
                    }
                } else {
                    for(int z = 0; z < daatPtrs.size(); z++) {
                        DaaTPtr ptrz = this.daatPtrs.get(z);
                        DocPosting posting = ptrz.invList.postings.get(ptrz.nextDoc);
                        if(min == posting.positions.get(posting.nextPostion)) {
                            posting.nextPostion++;
                            break;
                        }
                    }
                }
                pos.clear();
            }
            if(isFirst == 1) {
                result.invertedList.appendPosting(returnPost.docid, returnPost.positions);
            }
        }
        freeDaaTPtrs();
        return result;
    }

    public String toString() {
        String result = new String();
        for(Iterator<Qryop> i = this.args.iterator(); i.hasNext();) {
            result += (i.next().toString() + " ");
        }
        return "#WINDOW( " + result + ")";
    }

}
