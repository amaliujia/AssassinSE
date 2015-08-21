package SearchEngine.Assassin.RetrievalModel;

import SearchEngine.Assassin.Lucene.DocLengthStore;

import java.util.HashSet;

/**
 * Created by amaliujia on 14-9-21.
 */
public class RetrievalModelBM25 extends RetrievalModel {

    // BM25
    public transient DocLengthStore docLengthStore; // do not send via RMI.
    public int numDocs;
    public int avgLenDoc;
    public double k1;
    public double b;
    public double k3;
    public HashSet<Integer> docs;

    public RetrievalModelBM25(){
        super();
        docs = null;
    }

    @Override
    public boolean setParameter(String parameterName, double value) {
        if(parameterName.equals("k1")) {
            k1 = value;
        } else if(parameterName.equals("b")) {
            b = value;
        } else if(parameterName.equals("k3")) {
            k3 = value;
        } else if(parameterName.equals("avgLenDoc")) {
            avgLenDoc = (int)value;
        } else if(parameterName.equals("numDocs")) {
            numDocs = (int)value;
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean setParameter(String parameterName, String value) {
        return false;
    }

    @Override
    public boolean setParameter(String parameterName, Object value) {
        if(parameterName.equals("docs")){
            docs = (HashSet<Integer>) value;
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
