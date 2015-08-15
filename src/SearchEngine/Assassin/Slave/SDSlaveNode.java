package SearchEngine.Assassin.Slave;

import SearchEngine.Assassin.Master.SDIndexCollection;
import SearchEngine.Assassin.Operators.QryResult;
import SearchEngine.Assassin.Operators.Qryop;
import SearchEngine.Assassin.Protocol.SlaveService;
import SearchEngine.Assassin.QryEval;
import SearchEngine.Assassin.RetrievalModel.RetrievalModel;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by amaliujia on 15-6-27.
 */
public class SDSlaveNode {
    public boolean isShutdown;
    private SDSlaveIndexReader indexReader;

    public SDSlaveNode(){
        isShutdown = false;
    }


    private void initRMI() throws RemoteException {
        Registry registry = LocateRegistry.getRegistry(11642);
        SlaveService service = new SDSlaveRMIService(this);
        registry.rebind(SlaveService.class.getCanonicalName(), service);
    }

    public void startService() throws RemoteException {
        initRMI();

        // TODO: connect with master node.

        while (!isShutdown) {
            try {
                // sleep 10 second.
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                isShutdown = true;
            }
        }

        System.exit(0);
    }

    public QryResult query(String query, RetrievalModel model) throws IOException {
        Qryop qTree;
        qTree = QryEval.parseQuery(query, model);
        return qTree.evaluate(model);
    }

    public void updateIndexCollection(SDIndexCollection collection){
        indexReader.setIndexColletion(collection);
    }

}
