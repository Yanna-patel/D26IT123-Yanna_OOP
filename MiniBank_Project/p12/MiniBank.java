
import model.Account;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;
import model.InterestBearing;
import model.Premium;

import util.WithdrawRule;
import util.BankSession;

import exception.*;

import service.TransactionProcessor;
import service.TransactionTask;
import service.ProducerConsumerDemo;
import service.TransferService;

import service.StatePersister;
import service.TransactionLog;
import service.ReportGenerator;
import service.AccountStore;
import model.Command;
import util.CommandParser;

import java.util.ArrayList;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static service.StatementFormatter.buildStatement;

public class MiniBank {

    public static void main(String[] args) {

        Account[] accounts = new Account[3];

        accounts[0] =
                new SavingsAccount("Nandani", 1000, 500);

        accounts[1] =
                new CurrentAccount("Hitanshi", 1000, 2000);

        accounts[2] =
                new FixedDepositAccount("Priya", 5000);

        // Exception Handling

        System.out.println("\n----- Exception Handling -----");

        try {
            accounts[0].deposit(500);
            System.out.println("Deposit successful.");

        } catch (InvalidAmountException e) {
            System.out.println("Deposit failed: " + e.getMessage());

        } finally {
            System.out.println("Deposit operation checked.");
        }

        try {
            accounts[0].withdraw(10000);

        } catch (InsufficientFundsException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
            System.out.println("Shortfall: " + e.getShortfall());

        } catch (InvalidAmountException e) {
            System.out.println("Invalid withdrawal: " + e.getMessage());

        } finally {
            System.out.println("Withdrawal operation checked.");
        }

        try {
            accounts[0].transfer(accounts[1], 200);
            System.out.println("Transfer successful.");

        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
            System.out.println("Shortfall: " + e.getShortfall());

        } catch (InvalidAmountException e) {
            System.out.println("Transfer failed: " + e.getMessage());

        } catch (BankException e) {
            System.out.println("Banking error: " + e.getMessage());

        } finally {
            System.out.println("Transfer request processed.");
        }

        // Try-with-resources

        System.out.println("\n----- Try With Resources -----");

        try (BankSession session = new BankSession()) {
            session.performOperation();

        } finally {
            System.out.println("Session example finished.");
        }

        // Accounts and Interest Rates

        System.out.println(
                "\n----- Accounts and Interest Rates -----"
        );

        for (Account account : accounts) {
            System.out.println(account);
            System.out.println(
                    "Interest Rate: " + account.interestRate() + "%"
            );
            System.out.println();
        }

        // Withdrawal Testing

        System.out.println("----- Withdrawal Testing -----");

        try {
            System.out.println(
                    "Savings withdrawal: " + accounts[0].withdraw(400)
            );

        } catch (InsufficientFundsException |
                 InvalidAmountException e) {
            System.out.println(
                    "Savings withdrawal failed: " + e.getMessage()
            );
        }

        System.out.println(
                "Savings balance: " + accounts[0].getBalance()
        );

        try {
            System.out.println(
                    "Current withdrawal: " + accounts[1].withdraw(2500)
            );

        } catch (InsufficientFundsException |
                 InvalidAmountException e) {
            System.out.println(
                    "Current withdrawal failed: " + e.getMessage()
            );
        }

        System.out.println(
                "Current balance: " + accounts[1].getBalance()
        );

        try {
            System.out.println(
                    "Fixed Deposit withdrawal: " + accounts[2].withdraw(1000)
            );

        } catch (InsufficientFundsException |
                 InvalidAmountException e) {
            System.out.println(
                    "Fixed Deposit withdrawal failed: " + e.getMessage()
            );
        }

        System.out.println(
                "Fixed Deposit balance: " + accounts[2].getBalance()
        );

        // Withdraw Rule Testing

        System.out.println("\n----- Withdraw Rule Testing -----");

        WithdrawRule rule1 = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.getBalance() >= amount;
            }
        };

        System.out.println(
                "Anonymous class: " + rule1.allow(accounts[0], 300)
        );

        WithdrawRule rule2 =
                (account, amount) -> account.getBalance() >= amount;

        System.out.println(
                "Lambda expression: " + rule2.allow(accounts[0], 300)
        );

        // Yearly Interest

        System.out.println("\n----- Yearly Interest -----");

        for (Account account : accounts) {
            if (account instanceof InterestBearing interestBearing) {
                System.out.println(
                        account.getOwnerName()
                                + " Yearly Interest: "
                                + interestBearing.yearlyInterest()
                );
            }
        }

        // Premium Account

        System.out.println("\n----- Premium Account -----");

        for (Account account : accounts) {
            if (account instanceof Premium) {
                System.out.println(
                        account.getOwnerName()
                                + " is a Premium Account"
                );
            }
        }

        // Account Statement

        System.out.println();
        System.out.println(buildStatement(accounts[0]));

        // ==========================================
        // PRACTICAL 10 - PART 1
        // Thread Pool and Transaction Processing
        // ==========================================

        System.out.println("\n----- Practical 10: Thread Pool -----");

        TransactionProcessor processor = new TransactionProcessor();

        for (int i = 1; i <= 10; i++) {
            processor.submit(
                    new TransactionTask(accounts[0], true, 100)
            );
        }

        for (int i = 1; i <= 5; i++) {
            processor.submit(
                    new TransactionTask(accounts[0], false, 50)
            );
        }

        // Wait for all submitted transactions to finish.
        processor.stop();

        System.out.println("All thread-pool tasks completed.");
        System.out.println(
                "Balance after thread-pool tasks: "
                        + accounts[0].getBalance()
        );

        // ==========================================
        // PRACTICAL 10 - PART 2
        // Producer-Consumer using wait() and notifyAll()
        // ==========================================

        System.out.println(
                "\n----- Practical 10: Producer-Consumer -----"
        );

        try {
            ProducerConsumerDemo.run(accounts[0]);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Producer-consumer interrupted.");
            return;
        }

        System.out.println(
                "Balance after producer-consumer: "
                        + accounts[0].getBalance()
        );

        // ==========================================
        // PRACTICAL 10 - PART 3 AND 4
        // Concurrent Transfers with Safe Lock Ordering
        // ==========================================

        System.out.println(
                "\n----- Practical 10: Safe Transfers -----"
        );

        Thread t1 = new Thread(
                () -> TransferService.safeTransfer(
                        accounts[0], accounts[1], 500
                ),
                "Transfer-1"
        );

        Thread t2 = new Thread(
                () -> TransferService.safeTransfer(
                        accounts[1], accounts[0], 300
                ),
                "Transfer-2"
        );

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Transfer threads interrupted.");
            return;
        }

        System.out.println("\n----- Practical 10 Final Balances -----");

        System.out.println(accounts[0]);
        System.out.println(accounts[1]);

        // ==========================================
        // PRACTICAL 11
        // Serialization, Transaction Log and NIO Report
        // ==========================================

        System.out.println(
                "\n----- Practical 11: Save and Load Accounts -----"
        );

        Path dataDir = Paths.get("data");
        Path accountsFile = dataDir.resolve("accounts.dat");
        Path logsDir = dataDir.resolve("logs");
        Path reportFile = dataDir.resolve("report.txt");

        try {
            // 1. Save the accounts, then reload them into a NEW array.
            StatePersister.save(accounts, accountsFile);
            System.out.println("Saved " + accounts.length
                    + " accounts to " + accountsFile);

            Account[] reloaded = StatePersister.load(accountsFile);

            System.out.println("Reloaded accounts:");
            for (Account account : reloaded) {
                System.out.println(account);
            }

            boolean same = reloaded.length == accounts.length;
            for (int i = 0; same && i < accounts.length; i++) {
                same = accounts[i].equals(reloaded[i])
                        && accounts[i].getBalance() == reloaded[i].getBalance();
            }
            System.out.println("Reloaded data matches saved data: " + same);

            // 2. Write the transaction logs (append-only).
            System.out.println(
                    "\n----- Practical 11: Transaction Log -----"
            );

            Files.createDirectories(logsDir);
            clearFolder(logsDir);   // start each demo run with fresh logs

            String a1 = accounts[0].getAccountNumber();
            String a2 = accounts[1].getAccountNumber();
            String a3 = accounts[2].getAccountNumber();

            Path morningLog = logsDir.resolve("morning.log");
            Path eveningLog = logsDir.resolve("evening.log");

            TransactionLog.append(morningLog, "DEPOSIT " + a1 + " 500");
            TransactionLog.append(morningLog, "WITHDRAW " + a1 + " 400");
            TransactionLog.append(morningLog, "WITHDRAW " + a3 + " 1000");
            TransactionLog.append(eveningLog, "DEPOSIT " + a2 + " 200");
            TransactionLog.append(eveningLog, "TRANSFER " + a1 + " " + a2 + " 200");
            TransactionLog.append(eveningLog, "WITHDRAW " + a2 + " 300");

            // Files the report must skip (empty file, non-.log file).
            Files.createFile(logsDir.resolve("empty.log"));
            Files.writeString(logsDir.resolve("notes.txt"), "DEPOSIT X 99999");

            System.out.println("Logged 6 transactions in 2 files.");

            // 3. Read every log with NIO and build the report.
            System.out.println(
                    "\n----- Practical 11: Report -----"
            );

            String report = ReportGenerator.generate(logsDir, reportFile);
            System.out.println(report);
            System.out.println("Report written to " + reportFile);

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Practical 11 file error: " + e.getMessage());
        }

        // ==========================================
        // PRACTICAL 12
        // Collections: HashMap store, sorting, ConcurrentHashMap
        // ==========================================

        System.out.println(
                "\n----- Practical 12: Account Store (HashMap) -----"
        );

        AccountStore store = new AccountStore();

        Account s1 = new SavingsAccount("Riya", 5000, 500);
        Account s2 = new CurrentAccount("Karan", 2000, 1000);
        Account s3 = new FixedDepositAccount("Meera", 9000);

        store.add(s1);
        store.add(s2);
        store.add(s3);

        store.addHistory("DEPOSIT " + s1.getAccountNumber() + " 5000");
        store.addHistory("DEPOSIT " + s2.getAccountNumber() + " 2000");
        store.addHistory("DEPOSIT " + s3.getAccountNumber() + " 9000");

        System.out.println("Accounts in store: " + store.size());
        System.out.println(
                "get(" + s2.getAccountNumber() + ") -> "
                        + store.get(s2.getAccountNumber())
        );
        System.out.println(
                "get(AC9999) -> " + store.get("AC9999")
        );

        System.out.println("\nSorted by balance (highest first):");
        for (Account account : store.sortedByBalance()) {
            System.out.println(account);
        }

        System.out.println("\nNatural order (Comparable, by account number):");
        ArrayList<Account> natural = store.getAll();
        java.util.Collections.sort(natural);
        for (Account account : natural) {
            System.out.println(account);
        }

        System.out.println("\nBalance above 4000:");
        for (Account account : store.balanceAbove(4000)) {
            System.out.println(account);
        }

        System.out.println("\nTransaction history:");
        for (String entry : store.getHistory()) {
            System.out.println("  " + entry);
        }

        System.out.println(
                "\n----- Practical 12: Thread-safe Map in Processor -----"
        );

        TransactionProcessor batchProcessor = new TransactionProcessor();
        batchProcessor.registerAccount(s1);
        batchProcessor.registerAccount(s2);
        batchProcessor.registerAccount(s3);

        System.out.println(
                "Accounts registered in ConcurrentHashMap: "
                        + batchProcessor.accountCount()
        );

        for (int i = 0; i < 20; i++) {
            batchProcessor.submitTransaction(
                    CommandParser.parse(
                            "DEPOSIT " + s1.getAccountNumber() + " 100"
                    )
            );
        }

        batchProcessor.submitTransaction(
                CommandParser.parse("WITHDRAW " + s2.getAccountNumber() + " 500")
        );
        batchProcessor.submitTransaction(
                CommandParser.parse("DEPOSIT AC9999 100")
        );

        batchProcessor.stop();

        System.out.println(
                "Balance of " + s1.getAccountNumber()
                        + " after 20 concurrent deposits of 100: "
                        + s1.getBalance() + " (expected 7000)"
        );
        System.out.println(
                "Balance of " + s2.getAccountNumber()
                        + " after withdraw of 500: "
                        + s2.getBalance() + " (expected 1500)"
        );

        System.out.println("\nMiniBank Practical 12 completed.");
    }

    // Deletes the files inside a folder (used only to reset demo logs).
    private static void clearFolder(Path folder) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    Files.delete(file);
                }
            }
        }
    }
}
