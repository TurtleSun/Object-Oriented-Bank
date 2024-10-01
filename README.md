# ATM Bank System (Object-Oriented-Bank)

## Introduction
This project implements an ATM system with both customer and manager interfaces. It utilizes various design patterns such as Singleton, Proxy, and Observer to create a robust and flexible banking application.

## How to Run

1. Ensure you are in the project root directory.
2. Run the following command in the terminal:
``` bash
./make.sh
```
3. Make sure the `managerLogin.csv` file exists in the `bin` folder with the following content:
```
"Manager","password"
```
(exclude the outermost quotations)

> **Note**: There is no option to register as a Manager in the program itself. This must be done manually in the backend to prevent unauthorized access to the manager page.

### Default Manager Login:

- **Username**: Manager
- **Password**: password

## File Descriptions

### Front-End:

- **ManagerPortfolio.java**: Implements `ButtonObserver` and uses the proxy pattern. Allows managers to perform actions that modify the database.
- **CustomerPortfolio.java**: Implements `ButtonObserver` and uses the proxy pattern. Allows customers to perform ATM actions.
- **HomePage.java**: Implements `ButtonSubject` for the Observer pattern. Keeps track of all open windows in the application.
- **ButtonObserver.java** & **ButtonSubject.java**: Interfaces for the Observer pattern.
- **RegisterWindow.java**: Handles user registration.
- **ManagerLogin.java** & **CustomerLogin.java**: Handle login for managers and customers respectively.

### Back-End:

- **Bank.java**: Implements Proxy and Singleton patterns. Core of the banking system.
- **ATM.java**: Implements Proxy pattern, providing an abstraction layer between the front-end and the Bank.
- **BankInterface.java**: Interface ensuring proper implementation of ATM methods in Bank.
- **Account.java**: Abstract class for all account types.
- **CheckingAccount.java** & **SavingsAccount.java**: Specific account implementations.
- **SecurityAccount.java**, **SecurityObserver.java** & **SecuritySubject.java**: Implement the Observer pattern for security accounts.
- **User.java**, **Customer.java**, **Manager.java**: User-related classes.
- **Currency.java**, **Loan.java**, **Stock.java**, **StockDetails.java**: Helper classes for financial operations.
- **StockPortfolio.java**: Manages a customer's stock portfolio.
- **Transaction.java** & **StockTransaction.java**: Handle banking and stock transactions.
- **Main.java**: Entry point of the application.
- **CustomerDatabase.java**, **LoanDatabase.java**, **ManagerDatabase.java**, **StockDatabase.java**, **TransactionDatabase.java**: Database management classes.

## Design Patterns Used

- **Singleton Pattern**: Used for `Manager` and `Bank` classes to ensure only one instance exists.
- **Proxy Pattern**: Implemented with `ATM` class to provide an abstraction layer and control access to the `Bank`.
- **Observer Pattern**: Used to keep multiple windows updated with the latest data and notify security accounts of stock changes.

## Additional Design Choices

- Abstract `Account` class with specific implementations for different account types.
- "X has Y" approach in object design for logical relationships between entities.
- Calculations for unrealized profits based on stock data to avoid redundant storage.
- Static methods in database classes for efficient file I/O operations.
- Backend validation for most checks to ensure security and reduce code duplication.

For more detailed information about each class and its implementation, please refer to the comments in the respective `.java` files or the design document.
