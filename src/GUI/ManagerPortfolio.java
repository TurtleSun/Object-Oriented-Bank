 /*
  * ManagerPortfolio.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Utilizes the observer and proxy pattern. HomePage is the Subject that
  * keeps track of every window open in the application. When notified by any 
  * observer that it has done a action that modifies the database,
  * it tells all observers to re-read from the database, ensuring no stale
  * data is used. So when ManagerPortfolio makes a change to the date,
  * all windows reflect that change. ManagerPortfolio also utilizes Proxy pattern's
  * benefits, as unlike CustomerPortfolio, it has direct access to the Bank object
  * allowing for methods that wouldn't be available in the ATM.
  */

package GUI;

import src.*;


import java.text.DecimalFormat;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.swing.table.*;

import java.awt.*;
import java.time.LocalDate;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class ManagerPortfolio extends JFrame implements ButtonObserver {
    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel(cardLayout);
    private JPanel menuPanel = new JPanel();
    private JLabel dateLabel;
    private ATM atm = new ATM();
    private Manager manager;
    private HomePage hp;
    private JPanel userDataPanel;
    private JPanel transactionReportPanel;
    private JPanel loanManagementPanel;
    private JPanel stockManagementPanel;
    private JPanel timeSettingPanel;
    private String currentPanel = "userData"; //default panel


    public void refreshData() {
        refreshAppropriatePanel();
    }

    @Override
    public void refreshDate() {
        dateLabel.setText(CustomerDatabase.getDate().toString());
        dateLabel.revalidate();
        dateLabel.repaint();
    }

    public ManagerPortfolio(Manager manager, HomePage hp) {
        this.hp = hp;
        hp.addObserver(this);

        dateLabel = createDateLabel();  // global label
        this.manager = manager;
        atm.login(manager);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setupManagerMenuPanel();
        setupManagerCardPanel();
        add(menuPanel, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        setTitle("Manager Portfolio Management");
        setVisible(true);

        setResizable(true);
        int height = getSize().height;
        setMinimumSize(new Dimension(800, height));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, height));

    }

    private void setupManagerMenuPanel() {
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(235, 245, 251));
        menuPanel.setPreferredSize(new Dimension(200, 700));
        menuPanel.setMaximumSize(new Dimension(200, 700));

        JLabel welcomeLabel = new JLabel("<html><left>Manager</html>");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT); //
        welcomeLabel.setMaximumSize(new Dimension(200, 80)); //

        menuPanel.add(welcomeLabel);
        menuPanel.add(Box.createVerticalStrut(5));  // Add vertical space after welcome label
        menuPanel.add(dateLabel);
        menuPanel.add(Box.createVerticalStrut(20));  // Add vertical space after welcome label

        addButton("Customer Data", "userData");
        addButton("Transaction Report", "transactionReport");
        addButton("Loan Management", "loanManagement");
        addButton("Stock Management", "stockManagement");
        addButton("Time Setting", "settingTime");
        // ... more button ...

        menuPanel.add(Box.createVerticalStrut(140));
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


    private void setupManagerCardPanel() {
        userDataPanel = createUserDataPanel();
        transactionReportPanel = createTransactionReportPanel();
        loanManagementPanel = createLoanManagementPanel();
        stockManagementPanel = createStockManagementPanel();
        timeSettingPanel = createTimeSettingPanel();

        cardPanel.add(userDataPanel, "userData");
        cardPanel.add(transactionReportPanel, "transactionReport");
        cardPanel.add(loanManagementPanel, "loanManagement");
        cardPanel.add(stockManagementPanel, "stockManagement");
        cardPanel.add(timeSettingPanel, "settingTime");
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
            refreshAppropriatePanel();
        });
        menuPanel.add(button);
        menuPanel.add(Box.createVerticalStrut(10));  // Add vertical space between buttons
    }

    private void refreshAppropriatePanel() {
        switch (currentPanel) {
            case "userData":
                userDataPanel.removeAll();
                userDataPanel.add(createUserDataPanel());
                userDataPanel.revalidate();
                userDataPanel.repaint();
                break;
            case "transactionReport":
                transactionReportPanel.removeAll();
                transactionReportPanel.add(createTransactionReportPanel());
                transactionReportPanel.revalidate();
                transactionReportPanel.repaint();
                break;
            case "loanManagement":
                loanManagementPanel.removeAll();
                loanManagementPanel.add(createLoanManagementPanel());
                loanManagementPanel.revalidate();
                loanManagementPanel.repaint();
                break;
            case "stockManagement":
                stockManagementPanel.removeAll();
                stockManagementPanel.add(createStockManagementPanel());
                stockManagementPanel.revalidate();
                stockManagementPanel.repaint();
                break;
            case "settingTime":
                timeSettingPanel.removeAll();
                timeSettingPanel.add(createTimeSettingPanel());
                timeSettingPanel.revalidate();
                timeSettingPanel.repaint();
                break;
            default:
                System.out.println("Unknown Panel: " + currentPanel);
                break;
        }
    }

    /*----------------------------------------------------Users Data--------------------------------------------------------------*/
    private JPanel createUserDataPanel() {
        JPanel userDataPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Customer Data Overview", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        userDataPanel.add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addChangeListener(e -> {
            JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
            int index = sourceTabbedPane.getSelectedIndex();
            JPanel selectedPanel = (JPanel) sourceTabbedPane.getComponentAt(index);
            refreshPanel(selectedPanel, index);
        });

        // create tab
        tabbedPane.addTab("Checking Account", createAccountDetailPanel(Account.CHECKINGS_ACCOUNT));
        tabbedPane.addTab("Savings Account", createAccountDetailPanel(Account.SAVINGS_ACCOUNT));
        tabbedPane.addTab("Security Account", createSecAccountDetailPanel(Account.SECURITIES_ACCOUNT));
        tabbedPane.addTab("Stocks", createCustomerStockPanel());

        userDataPanel.add(tabbedPane, BorderLayout.CENTER);

        return userDataPanel;
    }

    private void refreshPanel(JPanel panel, int accountType) {
        panel.removeAll();
        JPanel newPanel = null;
        switch (accountType) {
            case 0:
                newPanel = createAccountDetailPanel(Account.CHECKINGS_ACCOUNT);
                break;
            case 1:
                newPanel = createAccountDetailPanel(Account.SAVINGS_ACCOUNT);
                break;
            case 2:
                newPanel = createSecAccountDetailPanel(Account.SECURITIES_ACCOUNT);
                break;
            case 3:
                newPanel = createCustomerStockPanel();
                break;
        }
        if (newPanel != null) {
            panel.add(newPanel);
        }
        panel.revalidate();
        panel.repaint();
    }



    private JPanel createAccountDetailPanel(int accountType) {
        JPanel accountDetailPanel = new JPanel(new BorderLayout());
        JLabel AccLabel = new JLabel("");
        accountDetailPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        if (accountType == Account.SAVINGS_ACCOUNT) {
            AccLabel = new JLabel("Saving Account " );
        } else if(accountType == Account.CHECKINGS_ACCOUNT){
            AccLabel = new JLabel("Checking Account " );
        }
        AccLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        accountDetailPanel.add(AccLabel, BorderLayout.NORTH);

        List<Account> ls = manager.getAllCustomerAccountType(accountType);
        List<String[]> displayData = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#.00");
        for (Account a : ls) {
            if (a == null) {
                continue;
            }
            String[] data = {a.getUsername(), df.format(a.getCurrency(Currency.DOLLARS).getAmount()),
                    df.format(a.getCurrency(Currency.YUAN).getAmount()),
                    df.format(a.getCurrency(Currency.WON).getAmount())};
            displayData.add(data);
        }

        String[][] data = displayData.toArray(new String[0][]);


        String[] columnNames = {"Username", "USD Balance", "RMB Balance", "KRW Balance"};

        JTable balanceTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(balanceTable);
        accountDetailPanel.add(scrollPane, BorderLayout.CENTER);

        return accountDetailPanel;
    }

    private JPanel createSecAccountDetailPanel(int accountType) {
        JPanel secAccountDetailPanel = new JPanel(new BorderLayout());
        secAccountDetailPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel secAccLabel = new JLabel("Security Account " );
        secAccLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        secAccountDetailPanel.add(secAccLabel, BorderLayout.NORTH);

        //

        List<Account> ls = manager.getAllCustomerAccountType(accountType);
        List<String[]> displayData = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#.00");

        for (Account a : ls) {
            if (a == null) {
                continue;
            }
            if (a instanceof SecurityAccount) {
                SecurityAccount secAcc = (SecurityAccount) a;
                String[] data = {
                    secAcc.getUsername(), df.format(secAcc.getCurrency(Currency.DOLLARS).getAmount()),
                    df.format(secAcc.getStockPortfolio().updateUnrealizedProfit(secAcc.getUsername())),
                        df.format(StockDatabase.getRealizedProfit(secAcc.getUsername()))
                };
                displayData.add(data);
            }
        }
        String[] columnNames = {"Username", "Balance", "Unrealized Profit","Realized Profit"};
        
        String[][] data = displayData.toArray(new String[0][]);

        JTable balanceTable = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(balanceTable);
        secAccountDetailPanel.add(scrollPane, BorderLayout.CENTER);

        return secAccountDetailPanel;
    }
    private JPanel createCustomerStockPanel() {
        JPanel customerStockPanel = new JPanel(new BorderLayout());
        customerStockPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel stockLabel = new JLabel("Stocks" );
        stockLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        customerStockPanel.add(stockLabel, BorderLayout.NORTH);

        Set<String> hs = StockDatabase.getAllUsernames();
        List<String[]> data = new ArrayList<>();
        for (String username: hs) {
            List<StockDetails> ls = StockDatabase.getAllStocksFromPortfolio(username);
            for (StockDetails stockDetails : ls) {
                data.add(new String[]{username, stockDetails.getName(), String.valueOf(stockDetails.getAmount())});
            }
        }

        String[] stockColumnNames = {"Username","Stock Name", "Shares Holding"};

        String[][] stockData = data.toArray(new String[0][]);

        DefaultTableModel stockModel = new DefaultTableModel(stockData, stockColumnNames);
        JTable stockTable = new JTable(stockModel);
        JScrollPane loanScrollPane = new JScrollPane(stockTable);
        customerStockPanel.add(loanScrollPane, BorderLayout.CENTER);

        return customerStockPanel;
    }

    /*----------------------------------------------------Users Data--------------------------------------------------------------*/

    /*--------------------------------------------------Transaction Report--------------------------------------------------------------*/
    private JPanel createTransactionReportPanel() {
        JPanel transactionReportPanel = new JPanel(new BorderLayout());
        transactionReportPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Transaction History", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        transactionReportPanel.add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"Date", "Sender", "Sender Account", "Receiver", "Receiver Account", "Amount", "Currency"};
        String[][] data = getTransactionData();  //get daily transaction data for all customers

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // not editable
                return false;
            }
        };
        JTable transactionTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        transactionReportPanel.add(scrollPane, BorderLayout.CENTER);

        // Today's cash flow
        JPanel cashFlowPanel = new JPanel();
        cashFlowPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel cashFlowLabel = new JLabel("Today's Cash Flow: " + calculateTodayCashFlow(data));
        cashFlowLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        cashFlowPanel.add(cashFlowLabel);

        transactionReportPanel.add(cashFlowPanel, BorderLayout.SOUTH);

        return transactionReportPanel;
    }

    private String[][] getTransactionData() {
        Map<Integer, String> hm = new HashMap<>();
        hm.put(Account.SAVINGS_ACCOUNT, "Savings");
        hm.put(Account.CHECKINGS_ACCOUNT, "Checking");
        hm.put(Account.SECURITIES_ACCOUNT, "Security");
        hm.put(Manager.MANAGER_ACCOUNT, "Manager Account");
        hm.put(-1, "N/A");
        List<Transaction> ls = TransactionDatabase.getAllTransactions();
        List<String[]> data = new ArrayList<>();
        for (Transaction transaction : ls) {
            String sender = transaction.getSender();
            String receiver = transaction.getReceiver();
            int senderAccount = transaction.getSenderAccountType();
            int receiverAccount = transaction.getReceiverAccountType();

            if (sender.equals("")) {
                sender = "N/A";
            }
            if (receiver.equals("")) {
                receiver = "N/A";
            }
            data.add(new String[] {transaction.getDate().toString(), sender, hm.get(senderAccount), receiver, hm.get(receiverAccount),
                    transaction.getCurrency().getCurrencyTypeString(), String.valueOf(transaction.getCurrency().getAmount())});
        }
        return data.toArray(new String[0][]);
    }

    private String calculateTodayCashFlow(String[][] data) {
        double cashFlow = 0;
        LocalDate today = CustomerDatabase.getDate();
        for (String[] row : data) {
            LocalDate date = LocalDate.parse(row[0]);
            if (date.equals(today)) {
                double amount = Double.parseDouble(row[6]);  //get amount
                if (row[5].equals("RMB")) {
                    amount /= 7;
                } else if (row[5].equals("WON")) {
                    amount /= 1300;
                }
                cashFlow += amount;
            }
        }
        return String.format("%.2f", cashFlow);
    }
    //    /*--------------------------------------------------Transaction Report--------------------------------------------------------------*/
