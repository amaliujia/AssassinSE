package TextMingingEngine.Witch.Index;

import java.util.Vector;

/**
 * Created by amaliujia on 15-1-23.
 */
public class ClusteringInvList {
    private Vector<ClusteringPosting> postings;
    public int nextPos = 0;

    public ClusteringInvList(){
        postings = new Vector<ClusteringPosting>();
    }

    public void addPosting(int id, int tf){
            postings.add(new ClusteringPosting(id, tf));
    }

    public int getPostingSize(){
        return postings.size();
    }

    public int currentWord(){
        return postings.get(nextPos).getId();
    }

    public double currentTf(){
        return postings.get(nextPos).getWeight();
    }

    public double vectorNorm(){
        double re = 0;
        for(int i = 0; i < postings.size(); i++){
            re += Math.pow(postings.get(i).getWeight(), 2);
        }
        re = Math.sqrt(re);
        return re;
    }
}
