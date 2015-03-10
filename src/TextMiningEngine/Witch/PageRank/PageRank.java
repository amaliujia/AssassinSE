package TextMiningEngine.Witch.PageRank;

import TextMiningEngine.Witch.LinearAlgebra.Matrix.*;

import java.util.HashMap;


public class PageRank implements LinkBase{

    private SparseMatrix sparseMatrix;

    public HashMap<Integer, Integer> outlinks;

    private double alpha;

    private double beta;

    private long N;

    public final static int iteration = 20;

    public PageRank(int row, int col){
        sparseMatrix = new SparseMatrix(row, col);
        N = Math.max(sparseMatrix.rows, sparseMatrix.columns);

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
        SparseVector r = new SparseVector();
        //TODO: how big should this N be?
        for(int i = 0; i < N; i++){
            r.addEntry(i + 1, 1.0 / N);
        }
        //TODO: not normalization of values in vector r
        for(int i = 0; i < iteration; i++){
            r = oneIteration(sparseMatrix, r);
        }
    }

    private SparseVector oneIteration(SparseMatrix matrix, SparseVector r){
        SparseVector returnVector = new SparseVector();

            for(int i = 0; i < N; i++){
                double temp = 0;
                if(i < matrix.columns) {
                    Vector c = matrix.getRowVector(i);
                    for (int j = 0; j < c.getNumElement(); j++) {
                        SparseEntry e = (SparseEntry) c.getEntry(j);
                        temp += e.value * ((SparseEntry) r.getEntry(e.id - 1)).value * beta;
                    }
                }
                temp += ((1 - beta) * 1.0) / (N * 1.0);
                returnVector.addEntry(i, temp);
            }

        return returnVector;
    }
}
