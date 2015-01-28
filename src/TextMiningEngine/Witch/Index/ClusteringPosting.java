package TextMiningEngine.Witch.Index;

import java.util.Comparator;

/**
 * Created by amaliujia on 15-1-23.
 */
public class ClusteringPosting implements Comparable<ClusteringPosting> {
    private int id;
    private double weight;

    public ClusteringPosting(int id, double tf){
        this.id = id;
        this.weight = tf;
    }

    public int getId(){
        return id;
    }

    public double getWeight(){
        return weight;
    }

    public void updateWeight(double w) {
        weight = w;
    }

    @Override
    public int compareTo(ClusteringPosting o) {
        if(this.id < o.id){
            return -1;
        }else if(this.id > o.id){
            return 1;
        }else{
            return 0;
        }
    }
}
