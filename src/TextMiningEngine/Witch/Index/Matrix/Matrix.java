package TextMiningEngine.Witch.Index.Matrix;

/**
 * Created by amaliujia on 15-2-22.
 */
public interface Matrix {
    public  Matrix mulMatrix(Matrix matrix);

    public  Vector mulVector(Vector vector);

    public  Matrix Vecotrmul(Vector vector);
}
