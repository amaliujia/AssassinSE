package TextMiningEngine.Witch.PageRank;

import SearchEngine.Assassin.RetrievalModel.LinkAnalysisModel;

/**
 * Created by amaliujia on 15-3-4.
 */
public class LinkAnalysisEngine {

    private LinkBase linkBase;

    private LinkAnalysisModel model;

    public LinkAnalysisEngine(LinkAnalysisModel model){
        this.model = model;
        if(model.exectuionName.equals("PageRank")){
            linkBase = PageRankBuilder.createPageRank(this.model);
        }
        System.out.println("11");
    }

    public void run(){
        linkBase.run();
    }

}
