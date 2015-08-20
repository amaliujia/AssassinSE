package SearchEngine.Assassin.Protocol;

import SearchEngine.Assassin.Master.SDIndexCollection;
import SearchEngine.Assassin.Master.SDSlaveObject;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by amaliujia on 15-7-1.
 */
public interface MasterService extends Remote {
    void collectArgs(SDSlaveObject object, SDIndexCollection collection) throws RemoteException;
}
