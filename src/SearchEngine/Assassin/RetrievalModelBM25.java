package SearchEngine.Assassin;

/**
 * Created by amaliujia on 14-9-21.
 */
public class RetrievalModelBM25 extends RetrievalModel{

    // BM25
    public DocLengthStore docLengthStore;
    public int numDocs;
    public int avgLenDoc;
    public double k1;
    public double b;
    public double k3;

    @Override
    public boolean setParameter(String parameterName, double value) {
        return false;
    }

    @Override
    public boolean setParameter(String parameterName, String value) {
        return false;
    }
}
