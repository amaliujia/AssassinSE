package TextMiningEngine.Witch.Classification.Factory;

import TextMiningEngine.Witch.Classification.Base.ClassificationAlgorithm;
import TextMiningEngine.Witch.Classification.Logistic.LogisticAlgorithm;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Classification.ClassificationSparseMatrix;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.PageRank.SparseMatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by amaliujia on 15-3-21.
 */
public class AlgorithmFactory {

    public ClassificationAlgorithm create(String type, HashMap<String, String> params) {
        if(type.equals("logistic")){
            return createLogistic(params);
        }
        return null;
    }


    private LogisticAlgorithm createLogistic(HashMap<String, String> params){
        LogisticAlgorithm lo = new LogisticAlgorithm();

        return lo;
    }

    private ClassificationSparseMatrix buildInputSpace(String path){
        ClassificationSparseMatrix m = new ClassificationSparseMatrix();

        Scanner scanner = null;

        try {
            scanner= new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line = "";
        while (scanner.hasNext()){
            line = scanner.nextLine();
            String[] ss = line.split(" ");
            ClassificationSparseMatrix
        }
        return null;
    }
}
