package TextMiningEngine.Witch.Clustering;


import SearchEngine.Assassin.Util.Util;
import TextMiningEngine.Witch.Index.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by amaliujia on 15-1-23.
 */
public class BipartiteClustering {

    private ClusteringIndex index;

    private static final double threshold = 0.00001;

    public BipartiteClustering(Map<String, String> params, boolean creatable){
        index = new ClusteringIndex();
        readInvLists(params);
        readDf(params);
        readDict(params);
        index.beginIndexing(creatable);

        //start bipartite clustering
        BipartiteClusteringByKMean_version2(index.matrix, 5, 5, ClusteringVectorType.DOCUMENT ,ClusteringVectorType.WORD);

    }

    /**
     * Bipartite clustering for row vectors and column vectors.
     * @param matrix
     *          Source matrix.
     * @param k1
     *          Number of clusters for row vectors.
     * @param k2
     *          Number of clusters for column vectors.
     * @return
     *          List of clusters of row vectors and column vectors, respectively.
     */
    public List<List<Cluster>> BipartiteClusteringByKMean_version2(ClusteringMatrix matrix, int k1, int k2,
                                                          ClusteringVectorType type1, ClusteringVectorType type2){
        List<Cluster> wordClusters = new ArrayList<Cluster>();
        List<Cluster> documentClusters = new ArrayList<Cluster>();

        WordToCluster word2cluster = new WordToCluster(matrix.getColumnVecSpaceSize());
        DocumentToCluster document2cluster = new DocumentToCluster(matrix.getRowVecSpaceSize());

        for(int i = 0; i < k2; i++){
            int ran = randInt(0, matrix.getColumnVecSpaceSize() - 1);
            wordClusters.add(new Cluster(type2, matrix.getColumnVectors().get(ran)));
        }
        // first word clustering
        wordClusters = KMean(matrix.getColumnVectors(), wordClusters);
        // build word to cluster linkage
        word2cluster.updateLinkage(matrix, wordClusters);
        updateCentroids(wordClusters);

        ClusteringMatrix intermediateMatrix = new ClusteringMatrix(matrix);
        intermediateMatrix.updateVectorSpace(word2cluster.getWeights(), ClusteringVectorType.DOCUMENT);

        for(int i = 0; i < k1; i++){
            int ran = randInt(0, matrix.getRowVecSpaceSize() - 1);
            documentClusters.add(new Cluster(type1, matrix.getRowVectors().get(ran)));
        }
        documentClusters = KMean(intermediateMatrix.getRowVectors(), documentClusters);
        document2cluster.updateLinkage(intermediateMatrix, documentClusters);
        updateCentroids(documentClusters);

        for(int j = 0; j < 20; j++){
            intermediateMatrix.copyColumnVectors(matrix);
            intermediateMatrix.updateVectorSpace(document2cluster.getWeights(), ClusteringVectorType.WORD);

            wordClusters = KMean(intermediateMatrix.getColumnVectors(), wordClusters);
            word2cluster.updateLinkage(intermediateMatrix, wordClusters);
            updateCentroids(wordClusters);

            intermediateMatrix.copyColumnVectors(matrix);
            intermediateMatrix.updateVectorSpace(word2cluster.getWeights(), ClusteringVectorType.DOCUMENT);
            documentClusters = KMean(intermediateMatrix.getRowVectors(), documentClusters);
            document2cluster.updateLinkage(intermediateMatrix, documentClusters);
            updateCentroids(documentClusters);
        }

        List<List<Cluster>> result = new ArrayList<List<Cluster>>();
        result.add(wordClusters);
        result.add(documentClusters);
        return result;
    }



    /**
     * Compute word2cluster or doc2cluster weight.
     * @param cluster
     *          cluster to which vec is belong.
     * @param vec
     *          vector occurs in one cluster.
     * @return
     */
    private double clusterRelationship(Cluster cluster, ClusteringInvList vec){
        return 0;
    }

