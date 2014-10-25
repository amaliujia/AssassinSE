package SearchEngine.Assassin;

import java.util.ArrayList;

/**
 * Created by amaliujia on 14-10-24.
 */
public class SDPesudoFeedBackEngine {
    private int docNum;
    private int termNum;
    private ArrayList<SortEntity> folder;
    private String queryID;

    public SDPesudoFeedBackEngine(int docNum, int termNum, ArrayList<SortEntity> result, String queryID){
        this.docNum = docNum;
        this.termNum = termNum;
        folder = result;
        this.queryID = queryID;
    }

    public void SDFeedback(){

    }


}
