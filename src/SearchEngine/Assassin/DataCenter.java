package SearchEngine.Assassin;

/**
 * Created by amaliujia on 14-9-21.
 */
public class DataCenter {
    public static DataCenter dataCenter = null;

    public DocLengthStore docLengthStore;
    public int numDocs;
    public int avgLenDoc;
    public static double k1;
    public static double b;
    public static double k3;

    public static DataCenter sharedDataCenter(){
       if(dataCenter == null)
           dataCenter = new DataCenter();
       return dataCenter;
    }


}
