package service;

import model.Account;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Practical 11 - saves and loads the whole array of accounts using
 * object serialization (byte streams).
 */
public class StatePersister {

    public static void save(Account[] accounts, Path file) throws IOException {

        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        // try-with-resources closes the stream even if writing fails.
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(accounts);
        }
    }

    public static Account[] load(Path file)
            throws IOException, ClassNotFoundException {

        Account[] accounts;

        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(file))) {
            accounts = (Account[]) in.readObject();
        }

        // Make sure newly created accounts continue after the loaded ones.
        long highest = 0;
        for (Account account : accounts) {
            String digits = account.getAccountNumber().replaceAll("\\D", "");
            if (!digits.isEmpty()) {
                highest = Math.max(highest, Long.parseLong(digits));
            }
        }
        Account.ensureCounterAtLeast(highest);

        return accounts;
    }
}
