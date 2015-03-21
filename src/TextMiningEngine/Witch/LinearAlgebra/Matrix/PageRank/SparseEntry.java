package TextMiningEngine.Witch.LinearAlgebra.Matrix.PageRank;

import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Entry;

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

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public void setValue(double v) {
        value = v;
    }

    @Override
    public int getId() {
        return id;
    }
}
