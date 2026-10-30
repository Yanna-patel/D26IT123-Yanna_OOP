import exception.AccountNotFoundException;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import exception.InvalidInputException;
import model.Account;
import model.Command;
import service.BankService;
import util.CommandParser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Practical 14 - unit tests for the BankService layer.
 * Every test uses its own temporary data folder, so no real data is touched.
 */
class BankServiceTest {

    @TempDir
    Path tempDir;

    BankService service;

    @BeforeEach
    void setUp() {
        service = new BankService(tempDir);
    }

    private Account savings(long opening) throws BankException {
        return service.openAccount(
                "Riya", "9876543210", "riya@mail.com", "SAVINGS", opening);
    }

    // ---- the three tests required by the practical ----

    @Test
    void depositIncreasesBalance() throws BankException {
        Account account = savings(1000);

        service.deposit(account.getAccountNumber(), 500);

        assertEquals(1500, account.getBalance());
    }

    @Test
    void withdrawalOverTheLimitThrowsException() throws BankException {
        Account account = savings(1000);

        InsufficientFundsException e = assertThrows(
                InsufficientFundsException.class,
                () -> service.withdraw(account.getAccountNumber(), 5000));

        assertEquals(4000, e.getShortfall());
        assertEquals(1000, account.getBalance());   // unchanged
    }

