package SearchEngine.Assassin.Master;

import SearchEngine.Assassin.Protocol.MasterService;
import SearchEngine.Assassin.Protocol.SlaveService;
import SearchEngine.Assassin.Util.Constant;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Iterator;
import java.util.Map;
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
        initRMI();

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

    private void initRMI() throws RemoteException {
        Registry registry = LocateRegistry.getRegistry(Constant.MASTER_PORT);
        MasterService service = new SDMasterRMIService(this);
        registry.rebind(MasterService.class.getCanonicalName(), service);
    }

    /**
     * Basic workflow of a query is:
     * 1. Ranked boolean retrieval on 7000M pages. (OR)
     * 2. Get top 100M results, created a Set with 100M doc ids.
     * 2. (Optional) query expansion. (SDM + multi representation)
     * 3. BM25 retrieval on 100M pages.
     * 4. Get top 10,000 pages.
     * 5. SVM on 10,000 pages.
     * 6. Return top 100 pages.
     *
     * @param query
     * @return
     */
    public Map<Integer, Double> query(String query){
        return null;
    }
}
