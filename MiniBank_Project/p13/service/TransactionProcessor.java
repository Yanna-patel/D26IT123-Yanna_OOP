
package service;

import model.Account;
import model.Command;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransactionProcessor {

    private final ExecutorService executor =
            Executors.newFixedThreadPool(4);

    // Practical 12: shared accounts kept in a thread-safe map, because the
    // pool threads look accounts up at the same time.
    private final ConcurrentHashMap<String, Account> accounts =
            new ConcurrentHashMap<>();

    public void submit(Runnable task) {
        executor.execute(task);
    }

    public void registerAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public int accountCount() {
        return accounts.size();
    }

    // Looks the account up in the shared map and submits the matching task.
    // Only DEPOSIT and WITHDRAW work from a single-account Command.
    public void submitTransaction(Command command) {

        Account account = accounts.get(command.accountNumber());

        if (account == null) {
            System.out.println(
                    "Transaction failed: account "
                            + command.accountNumber() + " not found");
            return;
        }

        switch (command.type()) {
            case DEPOSIT -> submit(
                    new TransactionTask(account, true, command.amount()));
            case WITHDRAW -> submit(
                    new TransactionTask(account, false, command.amount()));
            case TRANSFER -> System.out.println(
                    "Transaction failed: TRANSFER needs a destination account");
        }
    }

    public void stop() {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                executor.awaitTermination(10, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
