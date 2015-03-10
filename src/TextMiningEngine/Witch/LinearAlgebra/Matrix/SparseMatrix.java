package TextMiningEngine.Witch.LinearAlgebra.Matrix;

import com.sun.tools.javac.util.Pair;
import org.apache.commons.math3.exception.*;
import org.apache.commons.math3.linear.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by amaliujia on 15-2-22.
 */
public class SparseMatrix implements Matrix {

    public List<SparseVector> rowVectors;

    public int rows;

    public int columns;

    public SparseMatrix(){
        rowVectors = new ArrayList<SparseVector>();
        rows = 0;
        columns = 0;
    }

    public SparseMatrix(int row, int col){
        rows = row;
        columns = col;
        rowVectors = new ArrayList<SparseVector>();
        for(int i = 0; i < columns; i++){
            rowVectors.add(new SparseVector());
        }
    }

    public void addEntry(int row, int col, double value){
        rowVectors.get(row - 1).addEntry(col, value);
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
        return 0;
    }

    @Override
    public Vector getRowVector(int i) {
        return rowVectors.get(i);
    }
}



