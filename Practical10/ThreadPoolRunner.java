import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolRunner {
    public static void main (String [] args) throws InterruptedException{
        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (int i = 1 ; i <= 10 ; i++){
            int taskId = i;

            executor.submit(() ->{
                System.out.println("Task " + taskId + "running on " + Thread.currentThread().getName());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        
        executor.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS);

        System.out.println("All tasks completed.");
    }
}