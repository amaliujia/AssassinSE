package TextMingingEngine.Index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amaliujia on 15-1-23.
 */
public class ClusteringIndex {
    List<Integer> termFreqList;

    Map<String, Integer> dict;

    List<ClusteringInvList> invLists;

    public ClusteringIndex(){
        termFreqList = new ArrayList<Integer>();
        dict = new HashMap<String, Integer>();
        invLists = new ArrayList<ClusteringInvList>();
    }

    public void addTermFreq(int df){
        termFreqList.add(df);
    }

    public void addDictWord(String word, int id){
        dict.put(word, id);
    }

    public void addinvList(ClusteringInvList invList){
        invLists.add(invList);
    }
}
