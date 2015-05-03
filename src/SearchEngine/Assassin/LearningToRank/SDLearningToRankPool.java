package SearchEngine.Assassin.LearningToRank;

import SearchEngine.Assassin.Lucene.TermVector;
import SearchEngine.Assassin.QryEval;
import SearchEngine.Assassin.RetrievalModel.RetrievalModel;
import SearchEngine.Assassin.RetrievalModel.RetrievalModelLearningToRank;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by amaliujia on 14-11-12.
 */
public class SDLearningToRankPool {

    private final int NUM_FEATURES = 18;

    // save page rank scores
    //private HashMap<Integer, Double> pageRank;
    private HashMap<String, Double> pageRank;
    //private String[] terms;
    private ArrayList<String> terms;
    private ArrayList<Boolean> disableSetting;


    /**
     * Constructor for learning to rank pool
     */
    public SDLearningToRankPool() {
        terms = new ArrayList<String>();
        disableSetting = new ArrayList<Boolean>();
        for(int i = 0; i < 18; i++) {
            disableSetting.add(true);
        }
    }

    /**
     * Produce normalized feature vectors for a query and a set of docs
     * @param r
     *          Retrieval model provides necessary parameters.
     * @param docs
     *          docs id.
     * @return
     *          feature vectors in string
     */
    public ArrayList<String> produceNormalizedFeatureVector(RetrievalModel r,
            ArrayList<Integer> docs) {

        ArrayList<SDFeatureVector> vectors = new ArrayList<SDFeatureVector>();

        for(int c = 0; c < docs.size(); c++) {
            try {
                SDFeatureVector featureVector = produceFeatureVector(r, docs.get(c));
                vectors.add(featureVector);
            } catch (Exception e) {
                System.out.println("feature vector produce error");
                continue;
            }
        }

        for(int i = 0; i < NUM_FEATURES; i++) {
            normalizeFeature(vectors, i);
        }

        ArrayList<String> result = new ArrayList<String>();
        for(int i = 0; i <vectors.size(); i++) {
            String s;
            SDFeatureVector v = vectors.get(i);
            s = " 1:" + v.getFeature(0) + " 2:" + v.getFeature(1) + " 3:" + v.getFeature(2) +
                " 4:" + v.getFeature(3) + " 5:" + v.getFeature(4) + " 6:" + v.getFeature(5) +
                " 7:" + v.getFeature(6) + " 8:" + v.getFeature(7) + " 9:" + v.getFeature(8) +
                " 10:" + v.getFeature(9) + " 11:" + v.getFeature(10) + " 12:" + v.getFeature(11) +
                " 13:" + v.getFeature(12) + " 14:" + v.getFeature(13) + " 15:" + v.getFeature(14) +
                " 16:" + v.getFeature(15) + " 17:" + v.getFeature(16) + " 18:" + v.getFeature(17);
            result.add(s);
        }

        return result;
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
    public SDFeatureVector produceFeatureVector(RetrievalModel r, int docid)
    throws IOException {
        SDFeatureVector featureVector = new SDFeatureVector();

        Document d = QryEval.READER.document(docid);

        //int spamscore = 0;
        if(disableSetting.get(0)) {
            featureVector.spamscore = Integer.parseInt(d.get("score"));
            featureVector.features.set(0, featureVector.spamscore);
        }

        String rawUrl = d.get("rawUrl");

        if(disableSetting.get(1)) {
            featureVector.urlDepth = getDepth(rawUrl);
            featureVector.features.set(1, featureVector.urlDepth);
        }

        //int wiki = 0;
        if(disableSetting.get(2)) {
            featureVector.wiki = getWikipediaScore(rawUrl);
            featureVector.features.set(2, featureVector.wiki);
        }

        //double pageRankScrore = 0.0;
        if(disableSetting.get(3)) {
//            try {
//                featureVector.pageRankScrore = pageRank.get(QryEval.getExternalDocid(docid));
//            }catch (Exception e){
//               // System.out.println("No page rank score");
//                featureVector.pageRankScrore = -1.0;
//            }
//            featureVector.features.set(3, featureVector.pageRankScrore);
            String key = QryEval.getExternalDocid(docid);
            if(pageRank.containsKey(key)) {
                featureVector.pageRankScrore = pageRank.get(key);
                featureVector.features.set(3, featureVector.pageRankScrore);
            }
        }

        String field = "body";
        TermVector vector = null;
        try {
            vector = new TermVector(docid, field);
        } catch (Exception e) {
            // System.out.println("Term vector is not indexed");
        }

        //double BM25Body = 0.0;

        if(disableSetting.get(4) && vector != null) {
            featureVector.BM25Body = vectorSpaceBM25(r, vector, field, docid);
            featureVector.features.set(4, featureVector.BM25Body);
        }

        // double IndriBody = 0.0;
        if(disableSetting.get(5) && vector != null ) {
            featureVector.IndriBody = vectorSapceIndri(r, vector, field, docid);
            featureVector.features.set(5, featureVector.IndriBody);
        }

        if(disableSetting.get(6) && vector != null ) {
            featureVector.overlapBody = termOverlapping(vector);
            featureVector.features.set(6, featureVector.overlapBody);
        }

        field = "title";
        vector = null;
        try {
            vector = new TermVector(docid, field);
        } catch (Exception e) {
            //  System.out.println("Term vector is not indexed");
        }
        // double BM25Title = 0.0;
        if(disableSetting.get(7) && vector != null) {
            featureVector.BM25Title = vectorSpaceBM25(r, vector, field, docid);
            featureVector.features.set(7, featureVector.BM25Title);
        }

        //double IndriTitle = 0.0;
        if(disableSetting.get(8) && vector != null) {
            featureVector.IndriTitle = vectorSapceIndri(r, vector, field, docid);
            featureVector.features.set(8, featureVector.IndriTitle);
        }

        if(disableSetting.get(9) && vector != null) {
            featureVector.overlapTitle = termOverlapping(vector);
            featureVector.features.set(9, featureVector.overlapTitle);
        }

        field = "url";
        vector = null;
        try {
            vector = new TermVector(docid, field);
        } catch (Exception e) {
            System.out.println("Term vector is not indexed");
        }
        // double BM25Url = 0.0;
        if(disableSetting.get(10) && vector != null) {
            featureVector.BM25Url = vectorSpaceBM25(r, vector, field, docid);
            featureVector.features.set(10, featureVector.BM25Url);
        }

        //double IndriUrl = 0.0;
        if(disableSetting.get(11) && vector != null) {
            featureVector.IndriUrl = vectorSapceIndri(r, vector, field, docid);
            featureVector.features.set(11, featureVector.IndriUrl);
        }

        if(disableSetting.get(12) && vector != null) {
            featureVector.overlapUrl = termOverlapping(vector);
            featureVector.features.set(12, featureVector.overlapUrl);
        }

        field = "inlink";
        vector = null;
        try {
            vector = new TermVector(docid, field);
        } catch (Exception e) {
            //  System.out.println("Term vector is not indexed");
        }

        if(disableSetting.get(13) && vector != null) {
            featureVector.BM25Inlink = vectorSpaceBM25(r, vector, field, docid);
            featureVector.features.set(13, featureVector.BM25Inlink);
        }

        // double IndriLink = 0.0;
        if(disableSetting.get(14) && vector != null) {
            featureVector.IndriInlink = vectorSapceIndri(r, vector, field, docid);
            featureVector.features.set(14, featureVector.IndriInlink);
        }

        if(disableSetting.get(15) && vector != null) {
            featureVector.overlaplink = termOverlapping(vector);
            featureVector.features.set(15, featureVector.overlaplink);
        }


        // tf mean in all fields
        if(disableSetting.get(16) && vector != null) {
            featureVector.tfMean = tfMean(docid);
            featureVector.features.set(16, featureVector.tfMean);
        }

        // url domain feature
        if(disableSetting.get(17)) {
            featureVector.domain = domainUrl(rawUrl);
            featureVector.features.set(17, featureVector.domain);
        }

        return featureVector;
    }


    /**
     * splite current query into terms
     * @param query
     */
    public void addQuery(String query) throws IOException {
        int i = 0;
        this.terms.clear();
        String[] buffer = query.split(" ");

        for(int j = 0; j < buffer.length; j++) {
            String[] c = QryEval.tokenizeQuery(buffer[j]);
            if(c.length != 0) {
                this.terms.add(c[0]);
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

        for(int i = 0; i < rawUrl.length(); i++) {
            if(rawUrl.charAt(i) == '/') {
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
    private int getWikipediaScore(String rawUrl) {
        if(rawUrl.indexOf("wikipedia.org") != -1) {
            return 1;
        }
        return 0;
    }

    public void setDisableSetting(String[] settings) {
        if(settings.length == 0)
            return;

        for(int i = 0; i < settings.length; i++) {
            disableSetting.set(Integer.parseInt(settings[i]) - 1, false);
        }
    }


    /**
     * Set page rank scores
     * @param scores
     *         page rank scores collection
     */
    public void setPageRank(HashMap<String, Double> scores) {
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

        for(int j = 0; j < v.stemsLength(); j++) {
            String stem = v.stemString(j);
            if(stem != null && !vocabulary.containsKey(stem)) {
                vocabulary.put(stem, (double)(v.stemFreq(j)));
            }
        }

        double b = RetrievalModelLearningToRank.b;
        double k1 = RetrievalModelLearningToRank.k1;
        double k3 = RetrievalModelLearningToRank.k3;
        double docLen = model.docLengthStore.getDocLength(field, docid);

        // compute BM25 for document.
        for(int i = 0; i < terms.size(); i++) {
            String curTerm = terms.get(i);
            if(vocabulary.containsKey(curTerm)) {
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
        int count = 0;
        // global data
        double collectionLen = QryEval.READER.getSumTotalTermFreq(field);
        RetrievalModelLearningToRank model = (RetrievalModelLearningToRank)r;
        double lenDoc = (model).docLengthStore.getDocLength(field, docid);

        // build vocabulary
        HashMap<String, Double> vocabulary = new HashMap<String, Double>();

        for(int j = 0; j < v.stemsLength(); j++) {
            String stem = v.stemString(j);
            if(stem != null && !vocabulary.containsKey(stem)) {
                vocabulary.put(stem, (double)(v.stemFreq(j)));
            }
        }

        // compute P(document, query)
        for(int i = 0; i < terms.size(); i++) {
            String curTerm = terms.get(i);
            double ctf = QryEval.READER.totalTermFreq(new Term(field, new BytesRef(curTerm)));
            double pmle = ctf / collectionLen;
            if(vocabulary.containsKey(curTerm)) {
                count++;
                double tf = vocabulary.get(curTerm);
                double score = (((model.lambda * (tf + (model.mu * pmle))) / (lenDoc + model.mu))) +
                               (((double)1 - model.lambda) * pmle);
                result *= Math.pow(score, (double)1 / (terms.size()));
            } else {
                double score = (((model.lambda * ((model.mu * pmle))) / (lenDoc + model.mu))) +
                               (((double)1 - model.lambda) * pmle);
                result *= Math.pow(score, (double)1 / (terms.size()));
            }
        }
        if(count == 0) {
            result = 0;
        }
        return result;
    }

    /**
     * Normalize a specific feature and let range of feature between 0 and 1.
     * @param vectors
     */
    private void normalizeFeature(ArrayList<SDFeatureVector> vectors, int feature) {
        if(!disableSetting.get(feature)) {
            for(int i = 0; i < vectors.size(); i++) {
                vectors.get(i).features.set(feature, 0.0);
            }
            return;
        }

        // maximum and minimum;
        double maximum = Double.MIN_VALUE;
        double minimum = Double.MAX_VALUE;

        // find maximum and minimum by traversal all features.
        for(int i = 0; i < vectors.size(); i++) {
            if(vectors.get(i).getFeature(feature) != -1) {
                if (vectors.get(i).getFeature(feature) > maximum) {
                    maximum = vectors.get(i).getFeature(feature);
                }

                if (vectors.get(i).getFeature(feature) < minimum) {
                    minimum = vectors.get(i).getFeature(feature);
                }
            }
        }

        double divisor = maximum - minimum;
        if(divisor == 0) {
            for(int i = 0; i < vectors.size(); i++) {
                SDFeatureVector featureVector= vectors.get(i);
                featureVector.features.set(feature, divisor);
            }
            return;
        }

        // normalize feature
        for(int i = 0; i < vectors.size(); i++) {
            if(vectors.get(i).features.get(feature) == -1) {
                vectors.get(i).features.set(feature, 0.0);
            } else {
                SDFeatureVector featureVector = vectors.get(i);
                featureVector.features.set(feature, (featureVector.getFeature(feature) - minimum) / divisor);
            }
        }
        return;
    }

    /**
     * Compute term overlapping percentage for terms vectors and queries
     * @param termVector
     *          target term vector
     * @return
     *          overlap score
     */
    private double termOverlapping(TermVector termVector) {
        int count = 0;
        double queryLength = terms.size();

        for(int i = 0; i < terms.size(); i++) {
            String curTerm = terms.get(i);
            if(termVector.containStem(curTerm)) {
                count++;
            }
        }
        return ((double)count) / queryLength;
    }


    /**
     *
     * @param docid
     * @return
     */
    private double tfMean(int docid) {
        double tf = 0;

        tf += tfBasedOnField(docid, "body");
        tf += tfBasedOnField(docid, "url");
        tf += tfBasedOnField(docid, "title");
        tf += tfBasedOnField(docid, "inlink");

        return tf / (double)(terms.size() * 4);
    }

    /**
     *
     * @param docid
     * @param field
     * @return
     */
    private double tfBasedOnField(int docid, String field) {
        double tf = 1.0;
        TermVector vector = null;
        try {
            vector = new TermVector(docid, field);
        } catch (Exception e) {
            System.out.println("Term vector is not indexed");
        }
        if(vector == null) return 0;

        for(int i = 0; i < terms.size(); i++) {
            int curTf = vector.getStemTF(terms.get(i));
            if (curTf != -1) {
                tf += curTf;
            }
        }
        return tf;
    }

    /**
     *
     * @param url
     * @return
     */
    private int domainUrl(String url) {
        if(url.indexOf(".edu") != -1) {
            return 1;
        }

        if(url.indexOf(".gov") != -1) {
            return 1;
        }
        return 0;
    }


}