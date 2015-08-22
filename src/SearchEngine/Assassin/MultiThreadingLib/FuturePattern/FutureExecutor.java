package SearchEngine.Assassin.MultiThreadingLib.FuturePattern;

import SearchEngine.Assassin.Protocol.SlaveService;
import SearchEngine.Assassin.RetrievalModel.RetrievalModel;

import java.rmi.RemoteException;

/**
 * @author amaliujia
 */
public class FutureExecutor {
    public FutureData request(final SlaveService slaveService, final String query, final RetrievalModel model) {
        final FutureData futureData = new FutureData();

        new Thread() {
            public void run(){
                try {
                     RealData realData = new RealData(slaveService.query(query, model));
                    futureData.setRealData(realData);
                } catch (RemoteException e) {
                    System.out.println(slaveService.toString() + " fail");
                }
            }
        }.start();

        return futureData;
    }
}
