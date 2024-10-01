/*
  * CheckingAccount.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * CheckingAccount has no special properties and its only
  * distinction is its accountType. The file exists to support
  * future implementations that could be added CheckingAccounts if needed.
  * The withdraw fee is implemented in Bank rather than CheckingAccount as 
  * CheckingAccount by itself has no reference to other accounts in the bank. 
  * All transactions are handled in bank as bank has the reference to all accounts in the system.
  */

package src;
import java.util.*;
public class CheckingAccount extends Account{
    public CheckingAccount(Map<Integer, Currency> currencies, String username){
        super(currencies, username);
        this.accountType = Account.CHECKINGS_ACCOUNT;
    }
}
