package SearchEngine.Assassin;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


/**
 * Created by amaliujia on 14-11-12.
 */
public class SDLearningToRankPool {

    // save page rank scores
    private HashMap<Integer, Double> pageRank;
    private String[] terms;
    private ArrayList<Boolean> disableSetting;


    /**
     * Constructor for learning to rank pool
     */
    public SDLearningToRankPool(){
        disableSetting = new ArrayList<Boolean>();
        for(int i = 0; i < 18; i++){
            disableSetting.add(true);
        }
    }


    /**
     *
     * @param r
     * @param docid
     * @param docs
     * @return
     */
    public ArrayList<String> produceNormalizedFeatureVector(RetrievalModel r,
                                                            int docid, ArrayList<Integer> docs){

        for(int c = 0; c < docs.size(); c++){

        }

        return null;
    }



    /**
     * This fucntion will undertake the task in which a well defined
     * featrue vector need be constructed.
     * @param r
     *          learning to rank model
     * @param docid
     *          internal key for current document
     * @return
     *          features vector in string format
     * @throws IOException
     *          throws when indexreader throws an exception
     */
    public String produceFeatureVector(RetrievalModel r, int docid)
                                               throws IOException {

        Document d = QryEval.READER.document(docid);

        int spamscore = 0;
        if(disableSetting.get(0)) {
            spamscore = Integer.parseInt(d.get("score"));
        }

        String rawUrl = d.get("rawUrl");
        int urlDepth = 0;

        if(disableSetting.get(1)) {
            urlDepth = getDepth(rawUrl);
        }

        int wiki = 0;
        if(disableSetting.get(2)){
            wiki = getWikipediaScore(rawUrl);
        }

        double pageRankScrore = 0.0;
        if(disableSetting.get(3)){
           pageRankScrore = pageRank.get(docid);
        }

        String field = "body";
        TermVector vector = new TermVector(docid, field);
        double BM25Body = 0.0;
        if(disableSetting.get(4)) {
            BM25Body = vectorSpaceBM25(r, vector, field, docid);
        }
        double IndriBody = 0.0;
        if(disableSetting.get(5)){
            IndriBody = vectorSapceIndri(r, vector, field, docid);
        }

        // TODO: overlap get 6

        field = "title";
        vector = new TermVector(docid, field);
        double BM25Title = 0.0;
        if(disableSetting.get(7)) {
           BM25Title = vectorSpaceBM25(r, vector, field, docid);
        }

        double IndriTitle = 0.0;
        if(disableSetting.get(8)){
            IndriTitle = vectorSapceIndri(r, vector, field, docid);
        }


        // TODO: overlap get 10

        field = "url";
        vector = new TermVector(docid, field);
        double BM25Url = 0.0;
        if(disableSetting.get(10)){
           BM25Url = vectorSpaceBM25(r, vector, field, docid);;
        }

        double IndriUrl = 0.0;
        if(disableSetting.get(11)){
            IndriUrl = vectorSapceIndri(r, vector, field, docid);
        }

        // TODO: overlap

        field = "inlink";
        vector = new TermVector(docid, field);
        double BM25Inlink = 0.0;

        if(disableSetting.get(13)){
            BM25Inlink = vectorSpaceBM25(r, vector, field, docid);
        }

        double IndriLink = 0.0;
        if(disableSetting.get(14)){
            IndriLink = vectorSapceIndri(r, vector, field, docid);
        }

        // TODO: overlap


        // TODO: two custom features

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
     *        rawUrl in string
     * @return
     *        url depth
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
     * From Wikipedia score for d (1 if the rawUrl contains
     * "wikipedia.org", otherwise 0)
     * @param rawUrl
     *          rawUrl in string
     * @return
     *         if it is wikipedia
     */
    private int getWikipediaScore(String rawUrl){
            if(rawUrl.contains(rawUrl)) {
                 return 1;
            }
            return 0;
        }

    public void setDisableSetting(String[] settings){
        if(settings.length == 0)
            return;

        for(int i = 0; i < settings.length; i++){
            disableSetting.set(Integer.parseInt(settings[i]) - 1, false);
        }
    }


    /**
     * Set page rank scores
     * @param scores
     *         page rank scores collection
     */
    public void setPageRank(HashMap<Integer, Double> scores) {
        this.pageRank = scores;
    }

    /**
     * Compute BM25 based on term vector
     * @param r
     *      learning to rank model
     * @param v
     *      term vector
     * @param field
     *      current field
     * @return
     *      BM25 score for current document in specific field
     */
    private double vectorSpaceBM25(RetrievalModel r, TermVector v, String field, int docid)
                                                                      throws IOException {

        RetrievalModelLearningToRank model = (RetrievalModelLearningToRank)r;
        double result = 0.0;

        //necessary value
        int N = model.numDocs;
        double avgLen = (double)QryEval.READER.getSumTotalTermFreq(field) /
                        (double)QryEval.READER.getDocCount(field);

        // build vocabulary
        HashMap<String, Double> vocabulary = new HashMap<String, Double>();

        for(int j = 0; j < v.stemsLength(); j++){
            String stem = v.stemString(j);
            if(stem != null && !vocabulary.containsKey(stem)){
                vocabulary.put(stem, (double)(v.stemFreq(j)));
            }
        }

        double b = model.b;
        double k1 = model.k1;
        double k3 = model.k3;
        double docLen = model.docLengthStore.getDocLength(field, docid);

        // compute BM25 for document.
        for(int i = 0; i < terms.length; i++){
            String curTerm = terms[i];
            if(vocabulary.containsKey(curTerm)){
                //TODO: df for a specific field may not exit.
                double df = QryEval.READER.docFreq(new Term(field, new BytesRef(curTerm)));
                double tf = vocabulary.get(curTerm);
                double a = Math.log((N - df + 0.5) / (df + 0.5));
                double c = (tf / (tf + k1 * ((1.0 - b) + b * (docLen / avgLen))));
                double d = ((k3 + 1.0) * 1.0) / (k3 + 1.0);
                double docScore = a * c * d;
                result += docScore;
            }
        }
        return result;
    }

    /**
     * Compute Indri score based on term vector
     * @param r
     *          learning to rank model
     * @param v
     *          term vector
     * @param field
     *          current field
     * @return
     *      Indri score for current document in specific field
     */
    private double vectorSapceIndri(RetrievalModel r, TermVector v, String field, int docid)
                                                                        throws IOException {
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
            //TODO: ctf for a specific field may not exit.
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