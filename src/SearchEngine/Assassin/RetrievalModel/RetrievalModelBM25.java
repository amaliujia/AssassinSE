package SearchEngine.Assassin.RetrievalModel;

import SearchEngine.Assassin.Lucene.DocLengthStore;

/**
 * Created by amaliujia on 14-9-21.
 */
public class RetrievalModelBM25 extends RetrievalModel {

    // BM25
    public DocLengthStore docLengthStore;
    public int numDocs;
    public int avgLenDoc;
    public double k1;
    public double b;
    public double k3;

    @Override
    public boolean setParameter(String parameterName, double value) {
        if(parameterName.equals("k1")) {
            k1 = value;
        } else if(parameterName.equals("b")) {
            b = value;
        } else if(parameterName.equals("k3")) {
            k3 = value;
        } else if(parameterName.equals("avgLenDoc")) {
            avgLenDoc = (int)value;
        } else if(parameterName.equals("numDocs")) {
            numDocs = (int)value;
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean setParameter(String parameterName, String value) {
        return false;
    }
}