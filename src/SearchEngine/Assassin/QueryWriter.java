package SearchEngine.Assassin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by amaliujia on 14-10-16.
 */
public class QueryWriter {

    public static void main(String [] args) {
        FileWriter fileWriter = null;
        double url = 0.2;
        double keywords = 0.2;
        double body = 0.2;
        double title =0.2;
        double inlink = 0.2;
        try {
            fileWriter = new FileWriter(new File("./query/Test"));
            fileWriter.append("10:#AND(" + " #WSUM( " + url +  " cheap.url " + keywords
                                + " cheap.keywords " + title + " cheap.title " + inlink +
                                " cheap.inlink "  + body + " cheap.body )" + " #WSUM( " +
                                url +  " internet.url " + keywords
                                 + " internet.keywords " + title + " internet.title " + inlink +
                                  " internet.inlink "  + body + " internet.body )" +  " )\n");

            fileWriter.append("12:#AND(" + " #WSUM( " + url +  " djs.url " + keywords
                    + " djs.keywords " + title + " djs.title " + inlink +
                    " djs.inlink "  + body + " djs.body )" + " )\n");

            fileWriter.append("26:#AND(" + " #WSUM( " + url +  " lower.url " + keywords
                    + " lower.keywords " + title + " lower.title " + inlink +
                    " lower.inlink "  + body + " lower.body )" + " #WSUM( " +
                    url +  " heart.url " + keywords
                    + " heart.keywords " + title + " heart.title " + inlink +
                    " heart.inlink "  + body + " heart.body )" +  " #WSUM( " + url +  " rate.url " + keywords
                    + " rate.keywords " + title + " rate.title " + inlink +
                    " rate.inlink "  + body + " rate.body )" + " )\n");

            fileWriter.append("29:#AND(" + " #WSUM( " + url +  " ps.url " + keywords
                    + " ps.keywords " + title + " ps.title " + inlink +
                    " ps.inlink "  + body + " ps.body )" + " #WSUM( " +
                    url +  " 2.url " + keywords
                    + " 2.keywords " + title + " 2.title " + inlink +
                    " 2.inlink "  + body + " 2.body )" +  " #WSUM( " + url +  " games.url " + keywords
                    + " games.keywords " + title + " games.title " + inlink +
                    " games.inlink "  + body + " games.body )" + " )\n");

            fileWriter.append("33:#AND(" + " #WSUM( " + url +  " elliptical.url " + keywords
                    + " elliptical.keywords " + title + " elliptical.title " + inlink +
                    " elliptical.inlink "  + body + " elliptical.body )" + " #WSUM( " +
                    url +  " trainer.url " + keywords
                    + " trainer.keywords " + title + " trainer.title " + inlink +
                    " trainer.inlink "  + body + " trainer.body )" +  " )\n");

            fileWriter.append("52:#AND(" + " #WSUM( " + url +  " avp.url " + keywords
                    + " avp.keywords " + title + " avp.title " + inlink +
                    " avp.inlink "  + body + " avp.body )" + " )\n");

            fileWriter.append("71:#AND(" + " #WSUM( " + url +  " living.url " + keywords
                    + " living.keywords " + title + " living.title " + inlink +
                    " living.inlink "  + body + " living.body )" + " #WSUM( " +
                    url +  " in.url " + keywords
                    + " in.keywords " + title + " in.title " + inlink +
                    " in.inlink "  + body + " in.body )" +  " #WSUM( " + url +  " india.url " + keywords
                    + " india.keywords " + title + " india.title " + inlink +
                    " india.inlink "  + body + " india.body )" + " )\n");

            fileWriter.append("102:#AND(" + " #WSUM( " + url +  " fickle.url " + keywords
                    + " fickle.keywords " + title + " fickle.title " + inlink +
                    " fickle.inlink "  + body + " fickle.body )" + " #WSUM( " +
                    url +  " creek.url " + keywords
                    + " creek.keywords " + title + " creek.title " + inlink +
                    " creek.inlink "  + body + " creek.body )" +  " #WSUM( " + url +  " farm.url " + keywords
                    + " farm.keywords " + title + " farm.title " + inlink +
                    " farm.inlink "  + body + " farm.body )" + " )\n");

            fileWriter.append("149:#AND(" + " #WSUM( " + url +  " uplift.url " + keywords
                    + " uplift.keywords " + title + " uplift.title " + inlink +
                    " uplift.inlink "  + body + " uplift.body )" + " #WSUM( " +
                    url +  " at.url " + keywords
                    + " at.keywords " + title + " at.title " + inlink +
                    " at.inlink "  + body + " at.body )" +  " #WSUM( " + url +  " yellowstone.url " + keywords
                    + " yellowstone.keywords " + title + " yellowstone.title " + inlink +
                    " yellowstone.inlink "  + body + " yellowstone.body )" + " #WSUM( " + url +  " national.url " + keywords
                    + " national.keywords " + title + " national.title " + inlink +
                    " national.inlink "  + body + " national.body )" + " #WSUM( " +
                    url +  " park.url " + keywords
                    + " park.keywords " + title + " park.title " + inlink +
                    " park.inlink "  + body + " park.body )" + " )\n");

            fileWriter.append("190:#AND(" + " #WSUM( " + url +  " brooks.url " + keywords
                    + " brooks.keywords " + title + " brooks.title " + inlink +
                    " brooks.inlink "  + body + " brooks.body )" + " #WSUM( " +
                    url +  " brothers.url " + keywords
                    + " brothers.keywords " + title + " brothers.title " + inlink +
                    " brothers.inlink "  + body + " brothers.body )" +  " #WSUM( " + url +  " clearance.url " + keywords
                    + " clearance.keywords " + title + " clearance.title " + inlink +
                    " clearance.inlink "  + body + " clearance.body )" + " )\n");

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
