package TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface;

/**
 * Created by amaliujia on 15-3-9.
 */
public interface Entry {
    public double getValue();

    public void setValue(double v);

    public int getId();

    public int setId(int id);

    public double norm();

    public Entry copy();

}
