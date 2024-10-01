/*
  * Bank.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Uses Proxy pattern & singleton pattern, where the front end will only have an instance of
  * ATM and never an instance of the actual bank. All methods in this class are called
  * by ATM.java and never called from the front-end. It is also a singleton to avoid passing
  * instances of Bank to every Account, Customer, etc. As an Account by itself has no 
  * reference to other existing accounts, same for customers and so on.
  * This program was designed with one bank in mind.
  */

package src;
import java.time.LocalDate;
import java.util.*;

public class Bank implements BankInterface {
    public static final double ACCOUNT_INTEREST_RATE = 0.1;
    public static final double LOAN_INTEREST_RATE = 0.15;
    private static final double BALANCE_FOR_RICH = 5000;
    public static final double ACCOUNT_CREATION_DELETION_COST = 200;
    private static final double CHECKING_TRANSACTION_FEE = 100;
    public static final double WITHDRAWAL_FEE = 50;
    private Manager manager;
    private Customer currentCustomer = null;
    private static Bank bank;
    private List<Customer> customerList;
    private LocalDate date;

    private Bank() {
        
    }
    public static Bank getSingletonBank() {
        if (bank == null) {
            bank = new Bank();
            bank.populateBankAndCustomerList();
        }
        return bank;
    }

    // As mentioned in ATM.java, login will always be called first
    // and all other methods assumes a currentCustomer is assigned to the bank
    public void login(User user) {
        if (user instanceof Customer) {
            currentCustomer = (Customer) user;
        }
    }

