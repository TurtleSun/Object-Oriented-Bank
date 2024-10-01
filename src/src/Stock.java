/*
  * Stock.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Helper class to avoid pairs of data and for clear
  * distinction of what a stock is. Represents a stock 
  * in the market
  */

package src;
public class Stock {
    private String name;
    private double price;
    public Stock(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName(){
        return this.name;
    }

    public double getPrice(){
        return this.price;
    }
}
