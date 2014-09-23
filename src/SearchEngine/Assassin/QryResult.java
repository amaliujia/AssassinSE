/**
 *  All query operators return QryResult objects.  QryResult objects
 *  encapsulate the inverted lists (InvList) produced by QryopIl query
 *  operators and the score lists (ScoreList) produced by QryopSl
 *  query operators.  QryopIl query operators populate the
 *  invertedList and and leave the docScores empty.  QryopSl query
 *  operators leave the invertedList empty and populate the docScores.
 *  Encapsulating the two types of Qryop results in a single class
 *  makes it easy to build structured queries with nested query
 *  operators.
 */

package SearchEngine.Assassin;

import sun.misc.Sort;

import java.io.IOException;

public class QryResult {

  // Store the results of different types of query operators.

  ScoreList docScores = new ScoreList();
  InvList invertedList = new InvList();
  public void sort() throws IOException{
      docScores.sort();
      invertedList.sort();
  }
}
