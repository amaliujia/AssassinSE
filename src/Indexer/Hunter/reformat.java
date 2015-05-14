/**
 *  This utility reads a file in ClueWeb09 format (ClueWarc) and a
 *  file in Indri harvestlinks format (Inlinks).  It writes to stdout
 *  a file in pseudo-Gov2 format that has inlink data embedded in the
 *  document as a header field.  This file format can be read into
 *  Lucene with a lightly modified version of its Gov2 document
 *  parser.
 *
 *  The ClueWeb09 WARC Content-Length field is not always reliable,
 *  so this software detects the end of a document by seeing the
 *  beginning of (something that looks like) the next document.
 *  This could fail with the right kind of content, but fortunately
 *  the ClueWeb09 dataset doesn't have that kind of content.
 *
 *
 */

package Indexer.Hunter;

import java.io.*;
import java.lang.Integer;

/**
 *  @author Jamie Callan
 */
public class reformat {

  static int inlinkLineNo = 0;
  static String inlinkDocid=null;

  public static void main (String[] args) throws IOException {

    String clueLine = null;
    int clueLineNo = 0;
    String docid;

    if (args.length < 2) {
      System.err.println ("Usage:  " + System.getProperty("sun.java.command")
			  + " warcFile outFile");
      return;
    }

    BufferedReader rdrClueWarc = new BufferedReader (new FileReader (args[0]));
    BufferedWriter rdrInlinks = new BufferedWriter (new FileWriter (args[1]));

    /*
     *  Skip the WARC file header.
     */
    clueLine = rdrClueWarc.readLine();
    clueLineNo ++;

    while (true) {
      rdrClueWarc.mark (10*1024);

      clueLine = rdrClueWarc.readLine();
      clueLineNo++;

      if (clueLine.equals ("WARC/0.18")) {
	    rdrClueWarc.reset ();
	    clueLineNo--;
	    break;
      };
    };

    /*
     *  Each pass of this loop reads one document from the ClueWeb
     *  WARC file.
     */
    while (true) {
      
      docid = null;

      /*
       *  The document must begin with a WARC/0.18 record.
       */
      clueLine = rdrClueWarc.readLine();
      clueLineNo++;

      if (clueLine == null)
	      break;
      else
	      if (! clueLine.equals ("WARC/0.18"))
	          throw (new IOException ("WARC file error at line " + clueLineNo + "."));

      //System.out.println ("<DOC>");
      rdrInlinks.write("<DOC>\n");
      /*
       *  The document's WARC header.
       */
      while (true) {
	    clueLine = rdrClueWarc.readLine ();
	    clueLineNo++;

	    if (clueLine.startsWith ("WARC-TREC-ID: ")) {
	      docid = clueLine.substring ("WARC-TREC-ID: ".length());
	      //System.out.println ("<DOCNO> " + docid + " </DOCNO>");
          rdrInlinks.write("<DOCNO> " + docid + " </DOCNO>\n");
	    } else if (clueLine.startsWith ("WARC-Target-URI: ")) {
            //System.out.println("<DOCURL> " +  clueLine.substring("WARC-Target-URI: ".length()) + " </DOCURL>");
          rdrInlinks.write("<DOCURL> " +  clueLine.substring("WARC-Target-URI: ".length()) + " </DOCURL>\n");
        } else if (clueLine.startsWith ("WARC-") ||
	            clueLine.startsWith ("Content-")) {
                 continue;
        } else if (clueLine.length() == 0) {
             break;
        } else {
            continue;
            //throw (new IOException("WARC file error at line " + clueLineNo + "."));
          }
        }

      /*
       *  Find this document's links (if any) in the inlink file.
       *  There may be MANY inlinks, so get them one-at-a-time rather
       *  than creating a massive data structure.
       */
      //int numInlinks = inlinkCount (rdrInlinks, docid);

//      if (numInlinks > 0) {
//	System.out.println ("<INLINK>");
//	for (int i=0; i<numInlinks; i++)
//	  System.out.println (inlinkGetText (rdrInlinks, docid));
//	System.out.println ("</INLINK>");
//      };

      /*
       *  The ClueWeb document.  The first while loop handles the
       *  document's HTTP headers.  It is terminated by a blank line.
       *  The second while loop handles the web page that was
       *  downloaded by the crawler.  It is terminated by EOF or the
       *  beginning of the next document.
       */
      //System.out.println ("<DOCHDR>");
      rdrInlinks.write("<DOCHDR>\n");
      while (true) {
	    clueLine = rdrClueWarc.readLine();
	    clueLineNo++;
	
      	if (clueLine.length () > 0)
	      //System.out.println (clueLine);
          rdrInlinks.write(clueLine);
	    else {
	      //System.out.println ("</DOCHDR>");
          rdrInlinks.write("</DOCHDR>\n");
	      break;
	    };
      };

      while (true) {
	    rdrClueWarc.mark (10*1024*1024);
	
    	clueLine = rdrClueWarc.readLine();
	    clueLineNo++;
	
	    if (clueLine == null)
	      break;
	    else if (!clueLine.equals ("WARC/0.18"))
	        //System.out.println (clueLine);
            rdrInlinks.write(clueLine + "\n");
	    else {
	        rdrClueWarc.reset ();
            clueLineNo--;
	        break;
        };
      };

      //System.out.println ("</DOC>");
      rdrInlinks.write("</DOC>\n");
      rdrInlinks.flush();
    };
  }
}
