public class Thermostat {
    private String location;
    private int temperature ;
    private static final int MIN = 16;
    private static final int MAX = 30;
    private static int activecount = 0;

    private boolean maxWarningPrinted = false;
    private boolean minWarningPrinted = false;

    Thermostat (String location , int startTemp){
        this.location = location ;
        if (startTemp >= MIN && startTemp <= MAX){
            this.temperature = startTemp ;
        } else {
            this.temperature = 22;
        }
        activecount ++;
    }

    public Thermostat(String location) {
        this(location, 22);
    }

    public void raise() {
        if (temperature < MAX) {
            temperature++;
        } else {
            System.out.println("Already at maximum (" + MAX + ")");
        }
    }

    public void lower (){
        if (temperature > MIN){
            temperature--;
        } else {
            System.out.println("Already at minimum (" + MIN + ")");
        }
    }
    public int getTemperature (){
        return this.temperature;
    }

    public static int getactiveCount (){
        return activecount;
    }
    
    public static void main (String [] args){
        Thermostat t1 = new Thermostat("Living room",25);
        Thermostat t2 = new Thermostat("Bedroom"); 

        System.out.println("--- Testing Thermostat 1 (" + t1.location + ") ---");
        System.out.println("Starting temp: " + t1.getTemperature());
    

    for (int i = 0; i < 10; i++) {
            t1.raise();
            System.out.println("Temp: " + t1.getTemperature());
        }
    for (int i = 0; i < 20; i++) {
            t1.lower();
            System.out.println("Temp: " + t1.getTemperature());
        }

        // Print static active count
        System.out.println("\nActive count: " + Thermostat.getactiveCount());

    }
}