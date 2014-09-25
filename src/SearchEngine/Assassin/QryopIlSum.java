package SearchEngine.Assassin;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by amaliujia on 14-9-21.
 */
public class QryopIlSum extends QryopSl {
    @Override
    public double getDefaultScore(RetrievalModel r, long docid) throws IOException {
        return 0;
    }

    @Override
    public void add(Qryop q) throws IOException {
        this.args.add(q);
    }

    public void add(Qryop ...q) throws IOException{
        for(int i = 0; i < q.length; i++){
            this.args.add(q[i]);
        }
    }

    @Override
    public QryResult evaluate(RetrievalModel r) throws IOException {
       allocDaaTPtrs(r);
       QryResult result = new QryResult();
       DataCenter dataCenter = DataCenter.sharedDataCenter();

        HashMap<Integer, Double> map = new HashMap<Integer, Double>();
        for(int j = 0; j < this.daatPtrs.size(); j++){
            DaaTPtr ptrj = this.daatPtrs.get(j);
            while(ptrj.nextDoc < ptrj.scoreList.scores.size()){
                double score = ptrj.scoreList.getDocidScore(ptrj.nextDoc);
                int docid = ptrj.scoreList.getDocid(ptrj.nextDoc);
                if(map.containsKey(docid)){
                    double updateSocre = score + map.get(docid);
                    map.put(docid, updateSocre);
                }else{
                    map.put(docid, score);
                }
                ptrj.nextDoc++;
            }
        }
        for (Integer docid : map.keySet()){
//            if(map.get(docid) > 16) {
//                System.out.println(QryEval.getExternalDocid(docid) + "  " + docid + "   " + map.get(docid));
//            }
            result.docScores.add(docid, map.get(docid));
        }
        freeDaaTPtrs();
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
}
