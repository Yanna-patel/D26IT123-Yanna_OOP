package service;

import model.Account;

import java.util.List;

public class StatementFormatter {

    public static String buildStatement(Account account) {

        StringBuilder statement = new StringBuilder();

        statement.append("----- Account Statement -----\n");

        statement.append("Account Number: ")
                .append(account.getAccountNumber())
                .append("\n");

        statement.append("Owner Name: ")
                .append(account.getOwnerName())
                .append("\n");

        statement.append("Balance: ")
                .append(account.getBalance())
                .append("\n");

        statement.append("-----------------------------");

        return statement.toString();
    }

    // Practical 14: statement that also lists the account's transactions.
    public static String buildStatement(Account account,
                                        List<String> transactions) {

        StringBuilder statement = new StringBuilder();

        statement.append("----- Account Statement -----\n");

        statement.append("Account Number: ")
                .append(account.getAccountNumber())
                .append("\n");

        statement.append("Owner Name: ")
                .append(account.getOwnerName())
                .append("\n");

        statement.append("Account Type: ")
                .append(account.getClass().getSimpleName())
                .append("\n");

        statement.append("Balance: ")
                .append(account.getBalance())
                .append("\n");

        statement.append("Transactions (")
                .append(transactions.size())
                .append("):\n");

        if (transactions.isEmpty()) {
            statement.append("  (none)\n");
        }

        int number = 1;
        for (String transaction : transactions) {
            statement.append("  ")
                    .append(number++)
                    .append(". ")
                    .append(transaction)
                    .append("\n");
        }

        statement.append("-----------------------------");

        return statement.toString();
    }
}