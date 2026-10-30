
package service;

import model.Account;

public class ProducerConsumerDemo {

    public static void run(Account account) throws InterruptedException {

        TransactionBuffer buffer = new TransactionBuffer(3);

        Thread consumer = new Thread(() -> {
            try {
                Runnable task;

                while ((task = buffer.take()) != null) {
                    task.run();
                }

                System.out.println("Consumer finished.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    buffer.put(new TransactionTask(account, true, 100));
                    System.out.println("Produced transaction " + i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                buffer.close();
            }
        }, "Producer");

        consumer.start();
        producer.start();

        producer.join();
        consumer.join();
    }
}
