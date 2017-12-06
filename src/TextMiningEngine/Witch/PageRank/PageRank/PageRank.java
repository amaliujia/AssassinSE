package TextMiningEngine.Witch.PageRank.PageRank;

import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Vector;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.PageRank.SparseEntry;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.PageRank.SparseMatrix;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.PageRank.SparseVector;


public class PageRank extends LinkParentBase implements LinkBase{

    private long N;

    public final static int iteration = 20;

    public PageRank(int row, int col){
        sparseMatrix = new SparseMatrix(row, col);
        N = Math.max(sparseMatrix.rows, sparseMatrix.columns);

    }

    public void run() {
        SparseVector r = new SparseVector();
        //TODO: how big should this N be? Should it form a square matrix?
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
                        temp += e.value * ((SparseEntry) r.getEntry(e.id - 1)).value * beta;
                        temp += ((1 - beta) * 1.0) / (N * 1.0);
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
