package TextMiningEngine.Witch.PageRank;

import TextMiningEngine.Witch.LinearAlgebra.Matrix.SparseMatrix;
import org.apache.commons.math3.linear.OpenMapRealMatrix;

import java.security.PublicKey;

/**
 * Created by amaliujia on 15-2-22.
 */
public class PageRank implements LinkBase{

    private OpenMapRealMatrix sparseMatrix;

    public PageRank(int row, int col){
        sparseMatrix = new OpenMapRealMatrix(81433, 81260);
    }

    public void setEntry(int row, int col, double value){
        sparseMatrix.addToEntry(row, col, value);
    }

    @Override
    public void run() {

    }
}
