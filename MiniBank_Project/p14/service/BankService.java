package service;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InvalidAmountException;
import exception.InvalidInputException;
import model.Account;
import model.Command;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;
import util.Validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Practical 14 - the service layer. The menu (user interface) calls this class;
 * this class validates input, uses Repository<Account> for storage, throws the
 * custom exceptions, and writes every successful operation to the log.
 */
public class BankService {

    public static final long SAVINGS_MIN_BALANCE = 500;
    public static final long CURRENT_OVERDRAFT_LIMIT = 2000;
    public static final int MAX_NAME_LENGTH = 30;

    /** Outcome of a batch run. */
    public record BatchResult(int total, int succeeded, int failed) {
    }

    private final Repository<Account> repository = new Repository<>();

    private final Path accountsFile;
    private final Path logsDir;
    private final Path logFile;
    private final Path reportFile;

    public BankService() {
        this(Paths.get("data"));
    }

    public BankService(Path dataDir) {
        this.accountsFile = dataDir.resolve("accounts.dat");
        this.logsDir = dataDir.resolve("logs");
        this.logFile = logsDir.resolve("transactions.log");
        this.reportFile = dataDir.resolve("report.txt");
    }

    // ------------------------------------------------------------------
    // Persistence (Practical 11)
    // ------------------------------------------------------------------

    /** Loads saved accounts, if any. Returns how many were loaded. */
    public int load() throws IOException, ClassNotFoundException {
        if (!Files.exists(accountsFile)) {
            return 0;
        }
        Account[] loaded = StatePersister.load(accountsFile);
        repository.saveAll(loaded);
        return loaded.length;
    }

    /** Saves every account. Returns how many were saved. */
    public int save() throws IOException {
        ArrayList<Account> all = listAccounts();
        StatePersister.save(all.toArray(new Account[0]), accountsFile);
        return all.size();
    }

    public Path getAccountsFile() {
        return accountsFile;
    }

    // ------------------------------------------------------------------
    // Banking operations
    // ------------------------------------------------------------------

    public Account openAccount(String owner, String mobile, String email,
                               String type, long openingBalance)
            throws BankException {

        if (owner == null || owner.isBlank()) {
            throw new InvalidInputException("Owner name cannot be empty.");
        }
        owner = owner.trim();
        if (owner.length() > MAX_NAME_LENGTH) {
            throw new InvalidInputException(
                    "Owner name must be at most " + MAX_NAME_LENGTH
                            + " characters.");
        }
        if (mobile == null || !Validator.isValidMobile(mobile.trim())) {
            throw new InvalidInputException(
                    "Invalid mobile number (10 digits, starting with 6-9).");
        }
        if (email == null || !Validator.isValidEmail(email.trim())) {
            throw new InvalidInputException("Invalid email address.");
        }
        if (openingBalance < 0) {
            throw new InvalidAmountException(
                    "Opening balance cannot be negative.");
        }

        String accountType = type == null ? "" : type.trim().toUpperCase();

        Account account = switch (accountType) {
            case "SAVINGS" -> {
                if (openingBalance < SAVINGS_MIN_BALANCE) {
                    throw new InvalidAmountException(
                            "A savings account needs an opening balance of at least "
                                    + SAVINGS_MIN_BALANCE + ".");
                }
                yield new SavingsAccount(
                        owner, openingBalance, SAVINGS_MIN_BALANCE);
            }
            case "CURRENT" -> new CurrentAccount(
                    owner, openingBalance, CURRENT_OVERDRAFT_LIMIT);
            case "FD", "FIXED", "FIXED_DEPOSIT" -> {
                if (openingBalance <= 0) {
                    throw new InvalidAmountException(
                            "A fixed deposit needs an opening balance above 0.");
                }
                yield new FixedDepositAccount(owner, openingBalance);
            }
            default -> throw new InvalidInputException(
                    "Unknown account type '" + type
                            + "'. Use SAVINGS, CURRENT or FD.");
        };

        repository.save(account);

        // The opening balance is the account's first deposit.
        if (openingBalance > 0) {
            log("DEPOSIT " + account.getAccountNumber() + " " + openingBalance);
        }

        return account;
    }

    public void deposit(String accountNumber, long amount)
            throws BankException {
        applyDeposit(findAccount(accountNumber), amount);
    }

    public void withdraw(String accountNumber, long amount)
            throws BankException {
        applyWithdraw(findAccount(accountNumber), amount);
    }

    public void transfer(String fromNumber, String toNumber, long amount)
            throws BankException {

        Account from = findAccount(fromNumber);
        Account to = findAccount(toNumber);

        if (from.equals(to)) {
            throw new BankException("Cannot transfer to the same account.");
        }
        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Transfer amount must be positive.");
        }

        // Always lock the account with the smaller number first (Practical 10)
        // so two opposite transfers can never deadlock.
        Account first = from.compareTo(to) < 0 ? from : to;
        Account second = first == from ? to : from;

