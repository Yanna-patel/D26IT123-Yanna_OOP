package model;

import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import exception.BankException;

public abstract class Account implements Transactable {

    private final String accountNumber;
    private String ownerName;
    private long balance;
    private boolean active;

    private static long accountCounter = 0;

    private static String generateAccountNumber() {
        accountCounter++;
        return String.format("AC%04d", accountCounter);
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
    public void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                "Deposit amount must be positive."
            );
        }
        balance += amount;
    }

    @Override
    public boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
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
}