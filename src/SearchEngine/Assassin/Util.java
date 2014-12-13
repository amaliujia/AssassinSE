package SearchEngine.Assassin;

import java.io.IOException;
import java.util.*;

import static java.lang.System.exit;

/**
 * Created by amaliujia on 14-12-13.
 */
public class Util {

    /**
     *  Print a message indicating the amount of memory used.  The
     *  caller can indicate whether garbage collection should be
     *  performed, which slows the program but reduces memory usage.
     *  @param gc If true, run the garbage collector before reporting.
     *  @return void
     */
    public static void printMemoryUsage (boolean gc) {

        Runtime runtime = Runtime.getRuntime();

        if (gc) {
            runtime.gc();
        }

        System.out.println ("Memory used:  " +
                ((runtime.totalMemory() - runtime.freeMemory()) /
                        (1024L * 1024L)) + " MB");
    }


    /**
     *  Write an error message and exit.  This can be done in other
     *  ways, but I wanted something that takes just one statement so
     *  that it is easy to insert checks without cluttering the code.
     *  @param message The error message to write before exiting.
     *  @return void
     */
    static void fatalError (String message) {
        System.err.println (message);
        exit(1);
    }

    /**
     *
     * @param result
     * @return A sorted map entry list
     * @throws java.io.IOException
     */
    public static List<Map.Entry<String, Double>> sortHashMap(QryResult result) throws IOException {
        HashMap<String, Double> map = new HashMap<String, Double>();
        for (int i = 0; i < result.docScores.scores.size(); i++) {
            map.put(QryEval.getExternalDocid(result.docScores.scores.get(i).getDocid()),
                    result.docScores.getDocidScore(i));
        }
        List<Map.Entry<String, Double>> folder = new ArrayList<Map.Entry<String, Double>>(map.entrySet());
        Collections.sort(folder, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> e1,
                               Map.Entry<String, Double> e2) {
                if (!e1.getValue().equals(e2.getValue())) {
                    if (e2.getValue() > e1.getValue()) return 1;
                    else return -1;
                } else
                    return (e1.getKey()).toString().compareTo(e2.getKey().toString());
            }
        });
        return folder;
    }
}
