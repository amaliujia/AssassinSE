package SearchEngine.Assassin.RetrievalModel;

import java.util.Set;

/**
 * Created by amaliujia on 14-9-25.
 */
public class RetrievalModelIndri extends RetrievalModel {
    public double mu;
    public double lambda;
    public String smoothing;

    //docs
    public Set<Integer> docs;


    public RetrievalModelIndri(){
        super();
        docs = null;
    }

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

    @Override
    public boolean setParameter(String parameterName, Object value) {
        if(parameterName.equals("docs")){
            docs = (Set<Integer>) value;
            return true;
        }
        return false;
    }

    @Override
    public boolean hasParameter(String parameterName) {
        if(parameterName == "docs" && docs != null){
            return true;
        }
        // TODO: add other getter
        return false;
    }
}
