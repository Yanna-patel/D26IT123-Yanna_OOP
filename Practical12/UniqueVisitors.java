package Practical12;
import java.util.*;
public class UniqueVisitors {
    public static void main (String [] args){
        List<String> logins = Arrays.asList("Yanna" , "Misty" , "Priya", "Yanna", "Misty","Radhika");
        
        HashSet<String> unique = new HashSet<>(logins);
        LinkedHashSet<String> ordered = new LinkedHashSet<>(logins);

        System.out.println("Unique users count: " + unique.size());

        System.out.println("First three distinct names: ");
        int count = 0 ;

        for (String name : ordered){
            if (count == 3){
                break;
            }
            System.out.println(name);
            count ++;
        }
    }
}