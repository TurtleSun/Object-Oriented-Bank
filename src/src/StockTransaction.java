/*
  * StockTransaction.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Helper class consisting of attributes representing a StockTransaction.
  * This helps with grabbing and writing from the database a single instance of a
  * StockTransaction rather than having tuples
  */

package src;
import java.time.LocalDate;

public class StockTransaction {
    private LocalDate date;
    private String username;
    private String stockName;
    private double price;
    private int amount;
    private boolean isBuy;

    public StockTransaction(String username, String stockName, double price, int amount, boolean isBuy) {
        this.date = CustomerDatabase.getDate();
        this.username = username;
        this.stockName = stockName;
        this.price = price;
        this.amount = amount;
        this.isBuy = isBuy;
    }

    public StockTransaction(LocalDate date, String username, String stockName, double price, int amount, boolean isBuy) {
        this.date = date;
        this.username = username;
        this.stockName = stockName;
        this.price = price;
        this.amount = amount;
        this.isBuy = isBuy;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getUsername() {
        return username;
    }

    public String getStockName() {
        return stockName;
    }

    public double getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isBuy() {
        return isBuy;
    }
}
