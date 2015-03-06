package TextMiningEngine.Witch.LinearAlgebra.Matrix;

import com.sun.tools.javac.util.Pair;
import org.apache.commons.math3.exception.*;
import org.apache.commons.math3.linear.*;

import java.util.HashMap;

/**
 * Created by amaliujia on 15-2-22.
 */
public class SparseMatrix implements SparseRealMatrix {


    @Override
    public RealMatrix createMatrix(int i, int i1) throws NotStrictlyPositiveException {
        return null;
    }

    @Override
    public RealMatrix copy() {
        return null;
    }

    @Override
    public RealMatrix add(RealMatrix realMatrix) throws MatrixDimensionMismatchException {
        return null;
    }

    @Override
    public RealMatrix subtract(RealMatrix realMatrix) throws MatrixDimensionMismatchException {
        return null;
    }

    @Override
    public RealMatrix scalarAdd(double v) {
        return null;
    }

    @Override
    public RealMatrix scalarMultiply(double v) {
        return null;
    }

    @Override
    public RealMatrix multiply(RealMatrix realMatrix) throws DimensionMismatchException {
        return null;
    }

    @Override
    public RealMatrix preMultiply(RealMatrix realMatrix) throws DimensionMismatchException {
        return null;
    }

    @Override
    public RealMatrix power(int i) throws NotPositiveException, NonSquareMatrixException {
        return null;
    }

    @Override
    public double[][] getData() {
        return new double[0][];
    }

    @Override
    public double getNorm() {
        return 0;
    }

    @Override
    public double getFrobeniusNorm() {
        return 0;
    }

    @Override
    public RealMatrix getSubMatrix(int i, int i1, int i2, int i3) throws OutOfRangeException, NumberIsTooSmallException {
        return null;
    }

    @Override
    public RealMatrix getSubMatrix(int[] ints, int[] ints1) throws NullArgumentException, NoDataException, OutOfRangeException {
        return null;
    }

    @Override
    public void copySubMatrix(int i, int i1, int i2, int i3, double[][] doubles) throws OutOfRangeException, NumberIsTooSmallException, MatrixDimensionMismatchException {

    }

    @Override
    public void copySubMatrix(int[] ints, int[] ints1, double[][] doubles) throws OutOfRangeException, NullArgumentException, NoDataException, MatrixDimensionMismatchException {

    }

    @Override
    public void setSubMatrix(double[][] doubles, int i, int i1) throws NoDataException, OutOfRangeException, DimensionMismatchException, NullArgumentException {

    }

    @Override
    public RealMatrix getRowMatrix(int i) throws OutOfRangeException {
        return null;
    }

    @Override
    public void setRowMatrix(int i, RealMatrix realMatrix) throws OutOfRangeException, MatrixDimensionMismatchException {

    }

    @Override
    public RealMatrix getColumnMatrix(int i) throws OutOfRangeException {
        return null;
    }

    @Override
    public void setColumnMatrix(int i, RealMatrix realMatrix) throws OutOfRangeException, MatrixDimensionMismatchException {

    }

    @Override
    public RealVector getRowVector(int i) throws OutOfRangeException {
        return null;
    }

    @Override
    public void setRowVector(int i, RealVector realVector) throws OutOfRangeException, MatrixDimensionMismatchException {

    }

    @Override
    public RealVector getColumnVector(int i) throws OutOfRangeException {
        return null;
    }

    @Override
    public void setColumnVector(int i, RealVector realVector) throws OutOfRangeException, MatrixDimensionMismatchException {

    }

    @Override
    public double[] getRow(int i) throws OutOfRangeException {
        return new double[0];
    }

    @Override
    public void setRow(int i, double[] doubles) throws OutOfRangeException, MatrixDimensionMismatchException {

    }

    @Override
    public double[] getColumn(int i) throws OutOfRangeException {
        return new double[0];
    }

    @Override
    public void setColumn(int i, double[] doubles) throws OutOfRangeException, MatrixDimensionMismatchException {

    }

