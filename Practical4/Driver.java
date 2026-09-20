import java.util.*;
public class Driver {
    public static void main (String [] args){
        String [] logs = {"10:05 alice Hello there" , "Tony" , "10:20 bob Hi there"};

        Scanner sc = new Scanner (System.in);
        System.out.print("Enter keyword:");
        String keyword = sc.nextLine();

        String result = ChatFilter.filter(logs,keyword);
        System.out.print(result);
    }
}