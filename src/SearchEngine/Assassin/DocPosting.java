package SearchEngine.Assassin;

import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

/**
 * Created by amaliujia on 14-9-12.
 */
//  Utility class that makes it easier to construct postings.

public class DocPosting implements Comparable<DocPosting>{

    public int docid = 0;
    public int tf = 0;
    public Vector<Integer> positions = new Vector<Integer>();
    public int nextPostion;


    public DocPosting(int d, int... locations) {
        this.docid = d;
        this.tf = locations.length;
        for (int i = 0; i < locations.length; i++)
            this.positions.add(locations[i]);
        nextPostion = 0;
    }

    public DocPosting(int d, List<Integer> locations) {
        this.docid = d;
        this.tf = locations.size();
        for (int i = 0; i < locations.size(); i++)
            this.positions.add(locations.get(i));
        nextPostion = 0;
    }

    public DocPosting(int d){
        this.docid = d;
    }

    public DocPosting(){
    }

    public int getDocid(){
        return docid;
    }

    public int compareTo(DocPosting b) {
        return 0;

    }

}