package SearchEngine.Assassin.Slave;

import SearchEngine.Assassin.Operators.QryResult;
import SearchEngine.Assassin.Operators.Qryop;
import SearchEngine.Assassin.QryEval;
import SearchEngine.Assassin.RetrievalModel.RetrievalModel;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Created by amaliujia on 15-6-27.
 */
public class SDSlaveNode {
    public boolean isShutdown;

    public SDSlaveNode(){
        isShutdown = false;
    }

    public void startService(){
        while (!isShutdown){
            // waiting
        }

        System.exit(0);
    }

    public QryResult query(String query, RetrievalModel model) throws IOException {
        Qryop qTree;
        qTree = QryEval.parseQuery(query, model);
        return qTree.evaluate(model);
    }
}
