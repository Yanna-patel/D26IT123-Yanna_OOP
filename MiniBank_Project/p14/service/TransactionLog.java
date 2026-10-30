package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Practical 11 - append-only transaction log.
 * Each line looks like "DEPOSIT AC0001 500".
 */
public class TransactionLog {

    // synchronized: many threads (Practical 10) may log at the same time.
    public static synchronized void append(Path file, String line)
            throws IOException {

        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.write(
                file,
                (line + System.lineSeparator()).getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }
}
