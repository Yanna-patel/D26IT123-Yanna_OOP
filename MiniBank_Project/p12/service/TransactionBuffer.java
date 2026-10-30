
package service;

public class TransactionBuffer {

    private final Runnable[] buffer;
    private int head = 0;
    private int tail = 0;
    private int count = 0;
    private boolean closed = false;

    public TransactionBuffer(int capacity) {
        buffer = new Runnable[capacity];
    }

    public synchronized void put(Runnable task)
            throws InterruptedException {

        while (count == buffer.length && !closed) {
            wait();
        }

        if (closed) {
            throw new IllegalStateException("Buffer is closed.");
        }

        buffer[tail] = task;
        tail = (tail + 1) % buffer.length;
        count++;

        notifyAll();
    }

    public synchronized Runnable take()
            throws InterruptedException {

        while (count == 0 && !closed) {
            wait();
        }

        if (count == 0 && closed) {
            return null;
        }

        Runnable task = buffer[head];
        buffer[head] = null;
        head = (head + 1) % buffer.length;
        count--;

        notifyAll();
        return task;
    }

    public synchronized void close() {
        closed = true;
        notifyAll();
    }
}