    /**
     * Multi-iteration KMean.
     * @param vectors
     *        Matrix.
     * @param k
     *          Number of clusters
     * @return
     *      clusters.
     */
    public List<Cluster> KMeanIterations(List<ClusteringInvList> vectors, int k, ClusteringVectorType type){
        int len = vectors.size();
        List<Cluster> clusters = new ArrayList<Cluster>();

        for(int i = 0; i < k; i++){
            int ran = randInt(0, len - 1);
            clusters.add(new Cluster(type, vectors.get(ran)));
        }

        double preInterval = Double.MAX_VALUE;

        for(int i = 0; i < 1000000000; i++){
            ArrayList<ClusteringInvList> preCentroids = new ArrayList<ClusteringInvList>();
            for(Cluster c : clusters){
                preCentroids.add(c.centroid());
            }
            clusters = KMean(vectors, clusters);
            for(int j = 0; j < clusters.size(); j++){
                updateCentroid(clusters.get(j));
            }

            ArrayList<ClusteringInvList> curCentroids = new ArrayList<ClusteringInvList>();
            for(Cluster c : clusters){
                curCentroids.add(c.centroid());
            }
            if ((preInterval = isStable(preCentroids, curCentroids, preInterval)) == 0){
                System.err.println(i);
                break;
            }
            if(i % 10 == 0)
                 System.err.println(i);
        }

        return clusters;
    }

    /**
     *  KMean algorithm for one iteration.
     * @param vectors
     *          matrix waiting to be clustered.
     * @param clusters
     *          clusters used for provides centroids and collect vectors.
     * @return
     *          clusters.
     */
    public List<Cluster> KMean(List<ClusteringInvList> vectors, List<Cluster> clusters){
        for(int i = 0; i < clusters.size(); i++){
            Cluster cluster = clusters.get(i);
            cluster.clearVec();
        }


        for(int i = 0; i < vectors.size(); i++){
            int cur = -1;
            double min = Double.MAX_VALUE;

            for(int j = 0; j < clusters.size(); j++){
               double cos = CosineSimilarity(vectors.get(i), clusters.get(j).centroid());
                if(cos < min){
                    cur = j;
                    min = cos;
                }
            }
            clusters.get(cur).addVec(vectors.get(i));
        }
        return clusters;
    }

    /**
     * recompute cluster centroid.
     * @param cluster
     *          target cluster.
     */
    private void updateCentroid(Cluster cluster){
        Map<Integer, Double> features = new TreeMap<Integer, Double>();
        int totalSize = cluster.clusterSize();

        for (int i = 0; i < cluster.clusterSize(); i++){
            ClusteringInvList vec = cluster.getVec(i);
            for(int j = 0; j < vec.getPostingSize(); j++){
                int id = vec.getID(j);
                if(features.containsKey(id)){
                    features.put(id, vec.getWeight(j) + features.get(id));
                }else {
                    features.put(id, vec.getWeight(j));
                }
            }
        }

        ClusteringInvList centroid = new ClusteringInvList();
        Iterator<Integer> it = features.keySet().iterator();
        while (it.hasNext()){
            int id = it.next();
            centroid.addPosting(id, features.get(id) / totalSize);
        }
        cluster.setCentroid(centroid);
    }

    /**
     * Compute similarity between two vectors.
     * @param vec1
     *          vector one.
     * @param vec2
     *          vector two.
     * @return
     *          cosine similarity between vector one and vector two.
     */
    public static double CosineSimilarity(ClusteringInvList vec1, ClusteringInvList vec2){
        double result = 0;
        while(vec1.nextPos < vec1.getPostingSize() && vec2.nextPos < vec2.getPostingSize()){
            if(vec1.currentWord() == vec2.currentWord()){
                result += (vec1.currentTf() * vec2.currentTf());
                vec1.nextPos++;
                vec2.nextPos++;
            }else if(vec1.currentWord() < vec2.currentWord()){
               vec1.nextPos++;
            }else{
                vec2.nextPos++;
            }
        }

        vec1.nextPos = 0;
        vec2.nextPos = 0;

        double t = vec1.vectorNorm() * vec2.vectorNorm();

        if(t == 0){
            return result;
        }

        return result / t;
    }

