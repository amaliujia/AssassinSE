package TextMingingEngine.Index;

/**
 * Created by amaliujia on 15-1-23.
 */
public class ClusteringPosting {
    int wordID;
    int tf;

    public ClusteringPosting(int id, int tf){
        this.wordID = id;
        this.tf = tf;
    }
}
