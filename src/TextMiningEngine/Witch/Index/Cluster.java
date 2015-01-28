package TextMiningEngine.Witch.Index;

import java.util.ArrayList;
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
        this.vectors = new ArrayList<ClusteringInvList>();
    }

    public Cluster(ClusteringVectorType type, ClusteringInvList cen){
        this.type = type;
        this.centroid = cen;
        this.vectors = new ArrayList<ClusteringInvList>();
    }


    public void setCentroid(ClusteringInvList centroid){
        this.centroid = centroid;
    }

    public ClusteringInvList centroid(){
        return this.centroid;
    }

    public void clearVec(){
        this.vectors.clear();
    }

    public void addVec(ClusteringInvList vec){
        vectors.add(vec);
    }

    public String toString(){
        return new String(centroid.toString() + "\t" + vectors.size());
    }

    public ClusteringInvList getVec(int i){
        return vectors.get(i);
    }

    public int clusterSize(){
        return vectors.size();
    }

}
