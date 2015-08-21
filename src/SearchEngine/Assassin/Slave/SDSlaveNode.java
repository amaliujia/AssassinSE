package SearchEngine.Assassin.Slave;

import SearchEngine.Assassin.Master.SDIndexCollection;
import SearchEngine.Assassin.Master.SDSlaveObject;
import SearchEngine.Assassin.Operators.QryResult;
import SearchEngine.Assassin.Operators.Qryop;
import SearchEngine.Assassin.Protocol.MasterService;
import SearchEngine.Assassin.Protocol.SlaveService;
import SearchEngine.Assassin.QryEval;
import SearchEngine.Assassin.RetrievalModel.RetrievalModel;
import SearchEngine.Assassin.Util.Constant;
import SearchEngine.Assassin.Util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * @author amaliujia
 */
public class SDSlaveNode {
    public boolean isShutdown;
    public static SDSlaveIndexReader indexReader;
    private Map<String, String> params;

    public SDSlaveNode(){
        isShutdown = false;
    }


    private void initRMI() throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(Constant.SLAVE_PORT);
        SlaveService service = new SDSlaveRMIService(this);
        registry.rebind(SlaveService.class.getCanonicalName(), service);
    }

    /**
     * Read configuration from config file.
     * @param path
     * @throws FileNotFoundException
     */
    private void ReadArg(String path) throws FileNotFoundException {
        // read in the parameter file; one parameter per line in format of key=value
        params = new HashMap<String, String>();
        Scanner scan = new Scanner(new File(path));
        String line;
        do {
            line = scan.nextLine();
            String[] pair = line.split("=");
            params.put(pair[0].trim(), pair[1].trim());
        } while (scan.hasNext());
        scan.close();

        // parameters required for this example to run
        if (!params.containsKey("indexPath")) {
            System.err.println("Error: Parameters were missing.");
            exit(1);
        }
    }

    private void InitIndexReader(){
        indexReader = new SDSlaveIndexReader(params.get("indexPath"));
    }

    private SDSlaveObject createObject() {
        SDSlaveObject object = null;
        try {
            object = new SDSlaveObject(InetAddress.getLocalHost().getHostAddress(), Constant.SLAVE_PORT);
        } catch (UnknownHostException e) {
            System.out.println(e.toString());
        }
        return object;
    }

    private void connect() throws RemoteException, NotBoundException{
        Registry registry = LocateRegistry.getRegistry(Constant.MASTER_HOST, Constant.MASTER_PORT);
        MasterService service = (MasterService) registry.lookup(MasterService.class.getCanonicalName());
        SDSlaveObject object = createObject();
        service.collectArgs(object, null);
    }

    private void running(){

        new Thread(){
            public void run(){
                while (!isShutdown) {
                    try {
                        // sleep 10 second.
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        isShutdown = true;
                    }
                }
                System.exit(0);
            }
        }.start();
    }

    public void startService(String path) throws RemoteException, FileNotFoundException {
        ReadArg(path);
        InitIndexReader();
        initRMI();

        try {
            connect();
        } catch (Exception e) {
            System.out.println(e.toString());
            Util.fatalError("kill slave");
        }

        running();
    }

    public QryResult query(String query, RetrievalModel model) throws IOException {
        Qryop qTree;
        qTree = QryEval.parseQuery(query, model);
        QryResult result = qTree.evaluate(model);
        System.out.println("Finished retrieval");
        return result;
    }
}
