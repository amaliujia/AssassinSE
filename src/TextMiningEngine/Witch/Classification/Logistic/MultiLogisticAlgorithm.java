package TextMiningEngine.Witch.Classification.Logistic;

import TextMiningEngine.Witch.Classification.Base.ClassificationAlgorithm;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Classification.ClassificationSparseVector;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Matrix;
import TextMiningEngine.Witch.LinearAlgebra.Matrix.Interface.Vector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

/**
 * Created by amaliujia on 15-3-28.
 */
public class MultiLogisticAlgorithm extends ClassificationAlgorithm {

    public Matrix sparseMatrix;

    public Matrix testMatrix;

    public List<Vector> weights;

    private double rate = 0.05;

    public TreeSet<Integer> labels;

    private double dis = 1e-3;

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

        ParallelGradientDescent();
        //GradientDescent();
        try {
            writeModle();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void ParallelGradientDescent() {
        Thread threads[] = new Thread[labels.size()];
        int i = 0;
        for(Integer t : labels){
           threads[i] = new Thread(new graientDescentThread(t, i, weights.get(i)));
            threads[i].start();
            //System.out.println(t);
            i++;
        }
        for(int j = 0; j < threads.length; j++){
            try {
                threads[j].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     */
    private void GradientDescent(){
       //for(int i = 0; i < 10000; i++){
        int i = 0;
        while(true) {
            for (int j = 0; j < sparseMatrix.getRowDimension(); j++) {
                Vector v = sparseMatrix.getRowVector(j);
                int count = 0;
                for (Integer t : labels) {
                    trainOneLabel(t, v, weights.get(count));
                    count += 1;
                }
            }
            i++;
            if(i % 1000 == 0){
                System.out.println("Finish iteration " + i);
            }
            if (ifConverge()) {
                break;
            }
        }
       //}
        System.out.println("Finsh iteration ");
    }

    /**
     *
     * @throws IOException
     */
    private void writeModle() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("model/mlogistic.model"), false));
        int i = 0;
        for(Integer c : labels){
            Vector v = weights.get(i++);
            writer.write(c + " " + v.size() + " ");
            for(int j = 0; j < v.size(); j++){
                writer.write(v.getEntry(j).getValue() + " ");
            }
            writer.write("\n");
            writer.flush();
        }
    }

    /**
     *
     * @return
     */
    private List<Vector> copyWeight(){
        List l = new ArrayList<Vector>();
        for(int i = 0; i < weights.size(); i++){
           l.add(weights.get(i).copy());
        }
        return l;
    }

    /**
     *
     * @return
     */
    private boolean ifConverge(){
        if(this.wh == null){
           wh = copyWeight();
            return false;
        }
        for(int i = 0; i < wh.size(); i++){
            if(Math.abs(wh.get(i).norm() - weights.get(i).norm()) > dis){
                //System.out.println(Math.abs(wh.get(i).norm() - weights.get(i).norm()));
                this.wh = copyWeight();
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param label
     * @param v
     * @param weights
     */
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


    /**
     *
     */
    private class graientDescentThread implements Runnable{
        private int label;

        private int index;

        private Vector ws;

        private Vector wt = null;

        public graientDescentThread(int l, int i, Vector w){
           label = l;
            index = i;
            ws = w;
        }

        @Override
        public void run() {
            int i = 0;
            while(true) {
                for (int j = 0; j < sparseMatrix.getRowDimension(); j++) {
                    Vector v = sparseMatrix.getRowVector(j);
                    trainOneLabelThread(label, v, ws);
                }
                i++;
                if(i % 1000 == 0){
                    System.out.println("Thread " + index + " in iteration " + i + " with distance " +
                                        Math.abs(this.wt.norm() - this.ws.norm()));
                }

                if (ifConvergeThread()) {
                    break;
                }
            }
        }

        private boolean ifConvergeThread(){
            if(this.wt == null){
                this.wt = this.ws.copy();
                return false;
            }
           // for(int i = 0; i < wh.size(); i++){
                if(Math.abs(this.wt.norm() - this.ws.norm()) > dis){
                    //System.out.println(Math.abs(wh.get(i).norm() - weights.get(i).norm()));
                    this.wt = ws.copy();
                    return false;
                }
            //}
            return true;
        }

        /**
         *
         * @param label
         * @param v
         * @param weights
         */
        private void trainOneLabelThread(int label, Vector v, Vector weights){
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
}
