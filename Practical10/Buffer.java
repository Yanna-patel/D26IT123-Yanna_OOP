public class Buffer {
    int [] buffer = new int[3];
    
    int count = 0;
    int in = 0;
    int out = 0;

    synchronized void produce (int value) throws InterruptedException{
        while (count == buffer.length){
            wait();
        }

        buffer [in] = value;
        in = (in + 1)% buffer.length;
        count ++;

        System.out.println("Produced: " + value);

        notify();
            
    }
    synchronized int consume () throws InterruptedException{
        while (count ==0){
            wait();
        }

        int value = buffer[out];
        out = (out + 1)% buffer.length;
        count --;

        System.out.println("Consumed: " + value);

        notify();

        return value;
    }    
}


