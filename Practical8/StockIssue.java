package Practical8;
import java.util.Scanner;

class OutofStockException extends Exception{
    OutofStockException (String Message){
        super(Message);
    }
}

class InvalidQuantityException extends Exception{
    InvalidQuantityException(String Message){
        super(Message);
    }
}

public class StockIssue {
    public static void issue (int item , int qty)throws OutofStockException,InvalidQuantityException{
        
        if (qty <= 0){
            throw new InvalidQuantityException("Enter the value greater than 0");
        }
        System.out.println("Quantity: " + qty);

        if (qty > item){
            int shortfall = qty - item;
            throw new OutofStockException("Shortfall" + shortfall);
        }
        System.out.println("Stock : " + item);
    }

    public static void main (String [] args){
        Scanner sc = new Scanner(System.in);
        int[][] requests = {
        {5, 3},
        {2, 5},
        {10, 0},
        {4, 2}
    };

    for (int i = 0; i < requests.length; i++) {
        try {
            issue(requests[i][0], requests[i][1]);
        }catch (OutofStockException e){
            System.out.println(e.getMessage());
        } catch (InvalidQuantityException e){
            System.out.println(e.getMessage());
        }
        }
    }
}