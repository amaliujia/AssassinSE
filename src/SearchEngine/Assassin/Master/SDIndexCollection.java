package SearchEngine.Assassin.Master;

import SearchEngine.Assassin.Lucene.DocLengthStore;
import SearchEngine.Assassin.Slave.SDSlaveIndexReader;
import org.apache.lucene.index.NumericDocValues;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by amaliujia on 15-8-15.
 */
public class SDIndexCollection implements Serializable {
    private int numOfDocs;
    private Map<String, NumericDocValues> docLenMap;
    private Map<String, Double> avgDocLenCntMap;
    private Map<String, Integer> fieldDocCntMap;
    private Map<String, Long> fieldLenMap;

    public SDIndexCollection(SDSlaveIndexReader sr) throws IOException {
        final String[] fields = {"body", "title", "url", "keywords"};


        this.numOfDocs = sr.READER.numDocs();
        avgDocLenCntMap = new HashMap<String, Double>();
        fieldDocCntMap = new HashMap<String, Integer>();
        fieldLenMap = new HashMap<String, Long>();

        DocLengthStore dls;
            dls = new DocLengthStore(sr.READER);
            this.docLenMap = dls.getLengthMap();
            for (String field : fields){
                int fieldDocCnt = sr.READER.getDocCount(field);
                long fieldLen = sr.READER.getSumTotalTermFreq(field);
                double avgDocLen = 1.0*fieldLen/fieldDocCnt;
                this.avgDocLenCntMap.put(field, avgDocLen);
                this.fieldDocCntMap.put(field, fieldDocCnt);
                this.fieldLenMap.put(field, fieldLen);
            }
    }

    public long getFieldLength(String field){
        if (!this.fieldLenMap.containsKey(field)){
            return 0;
        }
        return this.fieldLenMap.get(field);
    }

    public int getFieldDocCnt(String field){
        if (!this.fieldDocCntMap.containsKey(field)){
            return 0;
        }
        return this.fieldDocCntMap.get(field);
    }

    public double getAvgDocLength(String field){
        if (!this.avgDocLenCntMap.containsKey(field)){
            return 0;
        }
        return this.avgDocLenCntMap.get(field);
    }

    public int getNumOfDocs(){
        return this.numOfDocs;
    }

    public double getDocLength(String field, int docid){
        return this.docLenMap.get(field).get(docid);
    }
}
