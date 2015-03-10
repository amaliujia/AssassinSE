package TextMiningEngine.Witch.PageRank;

import TextMiningEngine.Witch.LinearAlgebra.Matrix.SparseMatrix;


public class PageRank implements LinkBase{

    private SparseMatrix sparseMatrix;

    private double alpha;

    private double beta;

    public static int iteration = 20;

    public PageRank(int row, int col){
        sparseMatrix = new SparseMatrix(row, col);
    }

    public void setArguments(double alpha, double beta){
        this.alpha = alpha;
        this.beta = beta;
    }

    public void setEntry(int row, int col, double value){
        sparseMatrix.addEntry(row, col, value);
    }

    @Override
    public void run() {

    }
}
