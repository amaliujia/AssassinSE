package SearchEngine.Assassin;

/**
 * Created by amaliujia on 14-9-21.
 */
public class RetrievalModelBM25 extends RetrievalModel{
    @Override
    public boolean setParameter(String parameterName, double value) {
        return false;
    }

    @Override
    public boolean setParameter(String parameterName, String value) {
        return false;
    }
}
