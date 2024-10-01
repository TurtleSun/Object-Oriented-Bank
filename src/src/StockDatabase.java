/*
  * StockDatabase.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Database class regarding all things stock related,
  * which includes operations from the manager changing prices
  * to what price a customer bought a stock at
  */

package src;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

public class StockDatabase {
    public static final String STOCK_PRICE_CSV_FILEPATH = "stockPrice.csv";
    public static final String STOCK_PORTFOLIO_CSV_FILEPATH = "stockPortfolio.csv";
    public static final String CUSTOMER_SECURITY_CSV_FILEPATH = "customerSecurity.csv";

    public static double getRealizedProfit(String username) {
        boolean fileExists = new File (CUSTOMER_SECURITY_CSV_FILEPATH).exists();
        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(CUSTOMER_SECURITY_CSV_FILEPATH))) {
                List<String[]> accounts = reader.readAll();
                for (String[] acc : accounts) {
                    if (username.equals(acc[0])) {
                        return Double.parseDouble(acc[3]);
                    }
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public static boolean updateRealizedProfit(String username, double realizedProfit) {
        boolean fileExists = new File(CUSTOMER_SECURITY_CSV_FILEPATH).exists();
        List<String[]> accounts = null;
        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(CUSTOMER_SECURITY_CSV_FILEPATH))) {
                accounts = reader.readAll();
                for (String[] acc : accounts) {
                    if (username.equals(acc[0])) {
                        acc[3] = String.valueOf(Double.parseDouble(acc[3]) + realizedProfit);
                    }
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(CUSTOMER_SECURITY_CSV_FILEPATH))) {
            writer.writeAll(accounts);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }


    }

    public static Set<String> getAllUsernames() {
        Set<String> hs = new HashSet<>();
        boolean fileExists = new File(STOCK_PORTFOLIO_CSV_FILEPATH).exists();

        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(STOCK_PORTFOLIO_CSV_FILEPATH))) {
                List<String[]> stocks = reader.readAll();
                for (String[] stock : stocks) {
                    if (!hs.contains(stock[0])) {
                        hs.add(stock[0]);
                    }
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
        return hs;
    }

    public static Stock getStock(String stockName) {
        boolean fileExists = new File(STOCK_PRICE_CSV_FILEPATH).exists();
        
        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(STOCK_PRICE_CSV_FILEPATH))) {
                List<String[]> stocks = reader.readAll();
                for (String[] stock : stocks) {
                    if (stock[0].equals(stockName)) {
                        return new Stock(stock[0], Double.parseDouble(stock[1]));
                    }
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static StockDetails getStockFromPortfolio(String username, String stockName){
        boolean fileExists = new File(STOCK_PORTFOLIO_CSV_FILEPATH).exists();
        
        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(STOCK_PORTFOLIO_CSV_FILEPATH))) {
                List<String[]> stocks = reader.readAll();
                List<Double> initPrices = new ArrayList<Double>();
                for (String[] stock : stocks) {
                    if (stock[0].equals(username) && stock[1].equals(stockName)) {
                        for (int i = 3; i < stock.length; i++){
                            initPrices.add(Double.parseDouble(stock[i]));
                        }
                        return new StockDetails(stock[1], Integer.parseInt(stock[2]), initPrices);
                    }
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
        return null;
        
    }

    public static List<Stock> getAllStocks() {
        boolean fileExists = new File(STOCK_PRICE_CSV_FILEPATH).exists();

        List<Stock> ls = new ArrayList<>();
        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(STOCK_PRICE_CSV_FILEPATH))) {
                List<String[]> stocks = reader.readAll();
                for (String[] stock : stocks) {
                    ls.add(new Stock(stock[0], Double.parseDouble(stock[1])));
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
        return ls;
    }

    public static List<StockDetails> getAllStocksFromPortfolio(String username){
        boolean fileExists = new File(STOCK_PORTFOLIO_CSV_FILEPATH).exists();

        List<StockDetails> ls = new ArrayList<>();
        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(STOCK_PORTFOLIO_CSV_FILEPATH))) {
                List<String[]> stocks = reader.readAll();
                for (String[] stock : stocks) {
                    if (stock[0].equals(username)) {
                        ls.add(new StockDetails(stock[1], Integer.parseInt(stock[2]), null));
                    }
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
        return ls;
    }

    public static HashMap<String, List<Double>> getAllInitPrices(String username) {
        boolean fileExists = new File(STOCK_PORTFOLIO_CSV_FILEPATH).exists();
        if (fileExists) {
            HashMap<String, List<Double>> prices = new HashMap<>();
            try (CSVReader reader = new CSVReader(new FileReader(STOCK_PORTFOLIO_CSV_FILEPATH))) {
                List<String[]> stocks = reader.readAll();
                for (String[] stock : stocks) {
                    if (stock[0].equals(username)) {
                        // Get all initPrices for that line
                        int length = stock.length;
                        List<Double> initPrices = new ArrayList<Double>();
                        for (int i = 3; i < length; i++) {
                            initPrices.add(Double.parseDouble(stock[i]));
                        }
                        prices.put(stock[1], initPrices); // Assuming stock[1] contains the stock name
                    }
                }
                return prices;
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean addStock(Stock stock) {
        List<String[]> stocks = new ArrayList<>();
    
        File file = new File(STOCK_PRICE_CSV_FILEPATH);
        boolean fileExists = file.exists();

        if (fileExists) {
                try (CSVReader reader = new CSVReader(new FileReader(STOCK_PRICE_CSV_FILEPATH))) {
                    stocks = reader.readAll();
                    for (String[] s : stocks) {
                        if (s[0].equals(stock.getName())) {
                            return false;
                        }
                    }
                } catch (IOException | CsvException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(STOCK_PRICE_CSV_FILEPATH, true))) {
            String[] s = {stock.getName(), String.valueOf(stock.getPrice())};
            writer.writeNext(s);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean addStockToPortfolio(String username, Stock stock) {
        File file = new File(STOCK_PORTFOLIO_CSV_FILEPATH);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

                try (CSVReader reader = new CSVReader(new FileReader(file))){
                    List<String[]> stocks =  reader.readAll();
                    boolean stockFound = false;
                    String[] line = null;
                    //int index = -1;
                    Iterator<String[]> iterator = stocks.iterator();
                    while (iterator.hasNext()) {
                        String[] s = iterator.next();
                        if (s[0].equals(username) && s[1].equals(stock.getName())) {
                            line = Arrays.copyOf(s, s.length + 1);
                            line[2] = String.valueOf(Integer.parseInt(s[2]) + 1);
                            iterator.remove();
                            stockFound = true;
                            break;
                        }
                    }
                    if (stockFound) {
                        // remove the existing stock
                        writeToPortfolio(stocks);
                        line[line.length - 1] = String.valueOf(stock.getPrice());
                    } else {
                        line = new String[] {username, stock.getName(), "1", String.valueOf(stock.getPrice())};
                    }
                
                    // Write the updated loan requests back to the CSV file
                    try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                        stocks.add(line);
                        writer.writeAll(stocks);
                    }
                }catch (IOException | CsvException e) {
                    e.printStackTrace();
                    return false;
                }

                return true;
            
        
    }

    public static boolean removeStock(String name) {
        boolean fileExists = new File(STOCK_PRICE_CSV_FILEPATH).exists();
        if (!fileExists) {
            return false;
        }

        List<String[]> stocks = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(STOCK_PRICE_CSV_FILEPATH))) {
            stocks = reader.readAll();
            boolean stockFound = false;
            // written with chat
            Iterator<String[]> iterator = stocks.iterator();
            while (iterator.hasNext()) {
                String[] stock = iterator.next();
                if (stock[0].equals(name)) {
                    iterator.remove();
                    stockFound = true;
                    break;
                }
            }
            if (!stockFound) {
                return false;
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
            return false;
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(STOCK_PRICE_CSV_FILEPATH))) {
            writer.writeAll(stocks);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void writeToPortfolio(List<String[]> stocks){
        try (CSVWriter writer = new CSVWriter(new FileWriter(STOCK_PORTFOLIO_CSV_FILEPATH))) {
            writer.writeAll(stocks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Double> removeStockFromPortfolio(String username, Stock stock, int amount){
        List<Double> initPrices = new ArrayList<Double>();
        for (int i = 0; i < amount; i++) {
            double price = removeStockFromPortfolio(username, stock);
            if (price == -1.0){
                System.out.println("ERROR: " + username + " tried to get an invalid stock" + stock.getName());
                return initPrices;
            }
            initPrices.add(price);
        }
        return initPrices;
    }

    private static double removeStockFromPortfolio(String username, Stock stock){
        boolean fileExists = new File(STOCK_PORTFOLIO_CSV_FILEPATH).exists();

        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(STOCK_PORTFOLIO_CSV_FILEPATH))) {
                List<String[]> portfolioList = reader.readAll();
                // username stockName amountofStock initalStockPrice1 ... initalStockPricei
                double initPrice = -1.0;
                String[] line = null;
                
                Iterator<String[]> iterator = portfolioList.iterator();
                while (iterator.hasNext()) {
                    String[] portfolio = iterator.next();
                    if (portfolio[0].equals(username) && portfolio[1].equals(stock.getName())) {
                        line = Arrays.copyOf(portfolio, portfolio.length - 1);
                        line[2] = String.valueOf(Integer.parseInt(portfolio[2]) - 1);
                        //int index = portfolio.length;
                        initPrice = Double.parseDouble(portfolio[3]);
                        for (int i = 0; i < portfolio.length-1; i++){
                            if (i >= 3){
                                line[i] = portfolio[i+1]; // Shift portfolio initprices by one
                            }
                        }
                        iterator.remove();
                        break;
                    }
                }
                writeToPortfolio(portfolioList);
                // Write the updated portfolio list back to the CSV file
                try (CSVWriter writer = new CSVWriter(new FileWriter(STOCK_PORTFOLIO_CSV_FILEPATH))) {
                    if (Integer.parseInt(line[2]) > 0){
                        portfolioList.add(line);
                    }
                    writer.writeAll(portfolioList);
                }
                return initPrice;
            } catch (IOException | CsvException e) {
                e.printStackTrace();
                
            }
        }
        return -1;
    }

    public static boolean updateStockPrice(String name, double newPrice) {
        boolean fileExists = new File(STOCK_PRICE_CSV_FILEPATH).exists();
        List<String[]> stocks;
        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(STOCK_PRICE_CSV_FILEPATH))) {
                stocks = reader.readAll();
                boolean accountFound = false;
                for (String[] s : stocks) {
                    if (s[0].equals(name)) {
                        accountFound = true;
                        s[1] = String.valueOf(newPrice);
                        break;
                    }
                }
                if (!accountFound) {
                    return false;
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // csv file doesnt exist
            return false;
        }
    
        try (CSVWriter writer = new CSVWriter(new FileWriter(STOCK_PRICE_CSV_FILEPATH))) {
            writer.writeAll(stocks);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
