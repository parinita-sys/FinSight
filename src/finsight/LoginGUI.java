
package finsight;

import javax.swing.*;
import java.awt.*;

public class LoginGUI extends JFrame {

    public static int currentUserId = -1;
    public static String currentUsername = "";

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginGUI() {
        setTitle("FinSight - Login");
        setSize(420, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(250, 240, 243));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("FINSIGHT");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(new Color(128, 35, 65));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Personal Finance Analyzer");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JLabel usernameLabel = new JLabel("Username");
        JLabel passwordLabel = new JLabel("Password");

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(128, 35, 65));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton registerButton = new JButton("Create New Account");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(25));
        panel.add(usernameLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(12));
        panel.add(passwordLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(20));
        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(registerButton);

        add(panel);

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> {
            new RegisterGUI().setVisible(true);
            dispose();
        });
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this, "Please enter both username and password."
            );
            return;
        }

        int userId = DatabaseConnection.loginUser(username, password);

        if (userId != -1) {
            currentUserId = userId;
            currentUsername = username;
            JOptionPane.showMessageDialog(this, "Login successful!");
            dispose();
            FinSightGUI.main(new String[0]);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password. Please try again.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}
