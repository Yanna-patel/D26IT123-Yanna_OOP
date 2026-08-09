import java.util.* ;
public class TollBooth {
    record Vehicle (String number , String type ){

    }

    public static void main (String [] args){
        Scanner sc = new Scanner(System.in);

        int totaltoll = 0;
        int car = 0;
        int bike = 0;
        int truck = 0;

        while (true){
            System.out.println("Enter vechile number or (Done):");
            String number = sc.next();

            if (number.equalsIgnoreCase("Done")){
                break;
            }

            System.out.println("Enter Vehichle Type (bike/car/truck):");
            String type = sc.next().toLowerCase();

            Vehicle v = new Vehicle(number,type);

            int toll = switch (v.type()){
                case "bike" -> {
                    bike ++;
                    yield 20;
                }

                case "car" -> {
                    car ++;
                    yield 50;
                }

                case "truck" -> {
                    truck ++;
                    yield 150;
                }

                default -> {
                    System.out.println("Invalid vehicle type!");
                    yield 0;
                }
            };

            totaltoll += toll;

            System.out.println();
        }

        System.out.println("Total toll collected : " + totaltoll);
        System.out.println("Bikes:" + bike);
        System.out.println("Car:" + car);
        System.out.println("Truck:" + truck);

        String mostfrequent ;

        if (bike > car && bike > truck){
            mostfrequent = "Bike";
        } else if (car > bike && car > truck) {
            mostfrequent = "Car";
        } else if (truck > bike && truck > car) {
            mostfrequent = "Truck";
        } else {
            mostfrequent = null;
        }

        if (mostfrequent != null){
            System.out.println ("Most Frequent Vechicle:" + mostfrequent);
        } else {
            System.out.println("There is no frequent vechicle");
        }

        sc.close();

    }
}