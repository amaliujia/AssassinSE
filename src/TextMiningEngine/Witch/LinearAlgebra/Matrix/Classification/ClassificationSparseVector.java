package TextMiningEngine.Witch.LinearAlgebra.Matrix.Classification;

import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Entry;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amaliujia on 15-3-23.
 */
public class ClassificationSparseVector implements Vector {

    public List<Entry> v;

    public int label;


    public ClassificationSparseVector(){
        label = -1;
        v = new ArrayList<Entry>();
    }

    @Override
    public Entry getEntry(int i) {
        return null;
    }

    @Override
    public int getNumElement() {
        return 0;
    }

    @Override
    public void clear() {
      v.clear();
    }

    @Override
    public int size() {
        return v.size();
    }

    @Override
    public void addEntry(int col, double value) {
        v.add(new ClassificationEntry(col, value));
    }

}
