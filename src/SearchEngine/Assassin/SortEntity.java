package SearchEngine.Assassin;

import sun.misc.Sort;

/**
 * Created by amaliujia on 14-9-23.
 */
public class SortEntity implements Comparable<SortEntity>{
    private int internalDocID;
    private String externDocID;
    private double scores;

    public SortEntity(int a, String b, double c){
        internalDocID = a;
        externDocID = b;
        scores = c;
    }

    public SortEntity(String b, double c){
        externDocID = b;
        scores = c;
    }
//    public void getInternalDocID(){
//
//    }


    /**
     *
     * @return
     */
    public double getScore() {return this.scores;}

    /**
     *
     * @return
     */
    public String getExternalDocid()  {return this.externDocID;}

    /**
     *
     * @return
     */
    public int getInternalDocID() {return this.internalDocID;}

    @Override
    public int compareTo(SortEntity b) {
        if(this.getScore() == b.getScore()){
            String externalIdA = this.getExternalDocid();
            String externalIdB = b.getExternalDocid();
            return externalIdA.toString().compareTo(externalIdB.toString());
        }
        else if(this.getScore() < b.getScore())   return 1;
        else return -1;
    }
}
