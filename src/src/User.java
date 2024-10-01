/*
  * User.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Abstract class extended by Manager and Customer and holds
  * shared attributes and methods for both classes
  */

package src;
public abstract class User {
    protected String username;
    protected Bank bank;

    public User(String username) {
        this.username = username;
        bank = Bank.getSingletonBank();
    }

    public String getUsername() {
        return username;
    }
}
