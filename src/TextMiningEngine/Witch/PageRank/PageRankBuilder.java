package TextMiningEngine.Witch.PageRank;

import SearchEngine.Assassin.RetrievalModel.LinkAnalysisModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by amaliujia on 15-3-4.
 */
public class PageRankBuilder {
    public static PageRank createPageRank(LinkAnalysisModel model){
        HashMap<Integer, Integer> outlinks = new HashMap<Integer, Integer>();
        String path = model.path;

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

        PageRank pageRank = new PageRank(row, col);
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

        pageRank.setArguments(0.1, model.beta);

        return pageRank;
    }
}
