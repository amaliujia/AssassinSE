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

    protected SDMasterRMIService() throws RemoteException {
    }
}
