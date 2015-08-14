package SearchEngine.Assassin.Protocol;

import SearchEngine.Assassin.Operators.QryResult;
import SearchEngine.Assassin.RetrievalModel.RetrievalModel;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by amaliujia on 15-7-1.
 */
public interface SlaveService extends Remote {

    QryResult query(String query, RetrievalModel model) throws RemoteException;

    void shutdown() throws RemoteException;
}