//
//    /*----------------------------------------------------Loan Management--------------------------------------------------------------*/
    private JPanel createLoanManagementPanel() {
        JPanel loanManagementPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Loan Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        loanManagementPanel.add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addChangeListener(e -> {
            JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
            int index = sourceTabbedPane.getSelectedIndex();
            JPanel selectedPanel = (JPanel) sourceTabbedPane.getComponentAt(index);
            refreshLoanPanel(selectedPanel, index);
        });

        tabbedPane.addTab("Approval Loans", createApprovalLoanPanel());
        tabbedPane.addTab("Waiting List Loans", createWaitingListLoanPanel());

        loanManagementPanel.add(tabbedPane, BorderLayout.CENTER);

        return loanManagementPanel;
    }

    private JPanel createApprovalLoanPanel() {
        JPanel approveLoanPanel = new JPanel(new BorderLayout());
        approveLoanPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        List<Loan> loan = LoanDatabase.getAllLoans("Manager", false);

        JLabel loanLabel = new JLabel("Loan and Collateral " );
        loanLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        approveLoanPanel.add(loanLabel, BorderLayout.NORTH);

        String[] loanColumnNames = {"Username","Collateral Type","Loan Amount"};
        List<String[]> ls = new ArrayList<>();
        assert loan != null;
        for (Loan l : loan) {
            String[] curr = {l.getUsername(), l.getCollateral(), String.valueOf(l.getLoanAmount())};
            ls.add(curr);
        }
        String[][] data = ls.toArray(new String[0][]);

        DefaultTableModel loanModel = new DefaultTableModel(data, loanColumnNames);
        JTable loanTable = new JTable(loanModel);
        JScrollPane loanScrollPane = new JScrollPane(loanTable);
        approveLoanPanel.add(loanScrollPane, BorderLayout.CENTER);

        return approveLoanPanel;
    }

    private JPanel createWaitingListLoanPanel() {
        JPanel waitingListLoanPanel = new JPanel(new BorderLayout());
        waitingListLoanPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        List<Loan> loan = manager.getPendingLoans();

        String[] columnNames = {"Username","Collateral Type", "Collateral Value", "Loan Amount"};
        List<String[]> ls = new ArrayList<>();
        for (Loan l : loan) {
            String[] curr = {l.getUsername(), l.getCollateral(), String.valueOf(l.getCollateralAmount()),
                    String.valueOf(l.getLoanAmount())};
            ls.add(curr);
        }
        String[][] data = ls.toArray(new String[0][]);

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // make the cell non-editable
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        waitingListLoanPanel.add(scrollPane, BorderLayout.CENTER);

        // add approve button
        JButton approveButton = new JButton("Approve Selected Loan");
        approveButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                // select data from the table
                String username = (String) table.getValueAt(selectedRow, 0);
                String loanValue = (String) table.getValueAt(selectedRow, 3);

                // implement the logic of approve loan waiting list to approve list
                approveLoan(selectedRow);
                // Manager deposits x money into savings account
                manager.deposit(username, Double.parseDouble(loanValue));
                // remove the row from waiting list
                model.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(null, "Please select a loan to approve.", "No Selection", JOptionPane.ERROR_MESSAGE);
            }
            hp.notifyChange();
        });

        // add approve button
        JButton rejectButton = new JButton("Reject Selected Loan");
        rejectButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                // select data from the table
                String username = (String) table.getValueAt(selectedRow, 0);
                String loanValue = (String) table.getValueAt(selectedRow, 3);
                rejectLoan(selectedRow);
                // remove the row from waiting list
                model.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(null, "Please select a loan to approve.", "No Selection", JOptionPane.ERROR_MESSAGE);
            }
            hp.notifyChange();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        waitingListLoanPanel.add(buttonPanel, BorderLayout.SOUTH);

        return waitingListLoanPanel;
    }

    private void approveLoan(int selectedLoan) {
        // implement the logic of approving loan
        //
        Loan loan = manager.getPendingLoans().get(selectedLoan);
        if(manager.approveLoan(loan)) {
//            DefaultTableModel model = (DefaultTableModel) approvalLoanPanel.getComponents()[0].getComponent(0).getComponents()[1].getModel();
//            model.addRow(newRow);
            JOptionPane.showMessageDialog(null, "Loan approved and added to the approved list.", "Approved", JOptionPane.INFORMATION_MESSAGE);
        } else{
            JOptionPane.showMessageDialog(null, "Loan disapproved, it's not valid.", "Disapproved", JOptionPane.ERROR_MESSAGE);

        }
    }

    private void rejectLoan(int selectedLoan) {
        // implement the logic of approving loan
        //
        Loan loan = manager.getPendingLoans().get(selectedLoan);
        if(manager.rejectLoan(loan)) {
            JOptionPane.showMessageDialog(null, "Loan disapproved, Success!", "Disapproved", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshLoanPanel(JPanel panel, int index) {
        panel.removeAll();
        JPanel newPanel = null;
        switch (index) {
            case 0:
                newPanel = createApprovalLoanPanel();
                break;
            case 1:
                newPanel = createWaitingListLoanPanel();
                break;
        }
        if (newPanel != null) {
            panel.add(newPanel);
        }
        panel.revalidate();
        panel.repaint();
    }

    //    /*----------------------------------------------------Loan Management--------------------------------------------------------------*/
//
//    /*----------------------------------------------------Stock Management--------------------------------------------------------------*/
    private JPanel createStockManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columnNames = {"Stock Name", "Stock Price"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10));

        JLabel nameLabel = new JLabel("Stock Name:");
        JTextField nameField = new JTextField();
        JLabel priceLabel = new JLabel("Stock Price:");
        JTextField priceField = new JTextField();

        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(priceLabel);
        inputPanel.add(priceField);

        // button panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton removeButton = new JButton("Remove");

        List<Stock> stockList = StockDatabase.getAllStocks();
        for (Stock s : stockList) {
            if (s == null) {
                continue;
            }
            model.addRow(new String[]{s.getName(), String.valueOf(s.getPrice())});
        }

        addButton.addActionListener(e -> {
            String name = nameField.getText();
            Double price = tryParse(priceField.getText());
            if (!name.isEmpty() && price > 0) {
                // check if there already has the same stock name
                boolean stockExists = false;
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (model.getValueAt(i, 0).equals(name)) {
                        stockExists = true;
                        break;
                    }
                }
                if (!stockExists) {
                    if (manager.addStock(new Stock(name, price))) {
                        model.addRow(new Object[]{name, String.format("%.2f", price)});
                        nameField.setText("");  // clear text
                        priceField.setText("");
                        JOptionPane.showMessageDialog(panel, "Stock added successfully.", "Update Successful", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add stock. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Stock with same name already exists", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Invalid name or amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            hp.notifyChange();
        });


        updateButton.addActionListener(e -> {
            int selectedRowIndex = table.getSelectedRow();
            Double price = tryParse(priceField.getText());
            if (price > 0) {
                if (selectedRowIndex != -1) {
                    boolean found = false;
                    Stock selectedStock = null;
                    for (Stock s : manager.getStocksList()) {
                        if (s.getName().equals(table.getValueAt(selectedRowIndex, 0).toString())) {
                            found = true;
                            selectedStock = s;
                        }
                    }
                    if (found) {
                        manager.updateStockPrice(selectedStock, price);
                        model.setValueAt(String.format("%.2f", price), selectedRowIndex, 1);
                        JOptionPane.showMessageDialog(panel, "Stock price updated successfully.", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "No stock with this name exists", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a row in the table", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            hp.notifyChange();
        });

        removeButton.addActionListener(e -> {
            // remove stock logic
            int selectedRowIndex = table.getSelectedRow();
            if (selectedRowIndex != -1) {
                boolean found = false;
                Stock selectedStock = null;
                for (Stock s : manager.getStocksList()) {
                    if (s.getName().equals(table.getValueAt(selectedRowIndex, 0).toString())) {
                        found = true;
                        selectedStock = s;
                    }
                }
                if (found) {
                    manager.removeStock(selectedStock.getName());
                    model.removeRow(selectedRowIndex);
                    JOptionPane.showMessageDialog(panel, "Stock removed successfully.", "Removal Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No stock with this name exists", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row in the table", "Error", JOptionPane.ERROR_MESSAGE);
            }
            hp.notifyChange();
        });

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(removeButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(inputPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private double tryParse(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return -1;  // Return -1 if parsing fails
        }
    }
/*----------------------------------------------------Stock Management--------------------------------------------------------------*/

/*----------------------------------------------------Time Setting--------------------------------------------------------------*/
    private JPanel createTimeSettingPanel() {
        JPanel timeSettingPanel = new JPanel();
        timeSettingPanel.setLayout(new BoxLayout(timeSettingPanel, BoxLayout.Y_AXIS));

        // current date display
        JLabel dateLabel = new JLabel(CustomerDatabase.getDate().toString());
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        // next day button
        JButton nextDayButton = new JButton("Next Day");
        nextDayButton.addActionListener(e -> {
            manager.increaseDateByDay();
            dateLabel.setText(CustomerDatabase.getDate().toString());
            // update date
            hp.notifyDateChange();
            hp.notifyChange();
        });
        buttonPanel.add(nextDayButton);

        // next month Button
        JButton nextMonthButton = new JButton("Next Month");
        nextMonthButton.addActionListener(e -> {
            manager.increaseDateByMonth();
            dateLabel.setText(CustomerDatabase.getDate().toString());
            // update date
            hp.notifyDateChange();
            hp.notifyChange();
        });
        buttonPanel.add(nextMonthButton);

        timeSettingPanel.add(dateLabel);
        timeSettingPanel.add(buttonPanel);

        return timeSettingPanel;
    }

    public void updateDateDisplay() {
        dateLabel.setText(CustomerDatabase.getDate().toString());
        dateLabel.revalidate();
        dateLabel.repaint();
    }

    /*----------------------------------------------------Time Setting--------------------------------------------------------------*/


    private JLabel setupLogo() {
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("image/BankLogo.png"));
        Image scaledLogo = logoIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setSize(120, 120);
        return logoLabel;
    }


//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new ManagerPortfolio(new Manager("")));
//    }
}
