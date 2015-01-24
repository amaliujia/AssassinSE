package TextMingingEngine.Witch.Index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amaliujia on 15-1-23.
 */
public class ClusteringIndex {
    List<Integer> docFreqList;

    List<Double> idfList;

    Map<String, Integer> dict;

    List<ClusteringInvList> invLists;

    int collectionLength;

    public ClusteringIndex(){
        docFreqList = new ArrayList<Integer>();
        dict = new HashMap<String, Integer>();
        invLists = new ArrayList<ClusteringInvList>();
        collectionLength = invLists.size();
    }

    public void addTermFreq(int df){
        docFreqList.add(df);
    }

    public void addDictWord(String word, int id){
        dict.put(word, id);
    }

    public void addinvList(ClusteringInvList invList){
        invLists.add(invList);
    }
//
//    private void computeIDF(){
//        idfList = new ArrayList<Double>();
//        for (int i = 0; i < ClusteringInvList)
//    }
}
