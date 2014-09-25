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
    private int tf;
    private int df;
    private int docLen;
    private String field;
    private String term;

    public ScoreListEntry(int docid, double score) {
        this.docid = docid;
        this.score = score;
    }

    public ScoreListEntry(int docid, double score, int tf, int df, int docLen, String field, String term){
        this.docid = docid;
        this.score = score;
        this.df = df;
        this.tf = tf;
        this.docLen = docLen;
        this.field = field;
        this.term = term;
    }

    public void addDocumentFrequency(int df) {this.df = df;}
    public void addTermFrequency(int tf) {this.tf = tf;}
    public void addDocumentLen(int docLen) {this.docLen = docLen;}
    public void addTermAndField(String term, String field) {this.field = field; this.term = term;}

    public double getScore(){
        return this.score;
    }

    public int getDocid(){return docid;}

    public int getDf()  {return this.df;}
    public int getDocLen() { return this.docLen;}
    public String getField() {return this.field;}
    public String getTerm() {return this.term;}
    public int getTf() {return this.tf;}


    public int compareTo(ScoreListEntry b) {
        return 0;
//        if(this.getScore() == b.getScore()){
//            String externalIdA = null;
//            String externalIdB = null;
//            try{
//                externalIdA = getExternalDocid(this.getDocid());
//                externalIdB = getExternalDocid(b.getDocid());
//            }catch (Exception e){
//                System.out.println("Failed to get extern id");
//            }
//            return externalIdA.compareTo(externalIdB);
//        }
//        else if(this.getScore() < b.getScore())   return 1;
//        else return -1;
    }
    public String getExternalDocid (int iid) throws IOException {
        Document d = QryEval.READER.document (iid);
        String eid = d.get ("externalId");
        return eid;
    }
}
