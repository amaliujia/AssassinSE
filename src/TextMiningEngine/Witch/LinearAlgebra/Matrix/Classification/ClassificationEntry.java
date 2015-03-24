package TextMiningEngine.Witch.LinearAlgebra.Matrix.Classification;

import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Entry;

/**
 * Created by amaliujia on 15-3-23.
 */
public class ClassificationEntry implements Entry {

    int id;

    double value;

    public ClassificationEntry(int id, double value){
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
