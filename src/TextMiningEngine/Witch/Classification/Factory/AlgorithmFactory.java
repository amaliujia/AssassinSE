package TextMiningEngine.Witch.Classification.Factory;

import TextMiningEngine.Witch.Classification.Base.ClassificationAlgorithm;
import TextMiningEngine.Witch.Classification.Logistic.LogisticAlgorithm;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Classification.ClassificationSparseMatrix;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Classification.ClassificationSparseVector;
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

        lo.sparseMatrix = buildInputSpace(params.get(""));

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
            if(ss.length <= 1){
                continue;
            }
            ClassificationSparseVector v = new ClassificationSparseVector();
            v.label = Integer.parseInt(ss[0]);

            for(int i = 1; i < ss.length; i++){
                String[] sss = ss[i].split(":");
                if(sss.length != 2){
                    continue;
                }
                v.addEntry(Integer.parseInt(sss[0]), Double.parseDouble(sss[1]));
            }

            if(v.size() == 0){
                continue;
            }
            m.addRowVector(v);
        }

        return m;
    }
}
