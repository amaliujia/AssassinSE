package SearchEngine.Assassin.Slave;

import SearchEngine.Assassin.Protocol.SlaveService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Design of Slave RMI service:
 * a. Initialization (redo)
 * b. Heartbeat
 * c. Interrupt / shutdown
 * d. Query process request
 */
public class SDSlaveRMIService extends UnicastRemoteObject implements SlaveService {

    protected SDSlaveRMIService() throws RemoteException {
    }
}
