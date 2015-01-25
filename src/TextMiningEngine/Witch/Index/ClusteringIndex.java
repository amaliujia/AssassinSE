package TextMiningEngine.Witch.Index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amaliujia on 15-1-23.
 */
public class ClusteringIndex {
    private List<Integer> docFreqList;

    private List<Double> idfList;

    private Map<String, Integer> dict;

    private List<ClusteringInvList> invLists;

    public ClusteringMatrix matrix;

    int collectionLength = 0;

    public ClusteringIndex(){
        docFreqList = new ArrayList<Integer>();
        dict = new HashMap<String, Integer>();
        invLists = new ArrayList<ClusteringInvList>();
    }

    public void addTermFreq(int df){
        docFreqList.add(df);
    }

    public void addDictWord(String word, int id){
        dict.put(word, id);
    }

    public void addinvList(ClusteringInvList invList){
        invLists.add(invList);
        collectionLength++;
    }

    public void beginIndexing(){
        computeIDF();
        matrix = new ClusteringMatrix();
        matrix.createRowVectors(invLists, idfList);
        //matrix.createColumnVectors(invLists, idfList);
        matrix.readColumnVectors();
    }

    private void computeIDF() {
        idfList = new ArrayList<Double>();
        double idf = -1.0;
        for(int i = 0; i < docFreqList.size(); i++){
            idf =  Math.log(collectionLength / (double)docFreqList.get(i) + 1);
            idfList.add(idf);
        }
    }
}
