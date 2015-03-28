package TextMiningEngine.Witch.LinearAlgebra.Matrix.PageRank;

import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Entry;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amaliujia on 15-3-9.
 */
public class SparseVector implements Vector {
    public List<Entry> v;

    public SparseVector(){
        v = new ArrayList<Entry>();
    }

    public void addEntry(int col, double value){
        v.add(new SparseEntry(col, value));
    }

    @Override
    public double dotproduct(Vector v) {
        return 0;
    }

    @Override
    public Entry getEntry(int i) {
        return v.get(i);
    }

    @Override
    public int getNumElement() {
        return v.size();
    }

    @Override
    public void clear() {
        v.clear();
    }

    @Override
    public int size() {
        return v.size();
    }
}
