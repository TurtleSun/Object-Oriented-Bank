 /*
  * ButtonObserver.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Utilizies Observer pattern where each object (portfolio instance)
  * is added to the subject (home page). The subject waits for an observer
  * to notify that it has done something to create changes in the database.
  * The subject will then be notified to refresh their date and data.
  * This allows syncing of multiple windows to show up-to-date data.
  */

package GUI;

public interface ButtonObserver {
    void refreshData();
    void refreshDate();
}
