package SearchEngine.Assassin.Main;

import SearchEngine.Assassin.Master.SDMasterNode;
import SearchEngine.Assassin.Slave.SDSlaveNode;

/**
 * Created by amaliujia on 15-7-1.
 */
public class AssassinSE {
    public static void main(String[] args) throws Exception{
        if(args[0].equals("master")){
            SDMasterNode masterNode = new SDMasterNode();
            masterNode.startService();
        }else if(args[0].equals("slave")){
            SDSlaveNode slaveNode = new SDSlaveNode();
            slaveNode.startService();
        }else{
            throw new Exception("Wrong argument");
        }
    }
}
