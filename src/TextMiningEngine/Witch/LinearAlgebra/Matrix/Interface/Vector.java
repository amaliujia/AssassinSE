package TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface;

/**
 * Created by amaliujia on 15-2-22.
 */
public interface Vector {
    public Entry getEntry(int i);

    public int getNumElement();

    public void clear();

    public int size();

    public void addEntry(int col, double value);

    public double dotproduct(Vector v);

    public double norm();

    public Vector copy();

    public void addEntry(Entry e);
}
