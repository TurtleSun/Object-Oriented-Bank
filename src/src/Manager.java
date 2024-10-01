/*
  * Manager.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * A manager similar to Bank is singleton. And unlike customers in the front
  * end, managers when logged in the front end have methods that the manager
  * can directly call towards the bank, such as changing date, changing stock prices,
  * etc. By having Manager have access directly to the bank, we can make Manager call
  * methods not available on the ATM (which customers have access to), creating an
  * additional layer of security. Implements Observer Pattern, where if a manager updates
  * or removes a stock, it should notify all portfolios under the manager of their new
  * unrealized profits (if updated) and sell all stocks (if removed). Without this pattern,
  * a customer would constantly have to see if Manager has made any changes to the database.
  */

package src;
import java.time.LocalDate;
import java.util.*;
public class Manager extends User implements SecuritySubject{
    List<Customer> customerList;
    List<SecurityObserver> stockCustomersList;
    private List<Transaction> transactionList;
    public List<Stock> stocksList;
    private static Manager manager;
    private static String managerUsername = "Manager";

    public static final int MANAGER_ACCOUNT = 3;

    public static Manager getSingletonManager() {
        if (manager == null) {
            manager = ManagerDatabase.getManager(managerUsername);
        }
        return manager;
    }

    public Manager(String username, List<Customer> customerList, List<Transaction> transactionList, List<Stock> stocksList) {
        super(username);
        this.customerList = customerList;
        this.transactionList = transactionList;
        this.stocksList = stocksList;
        this.stockCustomersList = new ArrayList<>();
        initStockCustomersList();
    }

    private void initStockCustomersList(){
        for (Customer c : customerList) {
            for (Account a : c.getAccounts().values()) {
                if (a.getAccountType() == Account.SECURITIES_ACCOUNT) {
                    stockCustomersList.add((SecurityObserver)a);
                    break;
                }
            }
        }
    }

    public void increaseDateByDay() {
        bank.increaseDateByDay();
    }

    public void increaseDateByMonth() {
        bank.increaseDateByMonth();
    }

    public List<Account> getAllCustomerAccountType(int accountType) {
        List<Account> ls = new ArrayList<>();
        customerList = CustomerDatabase.getAllCustomers();
        for (Customer c : customerList) {
            ls.add(c.getBankAccount(accountType));
        }
        return ls;
    }

    // Gets daily report as specified in prompt
    public List<Transaction> dailyReport() {
        LocalDate today = CustomerDatabase.getDate();
        List<Transaction> ls = new ArrayList<>();
        for (Transaction t : transactionList) {
            if (Transaction.isSameDay(today, t)) {
                ls.add(t);
            }
        }
        return ls;
    }

    public List<Loan> getPendingLoans(){
        return LoanDatabase.getAllLoans(username, true);
    }

    public boolean approveLoan(Loan loan){
        if (loan.getCollateralAmount() >= loan.getLoanAmount()){
            LoanDatabase.addLoantoActiveList(loan.getUsername(), loan.getCollateral(), loan.getLoanAmount());
            return true;
        } else {
            LoanDatabase.rejectLoan(loan.getUsername(), loan.getCollateral());
            return false;
        }
    }

    public boolean rejectLoan(Loan loan){
        LoanDatabase.rejectLoan(loan.getUsername(), loan.getCollateral());
        return false;
    }

    public void deposit(String username, double loanValue){
        Currency loan = new Currency(loanValue, Currency.DOLLARS);
        CustomerDatabase.deposit(username, loan, Account.SAVINGS_ACCOUNT);
        Transaction tx = new Transaction(loan, managerUsername, username, -1, Account.SAVINGS_ACCOUNT);
        TransactionDatabase.createTransaction(tx);
    }

    public List<Stock> getStocksList() {
        return StockDatabase.getAllStocks();
        //return stocksList;
    }

    public void updateStockPrice(Stock stock, double price){
        // Find stock in stocksList and change the price accordingly
        StockDatabase.updateStockPrice(stock.getName(), price);
        stock.setPrice(price);
        // Notify all stock customers
        notifyObservers(stock); // Stock = {name of stock, price of stock}
                // if Price of stock is negative then that means the stock has been deleted
    }

    public Stock getStock(String stockName){
        Stock result = null;
        List<Stock> stockList = StockDatabase.getAllStocks();
        for (Stock stock : stockList){
            if (stock.getName().equals(stockName)){
                result = stock;
            }
        }
        return result;
    }

    public boolean addStock(Stock stock) {
        if (StockDatabase.addStock(stock)) {
            stocksList.add(stock);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeStock(String name) {
        if (StockDatabase.removeStock(name)) {
            for (Stock s : stocksList) {
                if ((s.getName()).equals(name)) {
                    notifyObservers(new Stock(s.getName(), -1*s.getPrice()));
                    stocksList.remove(s);
                    return true;
                }
            }
        }
        return false;
    }

    public void addObserver(SecurityObserver observer) {
        if (stockCustomersList == null) {
            stockCustomersList = new ArrayList<>();
        }
        stockCustomersList.add(observer);
    }
    public void removeObserver(SecurityObserver observer) {
        if (stockCustomersList != null) {
            stockCustomersList.remove(observer);
        }
    }
    public void notifyObservers(Stock stock) {
        if (stockCustomersList != null) {
//            System.out.println("Here at notifyObservers, we have a customer");
//            System.out.println("Here at notifyObservers, we are trying to notify: " + ((Account)stockCustomersList.get(0)).getUsername());
            for (SecurityObserver observer : stockCustomersList) {
//                System.out.println("Here at notifyObservers, we are trying to notify: " + ((Account) observer).getUsername());
                observer.update(stock);
            }
        } else {
            System.out.println("Warning: stockCustomersList is not initialized.");
        }
    }
}
