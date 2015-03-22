package SearchEngine.Assassin.LearningToRank;

import SearchEngine.Assassin.DataStructure.SortEntity;
import SearchEngine.Assassin.Operators.QryResult;
import SearchEngine.Assassin.Operators.Qryop;
import SearchEngine.Assassin.QryEval;
import SearchEngine.Assassin.RetrievalModel.RetrievalModel;
import SearchEngine.Assassin.RetrievalModel.RetrievalModelBM25;
import SearchEngine.Assassin.Util.DataCenter;
import SearchEngine.Assassin.Util.Util;

import java.io.*;
import java.util.*;

/**
 * Created by amaliujia on 15-3-22.
 */
public class SDLearningToRankEngine {
    private Map<String, String> params;

    private BufferedWriter featuresWriter;

    private RetrievalModel model;

    private List<String> keys;

    private List<String> queries;

    public SDLearningToRankEngine(Map<String, String > params, RetrievalModel model,
                                  List<String> keys, List<String> queries){
        this.params = params;
        this.model = model;
        this.keys = keys;
        this.queries = queries;
    }

    public void run() throws Exception {
        //initialization
        String traningFilePath = params.get("letor:trainingFeatureVectorsFile");
        String learningModelPath = params.get("letor:svmRankLearnPath");
        String FEAT_GEN = params.get("letor:svmRankParamC");
        String modelOutputFile = params.get("letor:svmRankModelFile");
        featuresWriter = new BufferedWriter(new FileWriter(new File(traningFilePath)));

        //preprocessing relevant documents
        HashMap<String, ArrayList<String>> revelanceMap = new HashMap<String, ArrayList<String>>();
        File relevanceFiles = new File(params.get("letor:trainingQrelsFile"));
        Scanner s;
        try {
            s= new Scanner(relevanceFiles);
        } catch (FileNotFoundException e) {
            System.out.println("No relevance judgement file");
            e.printStackTrace();
            return;
        }

        while(s.hasNext()) {
            String currLine = s.nextLine();
            String currKey = currLine.split(" ")[0];
            if(revelanceMap.containsKey(currKey)) {
                ArrayList<String> queryDocs = revelanceMap.get(currKey);
                queryDocs.add(currLine);
                revelanceMap.put(currKey, queryDocs);
            } else {
                ArrayList<String> queryDocs = new ArrayList<String>();
                queryDocs.add(currLine);
                revelanceMap.put(currKey, queryDocs);
            }
        }

        // get disable setting
        String[] settings = new String[0];
        if(params.containsKey("letor:featureDisable")) {
            String disableSetting = params.get("letor:featureDisable");
            settings = disableSetting.split(",");
        }

        //get page rank score
        //HashMap<Integer, Double> pageRank = new HashMap<Integer, Double>();
        HashMap<String, Double> pageRank = new HashMap<String, Double>();
        String pageRankFile = params.get("letor:pageRankFile");
        if(pageRankFile == null) {
            throw new Exception("PageRank file doesn't exist!");
        }
        Scanner scanner = new Scanner(new File(pageRankFile));

        while(scanner.hasNext()) {
            String cur = scanner.nextLine();
            String[] fileScore = cur.split("\t");
            // int id = getInternalDocid(fileScore[0]);
            pageRank.put(fileScore[0], Double.parseDouble(fileScore[1]));
        }

        //read training queies
        FileInputStream trainingFile = new FileInputStream(params.get("letor:trainingQueryFile"));
        InputStreamReader traningFileReader = new InputStreamReader(trainingFile);
        BufferedReader traningBufferReader = new BufferedReader(traningFileReader);
        ArrayList<String> traningQueries = new ArrayList<String>();
        ArrayList<String> traningKeys = new ArrayList<String>();

        // read traning queries
        String q;
        while((q = traningBufferReader.readLine()) != null) {
            String[] pair = q.split(":");
            traningQueries.add(pair[1]);
            traningKeys.add(pair[0]);
        }
        trainingFile.close();

        //start to build features vectors for documents of each query
        SDLearningToRankPool pool = new SDLearningToRankPool();
        pool.setPageRank(pageRank);
        pool.setDisableSetting(settings);

        for (int i = 0; i < traningKeys.size(); i++) {
            String key = traningKeys.get(i);
            String query = traningQueries.get(i);

            pool.addQuery(query);

            if(!revelanceMap.containsKey(key)) {
                throw new NoSuchElementException("Not relevance judgement files for query " + key + " " + query);
            }

            ArrayList<String> docs = revelanceMap.get(key);
            ArrayList<Integer> docsInternalIds = new ArrayList<Integer>();
            ArrayList<Integer> docsRelevance = new ArrayList<Integer>();
            for(int j = 0; j < docs.size(); j++) {
                String[] docinfo = docs.get(j).split(" ");
                int docid = QryEval.getInternalDocid(docinfo[2]);
                docsInternalIds.add(docid);
                int relevance = Integer.parseInt(docinfo[3]);
                docsRelevance.add(relevance);
            }

            ArrayList<String> vs = pool.produceNormalizedFeatureVector(model, docsInternalIds);

            //write feature vector into target file
            for(int j = 0; j < docsInternalIds.size(); j++) {
                featuresWriter.write(docsRelevance.get(j) + " qid:" + key + vs.get(j)
                        + " # " + docs.get(j).split(" ")[2] + "\n");
            }
        }
        try {
            featuresWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // train SVM model
        trainSVM(learningModelPath, FEAT_GEN, traningFilePath, modelOutputFile);

        //Read test queries
        RetrievalModel modelBM25 = new RetrievalModelBM25();
        DataCenter.k1 = Double.parseDouble(params.get("BM25:k_1"));
        DataCenter.b = Double.parseDouble(params.get("BM25:b"));
        DataCenter.k3 = Double.parseDouble(params.get("BM25:k_3"));

        HashMap<String, ArrayList<String>> rerankPool = new HashMap<String, ArrayList<String>>();
        String testFeatureVectors = params.get("letor:testingFeatureVectorsFile");
        featuresWriter = new BufferedWriter(new FileWriter(new File(testFeatureVectors)));

        Qryop qTree;

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String que = queries.get(i);
            qTree = QryEval.parseQuery(que, modelBM25);
            printFeatureVectors(key, qTree.evaluate(modelBM25), model, pool, que, rerankPool);
        }

        try {
            featuresWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // call SVM classifier to produce new score
        String execPath = params.get("letor:svmRankClassifyPath");
        String testDataPath = params.get("letor:testingFeatureVectorsFile");
        String modelPath = params.get("letor:svmRankModelFile");
        String predictionPath = params.get("letor:testingDocumentScores");

        svmClassifier(execPath, testDataPath, modelPath, predictionPath);

        //print rerank result
        printRerankResult(rerankPool, predictionPath, keys);
    }

    /**
     *
     * @param queryID
     * @param result
     * @throws IOException
     */
    private void printFeatureVectors(String queryID, QryResult result,
                                    RetrievalModel modelLeanringToRank, SDLearningToRankPool pool, String query,
                                    HashMap<String, ArrayList<String>> rerankPool) throws IOException {
        try {
            if (result.docScores.scores.size() < 1) {
                QryEval.writer.write(queryID + "\tQ0\tdummy\t1\t0\trun-1\n");
            } else {
                List<Map.Entry<String, Double>> folder = Util.sortHashMap(result);
                ArrayList<Integer> internalDocids = new ArrayList<Integer>();
                ArrayList<String> docExternIds = new ArrayList<String>();

                for (int i = 0; i < result.docScores.scores.size() && i < 100; i++) {
                    internalDocids.add(QryEval.getInternalDocid(folder.get(i).getKey()));
                    docExternIds.add(folder.get(i).getKey());
                }
                rerankPool.put(queryID, docExternIds);

                pool.addQuery(query);

                ArrayList<String> features = pool.produceNormalizedFeatureVector(modelLeanringToRank, internalDocids);
                for(int j = 0; j < internalDocids.size(); j++) {
                    featuresWriter.write("0 qid:" + queryID + features.get(j)
                            + " # " + folder.get(j).getKey() + "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param learningModelPath
     *        the SVM Rank trainer executable
     * @param FEAT_GEN
     *        argument for SVM
     * @param traningFilePath
     *        path to feature vectors file
     * @param modelOutputFile
     *        path to save trained model
     * @throws IOException
     *          throws if some files cannot be found
     * @throws InterruptedException
     *          throws if training process is interrupted
     * @throws Exception
     *          throws when SVM crash
     */
    private void trainSVM(String learningModelPath, String FEAT_GEN, String traningFilePath,
                                 String modelOutputFile) throws Exception {
        Process cmdProc = Runtime.getRuntime().exec(
                new String[] { learningModelPath, "-c", FEAT_GEN, traningFilePath,
                        modelOutputFile
                });

        // consume stdout and print it out for debugging purposes
        BufferedReader stdoutReader = new BufferedReader(
                new InputStreamReader(cmdProc.getInputStream()));
        String l;
        while ((l = stdoutReader.readLine()) != null) {
            System.out.println(l);
        }
        // consume stderr and print it for debugging purposes
        BufferedReader stderrReader = new BufferedReader(
                new InputStreamReader(cmdProc.getErrorStream()));
        while ((l = stderrReader.readLine()) != null) {
            System.out.println(l);
        }

        // get the return value from the executable. 0 means success, non-zero
        // indicates a problem
        int retValue = cmdProc.waitFor();
        if (retValue != 0) {
            throw new Exception("SVM Rank crashed.");
        }
    }

    /**
     * Use SVM classifier to produce new scores
     * @param execPath
     *          Path to SVM classifier executable
     * @param textDataPath
     *          path to target documents' feature vector
     * @param modelPath
     *          path to tranined SVM model.
     * @param predictionPath
     *         path to result score files.
     * @throws Exception
     *      throw IOException when files do not exist
     *      throw Exception when SVM Classifier crash
     */
    private void svmClassifier(String execPath, String textDataPath, String modelPath, String predictionPath)
            throws Exception {
        Process cmdProc = Runtime.getRuntime().exec(
                new String[] { execPath, textDataPath, modelPath, predictionPath });

        // consume stdout and print it out for debugging purposes
        BufferedReader stdoutReader = new BufferedReader(
                new InputStreamReader(cmdProc.getInputStream()));
        String line;
        while ((line = stdoutReader.readLine()) != null) {
            System.out.println(line);
        }
        // consume stderr and print it for debugging purposes
        BufferedReader stderrReader = new BufferedReader(
                new InputStreamReader(cmdProc.getErrorStream()));
        while ((line = stderrReader.readLine()) != null) {
            System.out.println(line);
        }

        // get the return value from the executable. 0 means success, non-zero
        // indicates a problem
        int retValue = cmdProc.waitFor();
        if (retValue != 0) {
            throw new Exception("SVM Classifier crashed.");
        }
    }

    /**
     *
     * @param rerankPool
     * @param predictionPath
     * @param keys
     * @throws IOException
     */
    private void printRerankResult(HashMap<String, ArrayList<String>> rerankPool,
                                          String predictionPath, List<String> keys) throws IOException {
        Scanner scanner = new Scanner(new File(predictionPath));
        if(scanner == null) {
            throw new FileNotFoundException("Prediction file not found");
        }


        for(int i = 0; i < keys.size(); i++) {
            String curKey = keys.get(i);
            ArrayList<String> curDocs = rerankPool.get(curKey);
            ArrayList<SortEntity> sortEntities = new ArrayList<SortEntity>();

            int j = 0;
            while(j < curDocs.size() && scanner.hasNext()) {
                String s = scanner.next();
                sortEntities.add(new SortEntity(curDocs.get(j), Double.parseDouble(s)));
                j++;
            }
            if(j < curDocs.size()) {
                System.err.println("Prediction files do not contain all scores");
                throw new NoSuchElementException("Prediction files do not contain all scores");
            }
            Collections.sort(sortEntities);

            for (int z = 0; z < sortEntities.size(); z++) {
                QryEval.writer.write(curKey + "\tQ0\t"
                        + sortEntities.get(z).getExternalDocid()
                        + "\t"
                        + (z + 1)
                        + "\t"
                        + sortEntities.get(z).getScore()
                        + "\trun-1\n");
            }
        }
    }

}
