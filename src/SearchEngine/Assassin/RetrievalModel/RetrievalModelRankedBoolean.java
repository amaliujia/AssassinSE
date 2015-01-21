package SearchEngine.Assassin.RetrievalModel;

/**
 * Created by amaliujia on 14-9-8.
 */
public class RetrievalModelRankedBoolean extends RetrievalModel {
    /**
     * Set a retrieval model parameter.
     * @param parameterName
     * @param value The parameter's value.
     * @return Always false because this retrieval model has no parameters.
     */
    public boolean setParameter (String parameterName, double value) {
        System.err.println ("Error: Unknown parameter name for retrieval model " +
                            "RankedBoolean: " +
                            parameterName);
        return false;
    }

    /**
     * Set a retrieval model parameter.
     * @param parameterName
     * @param value The parameter's value.
     * @return Always false because this retrieval model has no parameters.
     */
    public boolean setParameter (String parameterName, String value) {
        System.err.println ("Error: Unknown parameter name for retrieval model " +
                            "RankedBoolean: " +
                            parameterName);
        return false;
    }
}
