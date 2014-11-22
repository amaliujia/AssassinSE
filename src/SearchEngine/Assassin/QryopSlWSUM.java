package SearchEngine.Assassin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by amaliujia on 14-10-16.
 */
public class QryopSlWSUM extends QryopSl {

    public ArrayList<Double> weights;
    public ArrayList<Qryop> newargs = new ArrayList<Qryop>();


    public QryopSlWSUM(){
        weights = new ArrayList<Double>();
        newargs = new ArrayList<Qryop>();
    }

    /**
     *  Used for Indri as a kind of smoothing
     * @param r
     * @param docid
     * @return
     * @throws IOException
     */
    public double getDefaultScore(RetrievalModel r, long docid) throws IOException {
        if (r instanceof RetrievalModelUnrankedBoolean ||
            r instanceof  RetrievalModelRankedBoolean ||
            r instanceof RetrievalModelBM25)
            return 0.0;
        else if(r instanceof RetrievalModelIndri){
            double c = 0;
            for(int z = 0; z < this.weights.size(); z++){
                c += this.weights.get(z);
            }
            // the task of #WAND is call its args' getDefaultScore function and merge them.
            double defaultScore = 0.0;
            for (int i = 0; i < this.args.size(); i++){
                defaultScore += ((QryopSl)this.args.get(i)).getDefaultScore(r, docid) * (this.weights.get(i) / c);
            }
            return defaultScore;
        }
        return 0.0;
    }


    public void add(Qryop q) throws IOException {
        this.args.add(q);
    }

    public void add(Qryop... q) throws IOException {
        for(int i = 0; i < q.length; i++){
            this.args.add(q[i]);
        }
    }


    public QryResult evaluate(RetrievalModel r) throws IOException {
        if(r instanceof RetrievalModelIndri){
            return evaluateIndri(r);
        }
        return null;
    }

    /**
     * Indri WSUN
     * @param r
     * @return
     * @throws IOException
     */
    public QryResult evaluateIndri(RetrievalModel r) throws IOException{
       // WSUMAllocDaaTPtrs(r);
        allocDaaTPtrs(r);
        QryResult result = new QryResult();

        HashMap<Double, Integer> map = new HashMap<Double, Integer>();
        DaaTPtr currentPtr = null;
        int currentID = -1;
        int []a = new int[this.daatPtrs.size()];
        double c = 0;

        for(int z = 0; z < this.weights.size(); z++){
            c += this.weights.get(z);
        }

        for(int i = 0; i < this.daatPtrs.size(); i++)  a[i] = -1;

        while(true) {
            int smallestForThisIteration = Integer.MAX_VALUE;
            // find the smallest unvisited docid
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
            double docScore = 0.0;
            // compute scores and default scores.
            if(smallestForThisIteration != Integer.MAX_VALUE){
                for(int z = 0; z < this.daatPtrs.size(); z++){
                    if(a[z] == -1) {
                        DaaTPtr ptrz = this.daatPtrs.get(z);
                        int docid = ptrz.scoreList.getDocid(ptrz.nextDoc);
                        if (docid == smallestForThisIteration) {
                            double t = ptrz.scoreList.getDocidScore(ptrz.nextDoc);
                            double temp = (t * (this.weights.get(z) / c));
                            docScore += temp;
                            ptrz.nextDoc++;
                        } else {
                            double temp = (((QryopSl) this.args.get(z)).getDefaultScore(r, smallestForThisIteration) *
                                                                                            (this.weights.get(z) / c));
                            docScore += temp;
                        }
                    }else{
                        double temp = (((QryopSl) this.args.get(z)).getDefaultScore(r, smallestForThisIteration) *
                                                                                            (this.weights.get(z) / c));
                        docScore += temp;
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

    /**
     *
     * @return
     */
    public String toString() {
        String result = new String();
        for(Iterator<Qryop> i = this.args.iterator(); i.hasNext();){
            result += (i.next().toString() + " ");
        }
        return "#WSUM( " + result + ")";
    }

    /**
     *
     * Split weight and term and fetch inverted list.
     * @param r
     *         Indicate what model it is using
     * @throws IOException
     */
    public void WSUMAllocDaaTPtrs(RetrievalModel r) throws IOException{
        filter();
        this.weights = new ArrayList<Double>();
        for (int i = 0; i < this.args.size(); i++) {
            //  If this argument doesn't return ScoreLists, wrap it
            //  in a #SCORE operator.
            if(i % 2 != 0) {
                if (!QryopSl.class.isInstance(this.args.get(i)))
                    this.args.set(i, new QryopSlScore(this.args.get(i)));

                DaaTPtr ptri = new DaaTPtr();
                ptri.invList = null;
                ptri.scoreList = this.args.get(i).evaluate(r).docScores;
                ptri.nextDoc = 0;

                this.daatPtrs.add(ptri);
                this.newargs.add(this.args.get(i));
            }else{
                String term = ((QryopIlTerm)this.args.get(i)).getTerm();
                term += ("." + ((QryopIlTerm)this.args.get(i)).getField());
                Double weigh = Double.parseDouble(term);
                this.weights.add(weigh);
            }
        }
    }

    /**
     * filter those invalid args.
     */
    public void filter(){
        ArrayList<Qryop> tempArgs = new ArrayList<Qryop>();

        for(int i = 0; i < this.args.size() - 1; i++){
            if(this.args.get(i) instanceof QryopIlTerm && this.args.get(i + 1) instanceof QryopIlTerm) {
                  String filed1 = ((QryopIlTerm) this.args.get(i)).getField();
                  String filed2 =  ((QryopIlTerm) this.args.get(i + 1)).getField();
                if (!isValidFiled(filed1) && !isValidFiled(filed2)) {
                    continue;
                }

                tempArgs.add(this.args.get(i));
            }else{
                tempArgs.add(this.args.get(i));
            }
        }

        if((this.args.get(this.args.size() - 1) instanceof QryopIlTerm) &&
                ((QryopIlTerm)this.args.get(this.args.size() - 1)).getTerm().equals("0")){
            this.args = tempArgs;
            return;
        }

        tempArgs.add(this.args.get(this.args.size() - 1));
        this.args = tempArgs;
    }

    /**
     *
     * @param field
     * @return
     */
    public boolean isValidFiled(String field){
        if(field.equals("body") ||
                field.equals("inlink") ||
                field.equals("url") ||
                field.equals("title") ||
                field.equals("keywords")){
            return true;
        }
        return false;

    }
}
