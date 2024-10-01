/*
  * Currency.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Currency.java exists to simplify the creation of money
  * where instead of always dealing with a pair of objects
  * (Currency Type, Amount), we can pass in a object of type Currency.
  * Currency.java also includes helper methods regarding currencies.
  */

package src;
public class Currency {
    public static final int DOLLARS = 0;
    public static final int YUAN = 1;
    public static final int WON = 2;
    public static final int INVALID = -1;
    private double amount;
    private int currencyType;

    public Currency(int currencyType) {
        this.currencyType = currencyType;
        this.amount = 0;
    }

    public Currency(Currency currency) {
        this.amount = currency.getAmount();
        this.currencyType = currency.getCurrencyType();
    }

    public Currency(double amount, int currencyType) {
        this.amount = amount;
        this.currencyType = currencyType;
    }

    public double getAmount() {
        return amount;
    }

    public int getCurrencyType() {
        return currencyType;
    }

    public String getCurrencyTypeString() {
        if (currencyType == DOLLARS) {
            return "USD";
        } else if (currencyType == YUAN) {
            return "RMB";
        } else if (currencyType == WON) {
            return "WON";
        } else {
            return "";
        }
    }

    public Currency withdraw(double amount) {
        if (amount > this.amount) {
            return new Currency(0, INVALID);
        } else {
            this.amount -= amount;
            return new Currency(amount, currencyType);
        }
    }

    public void deposit(double amount) {
        this.amount += amount;
    }

    public static Currency exchange(Currency currency, int targetCurrencyType) {
        if (currency.getCurrencyType() == targetCurrencyType) {
            return currency;
        }
        double amount = -1;
        if (currency.getCurrencyType() == YUAN) {
            amount = currency.getAmount() / 7;
        } else if (currency.getCurrencyType() == WON) {
            amount = currency.getAmount() / 1300;
        } else if (currency.getCurrencyType() == DOLLARS) {
            amount = currency.getAmount();
        }

        if (targetCurrencyType == DOLLARS) {
            return new Currency(amount, DOLLARS);
        } else if (targetCurrencyType == YUAN) {
            return new Currency(amount * 7, YUAN);
        } else if (targetCurrencyType == WON) {
            return new Currency(amount * 1300, WON);
        } else {
            return null;
        }
    }
}