package SearchEngine.Assassin.RetrievalModel;

/**
 * Created by amaliujia on 15-3-4.
 */
public class LinkAnalysisModel extends RetrievalModel {

    public String exectuionName;

    public double beta;

    public String path;

    @Override
    public boolean setParameter(String parameterName, double value) {
        if(parameterName.equals("teleporation")){
            beta = value;
        } else{
           return false;
        }
        return true;
    }

    @Override
    public boolean setParameter(String parameterName, String value) {
        if(parameterName.equals("PageRank")) {
            exectuionName = value;
        }else if(parameterName.equals("smatrixPath")){
            path = value;
        }else{
            return false;
        }
            return true;
    }

    @Override
    public boolean setParameter(String parameterName, Object value) {
        return false;
    }
}
