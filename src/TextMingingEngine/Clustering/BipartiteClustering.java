package TextMingingEngine.Clustering;


import SearchEngine.Assassin.Util.Util;
import TextMingingEngine.Index.ClusteringIndex;
import TextMingingEngine.Index.ClusteringInvList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by amaliujia on 15-1-23.
 */
public class BipartiteClustering {

    private ClusteringIndex index;

    public BipartiteClustering(Map<String, String> params){
        index = new ClusteringIndex();
        readInvLists(params);
        readDf(params);
        readDict(params);
    }

    private void readDf(Map<String, String> params){
        if(!params.containsKey("bi:devdf")){
            Util.fatalError("bi:devdf does not exist");
        }

        String path = params.get("bi:devdf");
        try {
            Scanner scanner = new Scanner(new File(path));
            String line;
            while (scanner.hasNext()){
                line = scanner.nextLine();
                String[] args = line.split(":");
                if(args.length != 2){
                    System.err.println(args);
                }else{
                    index.addTermFreq(Integer.parseInt(args[1]));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void readDict(Map<String, String> params){
        if(!params.containsKey("bi:devdic")){
            Util.fatalError("bi:devdic does not exist");
        }

        String path = params.get("bi:devdic");
        try {
            Scanner scanner = new Scanner(new File(path));
            String line;
            while (scanner.hasNext()){
                line = scanner.nextLine();
                String[] args = line.split(" ");
                if(args.length != 2){
                    System.err.println(args);
                }else{
                    index.addDictWord(args[0], Integer.parseInt(args[1]));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void readInvLists(Map<String, String> params){
        if(!params.containsKey("bi:devdocVectors")){
            Util.fatalError("bi:devdocVectors does not exist");
        }

        String path = params.get("bi:devdocVectors");
        try {
            Scanner scanner = new Scanner(new File(path));
            String line;
            ClusteringInvList invList;
            while (scanner.hasNext()){
                line = scanner.nextLine();
                String[] args = line.split(" ");
                invList = new ClusteringInvList();
                for(int i = 0; i < args.length; i++){
                    String[] ss = args[i].split(":");
                    invList.addPosting(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]));
                }
                index.addinvList(invList);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
