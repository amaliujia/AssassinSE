package SearchEngine.Assassin.Master;

import java.io.Serializable;

/**
 * @author amaliujia
 */
public class SDSlaveObject implements Serializable{
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
