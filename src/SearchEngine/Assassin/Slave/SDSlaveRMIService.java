package SearchEngine.Assassin.Slave;

import SearchEngine.Assassin.Operators.QryResult;
import SearchEngine.Assassin.Protocol.SlaveService;
import SearchEngine.Assassin.RetrievalModel.RetrievalModel;

import java.io.IOException;
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

    private SDSlaveNode slaveNode;

    protected SDSlaveRMIService(SDSlaveNode node) throws RemoteException {
        super();
        slaveNode = node;
    }

    @Override
    public QryResult query(String query, RetrievalModel model) throws RemoteException {
        try {
            return this.slaveNode.query(query, model);
        } catch (IOException e) {
            throw new RemoteException(e.toString());
        }
    }

    @Override
    public void shutdown() throws RemoteException {
        slaveNode.isShutdown = true;
    }
}
