package SearchEngine.Assassin.Master;

/**
 * @author amaliujia
 */
public class SDSlaveObject {
    private String hostAddress;
    private int port;

    public SDSlaveObject(String addr, int p){
        hostAddress = addr;
        port = p;
    }

    public int getPort(){
        return port;
    }

    public String getHostAddress(){
        return hostAddress;
    }
}
