package TextMiningEngine.Witch.Classification.Factory;

import TextMiningEngine.Witch.Classification.Base.ClassificationAlgorithm;
import TextMiningEngine.Witch.Classification.Logistic.BinaryLogisticAlgorithm;
import TextMiningEngine.Witch.Classification.Logistic.MultiLogisticAlgorithm;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Classification.ClassificationSparseMatrix;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Classification.ClassificationSparseVector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by amaliujia on 15-3-21.
 */
public class AlgorithmFactory {

    private int dimension = 0;

    public ClassificationAlgorithm create(String type, Map<String, String> params) {
        if(type.equals("logistic")){
            return createLogistic(params);
        }else if(type.equals("mlogistic")){
            return createMultiLogistic(params);
        }
        return null;
    }


    private MultiLogisticAlgorithm createMultiLogistic(Map<String, String> params){
        MultiLogisticAlgorithm lo = new MultiLogisticAlgorithm();
        lo.sparseMatrix = buildInputSpace(params.get("Css:train"));
        lo.testMatrix = buildInputSpace(params.get("Css:test"));
        lo.labels = buildLabelSet(params.get("Css:train"));

        lo.dimension = dimension;
        return lo;
    }


    private BinaryLogisticAlgorithm createLogistic(Map<String, String> params){
        BinaryLogisticAlgorithm lo = new BinaryLogisticAlgorithm();

        lo.sparseMatrix = buildInputSpace(params.get("Css:train"));
        lo.testMatrix = buildInputSpace(params.get("Css:test"));

        lo.dimension = dimension;
        return lo;
    }

    private TreeSet<Integer> buildLabelSet(String path){
        TreeSet<Integer> s = new TreeSet<Integer>();

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
            int label = Integer.parseInt(ss[0]);
            if (!s.contains(label)){
               s.add(label);
            }
        }
        return s;
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
                if(Integer.parseInt(sss[0]) > dimension){
                    dimension = Integer.parseInt(sss[0]);
                }
            }

            if(v.size() == 0){
                continue;
            }
            m.addRowVector(v);
        }

        return m;
    }
}
