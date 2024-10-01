/*
  * CustomerDatabase.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Database class regarding all things customer related
  * especially accounts. All persistent data is saved in to
  * a CSV. There may seem to be a lot of repetitive code, but it is
  * necessary as every CSV has a unique structure to it and each method
  * needs all the boiler plate code with minor modifications in most cases.
  */

package src;

import com.opencsv.*;
import com.opencsv.exceptions.CsvException;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// This class will support getting and creating all objects from CSV files

public class CustomerDatabase {
    public static final String CUSTOMER_SAVINGS_CSV_FILEPATH = "customerSavings.csv";
    public static final String CUSTOMER_CHECKING_CSV_FILEPATH = "customerChecking.csv";
    public static final String CUSTOMER_SECURITY_CSV_FILEPATH = "customerSecurity.csv";
    public static final String DATE_TXT_FILEPATH = "date.txt";
    public static final String CUSTOMER_LOGIN_CSV_FILEPATH = "login.csv";
    public static final String CUSTOMER_TRANSACTIONS_CSV_FILEPATH = "customerTransactions.csv";

    public static Customer getCustomer(String username) {
        Customer customer = new Customer(username);
        boolean fileExists = new File(CUSTOMER_SAVINGS_CSV_FILEPATH).exists();
        
        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(CUSTOMER_SAVINGS_CSV_FILEPATH))) {
                List<String[]> accounts = reader.readAll();
                for (String[] account : accounts) {
                    if (account[0].equals(username)) {
                        Map<Integer, Currency> hm = new HashMap<>();
                        hm.put(Currency.DOLLARS, new Currency(Double.parseDouble(account[1]), Currency.DOLLARS));
                        hm.put(Currency.YUAN, new Currency(Double.parseDouble(account[2]), Currency.YUAN));
                        hm.put(Currency.WON, new Currency(Double.parseDouble(account[3]), Currency.WON));
                        customer.addBankAccount(Account.SAVINGS_ACCOUNT, new SavingsAccount(hm, username));
                    }
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }

        fileExists = new File(CUSTOMER_CHECKING_CSV_FILEPATH).exists();

        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(CUSTOMER_CHECKING_CSV_FILEPATH))) {
                List<String[]> accounts = reader.readAll();
                for (String[] account : accounts) {
                    if (account[0].equals(username)) {
                        Map<Integer, Currency> hm = new HashMap<>();
                        hm.put(Currency.DOLLARS, new Currency(Double.parseDouble(account[1]), Currency.DOLLARS));
                        hm.put(Currency.YUAN, new Currency(Double.parseDouble(account[2]), Currency.YUAN));
                        hm.put(Currency.WON, new Currency(Double.parseDouble(account[3]), Currency.WON));

                        customer.addBankAccount(Account.CHECKINGS_ACCOUNT, new CheckingAccount(hm, username));
                    }
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }

        fileExists = new File(CUSTOMER_SECURITY_CSV_FILEPATH).exists();
        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(CUSTOMER_SECURITY_CSV_FILEPATH))) {
                List<String[]> accounts = reader.readAll();
                for (String[] account : accounts) {
                    if (account[0].equals(username)) {
                        Map<Integer, Currency> hm = new HashMap<>();
                        hm.put(Currency.DOLLARS, new Currency(Double.parseDouble(account[1]), Currency.DOLLARS));
                        hm.put(Currency.YUAN, new Currency(Double.parseDouble(account[2]), Currency.YUAN));
                        hm.put(Currency.WON, new Currency(Double.parseDouble(account[3]), Currency.WON));
                        boolean isEnabled = Boolean.valueOf(account[4]);
                        customer.addBankAccount(Account.SECURITIES_ACCOUNT, new SecurityAccount(hm, username, isEnabled));
                    }
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }

        return customer;
    }

    public static List<Customer> getAllCustomers() {
        List<Customer> allCustomers = new ArrayList<>();

        boolean fileExists = new File(Login.LOGIN_CSV_FILEPATH).exists();

        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(Login.LOGIN_CSV_FILEPATH))) {
                List<String[]> accounts = reader.readAll();
                for (String[] account : accounts) {
                    allCustomers.add(getCustomer(account[0]));
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
        return allCustomers;
    }

    public static boolean createCustomerSavingsAccount(String username, Currency currency) {
        boolean fileExists = new File(CUSTOMER_SAVINGS_CSV_FILEPATH).exists();

        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(CUSTOMER_SAVINGS_CSV_FILEPATH))) {
                List<String[]> accounts = reader.readAll();
                for (String[] account : accounts) {
                    if (account[0].equals(username)) {
                        return false;
                    }
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
                return false;
            }
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(CUSTOMER_SAVINGS_CSV_FILEPATH, true))) {
            String[] account = {username, "0.0", "0.0", "0.0"};
            if (currency.getCurrencyType() == Currency.DOLLARS) {
                account[1] = "" + (currency.getAmount());
            } else if (currency.getCurrencyType() == Currency.YUAN) {
                account[2] = "" + currency.getAmount();
            } else if (currency.getCurrencyType() == Currency.WON) {
                account[3] = "" + currency.getAmount();
            }
            writer.writeNext(account);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean disableOrEnableCustomerCheckingAccount(String username, boolean enable) {
        boolean fileExists = new File(CUSTOMER_SECURITY_CSV_FILEPATH).exists();
        List<String[]> accounts;
        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(CUSTOMER_SECURITY_CSV_FILEPATH))) {
                accounts = reader.readAll();
                boolean accountFound = false;
                for (String[] account : accounts) {
                    if (account[0].equals(username)) {
                        account[4] = "" + enable;
                        accountFound = true;
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
    
        try (CSVWriter writer = new CSVWriter(new FileWriter(CUSTOMER_SECURITY_CSV_FILEPATH))) {
            writer.writeAll(accounts);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createCustomerCheckingAccount(String username, Currency currency) {
        boolean fileExists = new File(CUSTOMER_CHECKING_CSV_FILEPATH).exists();

        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(CUSTOMER_CHECKING_CSV_FILEPATH))) {
                List<String[]> accounts = reader.readAll();
                for (String[] account : accounts) {
                    if (account[0].equals(username)) {
                        return false;
                    }
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
                return false;
            }
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(CUSTOMER_CHECKING_CSV_FILEPATH, true))) {
            String[] account = {username, "0.0", "0.0", "0.0"};
            if (currency.getCurrencyType() == Currency.DOLLARS) {
                account[1] = "" + currency.getAmount();
            } else if (currency.getCurrencyType() == Currency.YUAN) {
                account[2] = "" + currency.getAmount();
            } else if (currency.getCurrencyType() == Currency.WON) {
                account[3] = "" + currency.getAmount();
            }
            writer.writeNext(account);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createCustomerSecurityAccount(String username, Currency currency) {
        boolean fileExists = new File(CUSTOMER_SECURITY_CSV_FILEPATH).exists();

        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(CUSTOMER_SECURITY_CSV_FILEPATH))) {
                List<String[]> accounts = reader.readAll();
                for (String[] account : accounts) {
                    if (account[0].equals(username)) {
                        return false;
                    }
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
                return false;
            }
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(CUSTOMER_SECURITY_CSV_FILEPATH, true))) {
            String[] account = {username, "0.0", "0.0", "0.0", "true"};
            if (currency.getCurrencyType() == Currency.DOLLARS) {
                account[1] = "" + currency.getAmount();
            } else if (currency.getCurrencyType() == Currency.YUAN) {
                account[2] = "" + currency.getAmount();
            } else if (currency.getCurrencyType() == Currency.WON) {
                account[3] = "" + currency.getAmount();
            }
            writer.writeNext(account);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deposit(String username, Currency currency, int accountType) {
        String filePath = "";
        if (accountType == Account.SAVINGS_ACCOUNT) {
            filePath = CUSTOMER_SAVINGS_CSV_FILEPATH;
        } else if (accountType == Account.CHECKINGS_ACCOUNT) {
            filePath = CUSTOMER_CHECKING_CSV_FILEPATH;
        } else if (accountType == Account.SECURITIES_ACCOUNT) {
            filePath = CUSTOMER_SECURITY_CSV_FILEPATH;
        } else {
            return false;
        }
        int index = currency.getCurrencyType() + 1;
        boolean fileExists = new File(filePath).exists();
        List<String[]> accounts;
        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
                accounts = reader.readAll();
                boolean accountFound = false;
                for (String[] account : accounts) {
                    if (account[0].equals(username)) {
                        account[index] = String.valueOf(Double.parseDouble(account[index]) + currency.getAmount());
                        accountFound = true;
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
    
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeAll(accounts);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean closeAccount(String username, int accountType) {
        String filePath = "";
        if (accountType == Account.SAVINGS_ACCOUNT) {
            filePath = CUSTOMER_SAVINGS_CSV_FILEPATH;
        } else if (accountType == Account.CHECKINGS_ACCOUNT) {
            filePath = CUSTOMER_CHECKING_CSV_FILEPATH;
        } else if (accountType == Account.SECURITIES_ACCOUNT) {
            filePath = CUSTOMER_SECURITY_CSV_FILEPATH;
        } else {
            return false;
        }

        boolean fileExists = new File(filePath).exists();
        if (!fileExists) {
            // CSV file doesn't exist
            return false;
        }

        List<String[]> accounts = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            accounts = reader.readAll();
            boolean accountFound = false;
            // written with chat
            Iterator<String[]> iterator = accounts.iterator();
            while (iterator.hasNext()) {
                String[] account = iterator.next();
                if (account[0].equals(username)) {
                    iterator.remove();
                    accountFound = true;
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

        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeAll(accounts);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean withdraw(String username, Currency currency, int accountType) {
        String filePath = "";
        if (accountType == Account.SAVINGS_ACCOUNT) {
            filePath = CUSTOMER_SAVINGS_CSV_FILEPATH;
        } else if (accountType == Account.CHECKINGS_ACCOUNT) {
            filePath = CUSTOMER_CHECKING_CSV_FILEPATH;
        } else if (accountType == Account.SECURITIES_ACCOUNT) {
            filePath = CUSTOMER_SECURITY_CSV_FILEPATH;
        } else {
            return false;
        }
        int index = currency.getCurrencyType() + 1;
        boolean fileExists = new File(filePath).exists();
        List<String[]> accounts;
        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
                accounts = reader.readAll();
                boolean accountFound = false;
                for (String[] account : accounts) {
                    if (account[0].equals(username)) {

                        account[index] = String.valueOf(Double.parseDouble(account[index]) - currency.getAmount());

                        accountFound = true;
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
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeAll(accounts);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static LocalDate getDate() {
        Path filePath = Paths.get(DATE_TXT_FILEPATH);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate date;

            if (Files.exists(filePath)) {
                byte[] bytes = Files.readAllBytes(filePath);
                String dateString = new String(bytes, StandardCharsets.UTF_8).trim();
                date = LocalDate.parse(dateString, formatter);
            } else {
                date = LocalDate.now();
                Files.write(filePath, date.format(formatter).getBytes(StandardCharsets.UTF_8));
            }
            return date;
        } catch (IOException e) {
            System.out.println("An error occurred while reading/writing the date file.");
            e.printStackTrace();
        }
        return null;
    }

    public static void increaseDateByDay() {
        Path filePath = Paths.get(DATE_TXT_FILEPATH);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Written with GPT
        try {
            LocalDate date;

            if (Files.exists(filePath)) {
                byte[] bytes = Files.readAllBytes(filePath);
                String dateString = new String(bytes, StandardCharsets.UTF_8).trim();
                date = LocalDate.parse(dateString, formatter);
            } else {
                date = LocalDate.now();
                Files.write(filePath, date.format(formatter).getBytes(StandardCharsets.UTF_8));
            }
            LocalDate updatedDate = date.plusDays(1);
            Files.write(filePath, updatedDate.format(formatter).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("An error occurred while reading/writing the date file.");
            e.printStackTrace();
        }
    }

    public static void increaseDateByMonth() {
        Path filePath = Paths.get(DATE_TXT_FILEPATH);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Written with GPT
        try {
            LocalDate date;

            if (Files.exists(filePath)) {
                byte[] bytes = Files.readAllBytes(filePath);
                String dateString = new String(bytes, StandardCharsets.UTF_8).trim();
                date = LocalDate.parse(dateString, formatter);
            } else {
                date = LocalDate.now();
                Files.write(filePath, date.format(formatter).getBytes(StandardCharsets.UTF_8));
            }
            LocalDate updatedDate = date.plusMonths(1);
            Files.write(filePath, updatedDate.format(formatter).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("An error occurred while reading/writing the date file.");
            e.printStackTrace();
        }
    }

    public static boolean updateCustomerAccount(Account account) {
        String filepath = "";
        if (account.getAccountType() == Account.SAVINGS_ACCOUNT) {
            filepath = CUSTOMER_SAVINGS_CSV_FILEPATH;
        } else if (account.getAccountType() == Account.CHECKINGS_ACCOUNT) {
            filepath = CUSTOMER_CHECKING_CSV_FILEPATH;
        } else if (account.getAccountType() == Account.SECURITIES_ACCOUNT) {
            filepath = CUSTOMER_SECURITY_CSV_FILEPATH;
        }
        boolean fileExists = new File(filepath).exists();
        List<String[]> accounts;
        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(filepath))) {
                accounts = reader.readAll();
                boolean accountFound = false;
                for (String[] acc : accounts) {
                    if (acc[0].equals(account.getUsername())) {
                        acc[1] = String.valueOf(account.getCurrency(Currency.DOLLARS).getAmount());
                        acc[2] = String.valueOf(account.getCurrency(Currency.YUAN).getAmount());
                        acc[3] = String.valueOf(account.getCurrency(Currency.WON).getAmount());
                        accountFound = true;
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
    
        try (CSVWriter writer = new CSVWriter(new FileWriter(filepath))) {
            writer.writeAll(accounts);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static double[] getAccountBalanceFromCSV(String username, int accountType) {
        String filePath;
        switch(accountType) {
            case Account.SAVINGS_ACCOUNT:
                filePath = CUSTOMER_SAVINGS_CSV_FILEPATH;
                break;
            case Account.CHECKINGS_ACCOUNT:
                filePath = CUSTOMER_CHECKING_CSV_FILEPATH;
                break;
            default:
                return new double[]{0, 0, 0, 0, -1}; // Returns if account type is not supported
        }

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine[0].equals(username)) {
                    double usd = Double.parseDouble(nextLine[1]);
                    double yuan = Double.parseDouble(nextLine[2]);
                    double won = Double.parseDouble(nextLine[3]);
                    double totalBalance = usd + yuan/7 + won/1300;
                    return new double[]{totalBalance, usd, yuan, won, 0};
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return new double[]{0, 0, 0, 0, -1}; // Returns if no account is found or on error
    }
}
