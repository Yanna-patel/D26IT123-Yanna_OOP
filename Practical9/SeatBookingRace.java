package Practical9;
class SeatBooking {
    int seatsLeft = 5;

    synchronized void book (){
        if (seatsLeft > 0){
            System.out.println (Thread.currentThread().getName() + "booked a seat");
            seatsLeft --;
        }
    }
}

public class SeatBookingRace {
    public static void main (String [] args)throws InterruptedException{
        SeatBooking booking = new SeatBooking();
        int numberOfThreads = 10;

        Thread [] threads = new Thread[numberOfThreads];

        for (int i = 0 ; i < numberOfThreads ; i++){
            threads[i] = new Thread (() ->{
                booking.book();
            });

            threads[i].start();
        }

        for (int i = 0 ; i< numberOfThreads ; i++){
            threads[i].join();
        }

        System.out.println("Seats left: " + booking.seatsLeft);
    }
}