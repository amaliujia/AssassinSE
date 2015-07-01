package SearchEngine.Assassin.Slave;

/**
 * Created by amaliujia on 15-6-27.
 */
public class SDSlaveNode {
    public boolean isShutdown;

    public SDSlaveNode(){
        isShutdown = false;
    }

    public void startService(){
        while (!isShutdown){
            // waiting
        }

        System.exit(0);
    }
}
