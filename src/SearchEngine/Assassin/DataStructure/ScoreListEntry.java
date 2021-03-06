package SearchEngine.Assassin.DataStructure;

import java.io.Serializable;

/**
 * Created by amaliujia on 14-9-7.
 */
public class ScoreListEntry implements Comparable<ScoreListEntry>, Serializable {

    private int docid;
    private double score;

    public ScoreListEntry(int docid, double score) {
        this.docid = docid;
        this.score = score;
    }

    public double getScore() {
        return this.score;
    }

    public int getDocid() {
        return docid;
    }

    public void setDocid(int n){
        docid = n;
    }

    public int compareTo(ScoreListEntry b) {
        if(score < b.getScore()){
            return -1;
        }else if(score > b.getScore()){
            return 1;
        }

        if(docid < b.getDocid()){
            return -1;
        }else if(docid > b.getDocid()) {
            return 1;
        }else{
            return 0;
        }
    }
}
