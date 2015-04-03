package TextMiningEngine.Witch.Classification.Classifier;

import TextMiningEngine.Witch.Classification.Base.Classifier;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Classification.ClassificationSparseVector;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Matrix;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Vector;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by amaliujia on 15-4-3.
 */
public class MultiClassLogisticClassifier extends Classifier {

    public Matrix testMatrix;

    public ArrayList<Vector> model;

    @Override
    public void classify() {
        double total = 0;
        double hit = 0;
        for(int i = 0; i < testMatrix.getRowDimension(); i++){
            int la = -1;
            double pre = -1;
            Vector v = testMatrix.getRowVector(i);
            for(int j = 0; j < model.size(); j++){
                double linear = v.dotproduct(model.get(j));
                double cpre = Math.tanh(linear);
                if(cpre > pre){
                    pre = cpre;
                    la = ((ClassificationSparseVector)model.get(j)).label;
                }
            }

            if(((ClassificationSparseVector)v).label == la){
                hit++;
            }
            total++;
        }

        System.out.println(hit / total);
    }
}
