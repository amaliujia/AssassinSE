package TextMiningEngine.Witch.Clustering;

import TextMiningEngine.Witch.Index.Cluster;
import TextMiningEngine.Witch.Index.ClusteringMatrix;

import java.util.List;
import java.util.Map;

/**
 * Created by amaliujia on 15-2-1.
 */
public abstract class ObjectToCluster {
    protected Map<Integer, Double> linkage;

    protected Map<Integer, Integer> clusterID;

    protected Map<Integer, Integer> clusterSize;

    public abstract void updateLinkage(ClusteringMatrix matrix, List<Cluster> clusters);

    public Map<Integer, Double>  getWeights(){
        return linkage;
    }

    public double getWeight(int i){
        return linkage.get(i);
    }

    public int getClustersSize(){
        return clusterSize.size();
    }

    public int getClusterID(int i){
        return clusterID.get(i);
    }

    public int getClusterSize(int i){
        return clusterSize.get(i);
    }
}
