public abstract class Employee{
    Employee (String name , int id){
        this.name = name;
        this.id = id;
    }
    public abstract int monthlySalary();
    String name;
    int id;

    public static void main (String [] args){
        int totalsalary = 0;
        Employee [] emp = {
            new FullTime ("Yanna" , 1 , 2000000),
            new PartTime ("Radhika" , 2 , 8, 1500),
            new Intern ("Ankita" , 3 , 50000)
        };
        for (Employee e : emp){
            if (e instanceof Intern){
                System.out.println("Only for interns");
            }
            System.out.println( e.id + " " + e.name + " Salary is : " + e.monthlySalary());
            totalsalary += e.monthlySalary() ;
        }
        
        System.out.println( " Total Salary is : " + totalsalary);
    }
}

class FullTime extends Employee{
    int salary;
    FullTime (String name , int id ,int salary){
        super(name,id);
        this.salary = salary;
    }
    public int monthlySalary(){
        return salary;
    }

}

class PartTime extends Employee{
    int hours;
    int rate;
    PartTime (String name , int id ,int hours , int rate){
        super(name,id);
        this.hours = hours;
        this.rate = rate;
    }
    public int monthlySalary(){
        return hours*rate;
    }

}

class Intern extends Employee {
    int stipend ;
    Intern (String name, int id, int stipend ){
        super(name,id);
        this.stipend = stipend;
    }
    public int monthlySalary(){
        return stipend;
    }

}