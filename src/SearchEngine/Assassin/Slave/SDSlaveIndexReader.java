package SearchEngine.Assassin.Slave;

import SearchEngine.Assassin.Master.SDIndexCollection;
import org.apache.lucene.index.IndexReader;

/**
 * Created by amaliujia on 15-8-15.
 */
public class SDSlaveIndexReader {
    /**
     * Data that should read following data from index
     * 1. numDocs
     * 2. docFreq
     * 3. totalTermFreq
     * 4. see class DocLengthStore, doclen.
     */
    public IndexReader READER;

    private SDIndexCollection collection;


    public void setIndexColletion(SDIndexCollection colletion){
        this.collection = colletion;
    }
}
