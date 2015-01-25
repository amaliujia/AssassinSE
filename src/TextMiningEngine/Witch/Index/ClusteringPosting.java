package TextMiningEngine.Witch.Index;

/**
 * Created by amaliujia on 15-1-23.
 */
public class ClusteringPosting {
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
}
