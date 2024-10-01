/*
  * SecurityAccount.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Utilizes the Observer Pattern. SecurityAccount supports all methods initiable from a SecurityAccount
  * such as buying and selling stocks. It implements SecurityObserver, meaning when a manager
  * that implements SecuritySubject calls a method regarding a stock a portfolio owns (such as
  * selling or removing), it lets all securityAccounts know that it should update its unrealized
  * profits or sell off their stocks. SecurityAccount is always in USD as well and therefore
  * have a different structure (only USD of currencies is being used from Account)
  */

package src;
import java.util.*;

public class SecurityAccount extends Account implements SecurityObserver {
    public static final double ELIGIBLE_TO_OPEN_SAVINGS_BALANCE = 5000;
    public static final double ELIGIBLE_TO_KEEP_OPEN_SAVINGS_BALANCE = 2500;
    public static final double MIN_DEPOSIT = 1000;
    private StockPortfolio stockPortfolio = new StockPortfolio();
    private double stockPortfolioValue = 0;
    private double unrealizedProfits = 0;
    private double realizedProfits = 0;
    private boolean isEnabled;

    public SecurityAccount(Map<Integer, Currency> currencies, String username){
        super(currencies, username);
        this.accountType = Account.SECURITIES_ACCOUNT;
        this.isEnabled = true;
    }

    public SecurityAccount(Map<Integer, Currency> currencies, String username, boolean isEnabled){
        super(currencies, username);
        this.accountType = Account.SECURITIES_ACCOUNT;
        this.isEnabled = isEnabled;
    }
    
    public double getRealizedProfits() {
        return realizedProfits;
    }

    public void enable() {
        isEnabled = true;
    }

    public void disable() {
        isEnabled = false;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public boolean buyStock(String stock, int amount){
        List<Stock> stockList = Manager.getSingletonManager().stocksList;
        // look through possible stocks, if you see the stock 
        for (Stock currStock : stockList){
            if (stock.equals(currStock.getName())){
                // if the stock is found
                // if balance >= stock.price then buy the stock and add it to portfolio
                if (this.balance >= currStock.getPrice() * amount){
                    // decrease account currencies 
                    this.withdraw(username, new Currency(currStock.getPrice() * amount, Currency.DOLLARS), true);
                    // add stock to the portfolio
                    for (int i = 0; i < amount; i++) {
                        this.stockPortfolio.addStock(username, currStock);
                    }
                    this.stockPortfolioValue = stockPortfolio.updatePortfolioValue(username);

                    this.unrealizedProfits = stockPortfolio.updateUnrealizedProfit(username);
                    TransactionDatabase.createStockTransaction(new StockTransaction(getUsername(), currStock.getName(), currStock.getPrice(), amount, true));
                    return true;
                }
            }
        }
        return false;
    }

    // money should always be of type USD
    public boolean withdraw(String username, Currency money, boolean toSelf) {
        if (money.getCurrencyType() != Currency.DOLLARS) {
            return false;
        }
        return super.withdraw(username, money, toSelf);
    }


    public boolean deposit(String username, Currency money, boolean toSelf) {
        if (money.getCurrencyType() != Currency.DOLLARS || money.getAmount() < 1000) {
            return false;
        }
        return super.deposit(username, money, toSelf);
    }

    public double sellStock(String stock, int amount){ 
        // If amount is negative, then sell everything
        Stock wantedStock = Manager.getSingletonManager().getStock(stock);
        if (wantedStock == null){
            System.out.println("Stock not found");
            return 0;
        }
        // try to sell the stock and remove it from portfolio
        double realizedProfit = this.stockPortfolio.removeStock(username, wantedStock, amount).get(0);
        // inc the balance accordingly
        Currency profit = new Currency (wantedStock.getPrice()*amount, Currency.DOLLARS);
        deposit(username, profit, false);

        Transaction t = new Transaction(profit, "Market", username, 0, accountType);
        TransactionDatabase.createTransaction(t);
        // update the stock portfolio value
        // update the unrealized value of the portfolio
        this.unrealizedProfits = stockPortfolio.updateUnrealizedProfit(username);
        this.stockPortfolioValue = stockPortfolio.updatePortfolioValue(username);

        TransactionDatabase.createStockTransaction(new StockTransaction(getUsername(), wantedStock.getName(), wantedStock.getPrice(), amount, false));
        StockDatabase.updateRealizedProfit(getUsername(), realizedProfit);
        // return realized profit from this stock sell
        return realizedProfit;
    }

    public StockPortfolio getStockPortfolio() {
        return stockPortfolio;
    }

    public double getUnrealizedProfits(){
        return this.unrealizedProfits;
    }

    public Map<String, Double> getUnrealizedProfitsMap(){
        return stockPortfolio.getUnrealizedProfitPerStock();
    }

    public double getStockPortfolioValue(){
        return this.stockPortfolioValue;
    }

    // Observer notify method, which is called by the Manager.
    @Override
    public void update(Stock stock){
        if (stock.getPrice() >= 0){
            System.out.println(stock.getName() + " has been updated to $" + stock.getPrice() + "!");
        } else {
            // immediately sell off the stock
            List<Double> profits = this.stockPortfolio.removeStock(username, new Stock(stock.getName(), -1*stock.getPrice()), -1);
            double realizedProfit = profits.get(0);
            double initalCosts = profits.get(1);
            double totalProfits = (realizedProfit + initalCosts);
            System.out.println("Stock sold! Here is the total profit: " + totalProfits);
            deposit(username, new Currency((realizedProfit + initalCosts), Currency.DOLLARS), true);
        }
        // update the unrealized profit and portfolio value
        this.unrealizedProfits = stockPortfolio.updateUnrealizedProfit(username);
        this.stockPortfolioValue = stockPortfolio.updatePortfolioValue(username);
    }
    
}
