package SearchEngine.Assassin;

/**
 * Created by amaliujia on 14-9-25.
 */
public class RetrievalModelIndri extends RetrievalModel {
    /**
     *
     * @param parameterName The name of the parameter to set.
     * @param value
     * @return
     */
    @Override
    public boolean setParameter(String parameterName, double value) {
        return false;
    }

    /**
     *
     * @param parameterName The name of the parameter to set.
     * @param value
     * @return
     */
    @Override
    public boolean setParameter(String parameterName, String value) {
        return false;
    }
}
