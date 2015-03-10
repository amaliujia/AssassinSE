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
        rows = row + 1;
        columns = col + 1;
        rowVectors = new ArrayList<SparseVector>();
        for(int i = 0; i < rows; i++){
            rowVectors.add(new SparseVector());
        }
    }

    public void addEntry(int row, int col, double value){
        rowVectors.get(row).addEntry(col, value);
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



