package TextMiningEngine.Witch.Clustering;


import SearchEngine.Assassin.Util.Util;
import TextMiningEngine.Witch.Index.Cluster;
import TextMiningEngine.Witch.Index.ClusteringIndex;
import TextMiningEngine.Witch.Index.ClusteringInvList;
import TextMiningEngine.Witch.Index.ClusteringVectorType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by amaliujia on 15-1-23.
 */
public class BipartiteClustering {

    private ClusteringIndex index;

    private static final double threshold = 0.1;

    public BipartiteClustering(Map<String, String> params){
        index = new ClusteringIndex();
        readInvLists(params);
        readDf(params);
        readDict(params);
        index.beginIndexing(false);

        KMeanIterations(index.matrix.getColumnVectors(), 5);
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
     * @param vectors
     * @param k
     * @return
     */
    private List<Cluster> KMeanIterations(List<ClusteringInvList> vectors, int k){
        int len = vectors.size();
        List<Cluster> clusters = new ArrayList<Cluster>();

        for(int i = 0; i < k; i++){
            int ran = randInt(0, len - 1);
            clusters.add(new Cluster(ClusteringVectorType.DOCUMENT, vectors.get(ran)));
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
     *
     * @param vectors
     * @param clusters
     * @return
     */
    private List<Cluster> KMean(List<ClusteringInvList> vectors, List<Cluster> clusters){
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
    private double CosineSimilarity(ClusteringInvList vec1, ClusteringInvList vec2){
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

        return result / (vec1.vectorNorm() * vec2.vectorNorm());
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
}


