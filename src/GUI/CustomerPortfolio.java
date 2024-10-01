 /*
  * CustomerPortfolio.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Page where customer can do every action specificed in ATM.
  * The proxy pattern allows us to prevent the front-end
  * from viewing the specifics of what is going on in the back-end
  * and abstracts what is going on.
  * This further adds protection as CustomerPortfolio has no access
  * to the bank object, enabling different actions to be performed
  * between CustomerPortfolio and ManagerPortfolio as ManagerPortfolio
  * has direct access to the bank object, allowing for more options in ManagerPortfolio.
  */

package GUI;

import javax.swing.*;
import java.awt.*;

import javax.swing.table.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

import src.*;
import src.Currency;

public class CustomerPortfolio extends JFrame implements ButtonObserver, SecurityObserver {
    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel(cardLayout);
    private JPanel menuPanel = new JPanel();
    private JLabel dateLabel;
    private ATM atm = new ATM();
    private HomePage hp;
    private Manager manager;
    private String currentPanel = "savings";  //default
    private JTextArea messageTextArea;



    public CustomerPortfolio(Customer customer, Manager manager ,HomePage hp) {
        this.hp = hp;
        hp.addObserver(this);
        this.manager = manager;
        manager.addObserver(this);

        dateLabel = createDateLabel();  // global label

        atm.login(customer);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setupMenuPanel();
        setupCardPanel();
        add(menuPanel, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        setTitle("Customer Portfolio Management");
        setVisible(true);

        setResizable(true);
        int height = getSize().height;
        setMinimumSize(new Dimension(800, height));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    private void setupMenuPanel() {
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(235, 245, 251));
        menuPanel.setPreferredSize(new Dimension(200, 700));
        menuPanel.setMaximumSize(new Dimension(200, 700));

        atm = new ATM();
        String username = atm.getUsername();
        JLabel welcomeLabel = new JLabel("<html><left>Welcome back, <br>" + username + "<left></html>");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT); //
        welcomeLabel.setMaximumSize(new Dimension(200, 80)); //


        menuPanel.add(welcomeLabel);
        menuPanel.add(Box.createVerticalStrut(15));  // Add vertical space after welcome label
        menuPanel.add(dateLabel);
        menuPanel.add(Box.createVerticalStrut(20));  // Add vertical space after welcome label

        addButton("Saving Account", "savings");
        addButton("Checking Account", "checking");
        addButton("Security Account", "security");
        addButton("Loans", "loans");
        addButton("Transfers", "transfers");
        addButton("Stocks", "stocks");
        addButton("Messages", "messages");

        menuPanel.add(Box.createVerticalStrut(40));
        menuPanel.add(setupLogo());
    }

    private JLabel createDateLabel() {
        // get current date
        LocalDate currentDate = CustomerDatabase.getDate();
        JLabel dateLabel = new JLabel(String.valueOf(currentDate));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateLabel.setMaximumSize(new Dimension(200, 30));
        dateLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        return dateLabel;
    }
    @Override
    public void refreshData(){
        refreshAppropriatePanel(currentPanel);
    }

    @Override
    public void refreshDate() {
        dateLabel.setText(CustomerDatabase.getDate().toString());
        dateLabel.revalidate();
        dateLabel.repaint();
    }


    private void setupCardPanel() {
        cardPanel.add(createCSAccountPanel(Account.SAVINGS_ACCOUNT), "savings");
        cardPanel.add(createCSAccountPanel(Account.CHECKINGS_ACCOUNT), "checking");
        cardPanel.add(createSecAccountPanel(Account.SECURITIES_ACCOUNT), "security");
        cardPanel.add(createLoanPanel("Loan Management"), "loans");
        cardPanel.add(createTransferPanel("Fund Transfer"), "transfers");
        cardPanel.add(createStockPanel("Stock Trading "), "stocks");
        cardPanel.add(createMessagePanel("Message Box"), "messages");
    }

