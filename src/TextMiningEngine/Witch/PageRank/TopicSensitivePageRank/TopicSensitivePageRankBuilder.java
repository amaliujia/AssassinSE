package TextMiningEngine.Witch.PageRank.TopicSensitivePageRank;

import SearchEngine.Assassin.RetrievalModel.TopicSensitivePRModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by amaliujia on 15-3-11.
 */
public class TopicSensitivePageRankBuilder {
    public static TopicSensitivePageRank createTSPageRank(TopicSensitivePRModel model){
        HashMap<Integer, Integer> outlinks = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> docToTopic = new HashMap<Integer, Integer>();
        HashMap<Integer, ArrayList<Integer>> docToTopicList = new HashMap<Integer, ArrayList<Integer>>();
        String path = model.matrixPath;

        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(scanner == null){
            return null;
        }

        int row = -1;
        int col = -1;
        String line = "";
        while (scanner.hasNext()){
            line = scanner.nextLine();
            String[] cell = line.split(" ");
            int outId = Integer.parseInt(cell[0]);

            if(Integer.parseInt(cell[0]) > row){
                row = Integer.parseInt(cell[0]);
            }

            if(Integer.parseInt(cell[1]) > col){
                col = Integer.parseInt(cell[1]);
            }

            if(outlinks.containsKey(outId)){
                int g = outlinks.get(outId);
                g += 1;
                outlinks.put(outId, g);
            } else{
                outlinks.put(outId, 1);
            }
        }

        TopicSensitivePageRank pageRank = new TopicSensitivePageRank(row, col);
        pageRank.outlinks = outlinks;

        try {
            scanner = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scanner.hasNext()){
            line = scanner.nextLine();
            String[] cell = line.split(" ");
            pageRank.setEntry( Integer.parseInt(cell[1]), Integer.parseInt(cell[0]), 1.0 / outlinks.get(Integer.parseInt(cell[0])));
        }

        pageRank.setArguments(model.alpha, model.beta, model.gama);

        try{
            scanner = new Scanner(new File(model.docTopics));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (scanner.hasNext()){
            line = scanner.nextLine();
            String[] cell = line.split(" ");
            int t = Integer.parseInt(cell[1]);
            int d = Integer.parseInt(cell[0]);
            if(docToTopic.containsKey(t)){
                docToTopic.put(t, docToTopic.get(t) + 1);
            }else{
                docToTopic.put(t, 1);
            }
            ArrayList a;
            if(docToTopicList.containsKey(t)){
                a = docToTopicList.get(t);
            }else{
                a = new ArrayList<Integer>();
            }
            a.add(d);
            docToTopicList.put(t, a);
        }

        pageRank.docToTopic = docToTopic;
        pageRank.curTop = model.curTop;
        pageRank.docToTopicList = docToTopicList;
        return pageRank;
    }
}
