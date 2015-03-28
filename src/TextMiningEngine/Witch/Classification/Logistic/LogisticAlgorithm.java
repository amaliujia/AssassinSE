package TextMiningEngine.Witch.Classification.Logistic;

import TextMiningEngine.Witch.Classification.Base.ClassificationAlgorithm;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Classification.ClassificationSparseVector;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Matrix;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Vector;

import java.util.Random;

/**
 * Created by amaliujia on 15-3-21.
 */
public class LogisticAlgorithm extends ClassificationAlgorithm {

    public Matrix sparseMatrix;

    public Matrix testMatrix;

    public Vector weights;

    private double rate = 0.01;

    public LogisticAlgorithm(){
        weights = new ClassificationSparseVector();
    }

    @Override
    public void run() {

        //initialize weights
        Random r = new Random();
        for(int i = 0; i < 10000000; i++){
            weights.addEntry(i, r.nextDouble() - 0.5);
        }

        GradientDescent();
    }

    private void GradientDescent(){
       for(int i = 0; i < 1000; i++){
           for(int j = 0; j < sparseMatrix.getRowDimension(); j++){
               Vector v = sparseMatrix.getRowVector(j);
               double z = weights.dotproduct(v);
               double qz = Math.tanh(z);
               double err = ((ClassificationSparseVector)v).label - qz;
               for(int p = 0; p < v.size(); p++){
                  int id = v.getEntry(p).getId();
                   double d = weights.getEntry(id).getValue();
                   double f = v.getEntry(p).getValue() * rate + d;
                   ((ClassificationSparseVector)v).setEntry(id, f);
               }
           }
           System.out.println("Finsh iteration " + i);
       }
        System.out.println("Finsh iteration ");
    }


}
