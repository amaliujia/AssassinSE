package SearchEngine.Assassin.Util;

/**
 * Created by amaliujia on 14-9-25.
 */

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Evaluator {

    public static void main(String[] args) throws IOException {
        File target = new File("output/standard.txt");
        Scanner scanner1 = new Scanner(target);
        HashMap<String, Integer>map = new HashMap<String, Integer>();
//        while(scanner1.hasNext()){
//            String s = scanner1.nextLine();
//            String [] t = s.split(" ");
//            map.put(t[2], 1);
//        }
        File standard = new File("output/HW1-queries-UB.teIn");
        Scanner scanner = new Scanner(standard);
        int hit = 0;
//        while (scanner.hasNext()){
//            String s = scanner.nextLine();
//            String [] t = s.split("\t");
//            if(map.containsKey(t[2])){
//                hit++;
//                map.put(t[2], 2);
//            }else{
//               // System.out.println(map.containsKey(t[2]));
//               // System.out.println(t[2]);
//            }
//        }
//       Set keyset =  map.keySet();
//        for (String key : map.keySet()) {
//            if(map.get(key) == 1){
//              // System.out.println(key);
//            }
//
//        }
        while (scanner.hasNext()) {
            String s1 = scanner.nextLine();
            String s2 = scanner1.nextLine();
            String[] t1 = s1.split("\t");
            String[] t2 = s2.split(" ");
            if(t1[2].equals(t2[2])) {
                hit++;
            } else {
                //System.out.println(t1[0]);
            }
        }
        System.out.println(hit);

    }
}
