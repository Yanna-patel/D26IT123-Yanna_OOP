package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Practical 11 - end-of-day report.
 * Walks a logs folder with NIO, reads every *.log file line by line and adds
 * up the deposits and withdrawals. Empty files and files that do not end
 * with .log are skipped.
 */
public class ReportGenerator {

    public static String generate(Path logsDir, Path reportFile)
            throws IOException {

        long totalDeposits = 0;
        long totalWithdrawals = 0;
        int transferCount = 0;
        int skippedLines = 0;
        int filesRead = 0;

        StringBuilder fileDetails = new StringBuilder();

        List<Path> logFiles = new ArrayList<>();

        if (Files.isDirectory(logsDir)) {
            try (DirectoryStream<Path> stream =
                         Files.newDirectoryStream(logsDir, "*.log")) {
                for (Path file : stream) {
                    logFiles.add(file);
                }
            }
        }

        // Directory order is not guaranteed, so sort for a stable report.
        Collections.sort(logFiles);

        for (Path file : logFiles) {

            BasicFileAttributes attributes =
                    Files.readAttributes(file, BasicFileAttributes.class);

            if (!attributes.isRegularFile() || attributes.size() == 0) {
                fileDetails.append("  ")
                        .append(file.getFileName())
                        .append(" | skipped (empty)\n");
                continue;
            }

            filesRead++;

            fileDetails.append("  ")
                    .append(file.getFileName())
                    .append(" | size: ").append(attributes.size())
                    .append(" bytes | last modified: ")
                    .append(attributes.lastModifiedTime())
                    .append("\n");

            try (BufferedReader reader = Files.newBufferedReader(file)) {

                String line;

                while ((line = reader.readLine()) != null) {

                    line = line.trim();

                    if (line.isEmpty()) {
                        continue;
                    }

                    String[] parts = line.split("\\s+");

                    try {
                        switch (parts[0].toUpperCase()) {
                            case "DEPOSIT" -> {
                                // DEPOSIT <account> <amount>
                                totalDeposits += Long.parseLong(parts[2]);
                            }
                            case "WITHDRAW" -> {
                                // WITHDRAW <account> <amount>
                                totalWithdrawals += Long.parseLong(parts[2]);
                            }
                            case "TRANSFER" -> {
                                // TRANSFER <from> <to> <amount>
                                Long.parseLong(parts[3]);
                                transferCount++;
                            }
                            default -> skippedLines++;
                        }
                    } catch (NumberFormatException
                             | ArrayIndexOutOfBoundsException e) {
                        skippedLines++;
                    }
                }
            }
        }

        StringBuilder report = new StringBuilder();

        report.append("----- End-of-Day Report -----\n");
        report.append("Log files read    : ").append(filesRead).append("\n");
        report.append(fileDetails);
        report.append("Total deposits    : ").append(totalDeposits).append("\n");
        report.append("Total withdrawals : ").append(totalWithdrawals).append("\n");
        report.append("Net change        : ")
                .append(totalDeposits - totalWithdrawals).append("\n");
        report.append("Transfers (count) : ").append(transferCount).append("\n");
        report.append("Skipped lines     : ").append(skippedLines).append("\n");
        report.append("-----------------------------");

        if (reportFile != null) {
            Path parent = reportFile.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(reportFile, report.toString());
        }

        return report.toString();
    }
}
