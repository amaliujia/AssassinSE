package SearchEngine.Assassin;

/**
 * Created by amaliujia on 14-11-11.
 */
public class RetrievalModelLearningToRank extends RetrievalModel {

    // Indri parameters
    public double mu;
    public double lambda;
    public String smoothing;

    // BM25
    public DocLengthStore docLengthStore;
    public int numDocs;
    public int avgLenDoc;
    public static double k1;
    public static double b;
    public static double k3;

    @Override
    public boolean setParameter(String parameterName, double value) {
        return false;
    }

    @Override
    public boolean setParameter(String parameterName, String value) {
        return false;
    }
}
