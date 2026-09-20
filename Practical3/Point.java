package Practical3;
import java.util.*;
public class Point {
    private int x;
    private int y;

    Point (int x , int y){
        this.x = x ;
        this.y = y;
    }

    public String toString (){
        return ("x:" + x + "y:" + y);

    }

    public boolean equals (Object obj){
        if (this == obj)
            return true;
        if (!(obj instanceof Point))
            return false;

        Point p = (Point) obj;
        return this.x == p.x && this.y == p.y;
    }

    public int hashCode (){
        return Objects.hash(x,y);
    }
}