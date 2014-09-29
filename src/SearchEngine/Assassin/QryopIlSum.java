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

    public QryResult evaluate(RetrievalModel r) throws IOException {
       allocDaaTPtrs(r);
       QryResult result = new QryResult();
       DataCenter dataCenter = DataCenter.sharedDataCenter();

        HashMap<Integer, Double> map = new HashMap<Integer, Double>();
        for(int j = 0; j < this.daatPtrs.size(); j++){
            DaaTPtr ptrj = this.daatPtrs.get(j);
            while(ptrj.nextDoc < ptrj.scoreList.scores.size()){
                int docid = ptrj.scoreList.getDocid(ptrj.nextDoc);
                double score = ptrj.scoreList.getDocidScore(ptrj.nextDoc);
                if(map.containsKey(ptrj.scoreList.getDocid(ptrj.nextDoc))){
                    double updateSocre = score + map.get(ptrj.scoreList.getDocid(ptrj.nextDoc));
                    map.put(ptrj.scoreList.getDocid(ptrj.nextDoc), updateSocre);
                }else{
                    map.put(ptrj.scoreList.getDocid(ptrj.nextDoc), score);
                }
                ptrj.nextDoc++;
            }
        }
        for (Integer docid : map.keySet()){
            result.docScores.add(docid, map.get(docid));
        }
        if(map.containsKey(104534)){
            System.out.println(map.get(104534));
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
