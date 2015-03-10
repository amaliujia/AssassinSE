package TextMiningEngine.Witch.LinearAlgebra.Matrix;

/**
 * Created by amaliujia on 15-2-22.
 */
public interface Vector {
    public Entry getEntry(int i);

    public int getNumElement();

    public void clear();
}
