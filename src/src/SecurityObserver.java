/*
  * SecurityObserver.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Utilizes the Observer Pattern. Read SecurityAccount.java for more
  * info on how this is implemented
  */

package src;

public interface SecurityObserver {
    void update(Stock stock);
}
