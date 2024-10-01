/*
  * Login.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Database class that handles logging in and registering.
  * No encryption is used, a simple csv file is where we keep track
  * of users.
  */

package src;

import com.opencsv.*;
import com.opencsv.exceptions.CsvException;

import java.io.*;
import java.util.*;


// https://www.geeksforgeeks.org/writing-a-csv-file-in-java-using-opencsv/
// https://www.geeksforgeeks.org/reading-csv-file-java-using-opencsv/
public class Login {
    public static final String LOGIN_CSV_FILEPATH = "login.csv";

    public static boolean createLoginAccount(String username, String password) {
        boolean fileExists = new File(LOGIN_CSV_FILEPATH).exists();

        if (fileExists) {
            try (CSVReader reader = new CSVReader(new FileReader(LOGIN_CSV_FILEPATH))) {
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

        try (CSVWriter writer = new CSVWriter(new FileWriter(LOGIN_CSV_FILEPATH, true))) {
            String[] account = {username, password};
            writer.writeNext(account);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean login(String username, String password) {
        boolean fileExists = new File(LOGIN_CSV_FILEPATH).exists();

        if (!fileExists) {
            return false;
        }

        try (CSVReader reader = new CSVReader(new FileReader(LOGIN_CSV_FILEPATH))) {
            List<String[]> accounts = reader.readAll();
            for (String[] account : accounts) {
                if (account[0].equals(username) && account[1].equals(password)) {
                    return true;
                }
            }
            return false;
        } catch (IOException | CsvException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static final String MANAGER_LOGIN_CSV_FILEPATH = "managerLogin.csv";
    
    public static boolean managerLogin(String username, String password) {
        boolean fileExists = new File(MANAGER_LOGIN_CSV_FILEPATH).exists();

        if (!fileExists) {
            return false;
        }

        try (CSVReader reader = new CSVReader(new FileReader(MANAGER_LOGIN_CSV_FILEPATH))) {
            List<String[]> accounts = reader.readAll();
            for (String[] account : accounts) {
                if (account[0].equals(username) && account[1].equals(password)) {
                    return true;
                }
            }
            return false;
        } catch (IOException | CsvException e) {
            e.printStackTrace();
            return false;
        }
    }
}
