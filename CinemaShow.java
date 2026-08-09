import java.util.*;

public class CinemaShow {
    private String title;
    private int seatsAvailable;
    private final int capacity;
    private static int totalBooked = 0;

    public CinemaShow(String Title , int capacity){
        this.title = title;
        this.capacity = capacity;
        this.seatsAvailable = capacity;

    }
    public CinemaShow(String Title){
        this(Title,100);

    }

    public boolean book (int n){
        if (n <= seatsAvailable){
            seatsAvailable -= n;
            totalBooked +=  n; 
            return true;
        } else {
            return false;
        }
    }

    public void cancel (int n){
        if (seatsAvailable + n <= capacity ){
            seatsAvailable += n;
        }else {
            System.out.println("Enter proper value");
        }
    }

    public int getseatsAvailable (){
        return seatsAvailable;
    }

    public static int gettotalBooked (){
        return totalBooked;
    }

    public static void main (String [] args){
       CinemaShow show = new CinemaShow("chhichore", 50);

        System.out.println("Book 20 seats: " + show.book(20));
        System.out.println("Seats Available: " + show.getseatsAvailable());

        System.out.println("Book 15 seats: " + show.book(15));
        System.out.println("Seats Available: " + show.getseatsAvailable());

        System.out.println("Cancel 5 seats");
        show.cancel(5);
        System.out.println("Seats Available: " + show.getseatsAvailable());


        System.out.println("Book 25 seats: " + show.book(25));
        System.out.println("Seats Available: " + show.getseatsAvailable());

        System.out.println("Total Booked: " + CinemaShow.gettotalBooked());
    }
}