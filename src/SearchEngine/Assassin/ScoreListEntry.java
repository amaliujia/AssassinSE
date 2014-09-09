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

    public int getDocid(){return docid;}


    public int compareTo(ScoreListEntry b) {
        if(this.getScore() == b.getScore()){
            String externalIdA = null;
            String externalIdB = null;
            try{
                externalIdA = getExternalDocid(this.getDocid());
                externalIdB = getExternalDocid(b.getDocid());
            }catch (Exception e){
                System.out.println("Failed to get extern id");
            }
            return externalIdA.compareTo(externalIdB);
        }
        else if(this.getScore() < b.getScore())   return 1;
        else return -1;
    }
    public String getExternalDocid (int iid) throws IOException {
        Document d = QryEval.READER.document (iid);
        String eid = d.get ("externalId");
        return eid;
    }
}
