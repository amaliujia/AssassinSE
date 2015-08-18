package SearchEngine.Assassin.MultiThreadingLib.FuturePattern;

import SearchEngine.Assassin.Operators.QryResult;

/**
 * Created by amaliujia on 15-8-18.
 */
public class FutureData implements Data {
    private RealData realData = null;
    private boolean ready = false;

    public synchronized void setRealData(RealData data){
        if (ready){
            return; //bulk
        }

        this.realData = data;
        ready = true;
        notifyAll();
    }

    @Override
    public synchronized QryResult getContent() {
        while (!ready){
            try{
                wait();
            } catch (InterruptedException e) {
                System.out.println("Future data died");
            }
        }
        return realData.getContent();
    }
}
