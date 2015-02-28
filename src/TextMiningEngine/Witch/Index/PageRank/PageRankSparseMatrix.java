package TextMiningEngine.Witch.Index.PageRank;

import TextMiningEngine.Witch.LinearAlgebra.Matrix.SparseMatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by amaliujia on 15-2-22.
 */
public class PageRankSparseMatrix extends SparseMatrix {
    public PageRankSparseMatrix(int rows, int columns, String path){
        super();
        this.rows = rows;
        this.columns = columns;

        setPageRankSparseMatrix(path);
    }

    public void setPageRankSparseMatrix(String path){
        try {
            Scanner scanner = new Scanner(new File(path));
            String line = "";
            while (scanner.hasNext()){
                line = scanner.nextLine();
                String[] s = line.split(" ");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
