package SearchEngine.Assassin;

/**
 * Created by amaliujia on 14-9-23.
 */
public class SortEntity implements Comparable<SortEntity>{
    private int internalDocID;
    private String outernalDocID;
    private double scores;

    public SortEntity(int a, String b, double c){
        internalDocID = a;
        outernalDocID = b;
        scores = c;
    }
    public double getScore() {return this.scores;}
    public String getExternalDocid()  {return this.outernalDocID;}

    @Override
    public int compareTo(SortEntity b) {
        if(this.getScore() == b.getScore()){
            String externalIdA = this.getExternalDocid();
            String externalIdB = b.getExternalDocid();
            return externalIdA.compareTo(externalIdB);
        }
        else if(this.getScore() < b.getScore())   return 1;
        else return -1;
    }
}
