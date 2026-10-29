package Practical13;
public class GenericContainers {
    public static <T> T firstElement (T [] array){
        return array[0];
    }

    public static void main (String [] args){
        Box <Integer> b1 = new Box<>();
        b1.set(100);
        System.out.println ("Box Integer: " + b1.get());

        Box<String> b2 = new Box<>();
        b2.set("Java");
        System.out.println("Box String: " + b2.get());

        Pair<String , Integer> p1 = new Pair<>("Yanna" , 20);
        System.out.println("Pair: " + p1.getFirst() + " , " + p1.getSecond());

        Integer [] numbers = {10,20,30};
        String [] words = {"java", "Python" , "C++"};

        System.out.println("First number: " + firstElement(numbers));
        System.out.println("First word: " + firstElement(words));
    }
}