package SearchEngine.Assassin.RetrievalModel;

import SearchEngine.Assassin.Lucene.DocLengthStore;

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
        if(parameterName.equals("mu")) {
            mu = value;
        } else if(parameterName.equals("lambda")) {
            lambda = value;
        } else if(parameterName.equals("k1")) {
            k1 = value;
        } else if(parameterName.equals("b")) {
            b = value;
        } else if(parameterName.equals("k3")) {
            k3 = value;
        } else if(parameterName.equals("avgLenDoc")) {
            avgLenDoc = (int)value;
        } else if(parameterName.equals("numDocs")) {
            numDocs = (int) value;
        } else {
            return false;
        }

        return true;
    }

    @Override
    public boolean setParameter(String parameterName, String value) {
        if(parameterName.equals("smoothing")) {
            smoothing = value;
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean setParamter(String parameterName, Object value) {
        return false;
    }
}
