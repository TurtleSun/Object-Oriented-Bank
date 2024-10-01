/*
  * SecuritySubject.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Utilizes the Observer Pattern. Read Manager.java for more
  * info on how this is implemented
  */

package src;

public interface SecuritySubject {
    void addObserver(SecurityObserver observer);
    void removeObserver(SecurityObserver observer);
    void notifyObservers(Stock stock);
}
