package SearchEngine.Assassin.RetrievalModel;

/**
 * Created by amaliujia on 15-3-11.
 */
public class TopicSensitivePRModel extends RetrievalModel {

    public String matrixPath;

    public String docTopics;

    public String queryTopicDis;

    public double alpha;

    public double beta;

    public double gama;

    @Override
    public boolean setParameter(String parameterName, double value) {
        if(parameterName.equals("alpha")){
            alpha = value;
        }else if(parameterName.equals("beta")){
            beta = value;
        }else if(parameterName.equals("gama")){
            gama = value;
        }else{
            return false;
        }
        return true;
    }

    @Override
    public boolean setParameter(String parameterName, String value) {
        if(parameterName.equals("doc_top")){
            docTopics = value;
        }else if(parameterName.equals("matrix")){
            matrixPath = value;
        }else if(parameterName.equals("query-topic-dis")){
            queryTopicDis = value;
        }else{
            return false;
        }
        return true;
    }
}
