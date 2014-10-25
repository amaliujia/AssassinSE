package SearchEngine.Assassin;

import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.Comparator;

/**
 * Created by amaliujia on 14-9-7.
 */
public class ScoreListEntry implements Comparable<ScoreListEntry>{

    private int docid;
    private double score;

    public ScoreListEntry(int docid, double score) {
        this.docid = docid;
        this.score = score;
    }



    public double getScore(){
        return this.score;
    }

    public int getDocid(){ return docid; }


    public int compareTo(ScoreListEntry b) {
        return 0;
    }
}
