package SearchEngine.Assassin.Master;

import SearchEngine.Assassin.Protocol.MasterService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Design of Slave RMI service:
 * a. Initialization (how long?)
 * b. Heartbeat
 * c. Interrupt / shutdown
 * d. Query process request
 * e. Query process result
 * f. Task monitering
 */
public class SDMasterRMIService extends UnicastRemoteObject implements MasterService {

    private SDMasterNode masterNode;

    protected SDMasterRMIService(SDMasterNode node) throws RemoteException {
        super();
        masterNode = node;
    }

    @Override
    public void collectArgs(SDSlaveObject object, SDIndexCollection collection) throws RemoteException {
        masterNode.collectArgs(object, collection);
    }
}
