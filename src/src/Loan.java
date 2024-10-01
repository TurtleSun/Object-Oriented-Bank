/*
  * LoanDatabase.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Loan class made to avoid having large tuple of data
  * and instead one object class.
  */

package src;

public class Loan {
    private String username;
    private String collateral;
    private double collateralAmount;
    private double loanAmount;

    public Loan(String username, String collateral, double collateralAmount, double loanAmount) {
        this.username = username;
        this.collateral = collateral;
        this.collateralAmount = collateralAmount;
        this.loanAmount = loanAmount;
    }

    public String getUsername(){
        return username;
    }

    public String getCollateral() {
        return collateral;
    }

    public double getCollateralAmount() {
        return collateralAmount;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

}
