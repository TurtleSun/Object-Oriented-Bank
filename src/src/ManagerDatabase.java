/*
  * ManagerDatabase.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Gets current instance of manager which contains all instances
  * manager should know about such as all customers, all transactions,
  * and all stocks. Also supports manager login.
  */

package src;

import java.util.*;

public class ManagerDatabase {
    public static final String MANAGER_ACCOUT_TXT_FILEPATH = "managerAccount.txt";

    
    public static Manager getManager(String username) {
        List<Customer> customerList = CustomerDatabase.getAllCustomers();
        List<Transaction> transactionList = TransactionDatabase.getAllTransactions();
        List<Stock> stocksList = StockDatabase.getAllStocks();
        return new Manager(username, customerList, transactionList, stocksList);
    }
}
