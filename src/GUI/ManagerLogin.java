 /*
  * ManagerLogin.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Simple login page that checks with Login class for authentication.
  * Manager has no option to register as that would mean anyone could
  * create an account and become a manager.
  */

package GUI;
import javax.swing.*;

import src.Login;
import src.Manager;

import java.awt.*;
import java.awt.event.ActionEvent;

public class ManagerLogin extends JFrame {
    private static final Font LARGE_FONT = new Font("Segoe UI", Font.PLAIN, 30);
    private static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 20);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final int WIDTH = 650, HEIGHT = 350;

    private HomePage hp;

    private JLabel topNameLabel = new JLabel("Manager Login", JLabel.CENTER);
    private JPanel centerPanel = new JPanel(new SpringLayout());
    private JLabel userNameLabel = new JLabel("Username: ");
    private JTextField userNameTextField = new JTextField();
    private JLabel passwordLabel = new JLabel("Password: ");
    private JPasswordField passwordField = new JPasswordField();
    private JButton loginButton = new JButton("Login");


    public ManagerLogin(HomePage hp) {
        this.hp = hp;
        setTitle("Manager Login");
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

        // Login Buttons
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, loginButton, 0, SpringLayout.HORIZONTAL_CENTER, centerPanel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, loginButton, 20, SpringLayout.VERTICAL_CENTER, centerPanel);

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
    }

    private void performLogin(ActionEvent e) {
        // Login logic here
        String username = userNameTextField.getText();
        String password = String.copyValueOf(passwordField.getPassword());
        if (Login.managerLogin(username, password)) {
            JOptionPane.showMessageDialog(ManagerLogin.this, "Login Success!");
            dispose();
            new ManagerPortfolio(Manager.getSingletonManager(), hp);
        } else {
            JOptionPane.showMessageDialog(ManagerLogin.this, "Login Failed, Check the username & password!");
        }
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

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(ManagerLogin::new);
//    }
//}
