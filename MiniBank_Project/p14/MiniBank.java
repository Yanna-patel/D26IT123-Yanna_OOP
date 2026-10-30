import exception.BankException;
import exception.InsufficientFundsException;
import model.Account;
import model.Command;
import service.BankService;
import util.CommandParser;
import util.Validator;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * MiniBank - final application (Practical 14).
 * The menu from Practical 1 now calls real BankService operations.
 * Accounts are loaded when the program starts and saved when it exits.
 */
public class MiniBank {

    // Practical 1: header record and menu enum.
    record BankInfo(String name, String branch) {
    }

    enum MenuOption {
        OPEN_ACCOUNT,
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
        STATEMENT,
        LIST_ACCOUNTS,
        BATCH_PROCESS,
        REPORT,
        SEARCH_ACCOUNT,
        RUN_DEMO,
        EXIT
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        BankInfo bank = new BankInfo("MiniBank", "CHARUSAT Branch");
        System.out.println(bank);

        BankService service = new BankService();

        try {
            int loaded = service.load();
            System.out.println(
                    "Loaded " + loaded + " saved account(s) from "
                            + service.getAccountsFile());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(
                    "Could not load saved accounts: " + e.getMessage());
        }

        MenuOption[] options = MenuOption.values();
        boolean running = true;

        try {
            while (running) {

                printMenu(options);

                int choice = readChoice();

                if (choice < 1 || choice > options.length) {
                    System.out.println(
                            "Invalid choice. Enter a number from 1 to "
                                    + options.length + ".");
                    continue;
                }

                MenuOption option = options[choice - 1];

                try {
                    switch (option) {
                        case OPEN_ACCOUNT -> openAccount(service);
                        case DEPOSIT -> deposit(service);
                        case WITHDRAW -> withdraw(service);
                        case TRANSFER -> transfer(service);
                        case STATEMENT -> statement(service);
                        case LIST_ACCOUNTS -> listAccounts(service);
                        case BATCH_PROCESS -> batchProcess(service);
                        case REPORT -> report(service);
                        case SEARCH_ACCOUNT -> searchAccount(service);
                        case RUN_DEMO -> LegacyDemo.run(
                                Paths.get("data", "demo"));
                        case EXIT -> {
                            running = false;
                            System.out.println(
                                    "Thank you for using MiniBank. Goodbye!");
                        }
                    }

                } catch (InsufficientFundsException e) {
                    System.out.println("Withdrawal failed: short by "
                            + e.getShortfall());
                } catch (BankException e) {
                    System.out.println("Operation failed: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("File error: " + e.getMessage());
                }
            }

        } catch (NoSuchElementException e) {
            // input ended (for example Ctrl+D) - leave the loop and save.
            System.out.println("\nInput closed. Exiting MiniBank.");

        } finally {
            // Always save on the way out.
            try {
                int saved = service.save();
                System.out.println("Saved " + saved + " account(s).");
            } catch (IOException e) {
                System.out.println(
                        "Could not save accounts: " + e.getMessage());
            }
        }
    }

    // ---------------------------------------------------------------
    // Menu display and input helpers
    // ---------------------------------------------------------------

