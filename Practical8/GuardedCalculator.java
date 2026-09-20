package Practical8;
import java.util.Scanner;
class DivideByZeroException extends Exception{
    DivideByZeroException(String msg){
        System.out.println("Do not divide by zero");
    }
}

public class GuardedCalculator {
    public static void main (String [] args){
        Scanner sc = new Scanner (System.in);
        try {
            System.out.println("Enter first number: ");
            int a = sc.nextInt();

            System.out.println("Enter second number: ");
            int b = sc.nextInt();

            if (b==0){
                throw new DivideByZeroException("Cannot divide by zero");

            } else {
                int result = a/b;
                System.out.println("Result: " + result);
            }
        } catch (DivideByZeroException e){
            System.out.println (e.getMessage());
        } finally {
            System.out.println("Attemp logged.");
        }
    }
}