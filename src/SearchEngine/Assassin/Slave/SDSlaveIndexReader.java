package SearchEngine.Assassin.Slave;

import SearchEngine.Assassin.QryEval;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

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

    private int base;

    public SDSlaveIndexReader(String indexPath){
        try {
            this.READER = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setBase(int offset){
        base = offset * 5000000;
    }

    public int getBase(){
        return base;
    }
}
