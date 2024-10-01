/*
  * LoanDatabase.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Database class regarding all things customer related
  * especially accounts. All persistent data is saved in to
  * a CSV. There is a lot of boiler plate code but it is required
  * as each method has small differences that make writing a method
  * very difficult
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

public class LoanDatabase {
    // Loans for Approval
    // Loans Active
    public static String LOAN_N_COLLATERAL_TOBEAPPROVED_CSV_FILEPATH = "loanNCollateralForApproval.csv";
    public static String LOAN_N_COLLATERAL_ACTIVE_CSV_FILEPATH = "loanNCollateralActive.csv";

    public static Loan getLoan(String username, String collateral, boolean isFromToBeApproved){
        File file = new File(LOAN_N_COLLATERAL_ACTIVE_CSV_FILEPATH);
        if (isFromToBeApproved) {
            file = new File(LOAN_N_COLLATERAL_TOBEAPPROVED_CSV_FILEPATH);
        }
        boolean fileExists = file.exists();

        if(!fileExists) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        try (CSVReader reader = new CSVReader(new FileReader(file))){
            List<String[]> loans = reader.readAll();
            for (String[] loan : loans) {
                if (loan[0].equals(username) && loan[1].equals(collateral)) {
                    if (isFromToBeApproved) {
                        return new Loan(username, loan[1], Double.parseDouble(loan[2]), Double.parseDouble(loan[3]));
                    } else {
                        return new Loan(username, loan[1], -1, Double.parseDouble(loan[2]));
                    } 
                }
            }
        }catch (IOException | CsvException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static List<Loan> getAllLoans(String username, boolean isFromToBeApproved){
        File file = new File(LOAN_N_COLLATERAL_ACTIVE_CSV_FILEPATH);
        if (isFromToBeApproved) {
            file = new File(LOAN_N_COLLATERAL_TOBEAPPROVED_CSV_FILEPATH);
        }

        boolean fileExists = file.exists();

        if(!fileExists) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        List<Loan> activeLoanList = new ArrayList<Loan>();
        try (CSVReader reader = new CSVReader(new FileReader(file))){
            List<String[]> loans = reader.readAll();
            for (String[] loan : loans) {
                if (loan[0].equals("")){
                    continue;
                }
                if (isFromToBeApproved) {
                    // Only manager looks at toBeApproved csv
                    activeLoanList.add(new Loan(loan[0], loan[1], Double.parseDouble(loan[2]), Double.parseDouble(loan[3])));
                } else {
                    // If manager looks for active csv, then get all active loans
                    if (username.equals("Manager")){
                        activeLoanList.add(new Loan(loan[0], loan[1], -1, Double.parseDouble(loan[2]))); 
                    } else if(loan[0].equals(username)) {
                        // else only look at loans with the given username
                        activeLoanList.add(new Loan(username, loan[1], -1, Double.parseDouble(loan[2]))); 
                    }
                }
            }
        }catch (IOException | CsvException e) {
            e.printStackTrace();
            return null;
        }
        return activeLoanList;
    }

    public static boolean addLoanToBeApproved(String username, String collateral, double collateralAmount, double loanAmount){
        // Add the given loan request to the database "to be approved"
        File file = new File(LOAN_N_COLLATERAL_TOBEAPPROVED_CSV_FILEPATH);
        boolean fileExists = file.exists();

        if(!fileExists) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        try (CSVReader reader = new CSVReader(new FileReader(file))){
            List<String[]> loans = reader.readAll();
            for (String[] loan : loans) {
                if (loan[0].equals(username) && loan[1].equals(collateral)) {
                    return false; // Loan already exists
                }
            }
        }catch (IOException | CsvException e) {
            e.printStackTrace();
            return false;
        }
        // If it gets here, that means the loan doens't already exist so add the loan
        try (CSVWriter writer = new CSVWriter(new FileWriter(file, true))) {
            String[] loan = {username, collateral, String.valueOf(collateralAmount), String.valueOf(loanAmount)};
            writer.writeNext(loan);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean addLoantoActiveList(String username, String collateral, double loanAmount){
        // Remove the given loan request from the database "to be approved"
        if(!(new File(LOAN_N_COLLATERAL_TOBEAPPROVED_CSV_FILEPATH).exists())) {
            // If it does not exist, that's bad
            System.out.println("ERROR: LOAN REQUEST CSV NOT CREATED");
            return false;
        } else {

            try (CSVReader reader = new CSVReader(new FileReader(LOAN_N_COLLATERAL_TOBEAPPROVED_CSV_FILEPATH))){
                List<String[]> loans =  reader.readAll();
                boolean loanRequestFound = false;
                Iterator<String[]> iterator = loans.iterator();
                while (iterator.hasNext()) {
                    String[] loan = iterator.next();
                    if (loan[0].equals(username) && loan[1].equals(collateral)) {
                        iterator.remove();
                        loanRequestFound = true;
                        break;
                    }
                }
                if (!loanRequestFound) {
                    // If it does not exist, that's bad
                    System.out.println("MAJOR ERROR: SUCH A LOAN REQUEST DOESN'T EXIST");
                    return false;
                }

                // Write the updated loan requests back to the CSV file
                try (CSVWriter writer = new CSVWriter(new FileWriter(LOAN_N_COLLATERAL_TOBEAPPROVED_CSV_FILEPATH))) {
                    writer.writeAll(loans);
                }
            }catch (IOException | CsvException e) {
                e.printStackTrace();
                return false;
            }
        }

        // Add the given loan request to the database "activeList"
        File file = new File(LOAN_N_COLLATERAL_ACTIVE_CSV_FILEPATH);
        boolean fileExists = file.exists();
        if(!fileExists) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        // Since it has been manually approved, no checks
        try (CSVWriter writer = new CSVWriter(new FileWriter(LOAN_N_COLLATERAL_ACTIVE_CSV_FILEPATH, true))) {
            String[] loan = {username, collateral, String.valueOf(loanAmount)};
            writer.writeNext(loan);
            return true;
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean rejectLoan(String username, String collateral){
        // Remove the given loan request from the database "to be approved"
        File file = new File(LOAN_N_COLLATERAL_TOBEAPPROVED_CSV_FILEPATH);
        boolean fileExists = file.exists();

        if(!fileExists){
            System.out.println("ERROR: LOAN REQUEST CSV NOT CREATED");
            return false;
        }

        try (CSVReader reader = new CSVReader(new FileReader(LOAN_N_COLLATERAL_TOBEAPPROVED_CSV_FILEPATH))){
            List<String[]> loans =  reader.readAll();
            boolean loanRequestFound = false;
            Iterator<String[]> iterator = loans.iterator();
            while (iterator.hasNext()) {
                String[] loan = iterator.next();
                if (loan[0].equals(username) && loan[1].equals(collateral)) {
                    iterator.remove();
                    loanRequestFound = true;
                    break;
                }
            }
            if (!loanRequestFound) {
                // If it does not exist, that's bad
                System.out.println("MAJOR ERROR: SUCH A LOAN REQUEST DOESN'T EXIST");
                return false;
            }

            // Write the updated loan requests back to the CSV file
            try (CSVWriter writer = new CSVWriter(new FileWriter(LOAN_N_COLLATERAL_TOBEAPPROVED_CSV_FILEPATH))) {
                writer.writeAll(loans);
                return true;
            }
        }catch (IOException | CsvException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Loan Close
    public static boolean closeLoan(String username, String collateral, Currency payment){
        // Remove the given loan request from the database "activeList"
        File file = new File(LOAN_N_COLLATERAL_ACTIVE_CSV_FILEPATH);
        boolean fileExists = file.exists();
        if(!fileExists) {
            System.out.println("Active Loan File Doesn't Exist");
            return false;
        }

        // Change payment to dollar value
        Currency dollarPayment = Currency.exchange(payment, 0);

        try (CSVReader reader = new CSVReader(new FileReader(file))){
            List<String[]> loans =  reader.readAll();
            boolean loanFound = false;
            Iterator<String[]> iterator = loans.iterator();
            while (iterator.hasNext()) {
                String[] loan = iterator.next();
                if (loan[0].equals(username) && loan[1].equals(collateral)) {
                    if (Double.parseDouble(loan[2]) > dollarPayment.getAmount()){
                        // Loan cannot be payed off since loan is more than provided payment
                        return false;
                    }
                    iterator.remove();
                    loanFound = true;
                    break;
                }
            }
            if (!loanFound) {
                // If it does not exist, that's bad
                System.out.println("MAJOR ERROR: SUCH A LOAN DOESN'T EXIST");
                return false;
            }

            // Write the updated loan requests back to the CSV file
            try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                writer.writeAll(loans);
            }
        }catch (IOException | CsvException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // loan update
    public static boolean updateLoan(String username, String collateral, double interest){
        // Change the loanAmount for a given loan based on the amount
        File file = new File(LOAN_N_COLLATERAL_ACTIVE_CSV_FILEPATH);

        try {
            if (!file.exists()) {
                return false;
            }

            try (CSVReader reader = new CSVReader(new FileReader(file))) {
                String[] line = null;
                boolean loanFound = false;
                List<String[]> loans =  reader.readAll();

                Iterator<String[]> iterator = loans.iterator();
                while (iterator.hasNext()) {
                    line = iterator.next();
                    if (line[0].equals(username) && line[1].equals(collateral)) {
                        // update the loanAmount based on interest
                        double price = Double.parseDouble(line[2]) + interest;
                        line[2] = String.valueOf(price);
                        iterator.remove();
                        loanFound = true;
                        break;
                    }
                }

                if (loanFound) {
                    // Write the updated data back to the CSV file
                    try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                        writer.writeNext(line);
                    }
                    return true; // Loan updated successfully
                } else {
                    return false;
                }
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Interest
    public static void addInterest(String username) {
        File file = new File(LOAN_N_COLLATERAL_ACTIVE_CSV_FILEPATH);

        if (file.exists()) {
            try (CSVReader reader = new CSVReader(new FileReader(file));
                CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                String[] line;
                while ((line = reader.readNext()) != null) {
                    double loanAmount = Double.parseDouble(line[2]);
                    double interest = Math.floor(loanAmount * 0.5);
                    updateLoan(username, line[1], interest);
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }
    }
}
