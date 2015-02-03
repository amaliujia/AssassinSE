package SearchEngine.Assassin.Operators;

import SearchEngine.Assassin.DataStructure.DocPosting;
import SearchEngine.Assassin.RetrievalModel.RetrievalModel;
import SearchEngine.Assassin.Util.Util;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by amaliujia on 14-9-9.
 */
public class QryopIlNear extends QryopIl {

    private int distance;

    /**
     *
     * @param d
     */
    public void setDistance(int d) {
        this.distance = d;
    }

    /**
     *
     * @param d
     * @return
     */
    public int getDistance(int d) {
        return this.distance;
    }

    /**
     *
     * @param d
     */
    public QryopIlNear(int d) {
        this.distance = d;
    }

    /**
     *
     * @param q
     */
    public void QryopIlNear(Qryop... q) {
        for(int i = 0; i < q.length; i++) {
            this.args.add(q[i]);
        }
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
     * @param r A retrieval model that controls how the operator behaves.
     * @return
     * @throws IOException
     */
    public QryResult evaluate(RetrievalModel r) throws IOException {
        return (evaluateRankedBoolean(r));
    }

    /**
     *
     * @param r
     * @return
     * @throws IOException
     */
    public  QryResult evaluateRankedBoolean(RetrievalModel r) throws IOException {
        allocDaaTPtrs(r);
        syntaxCheckArgResults(this.daatPtrs);
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
            DaaTPtr ptr0 = this.daatPtrs.get(0);
            DocPosting post = ptr0.invList.postings.get(ptr0.nextDoc);
            int isFirst = 0;
            DocPosting returnPosting = null;


            if(ptr0.invList.getDocid(ptr0.nextDoc) ==  530178){
                System.err.println();
            }

            OK:
            for(; post.nextPostion < post.positions.size(); post.nextPostion++) {
                int first = post.positions.get(post.nextPostion);
                for(int j = 1; j < this.daatPtrs.size(); j++) {
                    DaaTPtr ptrj = this.daatPtrs.get(j);
                    DocPosting postj = ptrj.invList.postings.get(ptrj.nextDoc);
                    while (true) {
                        if(postj.nextPostion >= postj.positions.size()) { // cannot match
                            break OK ;
                        } else if(postj.positions.get(postj.nextPostion) <= first) { // too small
                            postj.nextPostion++;
                            continue;
                        } else if(postj.positions.get(postj.nextPostion) <= (first + this.distance)) { // match
                            first = postj.positions.get(postj.nextPostion);
                            break;
                        } else { // try next combination
                            continue OK;
                        }
                    }
                }
                if(isFirst == 0) {
                    returnPosting = new DocPosting(ptr0.invList.getDocid(ptr0.nextDoc));
                    isFirst = 1;
                }
//                if(returnPosting == null){
//                    returnPosting = new DocPosting(ptr0.invList.getDocid(ptr0.nextDoc));
//                }
                returnPosting.tf++;
                returnPosting.positions.add(this.daatPtrs.get(this.daatPtrs.size() - 1).invList.postings.
                                            get(this.daatPtrs.get(this.daatPtrs.size() - 1).nextDoc).positions.
                                            get(this.daatPtrs.get(this.daatPtrs.size() - 1).invList.postings.
                                                get(this.daatPtrs.get(this.daatPtrs.size() - 1).nextDoc).nextPostion));
                for(int z = 1; z < this.daatPtrs.size(); z++) {
                    DaaTPtr ptrz = this.daatPtrs.get(z);
                    ptrz.invList.postings.get(ptrz.nextDoc).nextPostion++;
                }
            }

            if(isFirst == 1) {
                result.invertedList.appendPosting(ptr0.invList.getDocid(ptr0.nextDoc), returnPosting.positions);
                //result.invertedList.df++;
                // result.invertedList.postings.add(returnPosting);
            }
//                if (returnPosting != null){
//                    result.invertedList.appendPosting(ptr0.invList.getDocid(ptr0.nextDoc), returnPosting.positions);
//                }
        }

        freeDaaTPtrs();
        return result;
    }

    /**
     *
     * @param r
     * @return
     * @throws IOException
     */
    public QryResult evaluateBoolean(RetrievalModel r) throws IOException {
        allocDaaTPtrs(r);
        syntaxCheckArgResults(this.daatPtrs);
        QryResult result = new QryResult();

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
        // first use algorithm which is similar to #AND to find a doc containg all terms in query
        // Then fetch postion of this doc for every term, and test if these temrs satisfies near condition\
        // if so, push into result.
        // if not, jump to next loop.
        EVALUATEDOCUMENTS:
        for ( ; basePtr.nextDoc < basePtr.invList.postings.size(); basePtr.nextDoc++) {

            int baseDocid = basePtr.invList.getDocid(basePtr.nextDoc);
            //double docScore = 0;

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

            DaaTPtr ptr0 = this.daatPtrs.get(0);
            DocPosting post = ptr0.invList.postings.get(ptr0.nextDoc);
            double score = 1.0;

            POSTIONEND:
            for(; post.nextPostion < post.positions.size(); post.nextPostion++) {
                int first = post.positions.get(post.nextPostion);
                for(int j = 1; j < this.daatPtrs.size(); j++) {
                    DaaTPtr ptrj = this.daatPtrs.get(j);
                    DocPosting postj = ptrj.invList.postings.get(ptrj.nextDoc);
                    while (true) {
                        if(postj.nextPostion >= postj.positions.size()) {
                            break POSTIONEND;
                        } else if(postj.positions.get(postj.nextPostion) <= first) {
                            postj.nextPostion++;
                            continue;
                        } else if(postj.positions.get(postj.nextPostion) <= (first + this.distance)) {
                            first = postj.positions.get(postj.nextPostion);
                            break;
                        } else {
                            continue POSTIONEND;
                        }
                    }
                }
                DocPosting returnPosting = new DocPosting(ptr0.invList.getDocid(ptr0.nextDoc));
                result.invertedList.postings.add(returnPosting);
                result.invertedList.df++;
                break ;
            }
        }
        freeDaaTPtrs();
        return result;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        String result = new String();
        for(Iterator<Qryop> i = this.args.iterator(); i.hasNext();) {
            result += (i.next().toString() + " ");
        }
        return "#Near( " + result + ")";
    }

    /**
     *  syntaxCheckArgResults does syntax checking that can only be done
     *  after query arguments are evaluated.
     *  @param ptrs A list of DaaTPtrs for this query operator.
     *  @return True if the syntax is valid, false otherwise.
     */
    public Boolean syntaxCheckArgResults (List<DaaTPtr> ptrs) {

        for (int i=0; i<this.args.size(); i++) {

            if (! (this.args.get(i) instanceof QryopIl))
                Util.fatalError("Error:  Invalid argument in " +
                        this.toString());
            else if ((i>0) && (! ptrs.get(i).invList.field.equals (ptrs.get(0).invList.field)))
                Util.fatalError ("Error:  Arguments must be in the same field:  " +
                                    this.toString());
        }
        return true;
    }
}
