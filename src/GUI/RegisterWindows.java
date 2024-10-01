 /*
  * RegisterWindows.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Simple register window that uses Login.java's register
  * method.
  */

package GUI;
import javax.swing.*;

import src.Login;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterWindows extends JFrame {
    private JTextField usernameField, passwordField, confirmPasswordField;
    private HomePage hp;

    public RegisterWindows(HomePage hp) {
        this.hp = hp;
        // Set up the main window
        setupFrame();

        // Title label
        JLabel titleLabel = createTitleLabel();

        // Form panel
        JPanel formPanel = createFormPanel();

        // Submit button
        JButton submitButton = createSubmitButton();

        // Add components to the frame
        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);

        // Make frame visible
        setVisible(true);
    }

    private void setupFrame() {
        setTitle("Customer Registration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));
        setSize(500, 350);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel("Register Now!", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(new Color(30, 150, 155));
        return titleLabel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        addFormField(formPanel, "Username:", usernameField);
        addFormField(formPanel, "Password:", passwordField);
        addFormField(formPanel, "Confirm Password:", confirmPasswordField);

        return formPanel;
    }

    private JButton createSubmitButton() {
        JButton submitButton = new JButton("Submit");
        styleButton(submitButton);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmit();
            }
        });
        return submitButton;
    }

    private void handleSubmit() {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()
                || confirmPasswordField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required to be filled in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (usernameField.getText().equals("Manager")) {
            JOptionPane.showMessageDialog(this, "Sorry this username is reserved for the manager", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            JOptionPane.showMessageDialog(this, "Password and confirm password do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Registration success logic
        if (Login.createLoginAccount(usernameField.getText(), passwordField.getText())) {
            JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose(); // Closes the registration window
            openLoginWindow(); // Opens the login window
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed, username may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openLoginWindow() {
        EventQueue.invokeLater(() -> {
            JFrame loginWindow = new CustomerLogin(hp);
            loginWindow.setVisible(true);
        });
    }


    private void addFormField(JPanel panel, String labelText, JTextField textField) {
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(textField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button.setBackground(new Color(60, 141, 188));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}