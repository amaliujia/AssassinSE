package SearchEngine.Assassin.Master;

import SearchEngine.Assassin.Protocol.SlaveService;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author amaliujia
 */
public class SDMasterNode {
    private ConcurrentHashMap<SDSlaveObject, SlaveService> slaveToService;
    private boolean isShutdown;

    public SDMasterNode(){
        slaveToService = new ConcurrentHashMap<SDSlaveObject, SlaveService>();
        isShutdown = false;
    }

    public void startService() throws RemoteException {
        while (!isShutdown) {
            try {
                // sleep 10 second.
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Iterator<SlaveService> iter = slaveToService.values().iterator();
                while (iter.hasNext()){
                    SlaveService service = iter.next();
                    service.shutdown();
                }
                isShutdown = true;
            }
        }
        System.exit(0);
    }
}
