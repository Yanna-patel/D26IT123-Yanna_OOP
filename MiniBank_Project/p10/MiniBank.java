
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

        System.out.println("\nMiniBank Practical 10 completed.");
    }
}
