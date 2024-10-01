 /*
  * Account.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Abstract class for all types of accounts, and handles operations
  * performed by all of these accounts such as withdrawing, depositing,
  * and grabbing total balance (all currencies combined)
  */

package src;
import java.util.*;

public abstract class Account {
    // Abstract class for all possible accounts for customer
    public static final int SAVINGS_ACCOUNT = 0;
    public static final int CHECKINGS_ACCOUNT = 1;
    public static final int SECURITIES_ACCOUNT = 2;
    
    // Map has been created to reduce repetitive method initialization for each
    // type of currency
    protected Map<Integer, Currency> currencies;
    protected Currency yuan;
    protected Currency won;
    protected Currency dollars;

    // Username of who this account belongs to
    protected String username;
    // Type of account (i.e. Savings, Checkings, etc)
    protected int accountType; 
    // Balance of all currencies combined in account, based on USD
    protected double balance; 

    // Constructor called by frontend, which uses data grabbed from backend
    // to generate an account
    public Account(Map<Integer, Currency> currencies, String username) {
        this.username = username;
        this.currencies = currencies;
        // Database doesn't store balance (to avoid desync bugs), so we recalculate
        updateBalance();
    }

    public String getUsername() {
        return username;
    }

    public double getBalance(){
        updateBalance();
        return balance;
    }

    public int getAccountType() {
        return accountType;
    }

    public Currency getCurrency(int currencyType) {
        return currencies.get(currencyType);
    }

    // Triggered at the end of every month.
    // Check is done in Bank.java to only give to Savings Account
    // Method is kept here in case other accounts should be able to
    // receive interest in the future
    public void receiveAccountInterest() {
        for (Currency currency : currencies.values()) {
            if (currency.getAmount() > 0) {
                TransactionDatabase.createTransaction(new 
                        Transaction(new Currency(currency.getAmount() * Bank.ACCOUNT_INTEREST_RATE, 
                        currency.getCurrencyType()), "Manager", getUsername(), 
                        Manager.MANAGER_ACCOUNT, accountType));
                currency.deposit(currency.getAmount() * Bank.ACCOUNT_INTEREST_RATE);
            }
        }
        CustomerDatabase.updateCustomerAccount(this);
    }

    // withdraw() is used as a standalone funciton and helper function for
    // creating transfers. A transfer consists of a withdraw and a deposit.
    // We distinguish if it's being used as a helper function or not using
    // toSelf. This is done so for transaction bookkeeping. Without this check,
    // a transfer will create 3 transactions (1 for the transfer, 1 for the deposit,
    // 1 for the withdraw). So if toSelf is false (meaning it's a transfer),
    /// it will only create 1 transaction for a transfer
    public boolean withdraw(String username, Currency money, boolean toSelf) {
        Currency accountCurrency = currencies.get(money.getCurrencyType());
        if (accountCurrency.getAmount() >= money.getAmount() 
                && currencies.get(Currency.DOLLARS).getAmount() >= Bank.WITHDRAWAL_FEE) {
            accountCurrency.withdraw(money.getAmount());
            boolean result = CustomerDatabase.withdraw(username, money, accountType);
            if (toSelf && result) {
                Currency withdrawalFee = new Currency(Bank.WITHDRAWAL_FEE, Currency.DOLLARS);
                if (currencies.get(Currency.DOLLARS).getAmount() < withdrawalFee.getAmount()) {
                    CustomerDatabase.deposit(username, money, accountType);
                    accountCurrency.deposit(money.getAmount());
                    result = false;
                } else {
                    result = CustomerDatabase.withdraw(username, withdrawalFee, accountType);
                    TransactionDatabase.createTransaction(new Transaction(withdrawalFee, username, "Manager", accountType, Manager.MANAGER_ACCOUNT));
                    TransactionDatabase.createTransaction(new Transaction(money, username, "", accountType, -1));
                }
            }
            updateBalance();
            return result;
        } else {
            return false; // insufficient fund，return false
        }
    }

    // Same logic as withdraw
    public boolean deposit(String username, Currency money, boolean toSelf) {
        currencies.get(money.getCurrencyType()).deposit(money.getAmount());
        boolean result = CustomerDatabase.deposit(username, money, accountType);
        if (toSelf) {
            Transaction t = new Transaction(money, "", username, -1, accountType);
            TransactionDatabase.createTransaction(t);
        }
        updateBalance();
        return result;
    }

    // helper function for calculating total balance (in USD)
    private void updateBalance(){
        this.balance = 0;
        for (Currency currency : currencies.values()) {
            this.balance += Currency.exchange(currency, Currency.DOLLARS).getAmount();
        }
    }
}
