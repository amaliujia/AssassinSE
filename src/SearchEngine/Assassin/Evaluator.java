package SearchEngine.Assassin;

/**
 * Created by amaliujia on 14-9-25.
 */

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
public class Evaluator {

    public static void main(String[] args) throws IOException{
        File target = new File("output/standard.txt");
        Scanner scanner1 = new Scanner(target);
//        HashMap<String, Integer>map = new HashMap<String, Integer>();
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
//            }
//        }
        while (scanner.hasNext()){
            String s1 = scanner.nextLine();
            String s2 = scanner1.nextLine();
            String[] t1 = s1.split("\t");
            String[] t2 = s2.split(" ");
            if(t1[2].equals(t2[2])){
                hit++;
            }
        }
        System.out.println(hit);

    }
}
