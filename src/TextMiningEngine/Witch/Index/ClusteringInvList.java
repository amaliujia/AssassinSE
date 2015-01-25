package TextMiningEngine.Witch.Index;

import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

/**
 * Created by amaliujia on 15-1-23.
 */
public class ClusteringInvList {
    private Vector<ClusteringPosting> postings;
    public int nextPos = 0;
    private ClusteringVectorType type = ClusteringVectorType.NOTDEFINED;
    private int invlistID;

    public ClusteringInvList(){
        postings = new Vector<ClusteringPosting>();
    }

    public ClusteringInvList(ClusteringVectorType type){
        postings = new Vector<ClusteringPosting>();
        this.type = type;
    }

    public ClusteringInvList(ClusteringVectorType type, int invlistID){
        postings = new Vector<ClusteringPosting>();
        this.type = type;
        this.invlistID = invlistID;
    }

    public void addPosting(int id, double tf){
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

    public int getID(int i){
        return postings.get(i).getId();
    }

    public double getWeight(int i){
        return postings.get(i).getWeight();
    }

    public double exist(int id){
        for(int i = 0; i < postings.size(); i++){
            if(postings.get(i).getId() == id){
                return  postings.get(i).getWeight();
            }
        }
        return -1.0;
    }

    public double vectorNorm(){
        double re = 0;
        for(int i = 0; i < postings.size(); i++){
            re += Math.pow(postings.get(i).getWeight(), 2);
        }
        re = Math.sqrt(re);
        return re;
    }

    public void setType(ClusteringVectorType type){
        this.type = type;
    }

    public ClusteringVectorType type(){
        return this.type;
    }

    public String toString(){
        String re = "";
        for(int i = 0; i < postings.size(); i++){
            re += this.getID(i) + " ";
        }
        return re;
    }

    public void resetPoint(){
        this.nextPos = 0;
    }

    public void sortVec(){
        Collections.sort(postings);
    }
}
