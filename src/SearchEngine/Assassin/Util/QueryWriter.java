package SearchEngine.Assassin.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by amaliujia on 14-10-16.
 */
public class QueryWriter {

    public static void main(String [] args) {
        FileWriter fileWriter = null;
        double url = 0.0;
        double keywords = 0.0;
        double body = 0.3;
        double title =0.3;
        double inlink = 0.2;

        double w1 = 0.1;
        double w2 = 0.9;
        try {
            fileWriter = new FileWriter(new File("./query/experiement2"));

            fileWriter.append("10:#WAND ( " + w1 + " #AND( #WSUM( 0.0 cheap.url 0.0 cheap.keywords 0.2 cheap.title 0.0 cheap.inlink 0.8 cheap.body )" +
                              " #WSUM( 0.0 internet.url 0.0 internet.keywords 0.2 internet.title 0.0 internet.inlink 0.8 internet.body ) ) " + w2 +
                              " #WAND( 0.1 #AND( cheap internet ) 0.45 #AND( #NEAR/1( cheap internet ) ) 0.45 #AND( #WINDOW/8( cheap internet ) ) )" + ")\n");

            fileWriter.append("12:#WAND (" + w1 + " #AND( #WSUM( 0.0 djs.url 0.0 djs.keywords 0.2 djs.title 0.0 djs.inlink 0.8 djs.body ) ) " +
                              w2 + " #WAND ( 0.1 #AND( djs ) 0.45 #AND( #NEAR/1( djs ) ) 0.45  #AND ( #WINDOW/4 ( djs ) ) )" + ")\n");

            //lower heart rate
            fileWriter.append("26:#WAND (" + w1 + " #AND( #WSUM( 0.0 lower.url 0.0 lower.keywords 0.2 lower.title 0.0 lower.inlink 0.8 lower.body ) #WSUM( 0.0 heart.url 0.0 heart.keywords 0.2 heart.title 0.0 heart.inlink 0.8 heart.body ) #WSUM( 0.0 rate.url 0.0 rate.keywords 0.2 rate.title 0.0 rate.inlink 0.8 rate.body ) ) "
                              + w2 + " #WAND( 0.1 #AND( lower heart rate ) 0.45 #AND( #NEAR/1( heart rate )  #NEAR/1( lower heart ) ) 0.45 #AND( #WINDOW/8( heart rate )  #WINDOW/8( lower heart ) ) )" + ")\n");

            fileWriter.append("29:#WAND (" + w1 + " #AND( #WSUM( 0.0 ps.url 0.0 ps.keywords 0.2 ps.title 0.0 ps.inlink 0.8 ps.body ) #WSUM( 0.0 2.url 0.0 2.keywords 0.2 2.title 0.0 2.inlink 0.8 2.body ) #WSUM( 0.0 games.url 0.0 games.keywords 0.2 games.title 0.0 games.inlink 0.8 games.body ) ) " +  w2 +
                              " #WAND( 0.1 #AND( ps 2 games ) 0.45 #AND( #NEAR/1( 2 games )  #NEAR/1( ps 2 ) ) 0.45 #AND( #WINDOW/8( 2 games )  #WINDOW/8( ps 2 ) ) )" + ")\n");

            fileWriter.append("33:#WAND (" + w1 + " #AND( #WSUM( 0.0 elliptical.url 0.0 elliptical.keywords 0.2 elliptical.title 0.0 elliptical.inlink 0.8 elliptical.body ) #WSUM( 0.0 trainer.url 0.0 trainer.keywords 0.2 trainer.title 0.0 trainer.inlink 0.8 trainer.body ) ) "
                              + w2 + " #WAND( 0.1 #AND( elliptical trainer ) 0.45 #AND( #NEAR/1( elliptical trainer ) ) 0.45 #AND( #WINDOW/8( elliptical trainer ) ) )" + ")\n");

            fileWriter.append("52:#WAND (" + w1 + " #AND( #WSUM( 0.0 avp.url 0.0 avp.keywords 0.2 avp.title 0.0 avp.inlink 0.8 avp.body ) ) "
                              + w2 + " #WAND ( 0.1 #AND( avp ) 0.45 #AND( #NEAR/1( avp ) ) 0.45 #AND ( #WINDOW/4 ( avp ) ) )" + ")\n");

            fileWriter.append("71:#WAND (" + w1 + " #AND( #WSUM( 0.0 living.url 0.0 living.keywords 0.2 living.title 0.0 living.inlink 0.8 living.body ) #WSUM( 0.0 in.url 0.0 in.keywords 0.2 in.title 0.0 in.inlink 0.8 in.body ) #WSUM( 0.0 india.url 0.0 india.keywords 0.2 india.title 0.0 india.inlink 0.8 india.body ) ) "
                              + w2 + " #WAND( 0.1 #AND( living in india ) 0.45 #AND( #NEAR/1( in india )  #NEAR/1( living in ) ) 0.45 #AND( #WINDOW/8( in india )  #WINDOW/8( living in ) ) )" + ")\n");


            fileWriter.append("102:#WAND ( " + w1 + " #AND( #WSUM( 0.0 fickle.url 0.0 fickle.keywords 0.2 fickle.title 0.0 fickle.inlink 0.8 fickle.body ) " +
                              "#WSUM( 0.0 creek.url 0.0 creek.keywords 0.2 creek.title 0.0 creek.inlink 0.8 creek.body ) #WSUM( 0.0 farm.url 0.0 farm.keywords 0.2 farm.title 0.0 farm.inlink 0.8 farm.body ) ) "
                              + w2 + " #WAND( 0.1 #AND( fickle creek farm ) 0.45 #AND( #NEAR/1( creek farm )  #NEAR/1( fickle creek ) )  0.45 #AND( #WINDOW/8( creek farm )  #WINDOW/8( fickle creek ) ) ) )\n");

            fileWriter.append("149:#WAND (" + w1 + " #AND( #WSUM( 0.0 uplift.url 0.0 uplift.keywords 0.2 uplift.title 0.0 uplift.inlink 0.8 uplift.body ) #WSUM( 0.0 at.url 0.0 at.keywords 0.2 at.title 0.0 at.inlink 0.8 at.body ) #WSUM( 0.0 yellowstone.url 0.0 yellowstone.keywords 0.2 yellowstone.title 0.0 yellowstone.inlink 0.8 yellowstone.body ) #WSUM( 0.0 national.url 0.0 national.keywords 0.2 national.title 0.0 national.inlink 0.8 national.body ) #WSUM( 0.0 park.url 0.0 park.keywords 0.2 park.title 0.0 park.inlink 0.8 park.body ) ) "
                              + w2 + " #WAND( 0.1 #AND( uplift at yellowstone national park ) 0.45 #AND( #NEAR/1( national park )  #NEAR/1( yellowstone national )  #NEAR/1( at yellowstone )  #NEAR/1( uplift at ) ) 0.45 #AND( #WINDOW/8( national park )  #WINDOW/8( yellowstone national )  #WINDOW/8( at yellowstone )  #WINDOW/8( uplift at ) ) )" + ")\n");

            fileWriter.append("190:#WAND (" + w1 + " #AND( #WSUM( 0.0 brooks.url 0.0 brooks.keywords 0.2 brooks.title 0.0 brooks.inlink 0.8 brooks.body ) #WSUM( 0.0 brothers.url 0.0 brothers.keywords 0.2 brothers.title 0.0 brothers.inlink 0.8 brothers.body ) #WSUM( 0.0 clearance.url 0.0 clearance.keywords 0.2 clearance.title 0.0 clearance.inlink 0.8 clearance.body ) ) "
                              + w2 + " #WAND( 0.1 #AND( brooks brothers clearance ) 0.45 #AND( #NEAR/1( brothers clearance )  #NEAR/1( brooks brothers ) ) 0.45 #AND( #WINDOW/8( brothers clearance )  #WINDOW/8( brooks brothers ) ) )" + ")\n");

//            fileWriter.append("10:#AND(" + " #WSUM( " + url +  " cheap.url " + keywords
//                                + " cheap.keywords " + title + " cheap.title " + inlink +
//                                " cheap.inlink "  + body + " cheap.body )" + " #WSUM( " +
//                                url +  " internet.url " + keywords
//                                 + " internet.keywords " + title + " internet.title " + inlink +
//                                  " internet.inlink "  + body + " internet.body )" +  " )\n");
//
//            fileWriter.append("12:#AND(" + " #WSUM( " + url +  " djs.url " + keywords
//                    + " djs.keywords " + title + " djs.title " + inlink +
//                    " djs.inlink "  + body + " djs.body )" + " )\n");
//
//            fileWriter.append("26:#AND(" + " #WSUM( " + url +  " lower.url " + keywords
//                    + " lower.keywords " + title + " lower.title " + inlink +
//                    " lower.inlink "  + body + " lower.body )" + " #WSUM( " +
//                    url +  " heart.url " + keywords
//                    + " heart.keywords " + title + " heart.title " + inlink +
//                    " heart.inlink "  + body + " heart.body )" +  " #WSUM( " + url +  " rate.url " + keywords
//                    + " rate.keywords " + title + " rate.title " + inlink +
//                    " rate.inlink "  + body + " rate.body )" + " )\n");
//
//            fileWriter.append("29:#AND(" + " #WSUM( " + url +  " ps.url " + keywords
//                    + " ps.keywords " + title + " ps.title " + inlink +
//                    " ps.inlink "  + body + " ps.body )" + " #WSUM( " +
//                    url +  " 2.url " + keywords
//                    + " 2.keywords " + title + " 2.title " + inlink +
//                    " 2.inlink "  + body + " 2.body )" +  " #WSUM( " + url +  " games.url " + keywords
//                    + " games.keywords " + title + " games.title " + inlink +
//                    " games.inlink "  + body + " games.body )" + " )\n");
//
//            fileWriter.append("33:#AND(" + " #WSUM( " + url +  " elliptical.url " + keywords
//                    + " elliptical.keywords " + title + " elliptical.title " + inlink +
//                    " elliptical.inlink "  + body + " elliptical.body )" + " #WSUM( " +
//                    url +  " trainer.url " + keywords
//                    + " trainer.keywords " + title + " trainer.title " + inlink +
//                    " trainer.inlink "  + body + " trainer.body )" +  " )\n");
//
//            fileWriter.append("52:#AND(" + " #WSUM( " + url +  " avp.url " + keywords
//                    + " avp.keywords " + title + " avp.title " + inlink +
//                    " avp.inlink "  + body + " avp.body )" + " )\n");
//
//            fileWriter.append("71:#AND(" + " #WSUM( " + url +  " living.url " + keywords
//                    + " living.keywords " + title + " living.title " + inlink +
//                    " living.inlink "  + body + " living.body )" + " #WSUM( " +
//                    url +  " in.url " + keywords
//                    + " in.keywords " + title + " in.title " + inlink +
//                    " in.inlink "  + body + " in.body )" +  " #WSUM( " + url +  " india.url " + keywords
//                    + " india.keywords " + title + " india.title " + inlink +
//                    " india.inlink "  + body + " india.body )" + " )\n");
//
//            fileWriter.append("102:#AND(" + " #WSUM( " + url +  " fickle.url " + keywords
//                    + " fickle.keywords " + title + " fickle.title " + inlink +
//                    " fickle.inlink "  + body + " fickle.body )" + " #WSUM( " +
//                    url +  " creek.url " + keywords
//                    + " creek.keywords " + title + " creek.title " + inlink +
//                    " creek.inlink "  + body + " creek.body )" +  " #WSUM( " + url +  " farm.url " + keywords
//                    + " farm.keywords " + title + " farm.title " + inlink +
//                    " farm.inlink "  + body + " farm.body )" + " )\n");
//
//            fileWriter.append("149:#AND(" + " #WSUM( " + url +  " uplift.url " + keywords
//                    + " uplift.keywords " + title + " uplift.title " + inlink +
//                    " uplift.inlink "  + body + " uplift.body )" + " #WSUM( " +
//                    url +  " at.url " + keywords
//                    + " at.keywords " + title + " at.title " + inlink +
//                    " at.inlink "  + body + " at.body )" +  " #WSUM( " + url +  " yellowstone.url " + keywords
//                    + " yellowstone.keywords " + title + " yellowstone.title " + inlink +
//                    " yellowstone.inlink "  + body + " yellowstone.body )" + " #WSUM( " + url +  " national.url " + keywords
//                    + " national.keywords " + title + " national.title " + inlink +
//                    " national.inlink "  + body + " national.body )" + " #WSUM( " +
//                    url +  " park.url " + keywords
//                    + " park.keywords " + title + " park.title " + inlink +
//                    " park.inlink "  + body + " park.body )" + " )\n");
//
//            fileWriter.append("190:#AND(" + " #WSUM( " + url +  " brooks.url " + keywords
//                    + " brooks.keywords " + title + " brooks.title " + inlink +
//                    " brooks.inlink "  + body + " brooks.body )" + " #WSUM( " +
//                    url +  " brothers.url " + keywords
//                    + " brothers.keywords " + title + " brothers.title " + inlink +
//                    " brothers.inlink "  + body + " brothers.body )" +  " #WSUM( " + url +  " clearance.url " + keywords
//                    + " clearance.keywords " + title + " clearance.title " + inlink +
//                    " clearance.inlink "  + body + " clearance.body )" + " )\n");

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
