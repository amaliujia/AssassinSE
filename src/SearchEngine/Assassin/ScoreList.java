/**
 *  This class implements the document score list data structure
 *  and provides methods for accessing and manipulating them.
 */
package SearchEngine.Assassin;

import sun.rmi.transport.Connection;

import java.lang.reflect.Array;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;

public class ScoreList {

  List<ScoreListEntry> scores = new ArrayList<ScoreListEntry>();

  /**
   *  Append a document score to a score list.
   *  @param docid An internal document id.
   *  @param score The document's score.
   *  @return void
   */
  public void add(int docid, double score) {
      scores.add(new ScoreListEntry(docid, score));
  }

  /**
   *  Get the n'th document id.
   *  @param n The index of the requested document.
   *  @return The internal document id.
   */
  public int getDocid(int n) {
    return this.scores.get(n).getDocid();
  }

  /**
   *  Get the score of the n'th document.
   *  @param n The index of the requested document score.
   *  @return The document's score.
   */
  public double getDocidScore(int n) {
    return this.scores.get(n).getScore();
  }


   public void sort(){
        Collections.sort(scores);
   }

}
