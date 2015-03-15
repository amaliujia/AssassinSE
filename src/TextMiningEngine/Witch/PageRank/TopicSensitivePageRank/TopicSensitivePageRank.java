package TextMiningEngine.Witch.PageRank.TopicSensitivePageRank;

import TextMiningEngine.Witch.LinearAlgebra.Matrix.PageRank.SparseEntry;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.PageRank.SparseMatrix;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.PageRank.SparseVector;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Vector;
import TextMiningEngine.Witch.PageRank.PageRank.LinkBase;
import TextMiningEngine.Witch.PageRank.PageRank.LinkParentBase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by amaliujia on 15-3-11.
 */
public class TopicSensitivePageRank extends LinkParentBase implements LinkBase {

    private double gama;

    private long N;

    public final static int iteration = 20;

    public HashMap<Integer, Integer> docToTopic;

    public HashMap<Integer, ArrayList<Integer>> docToTopicList;

    public int curTop;


    public TopicSensitivePageRank(int row, int col){
        sparseMatrix = new SparseMatrix(row, col);
        N = Math.max(sparseMatrix.rows, sparseMatrix.columns);
        docToTopic = new HashMap<Integer, Integer>();
        docToTopicList = new HashMap<Integer, ArrayList<Integer>>();
    }

    public void setArguments(double alpha, double beta, double gama){
        super.setArguments(alpha, beta);
        this.gama = gama;
    }

    @Override
    public void run() {
        SparseVector r = new SparseVector();
        for(int i = 0; i < N; i++){
            r.addEntry(i + 1, 1.0 / (N * 1.0));
        }
        for(int i = 0; i < iteration; i++){
            r = oneIteration(sparseMatrix, r);
        }
    }

    /**
     *
     * @param matrix
     * @param r
     * @return
     */
    private SparseVector oneIteration(SparseMatrix matrix, SparseVector r){
        SparseVector returnVector = new SparseVector();

        for(int i = 0; i < N; i++){
            double temp = 0;
            Vector c = matrix.getRowVector(i);
            for (int j = 0; j < c.getNumElement(); j++) {
                SparseEntry e = (SparseEntry) c.getEntry(j);
                //TODO: change to topic selective teleporation matrix with gama proportion.
                temp += e.value * ((SparseEntry) r.getEntry(e.id - 1)).value *alpha;
                temp += ((beta) * 1.0) / (N * 1.0);
            }
            returnVector.addEntry(i, temp);
        }
        //renormalize
        double s = 0;
        for(int i = 0; i < returnVector.getNumElement(); i++){
            s += returnVector.getEntry(i).getValue();
        }
        for(int i = 0; i < returnVector.getNumElement(); i++) {
            returnVector.getEntry(i).setValue(returnVector.getEntry(i).getValue() + ((1.0 - s) / (N * 1.0)));
        }

        return returnVector;
    }
}
