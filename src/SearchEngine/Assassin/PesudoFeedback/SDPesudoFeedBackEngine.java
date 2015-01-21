package SearchEngine.Assassin.PesudoFeedback;

import SearchEngine.Assassin.DataStructure.SortEntity;
import SearchEngine.Assassin.Lucene.TermVector;
import SearchEngine.Assassin.QryEval;
import SearchEngine.Assassin.Util.DataCenter;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.*;

/**
 * Created by amaliujia on 14-10-24.
 */
public class SDPesudoFeedBackEngine {

    private int termNum;
    private ArrayList<SortEntity> folder;
    private String queryID;
    private ArrayList<TermVector> termVectors;
    private String field = "body";
    private HashMap<String, Double> expansionTermMap;
    private double pesudoFeedbackMu;

    /**
     * Initialze Pesudo Feedback Engine
     * @param termNum
     *
     * @param result
     *
     * @param queryID
     */
    public SDPesudoFeedBackEngine(int termNum, ArrayList<SortEntity> result, String queryID, Double mu) {
        this.termNum = termNum;
        folder = result;
        this.queryID = queryID;
        expansionTermMap = new HashMap<String, Double>();
        this.pesudoFeedbackMu = mu;
    }

    /**
     *  Feedback algorithm
     */
    public String SDFeedback() throws IOException {
        termVectors = new ArrayList<TermVector>();
        for(int i = 0; i < folder.size(); i++) {
            SortEntity entity = folder.get(i);
            try {
                TermVector vector = new TermVector(entity.getInternalDocID(), field);
                termVectors.add(vector);
            } catch (IOException e) {
                System.out.println("Failed to fetch term vector");
                continue;
            }
        }

        HashMap<Integer, Double> docWeightMap = new HashMap<Integer, Double>();
        for(int i = 0; i < folder.size(); i++) {
            docWeightMap.put(folder.get(i).getInternalDocID(), folder.get(i).getScore());
        }

        //record all terms in a hashmap

        for(int i = 0 ; i < termVectors.size(); i++) {
            TermVector vec = termVectors.get(i);
            for(int j = 0; j < vec.stemsLength(); j++) {
                String stem = vec.stemString(j);
                if(stem != null && !expansionTermMap.containsKey(stem)) {
                    expansionTermMap.put(stem, 0.0);
                }
            }
        }

        double collectionLen = QryEval.READER.getSumTotalTermFreq(field);

        for(int i = 0; i < folder.size(); i++) {
            // System.out.println("folder: " + i);
            TermVector vec = termVectors.get(i);
            HashMap<String, Double> termMap = new HashMap<String, Double>();
            for (int z = 0; z < vec.stemsLength(); z++) {
                String stem = vec.stemString(z);
                if (stem != null && !termMap.containsKey(stem)) {
                    termMap.put(stem, (double) vec.stemFreq(z));
                }
            }
            // double lenDoc = vec.positionsLength();
            double lenDoc = DataCenter.sharedDataCenter().docLengthStore.getDocLength("body",
                            folder.get(i).getInternalDocID());
            double docWeight = folder.get(i).getScore();

            //iterate vocabulary
            Iterator it = expansionTermMap.keySet().iterator();
            while (it.hasNext()) {
                String stem = (String) it.next();
                if (termMap.containsKey(stem)) {
                    double o = expansionTermMap.get(stem);
                    //double ctf = (double)vec.totalStemFreq(j);
                    double ctf = QryEval.READER.totalTermFreq(new Term("body", new BytesRef(stem)));
                    double tfidf = Math.log(collectionLen / ctf);
                    double pmle = ctf / collectionLen;
                    double p = (termMap.get(stem) + pesudoFeedbackMu * pmle) / (lenDoc + pesudoFeedbackMu);
                    o += (p * tfidf * docWeight);
                    expansionTermMap.put(stem, o);
                } else {
                    double o = expansionTermMap.get(stem);
                    //double ctf = (double)vec.totalStemFreq(j);
                    double ctf = QryEval.READER.totalTermFreq(new Term("body", new BytesRef(stem)));
                    double tfidf = Math.log(collectionLen / ctf);
                    double pmle = ctf / collectionLen;
                    double p = (pesudoFeedbackMu * pmle) / (lenDoc + pesudoFeedbackMu);
                    o += (p * tfidf * docWeight);
                    expansionTermMap.put(stem, o);
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
        String result = "#WAND ( ";
        for(int z = 0; z < termNum; z++) {
            if(sortedList.get(z).getKey().indexOf('.') != -1 ||
                    sortedList.get(z).getKey().indexOf(',') != -1) {
                termNum++;
                continue;
            }
            result += (sortedList.get(z).getValue() + " " + sortedList.get(z).getKey() + " ");
        }
        return result + " )";
    }
}
