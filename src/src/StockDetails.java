/*
  * StockDetails.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Helper class for creating StockDetails objects. This differs from
  * Stock.java as Stock.java is used for displaying current price for displaying
  * on the stock market. StockDetails is used by the Customer and keeps track of
  * what price a customer bought a number of stocks at (this is necessary for)
  * accurate unrealized profits and realized profits calculations. Having one price
  * won't work (i.e. Customer buys 1 share at 50$, another at 100$, and the stock is now
  * worth 200$.) Profit calculation would need a history of both prices.
  */


package src;
import java.util.*;

public class StockDetails {
    String name;
    int amount;
    List<Double> initPrices = new ArrayList<Double>();

    public StockDetails(String name, int amount, List<Double> initPrices) {
        this.name = name;
        this.amount = amount;
        this.initPrices = initPrices;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public double getNextPrice(){
        double price = initPrices.get(0);
        initPrices.remove(0);
        return price;
    }
}
