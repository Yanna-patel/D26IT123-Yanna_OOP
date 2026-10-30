import model.Account;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;
import util.WithdrawRule;
import model.InterestBearing;
import model.Premium;
import static service.StatementFormatter.buildStatement;

public class MiniBank {

    public static void main(String[] args) {

        Account[] accounts = new Account[3];

        accounts[0] =
                new SavingsAccount("Nandani", 1000, 500);

        accounts[1] =
                new CurrentAccount("Hitanshi", 1000, 2000);

        accounts[2] =
                new FixedDepositAccount("Priya", 5000);

        System.out.println("----- Accounts and Interest Rates -----");

        for (Account account : accounts) {

            System.out.println(account);

            System.out.println(
                    "Interest Rate: "
                            + account.interestRate()
                            + "%"
            );

            System.out.println();
        }

        System.out.println("----- Withdrawal Testing -----");

        System.out.println(
                "Savings withdrawal: "
                        + accounts[0].withdraw(400)
        );

        System.out.println(
                "Savings balance: "
                        + accounts[0].getBalance()
        );

        System.out.println(
                "Current withdrawal: "
                        + accounts[1].withdraw(2500)
        );

        System.out.println(
                "Current balance: "
                        + accounts[1].getBalance()
        );

        System.out.println(
                "Fixed Deposit withdrawal: "
                        + accounts[2].withdraw(1000)
        );

        System.out.println(
                "Fixed Deposit balance: "
                        + accounts[2].getBalance()
        );
        System.out.println("\n----- Withdraw Rule Testing -----");

        // Anonymous class
        WithdrawRule rule1 = new WithdrawRule() {

        @Override
        public boolean allow(Account account, long amount) {
                return account.getBalance() >= amount;
        }
        };

        System.out.println(
                "Anonymous class: "
                        + rule1.allow(accounts[0], 300)
        );

        // Lambda expression
        WithdrawRule rule2 =
                (account, amount) -> account.getBalance() >= amount;

        System.out.println(
                "Lambda expression: "
                        + rule2.allow(accounts[0], 300)
        );
        
                System.out.println("\n----- Yearly Interest -----");

        for (Account account : accounts) {
                if (account instanceof InterestBearing interestBearing) {
                        System.out.println(
                                account.getOwnerName()
                                + " Yearly Interest: "
                                + interestBearing.yearlyInterest()
                        );
                }
        }

        System.out.println("\n----- Premium Account -----");

        for (Account account : accounts) {
                if (account instanceof Premium) {
                        System.out.println(
                                account.getOwnerName() + " is a Premium Account"
                        );
                }
        }
        System.out.println("\n----- Account Statement -----");

        System.out.println(
                buildStatement(accounts[0])
        );
    }
}