    @Override
    public double getEntry(int i, int i1) throws OutOfRangeException {
        return 0;
    }

    @Override
    public void setEntry(int i, int i1, double v) throws OutOfRangeException {

    }

    @Override
    public void addToEntry(int i, int i1, double v) throws OutOfRangeException {

    }

    @Override
    public void multiplyEntry(int i, int i1, double v) throws OutOfRangeException {

    }

    @Override
    public RealMatrix transpose() {
        return null;
    }

    @Override
    public double getTrace() throws NonSquareMatrixException {
        return 0;
    }

    @Override
    public double[] operate(double[] doubles) throws DimensionMismatchException {
        return new double[0];
    }

    @Override
    public RealVector operate(RealVector realVector) throws DimensionMismatchException {
        return null;
    }

    @Override
    public double[] preMultiply(double[] doubles) throws DimensionMismatchException {
        return new double[0];
    }

    @Override
    public RealVector preMultiply(RealVector realVector) throws DimensionMismatchException {
        return null;
    }

    @Override
    public double walkInRowOrder(RealMatrixChangingVisitor realMatrixChangingVisitor) {
        return 0;
    }

    @Override
    public double walkInRowOrder(RealMatrixPreservingVisitor realMatrixPreservingVisitor) {
        return 0;
    }

    @Override
    public double walkInRowOrder(RealMatrixChangingVisitor realMatrixChangingVisitor, int i, int i1, int i2, int i3) throws OutOfRangeException, NumberIsTooSmallException {
        return 0;
    }

    @Override
    public double walkInRowOrder(RealMatrixPreservingVisitor realMatrixPreservingVisitor, int i, int i1, int i2, int i3) throws OutOfRangeException, NumberIsTooSmallException {
        return 0;
    }

    @Override
    public double walkInColumnOrder(RealMatrixChangingVisitor realMatrixChangingVisitor) {
        return 0;
    }

    @Override
    public double walkInColumnOrder(RealMatrixPreservingVisitor realMatrixPreservingVisitor) {
        return 0;
    }

    @Override
    public double walkInColumnOrder(RealMatrixChangingVisitor realMatrixChangingVisitor, int i, int i1, int i2, int i3) throws OutOfRangeException, NumberIsTooSmallException {
        return 0;
    }

    @Override
    public double walkInColumnOrder(RealMatrixPreservingVisitor realMatrixPreservingVisitor, int i, int i1, int i2, int i3) throws OutOfRangeException, NumberIsTooSmallException {
        return 0;
    }

    @Override
    public double walkInOptimizedOrder(RealMatrixChangingVisitor realMatrixChangingVisitor) {
        return 0;
    }

    @Override
    public double walkInOptimizedOrder(RealMatrixPreservingVisitor realMatrixPreservingVisitor) {
        return 0;
    }

    @Override
    public double walkInOptimizedOrder(RealMatrixChangingVisitor realMatrixChangingVisitor, int i, int i1, int i2, int i3) throws OutOfRangeException, NumberIsTooSmallException {
        return 0;
    }

    @Override
    public double walkInOptimizedOrder(RealMatrixPreservingVisitor realMatrixPreservingVisitor, int i, int i1, int i2, int i3) throws OutOfRangeException, NumberIsTooSmallException {
        return 0;
    }

    @Override
    public boolean isSquare() {
        return false;
    }

    @Override
    public int getRowDimension() {
        return 0;
    }

    @Override
    public int getColumnDimension() {
        return 0;
    }
//    public HashMap<Pair<Integer, Integer>, Double> matrix;
//    public int rows;
//    public int columns;
//
//    public SparseMatrix(){
//        matrix = new HashMap<Pair<Integer, Integer>, Double>();
//        rows = 0;
//        columns = 0;
//    }
//
//    @Override
//    public Matrix mulMatrix(Matrix matrix) {
//        return null;
//    }
//
//    @Override
//    public Vector mulVector(Vector vector) {
//        return null;
//    }
//
//    @Override
//    public Matrix Vecotrmul(Vector vector) {
//        return null;
//    }
}