    public Account getBankAccount(int accountType) {
        currentCustomer = CustomerDatabase.getCustomer(currentCustomer.getUsername());
        return currentCustomer.getBankAccount(accountType);
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    private void populateBankAndCustomerList() {
        date = CustomerDatabase.getDate();
        customerList = CustomerDatabase.getAllCustomers();
        manager = Manager.getSingletonManager();
    }

    public LocalDate getDate() {
        return CustomerDatabase.getDate();
    }

    // Called by Manager that sets the date
    public void increaseDateByDay() {
        LocalDate prevDate = getDate();
        CustomerDatabase.increaseDateByDay();
        date = getDate();
        if (prevDate.getMonth() != date.getMonth()) {
            for (Customer customer : customerList) {
                giveAccountInterest(customer);
            }
            addLoanInterest();
        }
    }

    // Called by Manager that sets the date
    public void increaseDateByMonth() {
        CustomerDatabase.increaseDateByMonth();
        date = getDate();
        for (Customer customer : customerList) {
            giveAccountInterest(customer);
        }
        addLoanInterest();
    }

    private void addLoanInterest(){
        List<Loan> loans = LoanDatabase.getAllLoans("Manager", false);
        for (Loan loan : loans) {
            String username = loan.getUsername();
            String collateral = loan.getCollateral();
            LoanDatabase.updateLoan(username, collateral, loan.getLoanAmount()*LOAN_INTEREST_RATE);
        }
    }

    // This prevents other accounts from getting a interest from the manager
    // and only gives interest to rich customers as given by prompt
    private void giveAccountInterest(Customer customer) {
        customer = CustomerDatabase.getCustomer(customer.getUsername());
        for (Account account : customer.getAccounts().values()) {
            if (account instanceof SavingsAccount && account.getBalance() >= BALANCE_FOR_RICH) {
                account.receiveAccountInterest();
            }
        }
    }

    public String getUsername() {
        if (currentCustomer != null) {
            return currentCustomer.getUsername();
        }
        return "[DEBUG]: Bank.java";
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }

    // Differs from Currency.exchange() as Currency.exchange() is simply
    // a helper function to convert Currency, meanwhile Bank.java manages
    // all backend book-keeping and account updating
    public Currency exchangeCurrency(int accountType, Currency existingCurrency, int targetCurrencyType) {
        int existingCurrencyType = existingCurrency.getCurrencyType();
        // if user attempts to exchange to the same currency
        if (existingCurrencyType == targetCurrencyType) {
            return null;
        }
        if (existingCurrency.getCurrencyType() == Currency.INVALID) {
            return null;
        }
        Account account = currentCustomer.getBankAccount(accountType);
        if (account.withdraw(currentCustomer.getUsername(), existingCurrency, true)) {
            Currency exchangedCurrency = Currency.exchange(existingCurrency, targetCurrencyType);
            account.deposit(currentCustomer.getUsername(), exchangedCurrency, true);
            return exchangedCurrency;
        } else {
            return null;
        }
    }

    // Different negative number represents different errors. -1 is an unnaccounted for error
    public int createBankAccount(int accountType, Currency currency) {
        if (currentCustomer == null) {
            System.out.println("[Debug]: Current customer is null");
        }
        if (currency.getAmount() < ACCOUNT_CREATION_DELETION_COST) {
            return -6;
        }
        if (currentCustomer.getBankAccount(accountType) == null) {
            if (accountType == Account.CHECKINGS_ACCOUNT) {
                CustomerDatabase.createCustomerCheckingAccount(currentCustomer.getUsername(), currency);
            } else if (accountType == Account.SAVINGS_ACCOUNT) {
                CustomerDatabase.createCustomerSavingsAccount(currentCustomer.getUsername(), currency);
            } else if (accountType == Account.SECURITIES_ACCOUNT) {
                double transferAmount = currency.getAmount();
                // Deposited less than 1000
                if (transferAmount < SecurityAccount.MIN_DEPOSIT) {
                    return -2;
                }
                // Doesn't have more than 5000 currently in Savings
                if (viewCurrentBalance(Account.SAVINGS_ACCOUNT)[0] < SecurityAccount.ELIGIBLE_TO_OPEN_SAVINGS_BALANCE ) {
                    return -3;
                }
                // if after transfer, account would be instantly disabled
                if (viewCurrentBalance(Account.SAVINGS_ACCOUNT)[0] - transferAmount < 2500) {
                    return -4;
                }
                if (viewCurrentBalance(Account.SAVINGS_ACCOUNT)[1] - transferAmount < 0) {
                    return -7;
                }
                CustomerDatabase.createCustomerSecurityAccount(currentCustomer.getUsername(), currency);
                Account savingsAccount = currentCustomer.getBankAccount(Account.SAVINGS_ACCOUNT);
                if (savingsAccount == null) {
                    System.out.println("[Debug]: Savings account is null");
                    return -5;
                } else {
                    // withdraw from savings
                    savingsAccount.withdraw(getUsername(), currency, true);
                    // add transfer to security
                    if (CustomerDatabase.createCustomerSecurityAccount(currentCustomer.getUsername(), currency)) {
                        currentCustomer = CustomerDatabase.getCustomer(currentCustomer.getUsername());
                        // add this Customer to the Observer for stock changes
                        SecurityObserver account = (SecurityObserver)currentCustomer
                                .getBankAccount(Account.SECURITIES_ACCOUNT);
                        manager.addObserver(account);
                        
                    }
                    
                }
            } else {
                System.out.println("[Debug]: Not a valid account type!");
                return -1;
            }
            currentCustomer = CustomerDatabase.getCustomer(currentCustomer.getUsername());
            if (!transferAmount(accountType, Manager.MANAGER_ACCOUNT, manager.getUsername(), new Currency(ACCOUNT_CREATION_DELETION_COST, Currency.DOLLARS))) {
                CustomerDatabase.closeAccount(getUsername(), accountType);
            }
            currentCustomer = CustomerDatabase.getCustomer(currentCustomer.getUsername());

            return 0;
        } else {
            return -1;
        }
    }
    public boolean requestLoan(String collateral, double collateralAmount, double loanAmount) {
        // Manager will approve or not by their one discretion 
        List<Loan> approvedLoans = LoanDatabase.getAllLoans(getUsername(), false);
        List<Loan> notApprovedLoans = LoanDatabase.getAllLoans(getUsername(), true);
        boolean valid = true;
        for (Loan l : approvedLoans) {
            if (l.getCollateral().equals(collateral)) {
                valid = false;
            }
        }
        for (Loan l : notApprovedLoans) {
            if (l.getCollateral().equals(collateral)) {
                valid = false;
            }
        }
        if (valid) {
            return LoanDatabase.addLoanToBeApproved(currentCustomer.getUsername()
                , collateral, collateralAmount, loanAmount);
        } else {
            return false;
        }
    }

    public boolean payLoan(String collateral, int accountType, int currencyType) {
        Currency payment = CustomerDatabase.getCustomer(currentCustomer.getUsername()).getBankAccount(accountType).getCurrency(currencyType);
        return LoanDatabase.closeLoan(currentCustomer.getUsername(), collateral, payment);
    }

    public List<Transaction> viewTransactions(int accountType) {
        return TransactionDatabase.getTransactionsFromUser(currentCustomer.getUsername(), accountType);
    }
    public double[] viewCurrentBalance(int accountType) {
        double[] currentBalance = new double[]{0,0,0,0,-1};
        Account account = currentCustomer.getBankAccount(accountType);
        if (account != null) {
            currentBalance[0] = account.getBalance();
            currentBalance[1] = account.getCurrency(Currency.DOLLARS).getAmount();
            currentBalance[2] = account.getCurrency(Currency.YUAN).getAmount();
            currentBalance[3] = account.getCurrency(Currency.WON).getAmount();
            
            currentBalance[4] = 0;
        }
        
        return currentBalance;
    }

    // Separate viewCurrentSecBalance given as they have different structure compared
    // to Savings and Checkings
    public double[] viewCurrentSecBalance(int accountType) {
        double[] currentBalance = new double[]{0,0,0,0,-1};
        // Last index indicates if in account actually exists.
        Account account = currentCustomer.getBankAccount(accountType);
        if (account != null) {
            currentBalance[0] = account.getBalance();
            currentBalance[1] = account.getCurrency(Currency.DOLLARS).getAmount();
            currentBalance[2] = ((SecurityAccount)account).getUnrealizedProfits();
            currentBalance[3] = ((SecurityAccount)account).getStockPortfolioValue();
            currentBalance[4] = 0;
        }

        return currentBalance;
    }

    public boolean deposit(int accountType, Currency currency, boolean toSelf) {
        Account currentCustomerAcc = currentCustomer.getBankAccount(accountType);
        return currentCustomerAcc.deposit(currentCustomer.getUsername(), currency, toSelf);
    } 

    public boolean withdraw(int accountType, Currency currency, boolean toSelf) {
        Account currentCustomerAcc = currentCustomer.getBankAccount(accountType);
        return currentCustomerAcc.withdraw(currentCustomer.getUsername(), currency, toSelf);
    } 

    public boolean transferAmount(int accountType, int targetAccountType, String targetUsername, Currency amount) {
        Account currentCustomerAcc = currentCustomer.getBankAccount(accountType);
        if (currentCustomerAcc == null) {
            return false;
        }

        if (targetAccountType == Manager.MANAGER_ACCOUNT) {
            // We do not have an actual account for Manager, but to simulate the money being sent to
            // the manager, we create Manager.MANAGER_ACCOUNT
            if (currentCustomerAcc.withdraw(currentCustomer.getUsername(), amount, false)) {
                TransactionDatabase.createTransaction(new Transaction(amount, currentCustomer.getUsername(), "Manager", accountType, targetAccountType));
                return true;
            } else {
                return false;
            }

        } else {
            if (accountType == Account.CHECKINGS_ACCOUNT) {
                double fee = 0;
                if (amount.getCurrencyType() == Currency.DOLLARS) {
                    fee += amount.getAmount();
                }
                fee += CHECKING_TRANSACTION_FEE;
                if (currentCustomerAcc.getCurrency(Currency.DOLLARS).getAmount() > fee) {
                    if (!transferAmount(accountType, Manager.MANAGER_ACCOUNT, manager.getUsername(), new Currency(CHECKING_TRANSACTION_FEE, Currency.DOLLARS))) {
                        return false;
                    }
                } else {
                    return false;
                }
                
            }
            Account targetCustomerAcc = CustomerDatabase.getCustomer(targetUsername).getBankAccount(targetAccountType);
            if (targetCustomerAcc != null) {
                // If we have enough money to send
                if (currentCustomerAcc.withdraw(currentCustomer.getUsername(), amount, false)) {
                    // if target can receive money (does account exist, did database fail, etc)
                    if (targetCustomerAcc.deposit(targetUsername, amount, false)) {
                        
                        TransactionDatabase.createTransaction(new Transaction(amount, currentCustomer.getUsername(),
                            targetUsername, accountType, targetAccountType));
                        return true;
                    // If target couldn't receive money, deposit back to sender
                    } else {
                        currentCustomerAcc.deposit(currentCustomer.getUsername(), amount, false);
                    }
                }
            }
        }
        
        return false;
    }

    public boolean[] closeAccount(int accountType) {
        boolean[] result = new boolean[] {false, false};
        if (currentCustomer == null) {
            System.out.println("[Debug]: Current customer is null");
        }
        if (currentCustomer.getBankAccount(accountType) != null) {
            Account account = currentCustomer.getBankAccount(accountType);
            // If account has enough money to delete an account
            if (account.getBalance() >= ACCOUNT_CREATION_DELETION_COST){
                if (accountType == Account.SECURITIES_ACCOUNT){
                    //Remove Customer from the oberve
                    manager.removeObserver((SecurityObserver)account);
                }
                // If customer can't send the money to Manager for any reason (database crash)
                if (!transferAmount(accountType, Manager.MANAGER_ACCOUNT, manager.getUsername(),
                        new Currency(ACCOUNT_CREATION_DELETION_COST, Currency.DOLLARS))) {
                    result[1] = true;
                } else {
                    currentCustomer.closeAccount(accountType);
                }
                double liquidAmount = account.getBalance();
                System.out.println("You have " + liquidAmount + " dollars in liquid funds. Currently this amount will be lost. Possibly put into the collateral pool? Means more money for Bank... 0-0");
                
            } else {
                result[1] = true;
            }
        } else {
            result[0] = true;
        }
        return result;
    }
}
