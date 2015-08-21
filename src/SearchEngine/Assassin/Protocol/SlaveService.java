package SearchEngine.Assassin.Protocol;

import SearchEngine.Assassin.Operators.QryResult;
import SearchEngine.Assassin.RetrievalModel.RetrievalModel;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by amaliujia on 15-7-1.
 */
public interface SlaveService extends Remote {

    /**
     * Ask slave node do a search on query and retrieval model.
     * @param query
     * @param model
     * @return
     * @throws RemoteException
     */
    QryResult query(String query, RetrievalModel model) throws RemoteException;

    /**
     * Shut down slave node.
     * @throws RemoteException
     */
    void shutdown() throws RemoteException;
}
