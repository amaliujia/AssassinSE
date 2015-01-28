package TextMiningEngine.Witch.Clustering;

import TextMiningEngine.Witch.Index.Cluster;
import TextMiningEngine.Witch.Index.ClusteringInvList;
import TextMiningEngine.Witch.Index.ClusteringMatrix;

import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by amaliujia on 15-1-27.
 */
public class DocumentToCluster {
    Map<Integer, Double> linkage;

    public DocumentToCluster(int vectorSpaceSize){
        linkage = new TreeMap<Integer, Double>();
        for(int i = 0; i < vectorSpaceSize; i++){
            linkage.put(i, -1.0);
        }
    }

    public Map<Integer, Double>  getWeights(){
        return linkage;
    }

    public void updateLinkage(ClusteringMatrix matrix, List<Cluster> clusters){
        for (Cluster c : clusters){
            int size = c.clusterSize();
            ClusteringInvList invList;
            for(int i = 0; i < size; i++){
                invList = c.getVec(i);
                if(!linkage.containsKey(invList.getInvlistID())) {
                    System.err.println("no inverted list id");
                    continue;
                }
                int invlistid = invList.getInvlistID();
                double cosine =  BipartiteClustering.CosineSimilarity(invList,
                        matrix.getRowVector(invList.getInvlistID()));
                linkage.put(invList.getInvlistID(), cosine);
            }
        }
    }
}
