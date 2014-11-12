package SearchEngine.Assassin;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by amaliujia on 14-11-12.
 */
public class SDLearningToRankPool {

    // save page rank scores
    private HashMap<Integer, Double> pageRank;
    private String[] terms;

    public String produceFeatureVector(RetrievalModel r, int docid) throws IOException {

        Document d = QryEval.READER.document(docid);
        int spamscore = Integer.parseInt(d.get("score"));
        String rawUrl = d.get("rawUrl");

        int urlDepth = getDepth(rawUrl);
        int wiki = getWikipediaScore(rawUrl);
        double pageRankScrore = pageRank.get(docid);

        String field = "body";
        TermVector vector = new TermVector(docid, field);
        double BM25Body = vectorSpaceBM25(r, vector, field);
        double IndriBody = vectorSapceIndri(r, vector, field, docid);

        field = "title";
        vector = new TermVector(docid, field);
        double BM25Title = vectorSpaceBM25(r, vector, field);
        double IndriTitle = vectorSapceIndri(r, vector, field, docid);

        field = "url";
        vector = new TermVector(docid, field);
        double BM25Url = vectorSpaceBM25(r, vector, field);
        double IndriUrl = vectorSapceIndri(r, vector, field, docid);

        field = "inlink";
        vector = new TermVector(docid, field);
        double BM25Inlink = vectorSpaceBM25(r, vector, field);
        double IndrInlink = vectorSapceIndri(r, vector, field, docid);

        return "";
    }


    /**
     * splite current query into terms
     * @param query
     */
    void addQuery(String query) throws IOException {
        int i = 0;
        String[] buffer = query.split(" ");

        for(int j = 0; j < buffer.length; j++){
            String[] c = QryEval.tokenizeQuery(buffer[j]);
            if(c.length != 0){
                this.terms[i++] = c[0];
            }
        }
    }

    /**
     * Url depth for d(number of '/' in the rawUrl field)
     * @param rawUrl
     * @return
     */
    private int getDepth(String rawUrl) {
        int d = 0;

        for(int i = 0; i < rawUrl.length(); i++){
            if(rawUrl.charAt(i) == '/'){
                d++;
            }
        }
        return d;
    }

    /**
     * FromWikipedia score for d (1 if the rawUrl contains "wikipedia.org", otherwise 0)
     * @param rawUrl
     * @return
     */
    private int getWikipediaScore(String rawUrl){
            if(rawUrl.contains(rawUrl)) {
                 return 1;
            }
            return 0;
        }


    /**
     * Set page rank scores
     * @param scores
     */
    public void setPageRank(HashMap<Integer, Double> scores) {
        this.pageRank = scores;
    }

    /**
     * Compute BM25 based on term vector
     * @param r
     * @param v
     * @param field
     * @return
     */
    public double vectorSpaceBM25(RetrievalModel r, TermVector v, String field){

        return 0.0;
    }

    /**
     * Compute Indri score based on term vector
     * @param r
     * @param v
     * @param field
     * @return
     */
    public double vectorSapceIndri(RetrievalModel r, TermVector v, String field, int docid) throws IOException {
        double result = 1.0;

        // global data
        double collectionLen = QryEval.READER.getSumTotalTermFreq(field);
        RetrievalModelLearningToRank model = (RetrievalModelLearningToRank)r;
        double lenDoc = (model).docLengthStore.getDocLength(field, docid);
        // build vocabulary
        HashMap<String, Double> vocabulary = new HashMap<String, Double>();

        for(int j = 0; j < v.stemsLength(); j++){
            String stem = v.stemString(j);
            if(stem != null && !vocabulary.containsKey(stem)){
                vocabulary.put(stem, (double)(v.stemFreq(j)));
            }
        }

        // compute P(document, query)
        for(int i = 0; i < terms.length; i++){
            String curTerm = terms[i];
            double ctf = QryEval.READER.totalTermFreq(new Term(field, new BytesRef(curTerm)));
            double pmle = ctf / collectionLen;
            if(vocabulary.containsKey(curTerm)){
                double tf = vocabulary.get(curTerm);
                double score = (((model.lambda * (tf + (model.mu * pmle))) / (lenDoc + model.mu))) +
                        ((1 - model.lambda) * pmle);
                result *= Math.pow(score, 1 / (terms.length));
            }else{
                double score = (((model.lambda * ((model.mu * pmle))) / (lenDoc + model.mu))) +
                        ((1 - model.lambda) * pmle);
                result *= Math.pow(score, 1 / (terms.length));
            }
        }
        return result;
    }
}
