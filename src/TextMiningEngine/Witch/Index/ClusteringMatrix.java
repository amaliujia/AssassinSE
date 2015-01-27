package TextMiningEngine.Witch.Index;

import SearchEngine.Assassin.Util.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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
        while (scanner.hasNext()){
            vec = new ClusteringInvList();
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

    public int getRowVecSpaceSize(){
        return this.rowVectors.size();
    }

    public int getColumnVecSpaceSize(){
        return this.columnVectors.size();
    }
}


