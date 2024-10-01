/*
  * ATM.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Uses Proxy pattern, where the front end will only have an instance of
  * ATM and never an instance of the actual bank. All methods will be called
  * on the ATM. This was done to provide a layer of abstraction and protection
  * for the end user. Almost all methods have a return type to indicate to the
  * front end if method succeeded in doing its job
  */

package src;

import java.util.*;
public class ATM implements BankInterface {
    private Bank bank;

    public ATM() {
        bank = Bank.getSingletonBank();
    }

    // login will be called before all other methods in this class
    // All other methods assume that a user has already logged into
    // to the account
    public void login(User user) {
        bank.login(user);
    }

    public String getUsername() {
        return bank.getUsername();
    }

    public int createBankAccount(int accountType, Currency currency) {
        return bank.createBankAccount(accountType, currency);
    }

    public boolean[] closeAccount(int accountType) {
        return bank.closeAccount(accountType);
    }

    public Currency exchangeCurrency(int accountType, Currency existingCurrency, int targetCurrencyType) {
        return bank.exchangeCurrency(accountType, existingCurrency, targetCurrencyType);
    }

    public boolean deposit(int accountType, Currency currency, boolean toSelf) {
        return bank.deposit(accountType, currency, toSelf);
    }

    public boolean withdraw(int accountType, Currency currency, boolean toSelf) {
        return bank.withdraw(accountType, currency, toSelf);
    }

    public boolean requestLoan(String collateral, double collateralAmount, double loanAmount) {
        return bank.requestLoan(collateral, collateralAmount, loanAmount);
    }

    public boolean payLoan(String collateral, int accountType, int currencyType) {
        return bank.payLoan(collateral, accountType, currencyType);
    }

    public List<Transaction> viewTransactions(int accountType) {
        return bank.viewTransactions(accountType);
    }
    
    // Shows balance in [total balance in USD, Amount of USD, Amount of WON, Amount of RMB]
    public double[] viewCurrentBalance(int accountType) {
        double[] currentBalance = bank.viewCurrentBalance(accountType);
        return currentBalance;
    }

    // Separate function as security account assumes there is only one
    public double[] viewCurrentSecBalance(int accountType) {
        double[] currentBalance = bank.viewCurrentSecBalance(accountType);
        return currentBalance;
    }

    public boolean transferAmount(int accountType, int targetAccountType, String targetUsername, Currency amount) {
        return bank.transferAmount(accountType, targetAccountType, targetUsername, amount);
    }

    public Account getBankAccount(int accountType) {
        return bank.getBankAccount(accountType);
    }
}
