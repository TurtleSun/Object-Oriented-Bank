/*
  * StockPortfolio.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Helper class consisting of methods for calculating a value of a StockPorfolio
  * of a Customer. StockPortfolio requires too many bookkeeping to keep an internal
  * state and updating to the database at the same time. Instead, we always get and
  * write to the database
  */

package src;
import java.util.*;

public class StockPortfolio {

    private Map<String, Double> unrealizedProfitPerStock = new HashMap<String, Double>();
    
    public void addStock(String username, Stock stock){
        StockDatabase.addStockToPortfolio(username, stock);
    }

    public List<Double> removeStock(String username, Stock stock, int amount) {
        // returns {realizedProfit, initalCostForStock}
        System.out.println("Here is the username in portfolio removeStock: " + username);
        // If amount is negative, then sell everything
        if (amount < 0){
            StockDetails stockDetails = StockDatabase.getStockFromPortfolio(username, stock.getName());
            if (stockDetails != null){
                amount = stockDetails.getAmount();
            } else {
                return new ArrayList<>(Arrays.asList(0.0, 0.0));
            }
        }
        List<Double> initPrices = StockDatabase.removeStockFromPortfolio(username, stock, amount);
        double realizedProfit = calculateRealizedProfits(stock, initPrices, amount);
        System.out.println("This is the realized profit after removing stock: " + realizedProfit);
        
        List<Double> profits = new ArrayList<Double>();
        profits.add(realizedProfit);
        profits.add(calculateTotalRevenue(initPrices));
        return profits;
    }

    private double calculateTotalRevenue(List<Double> initPrices){
        double initCost = 0;
        for (int i = 0; i < initPrices.size(); i++) {
            initCost = initCost + initPrices.get(i);
        }
        return initCost;
    }

    private double calculateRealizedProfits(Stock stock, List<Double> prices, int amount){
        double currStockPrice = stock.getPrice();
        double realizedProfits = 0;
        for (int i = 0; i < amount; i++) {
            realizedProfits = realizedProfits + (currStockPrice - prices.get(i));
        }
        return realizedProfits;
    }

    public Map<String, Double> getUnrealizedProfitPerStock() {
        return unrealizedProfitPerStock;
    }

    public double updateUnrealizedProfit(String username){
        double unrealizedProfit = 0;
        // Go through all stocks and calculate current price vs inital buying price
        HashMap<String, List<Double>> initPrices = StockDatabase.getAllInitPrices(username);
        if (initPrices != null) {
            for (Map.Entry<String, List<Double>>  entry : initPrices.entrySet()) {
                String stockName = entry.getKey();
                List<Double> stockValues = entry.getValue();
                int totalAmountofStock = stockValues.size();
                double unrealizedP4Stock = calculateRealizedProfits(Manager.getSingletonManager().getStock(stockName), stockValues, totalAmountofStock);
                if (unrealizedProfitPerStock.containsKey(stockName)){
                    unrealizedProfitPerStock.remove(stockName);
                }
                unrealizedProfitPerStock.put(stockName, unrealizedP4Stock);
                unrealizedProfit += unrealizedP4Stock;
            }
        }
        return unrealizedProfit;
    }

    public double updatePortfolioValue(String username){
        double portfolioValue = 0;
        HashMap<String, List<Double>> initPrices = StockDatabase.getAllInitPrices(username);
        for (Map.Entry<String, List<Double>>  entry : initPrices.entrySet()) {
            String stockName = entry.getKey();
            Stock currStock = Manager.getSingletonManager().getStock(stockName);
            int totalAmountofStock = entry.getValue().size();
            double worth = currStock.getPrice() * totalAmountofStock;
            portfolioValue += worth;
        }
        return portfolioValue;
    }

}
