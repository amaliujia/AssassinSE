package TextMiningEngine.Witch.LinearAlgebra.Matrix;

/**
 * Created by amaliujia on 15-3-9.
 */
public class SparseEntry implements Entry {
    public int id;

    public double value;


    public SparseEntry(int id, double value){
        this.id = id;
        this.value = value;
    }
}
