package TextMiningEngine.Witch.Index;

import SearchEngine.Assassin.Util.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by amaliujia on 15-1-24.
 */
public class ClusteringMatrix {
    List<ClusteringInvList> rowVectors;
    List<ClusteringInvList> columnVectors;

    public ClusteringMatrix(){
        rowVectors = new ArrayList<ClusteringInvList>();
        columnVectors = new ArrayList<ClusteringInvList>();
    }

    public ClusteringMatrix(ClusteringMatrix matrix){
        rowVectors = new ArrayList<ClusteringInvList>();
        columnVectors = new ArrayList<ClusteringInvList>();
        _copyFromMatrix(matrix);
    }

    public void updateVectorSpace(List<Double> vectors, ClusteringVectorType type){
        if(type.equals(ClusteringVectorType.DOCUMENT)){
           updateRowVectorSpace(vectors);
        }else if(type.equals(ClusteringVectorType.WORD)){
            updateColumnVectorSpace(vectors);
        }
    }

    private void updateRowVectorSpace(List<Double> weights){
        for(ClusteringInvList vec : rowVectors){
            for(int i = 0; i < vec.getPostingSize(); i++){
                vec.updatePosting(i, vec.getWeight(i) * weights.get(vec.getID(i)));
            }
        }
    }

    private void updateColumnVectorSpace(List<Double> weights){
        for(ClusteringInvList vec : columnVectors){
            for(int i = 0; i < vec.getPostingSize(); i++){
                vec.updatePosting(i, vec.getWeight(i) * weights.get(vec.getID(i)));
            }
        }
    }


    public void updateVectorSpace(Map<Integer, Double> linkage, ClusteringVectorType type){
        if(type.equals(ClusteringVectorType.DOCUMENT)){
            updateRowVectorSpace(linkage);
        }else if(type.equals(ClusteringVectorType.WORD)){
            updateColumnVectorSpace(linkage);
        }
    }

    private void updateRowVectorSpace(Map<Integer, Double> linkage){
        for(ClusteringInvList vec : rowVectors){
            for(int i = 0; i < vec.getPostingSize(); i++){
                vec.updatePosting(i, vec.getWeight(i) * linkage.get(vec.getID(i)));
            }
        }
    }

    private void updateColumnVectorSpace(Map<Integer, Double> linkage){
        for(ClusteringInvList vec : columnVectors){
            for(int i = 0; i < vec.getPostingSize(); i++){
                vec.updatePosting(i, vec.getWeight(i) * linkage.get(vec.getID(i)));
            }
        }
    }

    public void copyFromMatrix(ClusteringMatrix matrix){
        rowVectors.clear();
        columnVectors.clear();
        _copyFromMatrix(matrix);
    }

    private void _copyFromMatrix(ClusteringMatrix matrix){
        int rowsize = matrix.getRowVecSpaceSize();
        int columnsize = matrix.getColumnVecSpaceSize();

        ClusteringInvList cur;
        ClusteringInvList target;
        for(int i = 0; i < rowsize; i++){
            target = matrix.getRowVector(i);
            cur = new ClusteringInvList(target.type(), target.getInvlistID());
            for(ClusteringPosting posting : target.getPostings()){
                cur.addPosting(posting.getId(), posting.getWeight());
            }
            rowVectors.add(cur);
        }

        for(int i = 0; i < columnsize; i++){
            target = matrix.getColumnVector(i);
            cur = new ClusteringInvList(target.type(), target.getInvlistID());
            for(ClusteringPosting posting : target.getPostings()){
                cur.addPosting(posting.getId(), posting.getWeight());
            }
            columnVectors.add(cur);
        }
    }

    public void createRowVectors(List<ClusteringInvList> invLists, List<Double> idfList){
        ClusteringInvList vec;
        for(int i = 0; i < invLists.size(); i++){
            vec = new ClusteringInvList(ClusteringVectorType.DOCUMENT, i);
            ClusteringInvList invList = invLists.get(i);

            for(int j = 0; j < invList.getPostingSize(); j++){
                int id = invList.getID(j);
                vec.addPosting(id, invList.getWeight(j) * idfList.get(id));
            }
            rowVectors.add(vec);
        }
    }

    public void createColumnVectors(List<ClusteringInvList> invLists, List<Double> idfList){
        ClusteringInvList vec;
        for(int j = 0; j < idfList.size(); j++){
            vec = new ClusteringInvList(ClusteringVectorType.WORD, j);
            for(int i = 0; i < invLists.size(); i++){
                double w = invLists.get(i).exist(j);
                if(w != -1.0){
                    vec.addPosting(i, w * idfList.get(j));
                }
            }
            //System.err.println(j + " word finish");
            columnVectors.add(vec);
        }
        System.err.println("This part finished!!!!!! ------_____________---------");
        try {
            vectorWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readColumnVectors(){
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("data/bitipart/HW2_dev.wordVectors"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(scanner == null){
            Util.fatalError("No column vectors");
        }

        String line;
        ClusteringInvList vec;
        int count = 0;
        while (scanner.hasNext()){
            vec = new ClusteringInvList(ClusteringVectorType.WORD, count++);
            line = scanner.nextLine();
            String[] args = line.split(" ");
            for(int i = 0; i < args.length; i++){
                String[] s = args[i].split(":");
                vec.addPosting(Integer.parseInt(s[0]), Double.parseDouble(s[1]));
            }
            columnVectors.add(vec);
        }
    }

    private void addRowVector(ClusteringInvList invList){
        rowVectors.add(invList);
    }

    private void addColumnVector(ClusteringInvList invList){
        columnVectors.add(invList);
    }

    public String toString(){
        return new String("RowVector " + rowVectors.size() + "\nColumnVector " + columnVectors.size() );
    }

    private void vectorWriter() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("data/bitipart/HW2_dev.wordVectors")));
        for(int i = 0; i < columnVectors.size(); i++){
            ClusteringInvList vec = columnVectors.get(i);
            for(int j = 0; j < vec.getPostingSize(); j++){
                writer.write(vec.getID(j) + ":" + vec.getWeight(j) + " ");
            }
            writer.write("\n");
        }
        writer.flush();
        writer.close();
    }

    public List<ClusteringInvList> getRowVectors(){
        return rowVectors;
    }

    public List<ClusteringInvList> getColumnVectors(){
        return columnVectors;
    }

    public ClusteringInvList getRowVector(int i){
        return rowVectors.get(i);
    }

    public ClusteringInvList getColumnVector(int i){
        return columnVectors.get(i);
    }

    public int getRowVecSpaceSize(){
        return this.rowVectors.size();
    }

    public int getColumnVecSpaceSize(){
        return this.columnVectors.size();
    }

    public void copyColumnVectors(ClusteringMatrix matrix){
        this.columnVectors.clear();
        List<ClusteringInvList> columnVecs = matrix.getColumnVectors();
        for(ClusteringInvList vec : columnVecs){
            this.columnVectors.add(new ClusteringInvList(vec.type(), vec.getInvlistID()));
        }
    }

    public void copyRowVectors(ClusteringMatrix matrix){
        this.rowVectors.clear();
        List<ClusteringInvList> rowVectors = matrix.getRowVectors();
        for(ClusteringInvList vec : rowVectors){
            this.rowVectors.add(new ClusteringInvList(vec.type(), vec.getInvlistID()));
        }
    }
}


