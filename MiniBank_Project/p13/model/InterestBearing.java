package model;

public interface InterestBearing {

    double interestRate();

    default double yearlyInterest() {
        return ((Account) this).getBalance() * interestRate() / 100;
    }
}