    /**
     * produce a random integer between min and max.
     * @param min
     *          left edge of random edge.
     * @param max
     *          right edge of random edge.
     * @return
     *          random integer.
     */
    private static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    /**
     * Judge if algorithm converge.
     * @param preCentroid
     *           centroid of cluster in last iteration.
     * @param curCentroids
     *          centroid of cluster in current iteration.
     * @param preInternal
     *
     * @return
     *         if stable, return 0,
     *         else return current interval.
     */
    private double isStable(List<ClusteringInvList> preCentroid, List<ClusteringInvList> curCentroids, double preInternal){
        double interval = 0.0;

        for(int i = 0; i < preCentroid.size(); i++){
            interval += distanceNorm(preCentroid.get(i), curCentroids.get(i));
        }

       // interval = interval / preCentroid.size();
        System.out.println(interval);

        if(Math.abs(interval - preInternal) < threshold){
            return 0;
        }
        return interval;
    }

    /**
     * Compute distance between two vectors.
     * @param vec1
     *          vec1
     * @param vec2
     *          vec2
     * @return
     *          distance between two vectors.
     */
    private double distanceNorm(ClusteringInvList vec1, ClusteringInvList vec2){
        double result = 0;
        while(vec1.nextPos < vec1.getPostingSize() && vec2.nextPos < vec2.getPostingSize()){
            if(vec1.currentWord() == vec2.currentWord()){
                double d1 = vec1.currentTf();
                double d2 = vec2.currentTf();
                result += Math.pow(vec1.currentTf() - vec2.currentTf(), 2);
                vec1.nextPos++;
                vec2.nextPos++;
            }else if(vec1.currentWord() < vec2.currentWord()){
                double d1 = vec1.currentTf();
                result += Math.pow(vec1.currentTf(), 2);
                vec1.nextPos++;
            }else{
                double d2 = vec2.currentTf();
                result += Math.pow(vec2.currentTf(), 2);
                vec2.nextPos++;
            }
        }

        vec1.nextPos = 0;
        vec2.nextPos = 0;

        return Math.sqrt(result);
    }

    /**
     *
     * @param params
     */
    private void readDf(Map<String, String> params){
        if(!params.containsKey("bi:devdf")){
            Util.fatalError("bi:devdf does not exist");
        }

        String path = params.get("bi:devdf");
        try {
            Scanner scanner = new Scanner(new File(path));
            String line;
            while (scanner.hasNext()){
                line = scanner.nextLine();
                String[] args = line.split(":");
                if(args.length != 2){
                    System.err.println(args);
                }else{
                    index.addTermFreq(Integer.parseInt(args[1]));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param params
     */
    private void readDict(Map<String, String> params){
        if(!params.containsKey("bi:devdic")){
            Util.fatalError("bi:devdic does not exist");
        }

        String path = params.get("bi:devdic");
        try {
            Scanner scanner = new Scanner(new File(path));
            String line;
            while (scanner.hasNext()){
                line = scanner.nextLine();
                String[] args = line.split(" ");
                if(args.length != 2){
                    System.err.println(args);
                }else{
                    index.addDictWord(args[0], Integer.parseInt(args[1]));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param params
     */
    private void readInvLists(Map<String, String> params){
        if(!params.containsKey("bi:devdocVectors")){
            Util.fatalError("bi:devdocVectors does not exist");
        }

        String path = params.get("bi:devdocVectors");
        try {
            Scanner scanner = new Scanner(new File(path));
            String line;
            ClusteringInvList invList;
            while (scanner.hasNext()) {
                line = scanner.nextLine();
                String[] args = line.split(" ");
                invList = new ClusteringInvList();
                for (int i = 0; i < args.length; i++) {
                    String[] ss = args[i].split(":");
                    invList.addPosting(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]));
                }
                index.addinvList(invList);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        index.sortInvList();
    }

    /**
     *
     * @param clusters
     */
    private void updateCentroids(List<Cluster> clusters){
        for (Cluster cluster : clusters){
            updateCentroid(cluster);
        }
    }

//    private void printResult(List<>){
//
//    }
}


