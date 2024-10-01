/*
  * SavingsAccount.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * SavingsAccount differ from CheckingAccount as everytime a user deposits
  * money or withdraws money, we must check if it enables or disables their security
  * account. It overrides Account's withdraw and deposit methods and calls not only
  * account's withdraw and deposit but performs the additional checks as well 
  */

package src;

import java.util.*;

public class SavingsAccount extends Account{
    Bank bank = Bank.getSingletonBank();

    public SavingsAccount(Map<Integer, Currency> currencies, String username){
        super(currencies, username);
        super.accountType = Account.SAVINGS_ACCOUNT;
    }

    public boolean deposit(String username, Currency money, boolean toSelf) {
        if (super.deposit(username, money, toSelf)) {
            // If user previously lost access to SecurityAccount by having too little
            // money in their savings, reenable
            Customer customer = CustomerDatabase.getCustomer(username);
            System.out.println(customer.getBankAccount(Account.SAVINGS_ACCOUNT).getBalance());
            if (customer.getBankAccount(Account.SAVINGS_ACCOUNT).getBalance() >= SecurityAccount.ELIGIBLE_TO_KEEP_OPEN_SAVINGS_BALANCE) {
                Account acc = customer.getBankAccount(SECURITIES_ACCOUNT);
                System.out.println(acc);
                if (acc != null) {
                    SecurityAccount secAcc = (SecurityAccount) acc;
                    if (!secAcc.isEnabled()) {
                        secAcc.enable();
                        CustomerDatabase.disableOrEnableCustomerCheckingAccount(username, true);
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean withdraw(String username, Currency money, boolean toSelf) {
        Customer currentCustomer = bank.getCurrentCustomer();
        if (super.withdraw(username, money, toSelf)) {
            // If user drops below certain amount in Savings, disable their security account
            // if they have one
            if (username.equals(currentCustomer.getUsername()) && getBalance() < SecurityAccount.ELIGIBLE_TO_KEEP_OPEN_SAVINGS_BALANCE) {
                Account acc = currentCustomer.getBankAccount(SECURITIES_ACCOUNT);
                if (acc != null) {
                    SecurityAccount secAcc = (SecurityAccount) acc;
                    secAcc.disable();
                    CustomerDatabase.disableOrEnableCustomerCheckingAccount(username, false);
                }
            }
            return true;
        } else {
            return false;
        }
    }
}

