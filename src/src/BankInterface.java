/*
  * BankInterface.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Uses Proxy pattern, this interface ensures that all methods supported by
  * ATM are properly implemented into Bank as well.
  */

package src;
import java.util.*;
public interface BankInterface {
    int createBankAccount(int accountType, Currency currency);
    boolean requestLoan(String collateral, double collateralAmount, double loanAmount);
    List<Transaction> viewTransactions(int accountType);
    double[] viewCurrentBalance(int accountType); 
    void login(User user);
    String getUsername();
    boolean[] closeAccount(int accountType);
    Currency exchangeCurrency(int accountType, Currency existingCurrency, int targetCurrencyType);
    boolean deposit(int accountType, Currency currency, boolean toSelf);
    boolean withdraw(int accountType, Currency currency, boolean toSelf);
    boolean payLoan(String collateral, int accountType, int currencyType);
    double[] viewCurrentSecBalance(int accountType);
    boolean transferAmount(int accountType, int targetAccountType, String targetUsername, Currency amount);
    Account getBankAccount(int accountType);
}
