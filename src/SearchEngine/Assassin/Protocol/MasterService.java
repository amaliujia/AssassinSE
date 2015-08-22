package SearchEngine.Assassin.Protocol;

import SearchEngine.Assassin.Master.SDIndexCollection;
import SearchEngine.Assassin.Master.SDSlaveObject;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by amaliujia on 15-7-1.
 */
public interface MasterService extends Remote {
    /**
     * Collect index statistics from slave node.
     * @param object
     *             Object that represents slave node including address, port and so on.
     * @param collection
     *              Contains index statistics of index.
     * @throws RemoteException
     */
    void collectArgs(SDSlaveObject object, SDIndexCollection collection) throws RemoteException;

    /**
     * As a centralize server, master node assigns an unique id to slave node.
     * @return
     * @throws RemoteException
     */
    int assignSlaveID() throws RemoteException;
}
