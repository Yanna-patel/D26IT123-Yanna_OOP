package Practical12;
import java.util.*;
public class WordFrequency {
    public static void main (String [] args){
        String para = "Java is powerful but java little difficult";

        String [] words = para.toLowerCase().split("\\s+");
        HashMap<String,Integer> map = new HashMap<>();

        for (String word : words){
            map.put(word, map.getOrDefault(word, 0)+1);
        }

        List <String> list = new ArrayList<>(map.keySet());

        list.sort((a,b)-> map.get(b).compareTo(map.get(a)));

        for (String word : list){
            System.out.println(word + " : " + map.get(word));
        }
    }
}