public class ProducerConsumer{
    public static void main (String [] args)throws InterruptedException{
        Buffer buffer = new Buffer();
        int numberOfItems = 10;

        Thread producer = new Thread(() ->{
            try{
                for (int i = 0 ; i <= numberOfItems ; i++){
                    buffer.produce(i);
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        });

        Thread consumer = new Thread(() ->{
            try{
                for (int i = 1; i<= numberOfItems; i++){
                    buffer.consume();
                }
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        System.out.println("All items produced and consumed");
    }
}