package SearchEngine.Assassin.RetrievalModel;

/**
 * Created by amaliujia on 15-4-3.
 */
public class ClassifierModel extends RetrievalModel {

    public String algorithm;

    @Override
    public boolean setParameter(String parameterName, double value) {
        return false;
    }

    @Override
    public boolean setParameter(String parameterName, String value) {
        if(parameterName .equals("type")){
            algorithm = value;
        }else {
            return false;
        }
        return true;
    }
}
