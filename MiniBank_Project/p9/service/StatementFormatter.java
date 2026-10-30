package service;

import model.Account;

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
}