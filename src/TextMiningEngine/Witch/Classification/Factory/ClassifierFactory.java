package TextMiningEngine.Witch.Classification.Factory;

import TextMiningEngine.Witch.Classification.Base.Classifier;
import TextMiningEngine.Witch.Classification.Classifier.MultiClassLogisticClassifier;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Classification.ClassificationSparseMatrix;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Classification.ClassificationSparseVector;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by amaliujia on 15-4-3.
 */
public class ClassifierFactory {

    private int dimension = 0;

    public Classifier create(String type, Map<String, String> params) {
        if (type.equals("rmlogistic")) {
            return createMultiRegularizedLogisticClassifier(params);
        }
        return null;
    }

    public Classifier createMultiRegularizedLogisticClassifier(Map<String, String> params){
        MultiClassLogisticClassifier lo = new MultiClassLogisticClassifier();

        lo.testMatrix = buildInputSpace(params.get("Css:test"));
        lo.model = buildModel(params.get("Css:model"));
        return lo;

    }

    private ArrayList<Vector> buildModel(String path){
        ArrayList<Vector> re = new ArrayList<Vector>();

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
            if(ss.length <= 2){
                continue;
            }
            ClassificationSparseVector v = new ClassificationSparseVector();
            v.label = Integer.parseInt(ss[0]);
            int dimension = Integer.parseInt(ss[1]);
            for(int i = 0; i < dimension; i++){
              v.addEntry(i, Double.parseDouble(ss[2 + i]));
            }
            re.add(v);
        }

        return re;
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
            v.addEntry(0, -1);

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
