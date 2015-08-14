package SearchEngine.Assassin.Protocol;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by amaliujia on 15-7-1.
 */
public interface MasterService extends Remote {
    public void collectArgs(int df, int collection_size) throws RemoteException;
}
