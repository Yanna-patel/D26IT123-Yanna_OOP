import java.util.Scanner;

public class VendingMachine{
    enum Coin {ONE,TWO,FIVE,TEN}
    public static void main (String [] args){
        final int price = 15;
        int total = 0;
        Scanner sc = new Scanner(System.in);

        System.out.println("Snack price :" + price);
        System.out.println("Insert coin:(ONE, TWO, FIVE, TEN)");

        while (total < price) {
            System.out.println("coin:");
            String input = sc.next().toUpperCase();

            Coin coin ;
            try {
                coin = Coin.valueOf(input);
            } catch (IllegalArgumentException e){
                System.out.println("Not a valid coin");
                continue;
            }

            int value = switch (coin) {
                case ONE -> 1;
                case TWO -> 2;
                case FIVE -> 5 ;
                case TEN -> 10;
            };

            total += value ;
            System.out.println("Inserted so far " + total);
        }

        System.out.println("Paid. Change : " + (total - price));
        sc.close (); 
    }
}
