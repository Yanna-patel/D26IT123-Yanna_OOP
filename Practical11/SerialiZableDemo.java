import java.io.*;
public class SerialiZableDemo {
    public static void main (String [] args){
        Student [] students = {
            new Student(101,"Yanna","abc123"),
            new Student(102,"Harry","xyz789"),
            new Student(103,"Krishna","pqr456")
        };
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("students.dat"))){
            out.writeObject(students);
            System.out.println("Objects saved successfully");
        } catch (IOException e){
            e.printStackTrace ();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("students.dat"))){
            Student [] loadedStudents = (Student [])in.readObject();

            System.out.println("Objects loaded successfully");

            for (Student s: loadedStudents){
                s.display();
            }
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}