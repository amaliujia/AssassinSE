package Indexer.Hunter;

import SearchEngine.Assassin.EnglishAnalyzerConfigurable;
import SearchEngine.Assassin.Lucene.DocLenStoreSimilarity;
import org.apache.lucene.benchmark.byTask.feeds.DocData;
import org.apache.lucene.benchmark.byTask.feeds.DocMaker;
import org.apache.lucene.benchmark.byTask.feeds.NoMoreDataException;
import org.apache.lucene.benchmark.byTask.feeds.TrecContentSource;
import org.apache.lucene.benchmark.byTask.utils.Config;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.Properties;


/**
 * @author Jamie Callan
 */
public class IndexTrec {

	public static void main(String[] args) throws IOException {

		String usage = "java IndexTrec"
				+ " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
				+ "where options include\n"
				+ "    -lowercase [true | false]\n"
				+ "    -stop [true | false]\n"
				+ "    -stem [none | porter | kstem]\n"
				+ "\n"
				+ "This program indexes the TREC document files in DOCS_PATH, creating a\n"
				+ " Lucene index in INDEX_PATH that can be searched with SearchFiles.\n";

		EnglishAnalyzerConfigurable analyzer = new EnglishAnalyzerConfigurable(Version.LUCENE_43);
		String indexPath = "index";
		String docsPath = null;
		boolean create = true;

	/*
	 *  Process parameters.
	 */
		for (int i = 0; i < args.length; i++) {
			if (("-docs".equals(args[i])) && ((i + 1) < args.length)) {
				docsPath = args[++i];
			} else if ("-index".equals(args[i]) && ((i + 1) < args.length)) {
				indexPath = args[++i];
			} else if ("-update".equals(args[i])) {
				create = false;
			} else if (("-lowercase".equals(args[i])) && ((i + 1) < args.length)) {
				analyzer.setLowercase("true".equals(args[++i]));
			} else if (("-stop".equals(args[i])) && ((i + 1) < args.length)) {
				analyzer.setStopwordRemoval("true".equals(args[++i]));
			} else if (("-stem".equals(args[i])) && ((i + 1) < args.length)) {
				if ("porter".equals(args[i + 1]))
					analyzer.setStemmer(EnglishAnalyzerConfigurable.StemmerType.PORTER);
				else if ("kstem".equals(args[i + 1]))
					analyzer.setStemmer(EnglishAnalyzerConfigurable.StemmerType.KSTEM);
				else
					analyzer.setStemmer(EnglishAnalyzerConfigurable.StemmerType.NONE);
				i++;
			} else {
				System.err.println("Usage: " + usage);
				System.exit(1);
			}
			;
		}

		if ((docsPath == null) || (indexPath == null)) {
			System.err.println("Usage: " + usage);
			System.exit(1);
		}
		;

	/*
	 *  Prepare to read a file of documents in TREC web format.
	 */
		TrecContentSource dcsr = new TrecContentSource();
		Properties pr = new Properties();
		pr.setProperty("work.dir", (new File(docsPath)).getAbsolutePath());

		pr.setProperty("docs.dir", (new File(docsPath)).getAbsolutePath());
		//pr.setProperty ("trec.doc.parser", "org.apache.lucene.benchmark.byTask.feeds.TrecClueWebParser");
		pr.setProperty("content.source.forever", "false");
		pr.setProperty("content.source.log.step", "100");
		pr.setProperty("content.source.verbose", "true");
		Config cr = new Config(pr);
		dcsr.setConfig(cr);
		dcsr.resetInputs();

		DocMaker dm = new DocMaker();

		pr = new Properties();
		pr.setProperty("content.source", "TrecContentSource");
		pr.setProperty("doc.stored", "true");
		pr.setProperty("doc.tokenized", "true");
		pr.setProperty("doc.term.vector", "true");
		pr.setProperty("doc.term.vector.positions", "true");

		cr = new Config(pr);
		dm.setConfig(cr, dcsr);

	/*
	 *  Initialize the index.
	 */
		Date start = new Date();
		try {
			System.out.println("Indexing to directory '" + indexPath + "'...");

			Directory dir = FSDirectory.open(new File(indexPath));
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_43, analyzer);

			iwc.setSimilarity(new DocLenStoreSimilarity());

			// Create a new index in the directory, removing any
			// previously indexed documents:
			iwc.setOpenMode(OpenMode.CREATE);

			// Optional: for better indexing performance, if you
			// are indexing many documents, increase the RAM
			// buffer.  But if you do this, increase the max heap
			// size to the JVM (eg add -Xmx512m or -Xmx1g):
			//
			// iwc.setRAMBufferSizeMB(256.0);

			IndexWriter writer = new IndexWriter(dir, iwc);

			//  	    indexDocsMethod1 (writer, dcsr);
			indexDocsMethod2(writer, dcsr);

			System.out.println(dcsr.getTotalItemsCount() + " documents indexed");
			dcsr.close();

			// NOTE: if you want to maximize search performance,
			// you can optionally call forceMerge here.  This can be
			// a terribly costly operation, so generally it's only
			// worth it when your index is relatively static (ie
			// you're done adding documents to it):
			//
			// writer.forceMerge(1);

			writer.close();

			//  Timing information.
			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() +
					"\n with message: " + e.getMessage());
		}

	}

	/*
     *  indexDocsMethod1 seems more consistent with Lucene automation,
     *  but doesn't give much control over how different document
     *  fields are handled.
     */
	static void indexDocsMethod1(IndexWriter writer, TrecContentSource dcsr) {

	/*
	 *  Configure a DocMaker to handle document indexing.
	 */
		DocMaker dm = new DocMaker();

		Properties pr = new Properties();
		pr.setProperty("content.source", "TrecContentSource");
		pr.setProperty("doc.stored", "true");
		pr.setProperty("doc.tokenized", "true");
		pr.setProperty("doc.term.vector", "true");
		pr.setProperty("doc.term.vector.positions", "true");

		dm.setConfig(new Config(pr), dcsr);

	/*
	 *  Each pass of the loop indexes one document.
	 */
		while (true) {
			try {
				writer.addDocument(dm.makeDocument());
			} catch (Exception e) {
				break;
			}
			;
		}
		;
	}

	/*
     *  indexDocsMethod2 uses less Lucene automation, but gives more
     *  control over how different document fields are handled.
     */
	static void indexDocsMethod2(IndexWriter writer, TrecContentSource dcsr) {

	/*
	 *  Create a new fieldtype that is indexed, tokenized, and stored.
	 */
		FieldType storedTextField = new FieldType();
		storedTextField.setIndexed(true);
		storedTextField.setStoreTermVectors(true);
		storedTextField.setStoreTermVectorPositions(true);
		storedTextField.setTokenized(true);
		storedTextField.freeze();
	
	/*
	 *  Each pass of the loop indexes one document.
	 */
		DocData d = new DocData();

		int i = 1;

		while (true) {
			try {
				d = dcsr.getNextDocData(d);

				// make a new, empty document
				Document doc = new Document();

		/*
		 *  Add metadata fields.
		 */
				doc.add(new StringField("externalId", d.getName(), Field.Store.YES));
				String data = d.getDate();
				if (data != null) {
					doc.add(new StringField("date", d.getDate(), Field.Store.YES));
				}

		/*
		 *  Add the content fields.  Specify a Reader, so that the text is
		 *  tokenized, indexed, and stored.
		 */
				doc.add(new Field("title",
						new BufferedReader(new StringReader(d.getTitle())),
						storedTextField));

				doc.add(new Field("body",
						new BufferedReader(new StringReader(d.getBody())),
						storedTextField));

				if (d.getProps().getProperty("keywords") != null)
					doc.add(new Field("keywords",
							new BufferedReader(new StringReader(d.getProps().getProperty("keywords"))),
							storedTextField));

				if (d.getProps().getProperty("inlink") != null)
					doc.add(new Field("inlink",
							new BufferedReader(new StringReader(d.getProps().getProperty("inlink"))),
							storedTextField));

				if (d.getProps().getProperty("url") != null) {
					String u = d.getProps().getProperty("url");

					u = u.replace('.', ' ');
					u = u.replace('_', ' ');

					doc.add(new Field("url",
							new BufferedReader(new StringReader(u)),
							storedTextField));
				}

		/*
		 *  Add the document to the index.
		 */
				writer.addDocument(doc);
				i++;
			} catch (NoMoreDataException e) {
				break;
			} catch (IOException e) {
				System.out.println("Caught IOException: " + e.getMessage());
				break;
			}
			;

			if ((i % 100) == 0)
				System.out.println(i + " documents...");
		}
		;
	}

}
