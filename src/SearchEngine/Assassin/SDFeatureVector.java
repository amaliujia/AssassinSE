package SearchEngine.Assassin;

import java.util.ArrayList;

/**
 * Created by amaliujia on 14-11-15.
 */
public class SDFeatureVector {

    private final int NUM_FEATURES = 18;

    // feature 1
    public double spamscore;

    // feature 2
    public double urlDepth;

    // feature 3
    public double wiki;

    // feature 4
    public double pageRankScrore;

    // feature 5
    public double BM25Body;

    // feature 6
    public double IndriBody;

    // feature 7
    public double overlapBody;

    // feature 8
    public double BM25Title;

    // feature 9
    public double IndriTitle;

    // feature 10
    public double overlapTitle;

    // feature 11
    public double BM25Url;

    // feature 12
    public double IndriUrl;

    // feature 13
    public double overlapUrl;

    // feature 14
    public double BM25Inlink;

    // feature 15
    public double IndriInlink;

    // feature 16
    public double overlaplink;

    // feature 17
    public double tfMean;

    // feature 18
    public double domain;

    public ArrayList<Double> features;

    /**
     * This is constructor for SDFeatureVector
     */
    public SDFeatureVector(){
        spamscore = -1;
        urlDepth = -1;
        wiki = -1;
        pageRankScrore = -1;
        BM25Body = -1;
        IndriBody = -1;
        overlapBody = -1;
        BM25Title = -1;
        IndriTitle = -1;
        overlapTitle = -1;
        BM25Url = -1;
        IndriUrl = -1;
        overlapUrl = -1;
        BM25Inlink = -1;
        IndriInlink = -1;
        overlaplink = -1;
        tfMean = -1;
        domain = -1;

        features = new ArrayList<Double>();

        for(int i = 0; i < NUM_FEATURES; i++){
             features.add(-1.0);
        }
    }


    public double getFeature(int i){
        return features.get(i);
    }

    public String toString(){
        String s;
        s = "features: 1:" + getFeature(0) + " 2:" + getFeature(1) + " 3:" + getFeature(2) +
                " 4:" + getFeature(3) + " 5:" + getFeature(4) + " 6:" + getFeature(5) +
                " 7:" + getFeature(6) + " 8:" + getFeature(7) + " 9:" + getFeature(8) +
                " 10:" + getFeature(9) + " 11:" + getFeature(10) + " 12:" + getFeature(11) +
                " 13:" + getFeature(12) + " 14:" + getFeature(13) + " 15:" + getFeature(14) +
                " 16:" + getFeature(15) + " 17:" + getFeature(16) + " 18:" + getFeature(17);
        return s;
    }


}
