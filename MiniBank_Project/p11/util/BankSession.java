package util;

public class BankSession implements AutoCloseable {

    public BankSession() {
        System.out.println("Bank session opened.");
    }

    public void performOperation() {
        System.out.println("Performing banking operation...");
    }

    @Override
    public void close() {
        System.out.println("Bank session closed.");
    }
}