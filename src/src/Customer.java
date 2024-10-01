/*
  * Customer.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * A basic class that contains the attributes of Customer.
  * Customer has accounts and allows for a single customer
  * to access multiple accounts associated it at the same time
  */

package src;
import java.util.*;

public class Customer extends User {
    private Map<Integer, Account> accounts;
    private List<Transaction> transactionList;

    public Customer(String username) {
        super(username);
        this.accounts = new HashMap<>();
        this.transactionList = new ArrayList<>();
    }

    public Map<Integer, Account> getAccounts() {
        return accounts;
    }

    public boolean secAccountExists() {
        if (accounts.get(Account.SECURITIES_ACCOUNT) == null) {
            return false;
        } else {
            return true;
        }
    }

    public Account getBankAccount(int accountType) {
        return accounts.get(accountType);
    }


    public void addBankAccount(int accountType, Account account) {
        accounts.put(accountType, account);
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void closeAccount(int accountType) {
        accounts.remove(accountType);
        CustomerDatabase.closeAccount(username, accountType);
    }
}
