
package service;

import model.Account;

public class TransactionTask implements Runnable {

    private final Account account;
    private final boolean deposit;
    private final long amount;

    public TransactionTask(Account account, boolean deposit, long amount) {
        this.account = account;
        this.deposit = deposit;
        this.amount = amount;
    }

    @Override
    public void run() {
        try {
            if (deposit) {
                account.deposit(amount);

                System.out.println(
                    Thread.currentThread().getName()
                    + " deposited " + amount
                    + " into " + account.getAccountNumber()
                );
            } else {
                account.withdraw(amount);

                System.out.println(
                    Thread.currentThread().getName()
                    + " withdrew " + amount
                    + " from " + account.getAccountNumber()
                );
            }
        } catch (Exception e) {
            System.out.println("Transaction failed: " + e.getMessage());
        }
    }
}
