package model;

import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import exception.BankException;

import java.io.Serializable;

public abstract class Account
        implements Transactable, Serializable, Comparable<Account> {

    // Practical 11: needed so Account objects can be saved to a file.
    private static final long serialVersionUID = 1L;

    private final String accountNumber;
    private String ownerName;
    private long balance;
    private boolean active;

    private static long accountCounter = 0;

    private static synchronized String generateAccountNumber() {
        accountCounter++;
        return String.format("AC%04d", accountCounter);
    }

    // Practical 11: after accounts are loaded from a file, the static counter
    // (which restarts at 0 in every run) must continue after the highest
    // loaded number, otherwise a new account could reuse an old number.
    public static synchronized void ensureCounterAtLeast(long value) {
        if (value > accountCounter) {
            accountCounter = value;
        }
    }

    public Account(String ownerName, long balance) {
        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = balance;
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                "Deposit amount must be positive."
            );
        }
        balance += amount;
    }

    @Override
    public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                "Withdrawal amount must be positive."
            );
        }
        if (balance < amount) {
            long shortfall = amount - balance;

            throw new InsufficientFundsException(
                "Insufficient funds. Shortfall: " + shortfall,
                shortfall
            );
        }
        balance -= amount;
        return true;
    }

    public void transfer(Account to, long amount) throws BankException {

        try {
            if (to == null) {
                throw new BankException(
                    "Destination account cannot be null."
                );
            }

            withdraw(amount);
            to.deposit(amount);

            System.out.println("Transfer successful.");

        } catch (BankException e) {
            System.out.println("Transfer failed: " + e.getMessage());
            throw e;

        } finally {
            System.out.println("Transfer operation completed.");
        }
    }

    // Abstract methods
    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return accountNumber + " - "
                + ownerName + " - Balance: "
                + balance;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Account)) {
            return false;
        }

        Account account = (Account) o;

        return accountNumber.equals(account.accountNumber);
    }

    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }

    // Practical 12: natural ordering = by account number
    // (consistent with equals(), which also uses the account number).
    @Override
    public int compareTo(Account other) {
        return accountNumber.compareTo(other.accountNumber);
    }
}