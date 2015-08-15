/**
 *  The search engine must support multiple retrieval models.  Some
 *  retrieval models have parameters.  All of them influence the way a
 *  query operator behaves.  Passing around a retrieval model object
 *  during query evaluation allows this information to be shared with
 *  query operators (and nested query operators) conveniently.
 */

package SearchEngine.Assassin.RetrievalModel;

import java.io.Serializable;

public abstract class RetrievalModel implements Serializable{


    /**
    *  Set a retrieval model parameter.
    *  @param parameterName The name of the parameter to set.
    *  @param value The parameter's value.
    *  @return true if the parameter is set successfully, false otherwise.
    */
    public abstract boolean setParameter (String parameterName, double value);

    /**
     *  Set a retrieval model parameter.
     *  @param parameterName The name of the parameter to set.
     *  @param value The parameter's value.
     *  @return true if the parameter is set successfully, false otherwise.
     */
    public abstract boolean setParameter (String parameterName, String value);

    /**
     *  Set a retrieval model parameter.
     *  @param parameterName The name of the parameter to set.
     *  @param value The parameter's value.
     *  @return true if the parameter is set successfully, false otherwise.
     */
    public abstract boolean setParameter(String parameterName, Object value);


    public abstract boolean hasParameter(String parameterName);
}
