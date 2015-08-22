package SearchEngine.Assassin.Master;

import SearchEngine.Assassin.MultiThreadingLib.FuturePattern.FutureData;
import SearchEngine.Assassin.MultiThreadingLib.FuturePattern.FutureExecutor;
import SearchEngine.Assassin.Operators.QryResult;
import SearchEngine.Assassin.Protocol.MasterService;
import SearchEngine.Assassin.Protocol.SlaveService;
import SearchEngine.Assassin.RetrievalModel.RetrievalModel;
import SearchEngine.Assassin.RetrievalModel.RetrievalModelBM25;
import SearchEngine.Assassin.RetrievalModel.RetrievalModelLearningToRank;
import SearchEngine.Assassin.RetrievalModel.RetrievalModelRankedBoolean;
import SearchEngine.Assassin.Util.Constant;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author amaliujia
 */
public class SDMasterNode {
    private ConcurrentHashMap<SDSlaveObject, SlaveService> slaveToService;
    private SDIndexManager indexManager;
    private boolean isShutdown;
    public static AtomicInteger maxId = new AtomicInteger(0);

    public SDMasterNode(){
        slaveToService = new ConcurrentHashMap<SDSlaveObject, SlaveService>();
        indexManager = new SDIndexManager();
        isShutdown = false;
    }

    public void startService() throws RemoteException {
        initRMI();

        while (slaveToService.size() < 1){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        running();
        query("djs");
    }

    public void collectArgs(SDSlaveObject object, SDIndexCollection collection) throws RemoteException{
        Registry registry = LocateRegistry.getRegistry(object.getHostAddress(), object.getPort());

        try {
            SlaveService service = (SlaveService)registry.lookup(SlaveService.class.getCanonicalName());
            slaveToService.put(object, service);
            indexManager.getCollection().mergeWithCollection(collection);
        } catch (NotBoundException e) {
            slaveToService.put(object, null);
        }
    }

    public int assignSlaveID() {
        return maxId.getAndIncrement();
    }

    private void running(){
        new Thread(){
            public void run(){
                while (!isShutdown) {
                    try {
                        // sleep 10 second.
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        Iterator<SlaveService> iter = slaveToService.values().iterator();
                        while (iter.hasNext()){
                            SlaveService service = iter.next();
                            try {
                                service.shutdown();
                            } catch (RemoteException e1) {
                                e1.printStackTrace();
                            }
                        }
                        isShutdown = true;
                    }
                }
                System.exit(0);
            }
        }.start();
    }

    private void initRMI() throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(Constant.MASTER_PORT);
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
        String newQuery = expandQuery(query);
        Set<Integer> docs;
        QryResult result;
        RetrievalModel model;

        // Boolean retrieval distributed search.
        model = new RetrievalModelRankedBoolean();
        result = distributedSearch(query, model);
        // top 1M pages to BM25 retrieval.
        docs = docsMapToSet(result, 1000000);

        // BM25 retrieval distributed search.
        model = new RetrievalModelBM25();
        model.setParameter("docs", (Object)docs);
        //TODO: setup global arguments.

        result = distributedSearch(query, model);
        // top 1k pages to learning to rank.
        docs = docsMapToSet(result, 1000);

        model = new RetrievalModelLearningToRank();
        model.setParameter("docs", (Object)docs);
        //TODO: setup global arguments.
        result = distributedSearch("docs", model);

        return QryResultToMap(result, 1000);
    }


    private String expandQuery(String query){
        double url = 0.0;
        double keywords = 0.0;
        double body = 0.3;
        double title =0.3;
        double inlink = 0.2;

        double w1 = 0.1;
        double w2 = 0.9;


        return query;
    }

    private QryResult distributedSearch(String query, RetrievalModel model){
        ArrayList<FutureData> data = new ArrayList<FutureData>();
        FutureExecutor executor = new FutureExecutor();

        for (SDSlaveObject object : slaveToService.keySet()){
            FutureData futureData = executor.request(slaveToService.get(object), query, model);
            data.add(futureData);
        }
        QryResult result = new QryResult();
        for (FutureData futureData : data){
           result = mergeQryResult(result, futureData.getContent());
        }
        return result;
    }


    private Map<Integer, Double> QryResultToMap(QryResult result, int top){
        return null;
    }

    private HashSet<Integer> docsMapToSet(QryResult result, int top){
        HashSet<Integer> docs = new HashSet<Integer>();
        result.docScores.sort();

        int size = result.docScores.scores.size();
        for(int i = 0; i < top && i < size; i++){
            docs.add(result.docScores.scores.get(i).getDocid());
        }
        return docs;
    }

    private QryResult mergeQryResult(QryResult r1, QryResult r2){
        r1.docScores.scores.addAll(r2.docScores.scores);
        return r1;
    }
}
