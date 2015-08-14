package SearchEngine.Assassin.Protocol;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Created by amaliujia on 15-8-14.
 */
public interface ClientService extends Remote {
    Map<Integer, Double> query(String query) throws RemoteException;
}
