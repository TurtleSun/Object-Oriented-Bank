/*
  * TransactionDatabase.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Class in charge of writing and reading all transaction related
  * data to the database.
  */

package src;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.util.*;

public class TransactionDatabase {
    public static final String CUSTOMER_TRANSACTIONS_CSV_FILEPATH = "customerTransactions.csv";
    public static final String STOCK_TRANSACTIONS_CSV_FILEPATH = "stockTransactions.csv";

    public static boolean createStockTransaction(StockTransaction stockTransaction) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(STOCK_TRANSACTIONS_CSV_FILEPATH, true))) {
            String[] account = {stockTransaction.getDate().toString(), stockTransaction.getUsername(),
                stockTransaction.getStockName(), String.valueOf(stockTransaction.getPrice()), 
                String.valueOf(stockTransaction.getAmount()), String.valueOf(stockTransaction.isBuy())};
            writer.writeNext(account);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<StockTransaction> getStockTransactionsFromUser(String username) {
        List<StockTransaction> ls = new ArrayList<>();
        boolean fileExists = new File(STOCK_TRANSACTIONS_CSV_FILEPATH).exists();

        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(STOCK_TRANSACTIONS_CSV_FILEPATH))) {
                List<String[]> stockTransactions = reader.readAll();
                for (String[] sTransaction : stockTransactions) {
                    if (sTransaction[1].equals(username)) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate localDate = LocalDate.parse(sTransaction[0], formatter);
                        StockTransaction t = new StockTransaction(localDate, sTransaction[1], sTransaction[2],
                            Double.parseDouble(sTransaction[3]), Integer.parseInt(sTransaction[4]),
                            Boolean.parseBoolean(sTransaction[5]));
                        ls.add(t);
                    }
                    
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
                return null;
            }
        }
        return ls;
    }

    public static boolean createTransaction(Transaction transaction) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(CUSTOMER_TRANSACTIONS_CSV_FILEPATH, true))) {
            String[] account = {transaction.getDate().toString(), String.valueOf(transaction.getCurrency().getCurrencyType()),
                String.valueOf(transaction.getCurrency().getAmount()), transaction.getSender(), 
                String.valueOf(transaction.getSenderAccountType()), transaction.getReceiver(),
                String.valueOf(transaction.getReceiverAccountType())};
            writer.writeNext(account);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Transaction> getAllTransactions() {
        List<Transaction> ls = new ArrayList<>();
        boolean fileExists = new File(CUSTOMER_TRANSACTIONS_CSV_FILEPATH).exists();
        
        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(CUSTOMER_TRANSACTIONS_CSV_FILEPATH))) {
                List<String[]> transactions = reader.readAll();
                for (String[] transaction : transactions) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate localDate = LocalDate.parse(transaction[0], formatter);
                    Transaction t = new Transaction(localDate, new Currency(Double.parseDouble(transaction[2]), 
                            Integer.parseInt(transaction[1])), transaction[3], transaction[5], 
                            Integer.parseInt(transaction[4]), Integer.parseInt(transaction[6]));
                    ls.add(t);
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
                return null;
            }
        }
        return ls;
    }

    public static List<Transaction> getTransactionsFromUser(String username, int accountType) {
        List<Transaction> ls = new ArrayList<>();
        boolean fileExists = new File(CUSTOMER_TRANSACTIONS_CSV_FILEPATH).exists();

        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(CUSTOMER_TRANSACTIONS_CSV_FILEPATH))) {
                List<String[]> transactions = reader.readAll();
                for (String[] transaction : transactions) {
                    if ((transaction[3].equals(username) && transaction[4].equals(String.valueOf(accountType))) ||
                            (transaction[5].equals(username) && transaction[6].equals(String.valueOf(accountType)))) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate localDate = LocalDate.parse(transaction[0], formatter);
                        Transaction t = new Transaction(localDate, new Currency(Double.parseDouble(transaction[2]), 
                                Integer.parseInt(transaction[1])), transaction[3], transaction[5], 
                                Integer.parseInt(transaction[4]), Integer.parseInt(transaction[6]));
                        ls.add(t);
                    }
                    
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
                return null;
            }
        }
        return ls;
    }
}
