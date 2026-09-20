public abstract class Shape{
    public abstract double area();
    public static void main (String [] args){
        double runningtotal = 0.0;
        double max = 0.0;
        double currentarea = 0.0;   
        Shape [] shapes = { 
            new Circle(0.5f),
            new Rectangle(1.2 , 3.4), 
            new Triangle (2.5,6.1)
        };
        for (Shape s : shapes ){
            System.out.println (currentarea = s.area());
    
            runningtotal += currentarea;
            if (currentarea > max){
                max = currentarea;
            }
        }
        System.out.println("RunningTotal : " + runningtotal);
        System.out.println("Max area: " + max);
        
    }
}

class Circle extends Shape{
    double radius;
    Circle (double radius){
        this.radius = radius;
    }
    public double area (){
        return 3.14*radius*radius;
    }
}

class Rectangle extends Shape {
    double length;
    double bradth;
    Rectangle(double length, double bradth){
        this.length = length;
        this.bradth = bradth;
    }
    public double area(){
        return length * bradth;
    }
}

class Triangle extends Shape {
    double base;
    double height;
    Triangle (double base, double height){
        this.base = base;
        this.height = height;
    }
    public double area(){
        return 0.5 * base * height;
    }
}

