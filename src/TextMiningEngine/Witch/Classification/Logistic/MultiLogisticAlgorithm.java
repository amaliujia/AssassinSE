package TextMiningEngine.Witch.Classification.Logistic;

import TextMiningEngine.Witch.Classification.Base.ClassificationAlgorithm;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Classification.ClassificationSparseVector;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Matrix;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Vector;

import java.util.*;

/**
 * Created by amaliujia on 15-3-28.
 */
public class MultiLogisticAlgorithm extends ClassificationAlgorithm {

    public Matrix sparseMatrix;

    public Matrix testMatrix;

    public List<Vector> weights;

    private double rate = 0.05;

    public TreeSet<Integer> labels;

    private double dis = 1e-4;

    private List<Vector> wh = null;

    public MultiLogisticAlgorithm(){
        weights = new ArrayList<Vector>();
        labels = new TreeSet<Integer>();

    }

    private Vector createRandomWeights(){
        Vector v = new ClassificationSparseVector();
        Random r = new Random();
        for(int i = 0; i < dimension; i++){
            v.addEntry(i, r.nextDouble() - 0.5);
        }
        return v;
    }

    @Override
    public void run() {
       for(int i = 0; i < labels.size(); i++){
           weights.add(createRandomWeights());
       }

        GradientDescent();
    }

    private void GradientDescent(){
       //for(int i = 0; i < 10000; i++){
        int i = 0;
        while(true) {
            for (int j = 0; j < sparseMatrix.getRowDimension(); j++) {
                Vector v = sparseMatrix.getRowVector(j);
                int count = 0;
                for (Integer t : labels) {
                    trainOneLabel(i, v, weights.get(count));
                    count += 1;
                }
            }
            System.out.println("Finish iteration " + i++);
            if (ifConverge()) {
                break;
            }
        }
       //}
        System.out.println("Finsh iteration ");
    }

    private List<Vector> copyWeight(){
        List l = new ArrayList<Vector>();
        for(int i = 0; i < weights.size(); i++){
           l.add(weights.get(i).copy());
        }
        return l;
    }

    private boolean ifConverge(){
        if(this.wh == null){
           wh = copyWeight();
            return false;
        }
        for(int i = 0; i < wh.size(); i++){
            if(Math.abs(wh.get(i).norm() - weights.get(i).norm()) > dis){
                System.out.println(Math.abs(wh.get(i).norm() - weights.get(i).norm()));
                this.wh = copyWeight();
                return false;
            }
        }
        return true;
    }

    private void trainOneLabel(int label, Vector v, Vector weights){

        double z = weights.dotproduct(v);
        double qz = Math.tanh(z);
        double y;
        if(((ClassificationSparseVector)v).label == label){
            y = 1;
        }else{
            y = 0;
        }

        double err = y - qz;
        for(int p = 0; p < v.size(); p++){
            int id = v.getEntry(p).getId();
            double d = weights.getEntry(id).getValue();
            double f = err * v.getEntry(p).getValue() * rate + d;
            ((ClassificationSparseVector)weights).setEntry(id, f);
        }

    }
}
