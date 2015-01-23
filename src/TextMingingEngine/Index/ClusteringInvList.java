package TextMingingEngine.Index;

import java.util.Vector;

/**
 * Created by amaliujia on 15-1-23.
 */
public class ClusteringInvList {
    private Vector<ClusteringPosting> postings;

    public ClusteringInvList(){
        postings = new Vector<ClusteringPosting>();
    }

    public void addPosting(int id, int tf){
            postings.add(new ClusteringPosting(id, tf));
    }
}
