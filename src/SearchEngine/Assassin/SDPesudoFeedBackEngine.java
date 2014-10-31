package SearchEngine.Assassin;

import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.*;

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
    public String SDFeedback() throws IOException {
        termVectors = new ArrayList<TermVector>();
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
           // double lenDoc = vec.positionsLength();
            double lenDoc = DataCenter.sharedDataCenter().docLengthStore.getDocLength("body", folder.get(i).getInternalDocID());
            double docWeight = folder.get(i).getScore();
            for(int j = 0; j < vec.stemsLength(); j++){
                String stem = vec.stemString(j);
                if(stem == null) {
                    continue;
                }
                if(expansionTermMap.containsKey(stem)){
                   double o = expansionTermMap.get(stem);
                   //double ctf = (double)vec.totalStemFreq(j);
                   double ctf = QryEval.READER.totalTermFreq(new Term("body", new BytesRef(stem)));
                   double tfidf = Math.log(collectionLen / ctf);
                   double pmle = ctf / collectionLen;
                   double p = (((double)vec.stemFreq(j)) + pesudoFeedbackMu * pmle) / (lenDoc + pesudoFeedbackMu);
                   o += (p * tfidf * docWeight);
                   expansionTermMap.put(stem, o);
                }else{
                   //double ctf = (double)vec.totalStemFreq(j);
                   double ctf = QryEval.READER.totalTermFreq(new Term("body", new BytesRef(stem)));
                   double tfidf = Math.log(collectionLen / ctf);
                   double pmle = ctf / collectionLen;
                   double p = (((double)vec.stemFreq(j)) + pesudoFeedbackMu * pmle) / (lenDoc + pesudoFeedbackMu);
                   expansionTermMap.put(stem, p * tfidf * docWeight);
                }
           }
        }


        List<Map.Entry<String, Double>> sortedList = new ArrayList<Map.Entry<String, Double>>(expansionTermMap.entrySet());
        Collections.sort(sortedList, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> e1,
                               Map.Entry<String, Double> e2) {
                if (!e1.getValue().equals(e2.getValue())) {
                    if (e2.getValue() > e1.getValue()) return 1;
                    else return -1;
                } else
                    return (e1.getKey()).toString().compareTo(e2.getKey().toString());
            }
        });
        // Term computation done
      //  System.out.println("Term num: " + sortedList.get(sortedList.size() - 1));

        String result = "#WAND ( ";
        for(int z = 0; z < termNum; z++){
            if(sortedList.get(z).getKey().indexOf('.') != -1 ||
               sortedList.get(z).getKey().indexOf(',') != -1){
               z--;
               continue;
            }
            result += (sortedList.get(z).getValue() + " " + sortedList.get(z).getKey() + " ");
        }
        return result + " )";
    }
}
