 /*
  * HomePage.java
  * by Simon Kye & Michelle Sun & Chao Jen Chiu
  * 5/2/2024
  *
  * Utilizes the observer pattern. HomePage is the Subject that
  * keeps track of every window open in the application. When notified by any 
  * observer that it has done a action that modifies the database,
  * it tells all observers to re-read from the database, ensuring no stale
  * data is used.
  */

package GUI;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends JFrame implements ButtonSubject {
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 30);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Color BUTTON_BG_COLOR = new Color(0, 60, 70);
    private static final Color BUTTON_FG_COLOR = Color.DARK_GRAY;
    private static final int FRAME_WIDTH = 650, FRAME_HEIGHT = 350;
    private static HomePage instance;
    List<ButtonObserver> observers;


    public void addObserver(ButtonObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ButtonObserver observer) {
        observers.remove(observer);
    }

    public void notifyChange() {
        for (ButtonObserver observer : observers) {
            observer.refreshData();
        }
    }
    public void notifyDateChange() {
        for (ButtonObserver observer : observers) {
            observer.refreshDate();
        }
    }


    public HomePage() {
        observers = new ArrayList<>();
        JPanel backgroundPanel = createBackgroundPanel();
        setupComponents(backgroundPanel);
        finalizeFrame();
    }
    public static HomePage getInstance() {
        if (instance == null) {
            instance = new HomePage();
        }
        return instance;
    }

    private JPanel createBackgroundPanel() {
        return new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon(getClass().getResource("image/Background.png"));
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
    }

    private void setupComponents(JPanel backgroundPanel) {
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = createLabel("Welcome to 611 Online Bank", TITLE_FONT, Color.WHITE);
        JLabel logoLabel = createLogoLabel("image/BankLogo.png", 150, 150);
        JButton customerLoginButton = createButton("Customer Login");
        JButton managerLoginButton = createButton("Manager Login");

        arrangeComponents(backgroundPanel, titleLabel, logoLabel, customerLoginButton, managerLoginButton, gbc);
        add(backgroundPanel);
    }

    private JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private JLabel createLogoLabel(String imagePath, int width, int height) {
        ImageIcon originalIcon = new ImageIcon(getClass().getResource(imagePath));
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(scaledImage));
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setPreferredSize(new Dimension(200, 50));
        button.setBackground(BUTTON_BG_COLOR);
        button.setForeground(BUTTON_FG_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> handleButtonAction(text));
        return button;
    }

    private void arrangeComponents(JPanel panel, JLabel title, JLabel logo, JButton customer, JButton manager, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(20, 0, 50, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(title, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(customer, gbc);

        gbc.gridx++;
        panel.add(logo, gbc);

        gbc.gridx++;
        panel.add(manager, gbc);
    }

    private void handleButtonAction(String command) {
        if (command.equals("Customer Login")) {
            // Customer login logic
//            try {
            new CustomerLogin(this);
//            } catch (AWTException ex) {
//                throw new RuntimeException(ex);
//            }
        } else if (command.equals("Manager Login")) {
            // Manager login logic
//            try {
            new ManagerLogin(this);
//            } catch (AWTException ex) {
//                throw new RuntimeException(ex);
//            }
        }
    }

    private void finalizeFrame() {
        setTitle("Online Bank Homepage");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HomePage::new);
    }
}
