package Practical11;
import java.io.*;
public class Student implements Serializable {
    private int id;
    private String name ;
    private transient String password;

    public Student (int id, String name , String password){
        this.id = id;
        this.name = name;
        this.password = password;
    }
    public void display (){
        System.out.println("ID      :" + id);
        System.out.println("Name    :" + name);
        System.out.println("Password:" + password);
    }
}

