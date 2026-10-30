package service;

import model.Account;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Practical 12 - keeps accounts in a HashMap keyed by account number and a
 * transaction history in an ArrayList.
 */
public class AccountStore {

    private final HashMap<String, Account> accounts = new HashMap<>();
    private final ArrayList<String> history = new ArrayList<>();

    public void add(Account account) {
        accounts.put(account.getAccountNumber(), account);
        history.add("ADDED " + account.getAccountNumber());
    }

    // Returns null when the account number is not in the store.
    public Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public ArrayList<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    public int size() {
        return accounts.size();
    }

    public void addHistory(String entry) {
        history.add(entry);
    }

    public ArrayList<String> getHistory() {
        return new ArrayList<>(history);
    }

    // Highest balance first.
    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> list = new ArrayList<>(accounts.values());
        list.sort(Comparator.comparingLong(Account::getBalance).reversed());
        return list;
    }

    // Supplementary: every account whose balance is above a given value.
    public ArrayList<Account> balanceAbove(long limit) {
        ArrayList<Account> result = new ArrayList<>();
        for (Account account : accounts.values()) {
            if (account.getBalance() > limit) {
                result.add(account);
            }
        }
        result.sort(null);   // natural order = Comparable by account number
        return result;
    }
}
