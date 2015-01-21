package SearchEngine.Assassin.Operators;

import SearchEngine.Assassin.Util.DataCenter;
import SearchEngine.Assassin.RetrievalModel.RetrievalModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by amaliujia on 14-9-21.
 */
public class QryopIlSum extends QryopSl {

    /**
     *
     * @param r
     * @param docid
     * @return
     * @throws IOException
     */
    @Override
    public double getDefaultScore(RetrievalModel r, long docid) throws IOException {
        return 0;
    }


    /**
     *
     * @param q
     * @throws IOException
     */
    @Override
    public void add(Qryop q) throws IOException {
        this.args.add(q);
    }


    /**
     *
     * @param q
     * @throws IOException
     */
    public void add(Qryop ...q) throws IOException {
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
        allocDaaTPtrs(r);
        QryResult result = new QryResult();
        DataCenter dataCenter = DataCenter.sharedDataCenter();

        // use hashtable to compute and save intermediate scroes.
        HashMap<Integer, Double> map = new HashMap<Integer, Double>();
        for(int j = 0; j < this.daatPtrs.size(); j++) {
            DaaTPtr ptrj = this.daatPtrs.get(j);
            while(ptrj.nextDoc < ptrj.scoreList.scores.size()) {
                int docid = ptrj.scoreList.getDocid(ptrj.nextDoc);
                double score = ptrj.scoreList.getDocidScore(ptrj.nextDoc);
                if(map.containsKey(ptrj.scoreList.getDocid(ptrj.nextDoc))) {
                    double updateSocre = score + map.get(ptrj.scoreList.getDocid(ptrj.nextDoc));
                    map.put(ptrj.scoreList.getDocid(ptrj.nextDoc), updateSocre);
                } else {
                    map.put(ptrj.scoreList.getDocid(ptrj.nextDoc), score);
                }
                ptrj.nextDoc++;
            }
        }
        // computation finished, fetch results from hashtable and put it into scorelist
        for (Integer docid : map.keySet()) {
            result.docScores.add(docid, map.get(docid));
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
        return "#SUM( " + result + ")";
    }
}
