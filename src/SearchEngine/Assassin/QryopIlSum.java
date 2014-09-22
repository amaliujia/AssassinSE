package SearchEngine.Assassin;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by amaliujia on 14-9-21.
 */
public class QryopIlSum extends QryopIl {

    @Override
    public void add(Qryop q) throws IOException {
        this.args.add(q);
    }

    public void add(Qryop ...q) throws IOException{
        for(int i = 0; i < q.length; i++){
            this.args.add(q[i]);
        }
    }

    // This function is the place where our algorithm is implemented
    @Override
    public QryResult evaluate(RetrievalModel r) throws IOException {
        allocDaaTPtrs(r);
        syntaxCheckArgResults(this.daatPtrs);
        QryResult result = new QryResult();

        return result;
    }

    @Override
    public String toString() {
        String result = new String();
        for(Iterator<Qryop> i = this.args.iterator(); i.hasNext();){
            result += (i.next().toString() + " ");
        }
        return "#SUM( " + result + ")";
    }

    public Boolean syntaxCheckArgResults (List<DaaTPtr> ptrs) {

        for (int i=0; i<this.args.size(); i++) {

            if (! (this.args.get(i) instanceof QryopIl))
                QryEval.fatalError ("Error:  Invalid argument in " +
                        this.toString());
            else if ((i>0) && (! ptrs.get(i).invList.field.equals (ptrs.get(0).invList.field)))
                QryEval.fatalError ("Error:  Arguments must be in the same field:  " +
                        this.toString());
        }
        return true;
    }
}
