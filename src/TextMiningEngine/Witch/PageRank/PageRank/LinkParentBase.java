package TextMiningEngine.Witch.PageRank.PageRank;

import TextMiningEngine.Witch.LinearAlgebra.Matrix.SparseMatrix;

import java.util.HashMap;

/**
 * Created by amaliujia on 15-3-11.
 */
public class LinkParentBase {
    protected SparseMatrix sparseMatrix;

    protected double alpha;

    protected double beta;

    public HashMap<Integer, Integer> outlinks;

    public void setArguments(double alpha, double beta){
        this.alpha = alpha;
        this.beta = beta;
    }

    public void setEntry(int row, int col, double value){
        sparseMatrix.addEntry(row, col, value);
    }

}
