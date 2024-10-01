 /*
  * CustomerLogin.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Simple login page that checks with Login class for authentication
  */

package GUI;

import src.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CustomerLogin extends JFrame {
    private static final Font LARGE_FONT = new Font("Segoe UI", Font.PLAIN, 30);
    private static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 20);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final int WIDTH = 650, HEIGHT = 350;

    private JLabel topNameLabel = new JLabel("Customer Login & Registration", JLabel.CENTER);
    private JPanel centerPanel = new JPanel(new SpringLayout());
    private JLabel userNameLabel = new JLabel("Username: ");
    private JTextField userNameTextField = new JTextField();
    private JLabel passwordLabel = new JLabel("Password: ");
    private JPasswordField passwordField = new JPasswordField();
    private JButton loginButton = new JButton("Login");
    private JButton registerButton = new JButton("Register");

    private HomePage hp;


    public CustomerLogin(HomePage hp) {
        this.hp = hp;
        setTitle("Customer System Login");
        setupComponents();
        layoutComponents();
        addListeners();
        finalizeFrame();
    }

    private void setupComponents() {
        topNameLabel.setFont(LARGE_FONT);
        topNameLabel.setPreferredSize(new Dimension(0, 80));

        userNameLabel.setFont(SMALL_FONT);
        userNameTextField.setPreferredSize(new Dimension(200, 30));
        passwordLabel.setFont(SMALL_FONT);
        passwordField.setPreferredSize(new Dimension(200, 30));
        styleButton(loginButton);
        styleButton(registerButton);
    }

    private void layoutComponents() {
        Container contentPane = getContentPane();
        contentPane.add(topNameLabel, BorderLayout.NORTH);
        contentPane.add(centerPanel, BorderLayout.CENTER);

        centerPanel.add(userNameLabel);
        centerPanel.add(userNameTextField);
        centerPanel.add(passwordLabel);
        centerPanel.add(passwordField);
        centerPanel.add(loginButton);
        centerPanel.add(registerButton);

        SpringLayout layout = (SpringLayout) centerPanel.getLayout();

        // Calculate the offset for centering
        Spring userNameLabelWidth = Spring.width(userNameLabel);
        Spring userNameTextFieldWidth = Spring.width(userNameTextField);
        Spring gap = Spring.constant(5);
        Spring totalWidth = Spring.sum(Spring.sum(userNameLabelWidth, userNameTextFieldWidth), gap);
        int offsetX = totalWidth.getValue() / 2;

        // Username Label and Text Field
        layout.putConstraint(SpringLayout.WEST, userNameLabel, -offsetX, SpringLayout.HORIZONTAL_CENTER, centerPanel);
        layout.putConstraint(SpringLayout.NORTH, userNameLabel, 20, SpringLayout.NORTH, centerPanel);
        layout.putConstraint(SpringLayout.WEST, userNameTextField, 5, SpringLayout.EAST, userNameLabel);
        layout.putConstraint(SpringLayout.NORTH, userNameTextField, 0, SpringLayout.NORTH, userNameLabel);

        // Password Label and Password Field
        layout.putConstraint(SpringLayout.EAST, passwordLabel, 0, SpringLayout.EAST, userNameLabel);
        layout.putConstraint(SpringLayout.NORTH, passwordLabel, 20, SpringLayout.SOUTH, userNameLabel);
        layout.putConstraint(SpringLayout.WEST, passwordField, 5, SpringLayout.EAST, passwordLabel);
        layout.putConstraint(SpringLayout.NORTH, passwordField, 0, SpringLayout.NORTH, passwordLabel);

        // Login and Register Buttons
        layout.putConstraint(SpringLayout.WEST, loginButton, 40, SpringLayout.WEST, passwordLabel);
        layout.putConstraint(SpringLayout.NORTH, loginButton, 40, SpringLayout.SOUTH, passwordLabel);
        layout.putConstraint(SpringLayout.WEST, registerButton, 60, SpringLayout.EAST, loginButton);
        layout.putConstraint(SpringLayout.NORTH, registerButton, 0, SpringLayout.NORTH, loginButton);
    }

    private void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(new Color(50, 140, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void addListeners() {
        loginButton.addActionListener(this::performLogin);
        registerButton.addActionListener(this::performRegistration);
    }

    private void performLogin(ActionEvent e) {
        // Login logic here
        String username = userNameTextField.getText();
        String password = String.copyValueOf(passwordField.getPassword());
        if (Login.login(username, password)) {
            JOptionPane.showMessageDialog(CustomerLogin.this, "Login Success!");
            dispose();
            new CustomerPortfolio(CustomerDatabase.getCustomer(username), Manager.getSingletonManager(), hp);
        } else {
            JOptionPane.showMessageDialog(CustomerLogin.this, "Login Failed, Check the username & password!");
        }
    }

    private void performRegistration(ActionEvent e) {
        // Registration logic here
        dispose();
        new RegisterWindows(hp);
    }

    private void setupLogo() {
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("image/BankLogo.png"));
        Image scaledLogo = logoIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setSize(120, 120);
        logoLabel.setLocation(WIDTH - 110, HEIGHT - 130);
        getLayeredPane().add(logoLabel, JLayeredPane.PALETTE_LAYER);
    }

    private void finalizeFrame() {
        setupLogo();
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }
}