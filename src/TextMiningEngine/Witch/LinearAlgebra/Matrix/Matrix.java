package TextMiningEngine.Witch.LinearAlgebra.Matrix;

/**
 * Created by amaliujia on 15-2-22.
 */
public interface Matrix {
    public  Matrix mulMatrix(Matrix matrix);

    public  Vector mulVector(Vector vector);

    public  Matrix Vecotrmul(Vector vector);

    public long getRowDimension();

    public long getColDimension();

    public Vector getRowVector(int i);
}
