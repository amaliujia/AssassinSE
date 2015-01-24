package TextMingingEngine.Witch.Index;

import java.util.List;

/**
 * Created by amaliujia on 15-1-24.
 */
public class Cluster {
    public ClusteringVectorType type;
    private List<ClusteringInvList> vectors;
    private ClusteringInvList centroid;

    public Cluster(ClusteringVectorType type){
        this.type = type;
    }

    public void setCentroid(ClusteringInvList centroid){
        this.centroid = centroid;
    }

    public ClusteringInvList centroid(){
        return this.centroid;
    }
}