    private static void printMenu(MenuOption[] options) {
        System.out.println();
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + label(options[i]));
        }
        System.out.print("Enter your choice: ");
    }

    // OPEN_ACCOUNT -> "Open Account"
    private static String label(MenuOption option) {
        String[] words = option.name().toLowerCase().split("_");
        StringBuilder text = new StringBuilder();
        for (String word : words) {
            if (text.length() > 0) {
                text.append(' ');
            }
            text.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1));
        }
        if (option == MenuOption.RUN_DEMO) {
            text.append(" (Practical 1-13 recap)");
        }
        return text.toString();
    }

    private static int readChoice() {
        String line = scanner.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String ask(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static long askAmount(String prompt) {
        while (true) {
            String text = ask(prompt);
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    // ---------------------------------------------------------------
    // Menu actions - each one calls a BankService method
    // ---------------------------------------------------------------

    private static void openAccount(BankService service)
            throws BankException {

        String name = ask("Owner name: ");

        String mobile = ask("Mobile number: ");
        while (!Validator.isValidMobile(mobile)) {
            System.out.println(
                    "Invalid mobile (10 digits, starting with 6-9).");
            mobile = ask("Mobile number: ");
        }

        String email = ask("Email: ");
        while (!Validator.isValidEmail(email)) {
            System.out.println("Invalid email address.");
            email = ask("Email: ");
        }

        String type = ask("Account type (1=Savings, 2=Current, 3=Fixed Deposit): ");
        type = switch (type) {
            case "1" -> "SAVINGS";
            case "2" -> "CURRENT";
            case "3" -> "FD";
            default -> type;
        };

        long opening = askAmount("Opening balance: ");

        Account account =
                service.openAccount(name, mobile, email, type, opening);

        System.out.println("Account opened: " + account);
        System.out.println("Interest rate: " + account.interestRate() + "%");
    }

    private static void deposit(BankService service) throws BankException {
        String number = ask("Account number: ");
        long amount = askAmount("Amount to deposit: ");
        service.deposit(number, amount);
        System.out.println("Deposit successful. New balance: "
                + service.findAccount(number).getBalance());
    }

    private static void withdraw(BankService service) throws BankException {
        String number = ask("Account number: ");
        long amount = askAmount("Amount to withdraw: ");
        service.withdraw(number, amount);
        System.out.println("Withdrawal successful. New balance: "
                + service.findAccount(number).getBalance());
    }

    private static void transfer(BankService service) throws BankException {
        String from = ask("From account number: ");
        String to = ask("To account number: ");
        long amount = askAmount("Amount to transfer: ");
        service.transfer(from, to, amount);
        System.out.println("Transfer successful.");
        System.out.println("  " + service.findAccount(from));
        System.out.println("  " + service.findAccount(to));
    }

    private static void statement(BankService service) throws BankException {
        String number = ask("Account number: ");
        System.out.println(service.statement(number));
    }

    private static void listAccounts(BankService service) {
        ArrayList<Account> accounts = service.listAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts yet. Choose 'Open Account' first.");
            return;
        }
        System.out.println("Accounts (" + accounts.size() + "):");
        for (Account account : accounts) {
            System.out.println("  " + account);
        }
    }

    private static void searchAccount(BankService service) {
        String text = ask("Owner name contains: ");
        ArrayList<Account> found = service.searchByOwner(text);
        if (found.isEmpty()) {
            System.out.println("No account found for '" + text + "'.");
            return;
        }
        for (Account account : found) {
            System.out.println("  " + account);
        }
    }

    private static void batchProcess(BankService service) {

        System.out.println(
                "Enter commands such as DEPOSIT AC0001 500 or WITHDRAW AC0001 200.");
        System.out.println(
                "Press Enter on an empty line to run the batch "
                        + "(an empty batch runs a sample).");

        List<Command> commands = new ArrayList<>();

        while (true) {
            String line = ask("> ");
            if (line.isEmpty()) {
                break;
            }
            try {
                commands.add(CommandParser.parse(line.toUpperCase()));
            } catch (RuntimeException e) {
                System.out.println(
                        "Invalid command. Format: DEPOSIT|WITHDRAW <account> <amount>");
            }
        }

        if (commands.isEmpty()) {
            ArrayList<Account> accounts = service.listAccounts();
            if (accounts.isEmpty()) {
                System.out.println("No accounts to run a sample batch on.");
                return;
            }
            // Sample: 12 deposits of 100 spread over up to 3 accounts.
            int used = Math.min(3, accounts.size());
            for (int i = 0; i < 12; i++) {
                commands.add(CommandParser.parse(
                        "DEPOSIT "
                                + accounts.get(i % used).getAccountNumber()
                                + " 100"));
            }
            System.out.println(
                    "Running sample batch: 12 deposits of 100 on " + used
                            + " account(s).");
        }

        long before = totalBalance(service);

        BankService.BatchResult result = service.processBatch(commands);

        System.out.println("Batch finished: " + result.succeeded()
                + " succeeded, " + result.failed() + " failed (of "
                + result.total() + ").");
        System.out.println("Total balance change: "
                + (totalBalance(service) - before));
    }

    private static long totalBalance(BankService service) {
        long total = 0;
        for (Account account : service.listAccounts()) {
            total += account.getBalance();
        }
        return total;
    }

    private static void report(BankService service) throws IOException {
        System.out.println(service.report());
    }
}
