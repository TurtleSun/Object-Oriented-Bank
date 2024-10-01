/*
  * Transaction.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Helper class consisting of attributes representing a Transaction.
  * This helps with grabbing and writing from the database a single instance of a
  * Transaction rather than having tuples
  */


package src;
import java.time.LocalDate;
public class Transaction {
    private LocalDate date;
    private Currency currency;
    private String sender;
    private String receiver;
    private int senderAccountType;
    private int receiverAccountType;

    public Transaction(Currency currency, String sender, String receiver, int senderAccountType, int receiverAccountType) {
        this.date = CustomerDatabase.getDate();
        this.currency = currency;
        this.sender = sender;
        this.receiver = receiver;
        this.senderAccountType = senderAccountType;
        this.receiverAccountType = receiverAccountType;
    }
    public Transaction(LocalDate date, Currency currency, String sender, String receiver, int senderAccountType, int receiverAccountType) {
        this.date = date;
        this.currency = currency;
        this.sender = sender;
        this.receiver = receiver;
        this.senderAccountType = senderAccountType;
        this.receiverAccountType = receiverAccountType;
    }

    public Currency getCurrency() {
        return currency;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public int getSenderAccountType() {
        return senderAccountType;
    }

    public int getReceiverAccountType() {
        return receiverAccountType;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTarget(String username) {
        if (sender.equals(username)) {
            return receiver;
        } else if (receiver.equals(username)) {
            return sender;
        } else {
            return "[DEBUG]: Transaction.java";
        }
    }

    public boolean isDeposit() {
        if (senderAccountType == -1) {
            return true;
        }
        return false;
    }

    public boolean isDeposit(String username, int accountType) {
        if (receiver.equals(username) && receiverAccountType == accountType) {
            return true;
        }
        return false;
    }

    public boolean isWithdraw(String username, int accountType) {
        if (sender.equals(username) && senderAccountType == accountType) {
            return true;
        }
        return false;
    }

    public boolean isWithdraw() {
        if (receiverAccountType == -1) {
            return true;
        }
        return false;
    }
    

    // Source: https://stackoverflow.com/questions/2517709/comparing-two-java-util-dates-to-see-if-they-are-in-the-same-day
    public static boolean isSameDay(LocalDate d1, Transaction t) {
        LocalDate d2 = t.getDate();
        
        return d1.equals(d2);
    }
}
