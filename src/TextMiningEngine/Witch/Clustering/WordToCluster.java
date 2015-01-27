package TextMiningEngine.Witch.Clustering;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by amaliujia on 15-1-27.
 */
public class WordToCluster {
    Map<Integer, Double> Linkage;

    public WordToCluster(){
        Linkage = new TreeMap<Integer, Double>();
    }

    public void initialize(){

    }
}
