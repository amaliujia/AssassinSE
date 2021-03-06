package TextMiningEngine.Witch.LinearAlgebra.Matrix.Classification;

import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Matrix;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amaliujia on 15-3-23.
 */
public class ClassificationSparseMatrix implements Matrix {

    public List<ClassificationSparseVector> rowVectors;

    public int rows;

    public int columns;

    private int N;

    public ClassificationSparseMatrix(){
        rowVectors = new ArrayList<ClassificationSparseVector>();
        rows = 0;
        columns = 0;
    }

    @Override
    public Matrix mulMatrix(Matrix matrix) {
        return null;
    }

    @Override
    public Vector mulVector(Vector vector) {
        return null;
    }

    @Override
    public Matrix Vecotrmul(Vector vector) {
        return null;
    }

    @Override
    public long getRowDimension() {
        return rowVectors.size();
    }

    @Override
    public long getColDimension() {
        return columns;
    }

    @Override
    public Vector getRowVector(int i) {
        return rowVectors.get(i);
    }

    public void addRowVector(ClassificationSparseVector v){
        rowVectors.add(v);
        rows++;
    }
}
