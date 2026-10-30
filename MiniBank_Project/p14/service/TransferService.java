
package service;

import model.Account;

public class TransferService {

    // Unsafe: opposite transfer directions can deadlock.
    public static void unsafeTransfer(
            Account from, Account to, long amount) {

        synchronized (from) {
            synchronized (to) {
                performTransfer(from, to, amount);
            }
        }
    }

    // Safe: every thread acquires locks in the same order.
    public static void safeTransfer(
            Account from, Account to, long amount) {

        Account first;
        Account second;

        if (from.getAccountNumber().compareTo(
                to.getAccountNumber()) < 0) {
            first = from;
            second = to;
        } else {
            first = to;
            second = from;
        }

        synchronized (first) {
            synchronized (second) {
                performTransfer(from, to, amount);
            }
        }
    }

    private static void performTransfer(
            Account from, Account to, long amount) {

        try {
            from.withdraw(amount);
            to.deposit(amount);

            System.out.println(
                "Transferred " + amount + " from "
                + from.getAccountNumber() + " to "
                + to.getAccountNumber()
            );
        } catch (Exception e) {
            System.out.println(
                "Transfer failed: " + e.getMessage()
            );
        }
    }
}
