package SearchEngine.Assassin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by amaliujia on 14-10-24.
 */
public class SDPesudoFeedBackEngine {
 //   private int docNum;
    private int termNum;
    private ArrayList<SortEntity> folder;
    private String queryID;
    private ArrayList<TermVector> termVectors;
    private String field = "body";
    private HashMap<String, Double> expansionTermMap;
    private double pesudoFeedbackMu;

    /**
     *
     * @param termNum
     *
     * @param result
     *
     * @param queryID
     */
    public SDPesudoFeedBackEngine(int termNum, ArrayList<SortEntity> result, String queryID, Double mu){
       // this.docNum = docNum;
        this.termNum = termNum;
        folder = result;
        this.queryID = queryID;
        expansionTermMap = new HashMap<String, Double>();
        this.pesudoFeedbackMu = mu;
    }

    /**
     *
     */
    public void SDFeedback() throws IOException {
        ArrayList<TermVector> termVectors = new ArrayList<TermVector>();
        for(int i = 0; i < folder.size(); i++){
           SortEntity entity = folder.get(i);
           try {
               TermVector vector = new TermVector(entity.getInternalDocID(), field);
              // System.out.println("stem length: " + vector.stems.length + "  setFequ length: " + vector.stemsFreq.length);
               termVectors.add(vector);
           }catch (IOException e){
               System.out.println("Failed to fetch term vector");
               continue;
           }
        }

        HashMap<Integer, Double> docWeightMap = new HashMap<Integer, Double>();
        for(int i = 0; i < folder.size(); i++){
            docWeightMap.put(folder.get(i).getInternalDocID(), folder.get(i).getScore());
        }

        double collectionLen = QryEval.READER.getSumTotalTermFreq(field);

        for(int i = 0; i < folder.size(); i++){
            TermVector vec = termVectors.get(i);
            double lenDoc = vec.positionsLength();
            double docWeight = folder.get(i).getScore();
            for(int j = 0; j < vec.stems.length; j++){
                String stem = vec.stemString(j);
                if(stem == null)    continue;
                if(expansionTermMap.containsKey(stem)){
                   double o = expansionTermMap.get(stem);
                    double ctf = (double)vec.totalStemFreq(j);
                    double tfidf = Math.log(collectionLen / ctf);
                    double pmle = ctf / collectionLen;
                    double p = ((double)vec.stemFreq(j) + pesudoFeedbackMu * pmle) / (lenDoc + pesudoFeedbackMu);
                    o += (p * tfidf * docWeight);
                   expansionTermMap.put(stem, o);
                }else{
                   double ctf = (double)vec.totalStemFreq(j);
                   double tfidf = Math.log(collectionLen / ctf);
                   double pmle = ctf / collectionLen;
                   double p = ((double)vec.stemFreq(j) + pesudoFeedbackMu * pmle) / (lenDoc + pesudoFeedbackMu);
                   expansionTermMap.put(stem, p * tfidf * docWeight);
                }
           }
        }
        // Term computation done
        System.out.println("Term num: " + expansionTermMap.size());
    }


}
