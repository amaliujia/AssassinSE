package TextMiningEngine.Witch.LinearAlgebra.Matrix;

import com.sun.tools.javac.util.Pair;

import java.util.HashMap;

/**
 * Created by amaliujia on 15-2-22.
 */
public class SparseMatrix implements Matrix {
    public HashMap<Pair<Integer, Integer>, Double> matrix;
    public int rows;
    public int columns;

    public SparseMatrix(){
        matrix = new HashMap<Pair<Integer, Integer>, Double>();
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
}



