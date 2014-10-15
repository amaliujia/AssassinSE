package SearchEngine.Assassin;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by amaliujia on 14-10-15.
 */
public class QryopIlWindow extends QryopIl {

    /**
     *
     * @param q
     * @throws IOException
     */
    public void add(Qryop q) throws IOException {
        this.args.add(q);
    }

    /**
     *
     * @param q
     */
    public void QryopIlWindow(Qryop... q){
        for(int i = 0; i < q.length; i++){
            this.args.add(q[i]);
        }
    }


    public QryResult evaluate(RetrievalModel r) throws IOException {
        if(r instanceof RetrievalModelIndri || r instanceof RetrievalModelBM25)
            return evaluateBM25AndIndri(r);
        return null;
    }

    public QryResult evaluateBM25AndIndri(RetrievalModel r) throws IOException {
        allocDaaTPtrs(r);
        QryResult result = new QryResult();

        return result;
    }

    public String toString() {
        String result = new String();
        for(Iterator<Qryop> i = this.args.iterator(); i.hasNext();){
            result += (i.next().toString() + " ");
        }
        return "#WINDOW( " + result + ")";
    }
    }
}
