package SearchEngine.Assassin.RetrievalModel;
/**
 * Created by amaliujia on 14-9-25.
 */
public class RetrievalModelIndri extends RetrievalModel {
    public double mu;
    public double lambda;
    public String smoothing;
    /**
     *
     * @param parameterName The name of the parameter to set.
     * @param value
     * @return
     */
    @Override
    public boolean setParameter(String parameterName, double value) {
        if(parameterName.equals("mu")) {
            mu = value;
        } else if(parameterName.equals("lambda")) {
            lambda = value;
        } else {
            return false;
        }
        return true;
    }

    /**
     *
     * @param parameterName The name of the parameter to set.
     * @param value
     * @return
     */
    @Override
    public boolean setParameter(String parameterName, String value) {
        if(parameterName.equals("smoothing")) {
            smoothing = value;
        } else {
            return false;
        }
        return true;
    }
}