        synchronized (first) {
            synchronized (second) {
                checkWithdrawRule(from, amount);
                from.withdraw(amount);
                to.deposit(amount);
            }
        }

        log("TRANSFER " + from.getAccountNumber() + " "
                + to.getAccountNumber() + " " + amount);
    }

    public String statement(String accountNumber) throws BankException {
        Account account = findAccount(accountNumber);
        return StatementFormatter.buildStatement(
                account, transactionsFor(account.getAccountNumber()));
    }

    /** All accounts, in account-number order. */
    public ArrayList<Account> listAccounts() {
        ArrayList<Account> all = repository.findAll();
        Collections.sort(all);
        return all;
    }

    /** Supplementary: accounts whose owner name contains the text. */
    public ArrayList<Account> searchByOwner(String text) {
        ArrayList<Account> result = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return result;
        }
        String wanted = text.trim().toLowerCase();
        for (Account account : listAccounts()) {
            if (account.getOwnerName().toLowerCase().contains(wanted)) {
                result.add(account);
            }
        }
        return result;
    }

    public Account findAccount(String accountNumber)
            throws AccountNotFoundException {

        String key = accountNumber == null
                ? "" : accountNumber.trim().toUpperCase();

        Account account = repository.findById(key);

        if (account == null) {
            throw new AccountNotFoundException(
                    "Account not found: " + accountNumber);
        }
        return account;
    }

    // ------------------------------------------------------------------
    // Batch processing (Practical 10 processor + Practical 12 safe map)
    // ------------------------------------------------------------------

    public BatchResult processBatch(List<Command> commands) {

        TransactionProcessor processor = new TransactionProcessor();

        // The pool threads look accounts up in the processor's
        // ConcurrentHashMap (thread-safe store).
        for (Account account : repository.findAll()) {
            processor.registerAccount(account);
        }

        AtomicInteger succeeded = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();

        for (Command command : commands) {
            processor.submit(() -> {

                String text = command.type() + " "
                        + command.accountNumber() + " " + command.amount();

                try {
                    Account account =
                            processor.getAccount(command.accountNumber());

                    if (account == null) {
                        throw new AccountNotFoundException(
                                "Account not found: "
                                        + command.accountNumber());
                    }

                    switch (command.type()) {
                        case DEPOSIT -> applyDeposit(account, command.amount());
                        case WITHDRAW -> applyWithdraw(account, command.amount());
                        case TRANSFER -> throw new BankException(
                                "TRANSFER needs a destination account");
                    }

                    succeeded.incrementAndGet();
                    System.out.println("  [" + Thread.currentThread().getName()
                            + "] " + text + " -> OK");

                } catch (BankException e) {
                    failed.incrementAndGet();
                    System.out.println("  [" + Thread.currentThread().getName()
                            + "] " + text + " -> FAILED: " + e.getMessage());
                }
            });
        }

        // Wait until every submitted transaction has finished.
        processor.stop();

        return new BatchResult(
                commands.size(), succeeded.get(), failed.get());
    }

    // ------------------------------------------------------------------
    // Report (Practical 11)
    // ------------------------------------------------------------------

    public String report() throws IOException {
        return ReportGenerator.generate(logsDir, reportFile);
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    private void applyDeposit(Account account, long amount)
            throws BankException {
        account.deposit(amount);
        log("DEPOSIT " + account.getAccountNumber() + " " + amount);
    }

    private void applyWithdraw(Account account, long amount)
            throws BankException {
        synchronized (account) {
            checkWithdrawRule(account, amount);
            account.withdraw(amount);   // also checks amount and balance
        }
        log("WITHDRAW " + account.getAccountNumber() + " " + amount);
    }

    // Applies the account-type rule (minimum balance, locked deposit) when the
    // plain balance check would otherwise pass. Insufficient balance and bad
    // amounts are reported by Account.withdraw() itself.
    private void checkWithdrawRule(Account account, long amount)
            throws BankException {
        if (amount > 0
                && account.getBalance() >= amount
                && !account.canWithdraw(amount)) {
            throw new BankException(
                    "Withdrawal not allowed by the rules of this "
                            + "account type (minimum balance or locked deposit).");
        }
    }

    private void log(String line) {
        try {
            TransactionLog.append(logFile, line);
        } catch (IOException e) {
            System.out.println(
                    "Warning: could not write the transaction log: "
                            + e.getMessage());
        }
    }

    private List<String> transactionsFor(String accountNumber) {

        List<String> result = new ArrayList<>();

        if (!Files.exists(logFile)) {
            return result;
        }

        try {
            for (String line : Files.readAllLines(logFile)) {
                String[] parts = line.trim().split("\\s+");

                boolean mine = parts.length >= 3
                        && (parts[1].equals(accountNumber)
                        || (parts[0].equals("TRANSFER")
                        && parts.length >= 4
                        && parts[2].equals(accountNumber)));

                if (mine) {
                    result.add(line.trim());
                }
            }
        } catch (IOException e) {
            // an unreadable log simply gives an empty history
        }

        return result;
    }
}
