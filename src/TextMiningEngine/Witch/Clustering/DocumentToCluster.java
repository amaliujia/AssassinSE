package TextMiningEngine.Witch.Clustering;

import TextMiningEngine.Witch.Index.Cluster;
import TextMiningEngine.Witch.Index.ClusteringInvList;
import TextMiningEngine.Witch.Index.ClusteringMatrix;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by amaliujia on 15-1-27.
 */
public class DocumentToCluster extends ObjectToCluster{

    public DocumentToCluster(int vectorSpaceSize){
        linkage = new TreeMap<Integer, Double>();
        for(int i = 0; i < vectorSpaceSize; i++){
            linkage.put(i, -1.0);
        }

        clusterID = new HashMap<Integer, Integer>();
        clusterSize = new HashMap<Integer, Integer>();
    }


    public void updateLinkage(ClusteringMatrix matrix, List<Cluster> clusters){
        for(int j = 0; j < clusters.size(); j++){
            Cluster c = clusters.get(j);
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

                if(!clusterID.containsKey(invlistid)){
                    clusterID.put(invlistid, j);
                }

                if(!clusterSize.containsKey(j)){
                    clusterSize.put(j, 1);
                } else{
                    clusterSize.put(j, clusterSize.get(j + 1));
                }
            }
        }
    }
}
