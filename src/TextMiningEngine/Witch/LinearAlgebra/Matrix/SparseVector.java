package TextMiningEngine.Witch.LinearAlgebra.Matrix;

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
}