    @Test
    void manyConcurrentDepositsKeepBalanceCorrect() throws Exception {
        Account account = service.openAccount(
                "Karan", "9123456780", "karan@mail.com", "CURRENT", 0);

        int threads = 10;
        int depositsEach = 500;
        Thread[] workers = new Thread[threads];

        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                for (int j = 0; j < depositsEach; j++) {
                    try {
                        service.deposit(account.getAccountNumber(), 1);
                    } catch (BankException e) {
                        fail(e.getMessage());
                    }
                }
            });
            workers[i].start();
        }
        for (Thread worker : workers) {
            worker.join();
        }

        assertEquals(threads * depositsEach, account.getBalance());

        // the thread-safe log must also hold every single deposit
        Path log = tempDir.resolve("logs").resolve("transactions.log");
        assertEquals(threads * depositsEach, Files.readAllLines(log).size());
    }

    @Test
    void heavyConcurrentDepositsOnOneAccountStayCorrect() throws Exception {
        // No file I/O here, so the threads really do collide on the balance.
        Account account = service.openAccount(
                "Karan", "9123456780", "karan@mail.com", "CURRENT", 0);

        int threads = 8;
        int depositsEach = 200_000;
        Thread[] workers = new Thread[threads];

        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                for (int j = 0; j < depositsEach; j++) {
                    try {
                        account.deposit(1);
                    } catch (BankException e) {
                        fail(e.getMessage());
                    }
                }
            });
            workers[i].start();
        }
        for (Thread worker : workers) {
            worker.join();
        }

        assertEquals((long) threads * depositsEach, account.getBalance());
    }

    // ---- account rules ----

    @Test
    void nonPositiveAmountsAreRejected() throws BankException {
        Account account = savings(1000);
        String number = account.getAccountNumber();

        assertThrows(InvalidAmountException.class,
                () -> service.deposit(number, 0));
        assertThrows(InvalidAmountException.class,
                () -> service.deposit(number, -5));
        assertThrows(InvalidAmountException.class,
                () -> service.withdraw(number, 0));
        assertEquals(1000, account.getBalance());
    }

    @Test
    void savingsMinimumBalanceRuleBlocksWithdrawal() throws BankException {
        Account account = savings(1000);   // minimum balance is 500

        assertThrows(BankException.class,
                () -> service.withdraw(account.getAccountNumber(), 600));
        assertEquals(1000, account.getBalance());

        assertDoesNotThrow(
                () -> service.withdraw(account.getAccountNumber(), 500));
        assertEquals(500, account.getBalance());
    }

    @Test
    void fixedDepositIsLocked() throws BankException {
        Account fd = service.openAccount(
                "Meera", "9988776655", "meera@mail.com", "FD", 9000);

        assertThrows(BankException.class,
                () -> service.withdraw(fd.getAccountNumber(), 100));
        assertEquals(9000, fd.getBalance());
    }

    @Test
    void unknownAccountThrowsAccountNotFound() {
        assertThrows(AccountNotFoundException.class,
                () -> service.deposit("AC9999", 100));
        assertThrows(AccountNotFoundException.class,
                () -> service.statement("AC9999"));
    }

    // ---- opening accounts (validation) ----

    @Test
    void openAccountRejectsBadInput() {
        assertThrows(InvalidInputException.class, () -> service.openAccount(
                "Riya", "12345", "riya@mail.com", "SAVINGS", 1000));
        assertThrows(InvalidInputException.class, () -> service.openAccount(
                "Riya", "9876543210", "not-an-email", "SAVINGS", 1000));
        assertThrows(InvalidInputException.class, () -> service.openAccount(
                "  ", "9876543210", "riya@mail.com", "SAVINGS", 1000));
        assertThrows(InvalidInputException.class, () -> service.openAccount(
                "Riya", "9876543210", "riya@mail.com", "GOLD", 1000));
        assertThrows(InvalidAmountException.class, () -> service.openAccount(
                "Riya", "9876543210", "riya@mail.com", "SAVINGS", 100));
        assertEquals(0, service.listAccounts().size());
    }

    // ---- transfer ----

    @Test
    void transferMovesMoneyBetweenAccounts() throws BankException {
        Account a = savings(5000);
        Account b = service.openAccount(
                "Karan", "9123456780", "karan@mail.com", "CURRENT", 100);

        service.transfer(a.getAccountNumber(), b.getAccountNumber(), 1500);

        assertEquals(3500, a.getBalance());
        assertEquals(1600, b.getBalance());
    }

    @Test
    void failedTransferChangesNothing() throws BankException {
        Account a = savings(1000);
        Account b = service.openAccount(
                "Karan", "9123456780", "karan@mail.com", "CURRENT", 100);

        assertThrows(InsufficientFundsException.class, () ->
                service.transfer(a.getAccountNumber(), b.getAccountNumber(), 9999));
        assertThrows(BankException.class, () ->
                service.transfer(a.getAccountNumber(), a.getAccountNumber(), 10));

        assertEquals(1000, a.getBalance());
        assertEquals(100, b.getBalance());
    }

    @Test
    void oppositeTransfersAtTheSameTimeDoNotDeadlock() throws Exception {
        Account a = savings(100000);
        Account b = service.openAccount(
                "Karan", "9123456780", "karan@mail.com", "CURRENT", 100000);

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 500; i++) {
                try {
                    service.transfer(a.getAccountNumber(), b.getAccountNumber(), 10);
                } catch (BankException e) {
                    fail(e.getMessage());
                }
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 500; i++) {
                try {
                    service.transfer(b.getAccountNumber(), a.getAccountNumber(), 10);
                } catch (BankException e) {
                    fail(e.getMessage());
                }
            }
        });
        t1.start();
        t2.start();
        t1.join(10_000);
        t2.join(10_000);

        assertFalse(t1.isAlive() || t2.isAlive(), "deadlock detected");
        assertEquals(200000, a.getBalance() + b.getBalance());
    }

    // ---- batch processing over the thread-safe store ----

    @Test
    void batchOfConcurrentTransactionsIsProcessedCorrectly()
            throws BankException {
        Account account = savings(1000);
        String number = account.getAccountNumber();

        List<Command> commands = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            commands.add(CommandParser.parse("DEPOSIT " + number + " 10"));
        }
        commands.add(CommandParser.parse("DEPOSIT AC9999 10"));   // will fail

        BankService.BatchResult result = service.processBatch(commands);

        assertEquals(101, result.total());
        assertEquals(100, result.succeeded());
        assertEquals(1, result.failed());
        assertEquals(2000, account.getBalance());
    }

    // ---- persistence ----

    @Test
    void savedDataIsLoadedUnchanged() throws Exception {
        Account a = savings(5000);
        service.deposit(a.getAccountNumber(), 250);
        Account b = service.openAccount(
                "Meera", "9988776655", "meera@mail.com", "FD", 9000);

        assertEquals(2, service.save());

        BankService fresh = new BankService(tempDir);
        assertEquals(2, fresh.load());

        Account loadedA = fresh.findAccount(a.getAccountNumber());
        Account loadedB = fresh.findAccount(b.getAccountNumber());

        assertEquals(5250, loadedA.getBalance());
        assertEquals("Riya", loadedA.getOwnerName());
        assertEquals(9000, loadedB.getBalance());
        assertEquals(4.0, loadedA.interestRate());
        assertEquals(7.0, loadedB.interestRate());
    }

    @Test
    void loadWithNoSavedFileReturnsZero() throws Exception {
        assertEquals(0, service.load());
    }

    @Test
    void newAccountAfterLoadDoesNotReuseAnOldNumber() throws Exception {
        Account a = savings(1000);
        service.save();

        BankService fresh = new BankService(tempDir);
        fresh.load();
        Account created = fresh.openAccount(
                "Karan", "9123456780", "karan@mail.com", "CURRENT", 0);

        assertNotEquals(a.getAccountNumber(), created.getAccountNumber());
    }

    // ---- statement, report, search ----

    @Test
    void statementListsEveryOperation() throws BankException {
        Account account = savings(1000);
        String number = account.getAccountNumber();

        service.deposit(number, 200);
        service.withdraw(number, 100);

        String statement = service.statement(number);

        assertTrue(statement.contains("Balance: 1100"));
        assertTrue(statement.contains("Transactions (3)"));
        assertTrue(statement.contains("DEPOSIT " + number + " 1000"));
        assertTrue(statement.contains("DEPOSIT " + number + " 200"));
        assertTrue(statement.contains("WITHDRAW " + number + " 100"));
    }

    @Test
    void reportTotalsMatchTheLoggedTransactions() throws Exception {
        Account a = savings(1000);
        String number = a.getAccountNumber();
        service.deposit(number, 300);
        service.withdraw(number, 100);

        String report = service.report();

        assertTrue(report.contains("Total deposits    : 1300"));
        assertTrue(report.contains("Total withdrawals : 100"));
        assertTrue(report.contains("Net change        : 1200"));
        assertTrue(Files.exists(tempDir.resolve("report.txt")));
    }

    @Test
    void searchByOwnerIsCaseInsensitive() throws BankException {
        savings(1000);
        service.openAccount(
                "Karan", "9123456780", "karan@mail.com", "CURRENT", 0);

        assertEquals(1, service.searchByOwner("RIY").size());
        assertEquals(0, service.searchByOwner("zzz").size());
    }
}
