package TextMiningEngine.Witch.PageRank;

import SearchEngine.Assassin.RetrievalModel.LinkAnalysisModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by amaliujia on 15-3-4.
 */
public class PageRankBuilder {
    public static PageRank createPageRank(LinkAnalysisModel model){
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
            if(Integer.parseInt(cell[0]) > row){
                row = Integer.parseInt(cell[0]);
            }

            if(Integer.parseInt(cell[1]) > col){
                col = Integer.parseInt(cell[1]);
            }
        }

        PageRank pageRank = new PageRank(row, col);

        scanner = new Scanner(path);
        while (scanner.hasNext()){
            String[] cell = scanner.next().split(" ");
            pageRank.setEntry(Integer.parseInt(cell[0]), Integer.parseInt(cell[1]), Double.parseDouble(cell[2]));
        }

        return pageRank;
    }
}
