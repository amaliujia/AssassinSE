package TextMiningEngine.Witch.Clustering;

import java.security.PublicKey;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by amaliujia on 15-1-27.
 */
public class DocumentToCluster {
    Map<Integer, Double> Linkage;

    public DocumentToCluster(){
        Linkage = new TreeMap<Integer, Double>();
    }
}
