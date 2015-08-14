package SearchEngine.Assassin.Master;

import SearchEngine.Assassin.Protocol.ClientService;
import sun.security.ntlm.Client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

/**
 * @author amaliujia
 **/
public class SDClientRMIService extends UnicastRemoteObject implements ClientService {
    SDMasterNode masterNode;

    protected SDClientRMIService(SDMasterNode masterNode) throws RemoteException {
        super();
        this.masterNode = masterNode;
    }

    @Override
    public Map<Integer, Double> query(String query) throws RemoteException {
        return masterNode.query(query);
    }
}
