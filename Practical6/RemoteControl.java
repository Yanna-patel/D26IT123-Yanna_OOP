package Practical6;
interface Switchable {
    void on ();
    void off ();

    default void toggle (){
        System.out.println ("toggling the switch");
    }
} 

class Fan implements Switchable {
    public void on (){
        System.out.println("Fan is on");
    }
    public void off (){
        System.out.println("Fan is off");
    }
}

class Light implements Switchable{
    public void on (){
        System.out.println("Light is on");
    }
    public void off(){
        System.out.println("Light is off");
    }
}

interface permission{
    boolean maySwitchOn (Switchable device , int hours);
}

public class RemoteControl {
    public static void main (String [] args){
        Switchable [] devices = {
            new Fan(),
            new Light()
        };

        for (Switchable d: devices){
            d.toggle();
        }

        permission p1 = new permission(){
            public boolean maySwitchOn(Switchable device , int hours){
                return hours >=6 && hours <=22;
            }
        };

        System.out.println(p1.maySwitchOn(devices[0],10));

        permission p2 = (device, hours) ->  hours >= 6 && hours <=22;
        System.out.println(p2.maySwitchOn(devices[1],50));
    }
}