    private void addButton(String text, String command) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setMaximumSize(new Dimension(200, 50)); // Fixed button size
        button.setMinimumSize(new Dimension(200, 50));
        button.setPreferredSize(new Dimension(200, 50));
        button.setBackground(new Color(199, 213, 224));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            cardLayout.show(cardPanel, command);
            currentPanel = command;
            refreshAppropriatePanel(command);
            }
        );
        menuPanel.add(button);
        menuPanel.add(Box.createVerticalStrut(10));  // Add vertical space between buttons
    }

    private void refreshAppropriatePanel(String command) {
        switch (command) {
            case "savings":
                refreshPanel(cardPanel, Account.SAVINGS_ACCOUNT);
                break;
            case "checking":
                refreshPanel(cardPanel, Account.CHECKINGS_ACCOUNT);
                break;
            case "security":
                refreshSecPanel(cardPanel, Account.SECURITIES_ACCOUNT);
                break;
            case "stocks":
                refreshStockPanel(cardPanel, command);
                break;
            case "loans":
                refreshLoanPanel(cardPanel, command);
                break;
            case "transfers":
                refreshTransPanel(cardPanel, command);
                break;
            case "messages":
                refreshMessagePanel(cardPanel, command);
                break;
            default:
                System.out.println("Command not recognized for refreshing panel.");
                break;
        }
    }

    /*----------------------------------------------Checking & Saving Account--------------------------------------------------------------*/
    private JPanel createCSAccountPanel(int accountType) {
        String text = "";
        if (accountType == Account.SAVINGS_ACCOUNT) {
            text = "Savings Account";
        } else if (accountType == Account.CHECKINGS_ACCOUNT) {
            text = "Checking Account";
        }
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(text, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);  // Ensure text is centered horizontally
        panel.add(titleLabel, BorderLayout.NORTH);

        // 4th index indicates if the account exists
        if (atm.viewCurrentBalance(accountType)[4] == -1) {
            // If there is no account yet, show create account button
            JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JLabel messageLabel = new JLabel("<html>It seems like you don't have a " + text + " yet. Would you like to create one? <br>(You will be charged $200 for creating an account)</html>");
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            messageLabel.setPreferredSize(new Dimension(700, 300));
            messagePanel.add(messageLabel);
            panel.add(messagePanel, BorderLayout.CENTER);

            JButton createButton = new JButton("Create Account");
            createButton.setFont(new Font("Segoe UI", Font.PLAIN, 20));

            createButton.addActionListener(e -> {
                double initialDeposit = getInitialDepositAmount();
                if (atm.createBankAccount(accountType, new Currency(initialDeposit, Currency.DOLLARS)) == 0) {
                    JOptionPane.showMessageDialog(this, "Account created successfully with initial deposit of $" + initialDeposit);
                    refreshPanel(panel, accountType);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create account. Minimum initial deposit is $200.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                hp.notifyChange();
            });
            panel.add(createButton, BorderLayout.SOUTH);
        } else {
            // If the customer already created an account, show all information, deposit/withdraw button and Close Account button
            // Account information display updated
            double[] balances = getAccountBalance(text);
            JPanel accountInfoPanel = new JPanel(new GridLayout(5, 2, 10, 10));  // Adjust grid layout for better arrangement

            addBalanceLabel(accountInfoPanel, "Total Balance: $", balances[0], new Color(8, 117, 39));
            addBalanceLabel(accountInfoPanel, "USD Balance: $", balances[1], new Color(39, 43, 129));
            addBalanceLabel(accountInfoPanel, "RMB Balance: ¥", balances[2], new Color(110, 13, 19));
            addBalanceLabel(accountInfoPanel, "KRW Balance: ₩", balances[3], new Color(161, 80, 22));

            // 添加按钮，使用流式布局使其更加均匀地分布
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton depositButton = createCurrencyOperationButton("Deposit", panel, accountType);
            JButton withdrawButton = createCurrencyOperationButton("Withdraw", panel, accountType);
            JButton exchangeButton = createCurrencyExchangeButton("CurrencyExchange", panel, accountType);
            JButton closeButton = new JButton("Close Account");
            closeButton.addActionListener(e -> {
                confirmAndCloseAccount(panel, accountType);
                hp.notifyChange();
            });

            setButtonStyle(depositButton);
            setButtonStyle(withdrawButton);
            setButtonStyle(exchangeButton);
            setButtonStyle(closeButton);

            buttonPanel.add(depositButton);
            buttonPanel.add(withdrawButton);
            buttonPanel.add(closeButton);
            buttonPanel.add(exchangeButton);

            accountInfoPanel.add(buttonPanel);

            panel.add(accountInfoPanel, BorderLayout.CENTER);
            panel.add(createTransactionHistoryPanel(accountType), BorderLayout.EAST);
        }


        return panel;
    }

    private void addBalanceLabel(JPanel panel, String prefix, double amount, Color color) {
        JLabel balanceLabel = new JLabel(prefix + String.format("%.2f", amount));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        balanceLabel.setForeground(color);
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(balanceLabel);
    }

    private void setButtonStyle(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        button.setBackground(new Color(5, 63, 75, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 227, 250), 3),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        button.setContentAreaFilled(false);
//        button.setContentAreaFilled(true);
    }

    private JButton createCurrencyExchangeButton(String operation, JPanel panel, int accountType) {
        JButton button = new JButton(operation);
        button.addActionListener(e -> {
            JComboBox<String> fromCurrencySelector = new JComboBox<>(new String[]{"USD", "RMB", "KRW"});
            JComboBox<String> toCurrencySelector = new JComboBox<>(new String[]{"USD", "RMB", "KRW"});
            JTextField amountField = new JTextField();

            Object[] message = {
                    "From Currency:", fromCurrencySelector,
                    "Amount:", amountField,
                    "To Currency:", toCurrencySelector,
            };

            int option = JOptionPane.showConfirmDialog(null, message, operation, JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    if (amount <= 0) {
                        throw new NumberFormatException("Amount must be greater than zero.");
                    }
                    if (isValidDouble(String.valueOf(amount))) {
                        String fromCurrency = fromCurrencySelector.getSelectedItem().toString();
                        String toCurrency = toCurrencySelector.getSelectedItem().toString();
                        if (fromCurrency.equals(toCurrency)) {
                            JOptionPane.showMessageDialog(panel, "Cannot exchange the same currency type.", "Error", JOptionPane.ERROR_MESSAGE);
                        } else {
                            performCurrencyExchange(accountType, fromCurrency, toCurrency, amount);
                            refreshPanel(panel, accountType);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid input. Please enter a positive number with up to 2 decimal digits.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(panel, "Invalid amount entered. " + nfe.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            hp.notifyChange();
        });
        return button;
    }

    private void performCurrencyExchange(int accountType, String fromCurrencyStr, String toCurrencyStr, double amount) {
        int fromCurrencyType = getCurrencyTypeFromString(fromCurrencyStr);
        int toCurrencyType = getCurrencyTypeFromString(toCurrencyStr);

        if (fromCurrencyType == Currency.INVALID || toCurrencyType == Currency.INVALID) {
            JOptionPane.showMessageDialog(this, "Invalid currency type selected.", "Exchange Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Currency fromCurrency = new Currency(amount, fromCurrencyType);

        // Using the ATM class to handle the currency exchange logic
        Currency exchangedCurrency = atm.exchangeCurrency(accountType, fromCurrency, toCurrencyType);

        if (exchangedCurrency == null || exchangedCurrency.getCurrencyType() == Currency.INVALID) {
            JOptionPane.showMessageDialog(this, "Exchange failed. Please check the amounts and currency types.", "Exchange Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Successfully exchanged to " + exchangedCurrency.getCurrencyTypeString(), "Exchange Successful", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private int getCurrencyTypeFromString(String currencyStr) {
        switch (currencyStr) {
            case "USD":
                return Currency.DOLLARS;
            case "RMB":
                return Currency.YUAN;
            case "KRW":
                return Currency.WON;
            default:
                return Currency.INVALID;
        }
    }


    private JButton createCurrencyOperationButton(String operation, JPanel panel, int accountType) {
        JButton button = new JButton(operation);
        button.addActionListener(e -> {
            JComboBox<String> currencySelector = new JComboBox<>(new String[]{"USD", "RMB", "KRW"});
            JTextField amountField = new JTextField();
            Object[] message = {
                    "Select Currency:", currencySelector,
                    "Amount:", amountField
            };
            int option = JOptionPane.showConfirmDialog(null, message, operation, JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String amountText = amountField.getText();
                if (isValidDouble(amountText)) {
                    double amount = Double.parseDouble(amountText);
                    performCurrencyOperation(accountType, operation, currencySelector.getSelectedItem().toString(), amount);
                    refreshPanel(panel, accountType);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid input. Please enter a positive number with up to 2 decimal digits.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            hp.notifyChange();
        });
        return button;
    }

    private boolean isValidDouble(String text) {
        try {
            double value = Double.parseDouble(text);
            // Written with chat
            return value >= 0 && text.matches("^\\d+(\\.\\d{0,2})?$");
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void performCurrencyOperation(int accountType, String operation, String currencyType, double amount) {
        Currency currency = null;
        if (currencyType.equals("USD")) {
            currency = new Currency(amount, Currency.DOLLARS);
        } else if (currencyType.equals("RMB")) {
            currency = new Currency(amount, Currency.YUAN);
        } else if (currencyType.equals("KRW")) {
            currency = new Currency(amount, Currency.WON);
        }

        boolean success = false;  // Default to false
        // Here, add logic to deposit or withdraw in specified currency
        if (operation.equals("Deposit")) {
            success = atm.deposit(accountType, currency, true);  // Assuming deposit returns a boolean as well
            if (success) {
                JOptionPane.showMessageDialog(this, "Deposit of " + amount + " " + currencyType + " successful.");
            } else {
                JOptionPane.showMessageDialog(this, "Deposit failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (operation.equals("Withdraw")) {
            success = atm.withdraw(accountType, currency, true);
            if (success) {
                JOptionPane.showMessageDialog(this, "Withdrawal of " + amount + " " + currencyType + " successful.");
            } else {
                JOptionPane.showMessageDialog(this, "Withdrawal failed: insufficient funds.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void confirmAndCloseAccount(JPanel panel, int accountType) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to close this account?", "Confirm Close", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            closeAccount(accountType);
        }
        refreshPanel(panel, accountType);

    }

private double[] getAccountBalance(String text) {
    int accountType = text.equals("Savings Account") ? Account.SAVINGS_ACCOUNT : Account.CHECKINGS_ACCOUNT;
    return CustomerDatabase.getAccountBalanceFromCSV(atm.getUsername(), accountType);
}

    private void closeAccount(int accountType) {
        boolean[] result = atm.closeAccount(accountType);
        if (result[0]) {
            JOptionPane.showMessageDialog(this, "Account never existed.", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (result[1]) {
            JOptionPane.showMessageDialog(this, "Account close failed, there wasn't enough money.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Account closed successfully.");
        }
    }

    private double getInitialDepositAmount() {
        String input = JOptionPane.showInputDialog(this, "Enter initial deposit amount (min amount of money: $200):", "Initial Deposit", JOptionPane.PLAIN_MESSAGE);
        if (input != null) {
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                return 0; // Return 0 if input is invalid, not sure if this right!!
            }
        } else {
            return 0;
        }
        
    }
    private JScrollPane createTransactionHistoryPanel(int accountType) {
        String username = atm.getUsername();
        List<Transaction> transactions = atm.viewTransactions(accountType);
        // Simulate fetching transaction history data
        String[] columnNames = {"Date", "Type", "Amount", "Currency", "Transfer Target"};
        List<String[]> ls = new ArrayList<>();
        for (Transaction t : transactions) {
            String[] curr = {t.getDate().toString(), "", String.valueOf(t.getCurrency().getAmount()),
                    t.getCurrency().getCurrencyTypeString(), ""};
            if (t.isDeposit()) {
                curr[1] = "Deposit";
                curr[4] = "N/A";
            } else if (t.isWithdraw()) {
                curr[1] = "Withdraw";
                curr[4] = "N/A";
            } else {
                if (t.isDeposit(username, accountType)) {
                    curr[1] = "Deposit";
                } else if (t.isWithdraw(username, accountType)) {
                    curr[1] = "Withdraw";
                }
                curr[4] = t.getTarget(username);
            }
            ls.add(curr);
        }
        String[][] data = ls.toArray(new String[0][]);
        ;

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // make the cell non-editable
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(300, 50));
        table.setFillsViewportHeight(false);

        JScrollPane scrollPane = new JScrollPane(table);
        return scrollPane;
    }


    private void refreshPanel(JPanel panel, int accountType) {
        panel.removeAll();  //
        panel.revalidate();  //
        panel.repaint();  //

        JPanel updatedPanel = createCSAccountPanel(accountType);
        panel.add(updatedPanel);
        panel.revalidate();
        panel.repaint();
    }
    /*----------------------------------------------Checking & Saving Account--------------------------------------------------------------*/

    /*-------------------------------------------------Security Account--------------------------------------------------------------*/
    private JPanel createSecAccountPanel(int accountType) {
        String text ="Security Account";
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(text, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);  // Ensure text is centered horizontally
        panel.add(titleLabel, BorderLayout.NORTH);

        // Check if the account exists
        if (atm.viewCurrentSecBalance(accountType)[4] == -1) {
            // If there is no account yet, show the create account button with specific instructions
            JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JLabel messageLabel = new JLabel("<html><p style=\"text-align: center\">How do the security account work?</p><br>" +
                    "<p style=\"text-align: justify\">Any customer who has more than $5000.00 in their savings account, " +
                    "can choose to transfer any amount over $1000.00 into a new securities account that they can use to trade stocks. But the customer must maintain a " +
                    "$2500.00 balance in their savings account. From within their securities account, a customer can enter trades (buy or sell stocks)," +
                    " see their current open positions, and both their realized and unrealized profit.</html>");
            messageLabel.setPreferredSize(new Dimension(600, 300));

            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            messagePanel.add(messageLabel);
            panel.add(messagePanel, BorderLayout.CENTER);

            JButton createButton = new JButton("Create Account");
            createButton.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            createButton.addActionListener(e -> {
                double transferAmount = getTransferAmount();
                int result = atm.createBankAccount(accountType, new Currency(transferAmount, Currency.DOLLARS));
                if (result == 0) {
                    JOptionPane.showMessageDialog(this, "Security Account created successfully with a transfer of $" + transferAmount);
                    refreshSecPanel(panel, accountType);
                } else if (result == -4){
                    JOptionPane.showMessageDialog(this, "Failed to create account.\nMake sure you keep your saving account balance over $2500", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (result == -3) {
                    JOptionPane.showMessageDialog(this, "Failed to create account.\nMake sure you already have the qualification for creating a security account", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (result == -2) {
                    JOptionPane.showMessageDialog(this, "Failed to create account. Minimum transfer is $1000.", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (result == -5) {
                    JOptionPane.showMessageDialog(this, "Failed to create account.\nCreate a savings account before creating a securities account", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (result == -7) {
                    JOptionPane.showMessageDialog(this, "Failed to create account.\nRequesting to deposit more USD than existing in savings account", "Error", JOptionPane.ERROR_MESSAGE);
                }
                hp.notifyChange();
            });
            panel.add(createButton, BorderLayout.SOUTH);
        } else {
            // Display account information
            SecurityAccount secAcc = (SecurityAccount) atm.getBankAccount(Account.SECURITIES_ACCOUNT);
            System.out.println(secAcc);
            if (secAcc.isEnabled()) {
                JPanel accountInfoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
                double[] balances = getSecAccountBalance(text);
                addBalanceLabel(accountInfoPanel, "Total Balance: $", balances[0],new Color(0,0,0));
                addBalanceLabel(accountInfoPanel, "USD Balance: $", balances[1],new Color(0,123,0));
                addBalanceLabel(accountInfoPanel, "Unrealized Profit: $", secAcc.getStockPortfolio().updateUnrealizedProfit(atm.getUsername()), new Color(123,0,0));
                addBalanceLabel(accountInfoPanel, "Realized Balance: $", StockDatabase.getRealizedProfit(atm.getUsername()),new Color(0,0,123));

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

                JButton depositButton = createCurrencyOperationButton("Deposit", panel, accountType);
                JButton withdrawButton = createCurrencyOperationButton("Withdraw", panel, accountType);

                JButton closeButton = new JButton("Close Account");
                closeButton.addActionListener(e -> {
                    confirmAndCloseAccount(panel, accountType);
                    hp.notifyChange();
                });

                setButtonStyle(depositButton);
                setButtonStyle(withdrawButton);
                setButtonStyle(closeButton);

                buttonPanel.add(depositButton);
                buttonPanel.add(withdrawButton);
                buttonPanel.add(closeButton);

                accountInfoPanel.add(buttonPanel);

                panel.add(accountInfoPanel, BorderLayout.CENTER);
                panel.add(createTransactionHistoryPanel(accountType), BorderLayout.EAST);
            } else {
                JOptionPane.showMessageDialog(this, "Your security account is currently disabled.\nMake sure you keep your saving account balance over $2500 to re-enable it.", "Account Disabled", JOptionPane.WARNING_MESSAGE);
            }

        }

        return panel;
    }

    private double getTransferAmount() {
        String input = JOptionPane.showInputDialog(this, "Enter the amount to transfer from your Savings Account: (min: $1000)", "Transfer Amount", JOptionPane.PLAIN_MESSAGE);
        if (input != null) {
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                return 0; // Return 0 if input is invalid
            }
        }
        return 0;
    }

    private double [] getSecAccountBalance(String text) {
        // get amount logic
        double[] balance = null;
        if (text.equals("Security Account")){
            balance = atm.viewCurrentSecBalance(Account.SECURITIES_ACCOUNT);
        } else {
        }
        return balance;  // test
    }

    private void refreshSecPanel(JPanel panel, int accountType) {
        panel.removeAll();  //
        panel.revalidate();  //
        panel.repaint();  //

        JPanel updatedPanel = createSecAccountPanel(accountType);
        panel.add(updatedPanel);
        panel.revalidate();
        panel.repaint();
    }

    /*-------------------------------------------------Security Account--------------------------------------------------------------*/

    /*----------------------------------------------------Loan--------------------------------------------------------------*/

    private JPanel createLoanPanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(text, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        if (LoanDatabase.getAllLoans(atm.getUsername(), false) == null) {
            // Information area about how loans work
            JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JLabel messageLabel = new JLabel("<html><p style=\"text-align: justify\">Loan process involves collateral. " +
                    "You can add a new loan by providing a collateral item and the loan amount. To redeem the collateral, repay the loan amount.</html>");
            messageLabel.setPreferredSize(new Dimension(600, 300));
            messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            messagePanel.add(messageLabel);
            panel.add(messagePanel, BorderLayout.CENTER);
        } else {
            // Loan Data Panel
            JPanel loanDataPanel = createLoanDataPanel();
            panel.add(loanDataPanel, BorderLayout.CENTER);
        }

        // Input area for adding a new loan
        JPanel inputPanel = new JPanel(new GridLayout(1, 4, 10, 20));  // Adjusted to have one row, four columns

        JLabel collateralLabel = new JLabel("Collateral:");
        collateralLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JTextField collateralField = new JTextField();
        JLabel collateralAmountLabel = new JLabel("Collateral Amount:");
        collateralAmountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JTextField collateralAmountField = new JTextField();
        JLabel amountLabel = new JLabel("Loan Amount:");
        amountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JTextField amountField = new JTextField();

        inputPanel.add(collateralLabel);
        inputPanel.add(collateralField);
        inputPanel.add(collateralAmountLabel);
        inputPanel.add(collateralAmountField);
        inputPanel.add(amountLabel);
        inputPanel.add(amountField);

        // Button panel for actions
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addLoanButton = new JButton("Add Loan");
        JButton payLoanButton = new JButton("Pay Loan");

        addLoanButton.addActionListener(e -> {
            if (CustomerDatabase.getCustomer(atm.getUsername()).getBankAccount(Account.SAVINGS_ACCOUNT) != null) {
                String collateral = collateralField.getText();
                double collateralAmount = tryParse(collateralAmountField.getText());
                double amount = tryParse(amountField.getText());
                if (amount > 0 && collateralAmount > 0) {
                    if (addLoan(collateral, collateralAmount, amount)){
                        JOptionPane.showMessageDialog(panel, "Loan requested successfully for $" + amount + ". The loan will be approved or rejected by the Bank Manager shortly.");
                        collateralField.setText("");
                        collateralAmountField.setText("");
                        amountField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(panel, "Invalid amount entered or duplicate collateral name.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }else {
                JOptionPane.showMessageDialog(panel, "You do not have a savings account for this loan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            hp.notifyChange();
        });

        payLoanButton.addActionListener(e -> {
            handlePayLoan();
            hp.notifyChange();
        });

        setButtonStyle(addLoanButton);
        setButtonStyle(payLoanButton);

        buttonPanel.add(addLoanButton);
        buttonPanel.add(payLoanButton);

        // South panel to hold both input and buttons
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(inputPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createLoanDataPanel() {
        JPanel loanDataPanel = new JPanel(new BorderLayout());
        JLabel loanTableLabel = new JLabel("Stock Details", JLabel.CENTER);
        loanTableLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        loanDataPanel.add(loanTableLabel, BorderLayout.NORTH);
    
        String[] columnNames = {"Collateral", "Current Loan Amount"};
    
        List<String[]> data = new ArrayList<>();
        List<Loan> customerLoans = LoanDatabase.getAllLoans(atm.getUsername(), false);
        for (Loan loan : customerLoans) {
            data.add(new String[]{loan.getCollateral(), String.valueOf(loan.getLoanAmount()), "", ""});
        }
    
        // Create the JTable with the data
        JTable loanTable = new JTable(data.toArray(new String[0][0]), columnNames);
    
        // Add the JTable to a JScrollPane
        JScrollPane scrollPane = new JScrollPane(loanTable);
        loanDataPanel.add(scrollPane, BorderLayout.CENTER);
    
        return loanDataPanel;
    }

    private boolean addLoan(String collateral, double collateralAmount, double amount) {
        // Logic to add a new loan with the specified collateral and amount
        return atm.requestLoan(collateral, collateralAmount, amount);
    }

    private void handlePayLoan() {
        // Fetch all loans to display them for payment options
        List<Loan> loans = LoanDatabase.getAllLoans(atm.getUsername(), false); // Assuming a method to fetch all loans
        JComboBox<Loan> loanComboBox = new JComboBox<>(new Vector<>(loans));
        loanComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Loan) {
                    Loan loan = (Loan) value;
                    setText(String.format("%s - $%.2f", loan.getCollateral(), loan.getLoanAmount()));
                }
                return this;
            }
        });

        int result = JOptionPane.showConfirmDialog(null, loanComboBox, "Select Loan to Pay", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            Loan selectedLoan = (Loan) loanComboBox.getSelectedItem();
            if (selectedLoan != null) {
                // Logic to pay the selected loan
                boolean success = atm.payLoan(selectedLoan.getCollateral() ,Account.SAVINGS_ACCOUNT, Currency.DOLLARS);
                if (success) {
                    Currency currency;
                    currency = new Currency(selectedLoan.getLoanAmount(), Currency.DOLLARS);
                    atm.withdraw(Account.SAVINGS_ACCOUNT, currency ,true);
                    JOptionPane.showMessageDialog(null, "Loan paid successfully!", "Payment Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to pay the loan.", "Payment Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private double tryParse(String value) {
        if (value != null) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return -1;  // Return -1 if parsing fails
            }
        } else {
            return -1;
        }
        
    }

    private int tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;  // Return -1 if parsing fails
        }
    }

    private void refreshLoanPanel(JPanel panel, String text) {
        panel.removeAll();  //
        panel.revalidate();  //
        panel.repaint();  //

        JPanel updatedPanel = createLoanPanel(text);
        panel.add(updatedPanel);
        panel.revalidate();
        panel.repaint();
    }

    /*----------------------------------------------------Loan--------------------------------------------------------------*/

    /*----------------------------------------------------Transfers--------------------------------------------------------------*/
    private JPanel createTransferPanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(text, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField recipientField = new JTextField(15);
        JComboBox<String> fromAccount = new JComboBox<>(new String[]{"Checking Account", "Savings Account"});
        JComboBox<String> toAccount = new JComboBox<>(new String[]{"Checking Account", "Savings Account", "Security Account"});
        JTextField amountField = new JTextField(10);
        JComboBox<String> currencySelector = new JComboBox<>(new String[]{"USD", "Yuan", "Won"});

        Map<String, Integer> accountHm = new HashMap<>();
        accountHm.put("Checking Account", Account.CHECKINGS_ACCOUNT);
        accountHm.put("Savings Account", Account.SAVINGS_ACCOUNT);
        accountHm.put("Security Account", Account.SECURITIES_ACCOUNT);

        Map<String, Integer> currencyHm = new HashMap<>();
        currencyHm.put("USD", Currency.DOLLARS);
        currencyHm.put("Yuan", Currency.YUAN);
        currencyHm.put("Won", Currency.WON);

        // Set currency based on selected "To Account"
        toAccount.addActionListener(e -> {
            if ("Security Account".equals(toAccount.getSelectedItem())) {
                currencySelector.setModel(new DefaultComboBoxModel<>(new String[]{"USD"})); // Only USD for Security Account
            } else {
                currencySelector.setModel(new DefaultComboBoxModel<>(new String[]{"USD", "Yuan", "Won"}));
            }
        });

        addLabeledComponent("Recipient Username:", recipientField, inputPanel, gbc);
        addLabeledComponent("From Account:", fromAccount, inputPanel, gbc);
        addLabeledComponent("To Account:", toAccount, inputPanel, gbc);
        addLabeledComponent("Amount ($):", amountField, inputPanel, gbc);
        addLabeledComponent("Currency:", currencySelector, inputPanel, gbc);

        // Transfer button
        JButton transferButton = new JButton("Transfer");
        transferButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        transferButton.setBackground(new Color(199, 213, 224));
        transferButton.addActionListener(e -> {
            String fromAccountText = (String) fromAccount.getSelectedItem();
            String toAccountText = (String) toAccount.getSelectedItem();
            if (recipientField.getText().isEmpty() || !validateRecipient(recipientField.getText(), accountHm.get(toAccountText))) {
                JOptionPane.showMessageDialog(panel, "Recipient username or target's account does not exist.", "Transfer Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (fromAccount.getSelectedItem().equals(toAccount.getSelectedItem()) && recipientField.getText().equals( atm.getUsername())) {
                JOptionPane.showMessageDialog(panel, "Cannot transfer to the same account.", "Transfer Error", JOptionPane.ERROR_MESSAGE);
            } else {
                double amount = tryParse(amountField.getText());
                if (amount > 0) {
                    if (atm.transferAmount(accountHm.get(fromAccountText), accountHm.get(toAccountText), recipientField.getText(), new Currency(Double.parseDouble(amountField.getText()), currencyHm.get(currencySelector.getSelectedItem())))){
                        JOptionPane.showMessageDialog(panel, "Transfer completed successfully to " + recipientField.getText(), "Transfer Status", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(panel, "Transfer failed.", "Transfer Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            hp.notifyChange();
        });
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        inputPanel.add(transferButton, gbc);

        panel.add(inputPanel, BorderLayout.CENTER);
        return panel;
    }

    private boolean validateRecipient(String username, int accountType) {
        Customer customer = CustomerDatabase.getCustomer(username);
        if (customer != null) {
            if (customer.getBankAccount(accountType) == null) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }


    private void addLabeledComponent(String labelText, Component component, Container container, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        container.add(label, gbc);
        container.add(component, gbc);
    }

    private void refreshTransPanel(JPanel panel, String text) {
        panel.removeAll();  //
        panel.revalidate();  //
        panel.repaint();  //

        JPanel updatedPanel = createTransferPanel(text);
        panel.add(updatedPanel);
        panel.revalidate();
        panel.repaint();
    }

    /*----------------------------------------------------Transfers--------------------------------------------------------------*/


    /*----------------------------------------------------Stocks--------------------------------------------------------------*/

    private JPanel createStockPanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleStockLabel = new JLabel(text, JLabel.CENTER);
        titleStockLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        panel.add(titleStockLabel, BorderLayout.NORTH);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2)); // 2 tables (Horizontal)

        // StockTable
        JPanel stockDataPanel = new JPanel(new BorderLayout());
        JLabel stockTableLabel = new JLabel("Stock Details", JLabel.CENTER);
        stockTableLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        stockDataPanel.add(stockTableLabel, BorderLayout.NORTH);

        String[] columnNames = {"Stock Name", "Current Price", "Holding Shares", "Unrealized Profits"};

        List<String[]> data = new ArrayList<>();
        List<Stock> ls = StockDatabase.getAllStocks();
        for (Stock s : ls) {
            data.add(new String[]{s.getName(), String.valueOf(s.getPrice()), "", ""});
        }

        SecurityAccount secAcc = (SecurityAccount) atm.getBankAccount(Account.SECURITIES_ACCOUNT);


        if (secAcc != null) {
            secAcc.getStockPortfolio().updateUnrealizedProfit(atm.getUsername());
            Map<String, Double> unrealizedProfitHm = secAcc.getStockPortfolio().getUnrealizedProfitPerStock();
            for (String[] row : data) {
                StockDetails stockDetails = StockDatabase.getStockFromPortfolio(atm.getUsername(), row[0]);
                if (stockDetails != null) {
                    row[2] = String.valueOf(stockDetails.getAmount());
                    row[3] = String.valueOf(unrealizedProfitHm.get(row[0]));
                }
            }
        }
        
        String[][] parsedData = data.toArray(new String[0][]);

        DefaultTableModel stockModel = new DefaultTableModel(parsedData, columnNames);
        JTable stockTable = new JTable(stockModel);
        JScrollPane stockScrollPane = new JScrollPane(stockTable);
        stockDataPanel.add(stockScrollPane, BorderLayout.CENTER);
        tablesPanel.add(stockDataPanel); // add to tablesPanel

        // Transaction History Table
        JPanel historyPanel = new JPanel(new BorderLayout());
        JLabel historyLabel = new JLabel("Transaction History", JLabel.CENTER);
        historyLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        historyPanel.add(historyLabel, BorderLayout.NORTH);

        String[] historyColumns = {"Transaction Date", "Transaction Type", "Stock Name", "Shares", "Price Per Share"};
        List<String[]> historyLs = new ArrayList<>();
        for (StockTransaction st : TransactionDatabase.getStockTransactionsFromUser(atm.getUsername())) {
            String[] arr = {st.getDate().toString(), "", st.getStockName(), String.valueOf(st.getAmount()), String.valueOf(st.getPrice())};
            if (st.isBuy()) {
                arr[1] = "Buy";
            } else {
                arr[1] = "Sell";
            }
            historyLs.add(arr);
        }
        String[][] historyData = historyLs.toArray(new String[0][]);
        DefaultTableModel historyModel = new DefaultTableModel(historyData, historyColumns);
        JTable historyTable = new JTable(historyModel);
        JScrollPane historyScrollPane = new JScrollPane(historyTable);
        historyPanel.add(historyScrollPane, BorderLayout.CENTER);
        tablesPanel.add(historyPanel); // add to tablesPanel

        // add Buy Button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JTextField buyAmount = new JTextField(10);
        JButton buyButton = new JButton("Buy");
        buyButton.addActionListener(e -> {
            if (atm.getBankAccount(Account.SECURITIES_ACCOUNT) != null && ((SecurityAccount)atm.getBankAccount(Account.SECURITIES_ACCOUNT)).isEnabled()) {
                int selectedRowIndex = stockTable.getSelectedRow();
                if (selectedRowIndex != -1) {
                    int amount = tryParseInt(buyAmount.getText());
                    if (amount > 0) {
                        if (secAcc.buyStock(stockTable.getValueAt(selectedRowIndex, 0).toString(), Integer.parseInt(buyAmount.getText()))) {
                            JOptionPane.showMessageDialog(panel, "Stock bought successfully.", "Buy Successful", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(panel, "Invalid stock name or insufficient fund in security account.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(panel, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    refreshStockPanel(panel,"stocks");
                }
                hp.notifyChange();
            } else {
                JOptionPane.showMessageDialog(panel, "Please open the security account first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        });
        JTextField sellAmount = new JTextField(10);
        JButton sellButton = new JButton("Sell");
        sellButton.addActionListener(e -> {
            if (atm.getBankAccount(Account.SECURITIES_ACCOUNT) != null && ((SecurityAccount)atm.getBankAccount(Account.SECURITIES_ACCOUNT)).isEnabled()) {
                int selectedRowIndex = stockTable.getSelectedRow();
                if (selectedRowIndex != -1) {
                    int amount = tryParseInt(sellAmount.getText());
                    if (amount > 0) {
                        StockDetails stockDetails = StockDatabase.getStockFromPortfolio(atm.getUsername(), stockTable.getValueAt(selectedRowIndex, 0).toString());
                        if (stockDetails.getAmount() >= amount) {
                            secAcc.sellStock(stockTable.getValueAt(selectedRowIndex, 0).toString(), Integer.parseInt(sellAmount.getText()));
                            JOptionPane.showMessageDialog(panel, "Stock sold successfully.", "Sell Successful", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(panel, "Invalid amount (You are selling more stocks than you own).", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(panel, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    refreshStockPanel(panel,"stocks");
                }
                hp.notifyChange();
            } else {
                JOptionPane.showMessageDialog(panel, "Please open a security account first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        });
        buttonPanel.add(new JLabel("Buy Amount:"));
        buttonPanel.add(buyAmount);
        buttonPanel.add(buyButton);
        buttonPanel.add(new JLabel("Sell Amount:"));
        buttonPanel.add(sellAmount);
        buttonPanel.add(sellButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tablesPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(mainPanel, BorderLayout.CENTER);
        return panel;
    }

    private void refreshStockPanel(JPanel panel, String text) {
        panel.removeAll();  //
        panel.revalidate();  //
        panel.repaint();  //

        JPanel updatedPanel = createStockPanel(text);
        panel.add(updatedPanel);
        panel.revalidate();
        panel.repaint();
    }
    /*----------------------------------------------------Stocks--------------------------------------------------------------*/

    /*----------------------------------------------------Messages--------------------------------------------------------------*/

    private JPanel createMessagePanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());

        // Title Label with custom panel for margin
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 60, 0)); // 为标题下方添加空间
        JLabel titleLabel = new JLabel(text, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        panel.add(titlePanel, BorderLayout.NORTH);


        // Large JTextArea for displaying messages
//        String text = Message.getmessage;
        messageTextArea = new JTextArea("Here are your messages. Update this to show actual messages."); //replace with text
        messageTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        messageTextArea.setLineWrap(true);
        messageTextArea.setWrapStyleWord(true);
        messageTextArea.setEditable(false); // Set to non-editable

        // ScrollPane for the JTextArea
        JScrollPane scrollPane = new JScrollPane(messageTextArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        panel.add(scrollPane, BorderLayout.CENTER);

        // OK Button to clear messages or close the message box
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        okButton.addActionListener(e -> clearMessages());
        panel.add(okButton, BorderLayout.SOUTH);

        // Set padding around the panel
        panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        return panel;
    }
    private void clearMessages() {
        MessageDatabase.clearMessages();
        messageTextArea.setText("No new messages.");
    }
    private void fetchNewMessages() {
        List<String> messages = MessageDatabase.readMessages();
        SwingUtilities.invokeLater(() -> {
            messageTextArea.setText("");
            for (String msg : messages) {
                messageTextArea.append(msg + "\n");
            }
        });
    }

    private void refreshMessagePanel(JPanel panel, String text) {
        panel.removeAll();  //
        panel.revalidate();  //
        panel.repaint();  //

        JPanel updatedPanel = createMessagePanel(text);
        panel.add(updatedPanel);
        panel.revalidate();
        panel.repaint();
        fetchNewMessages();
    }
    /*----------------------------------------------------Messages--------------------------------------------------------------*/


    private JLabel setupLogo() {
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("image/BankLogo.png"));
        Image scaledLogo = logoIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setSize(120, 120);
        return logoLabel;
    }
    @Override
    public void update(Stock stock) {
        List<String> messages = new ArrayList<>();
        messages.add("UPDATE: " + stock.getName() + " stock has been changed.");
        if (stock.getPrice() >= 0) {
            messages.add(stock.getName() + " has been updated to $" + stock.getPrice() + "!!");
        } else {
            messages.add(stock.getName() + " has been removed, initiating all sell!");
        }

        MessageDatabase.writeMessages(messages);
        fetchNewMessages();
        System.out.println(messages);
    }



//    private JPanel createPanel(String text) {
//        JPanel panel = new JPanel();
//        panel.add(new JLabel(text));
//        return panel;
//    }

    // public static void main(String[] args ) {
    //     SwingUtilities.invokeLater(CustomerPortfolio::new);
    // }
}
