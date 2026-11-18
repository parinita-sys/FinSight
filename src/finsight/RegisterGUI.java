
package finsight;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class RegisterGUI extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public RegisterGUI() {

        setTitle("FinSight - Create Account");
        setSize(420, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(250, 240, 243));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("CREATE ACCOUNT");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(128, 35, 65));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        JButton registerButton = new JButton("Register");
        registerButton.setBackground(new Color(128, 35, 65));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backButton = new JButton("Back to Login");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(25));

        panel.add(new JLabel("Username"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(12));

        panel.add(new JLabel("Password"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(12));

        panel.add(new JLabel("Confirm Password"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(confirmPasswordField);
        panel.add(Box.createVerticalStrut(20));

        panel.add(registerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(backButton);

        add(panel);

        registerButton.addActionListener(e -> register());

        backButton.addActionListener(e -> {
            new LoginGUI().setVisible(true);
            dispose();
        });
    }

    private void register() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword =
                new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()
                || confirmPassword.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Please fill in all fields."
            );
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(
                    this, "Password must contain at least 6 characters."
            );
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(
                    this, "Passwords do not match."
            );
            return;
        }

        boolean registered =
                DatabaseConnection.registerUser(username, password);

        if (registered) {

            JOptionPane.showMessageDialog(
                    this, "Account created successfully! Please log in."
            );

            new LoginGUI().setVisible(true);
            dispose();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Registration failed. The username may already exist, "
                            + "or the database connection may have failed.",
                    "Registration Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
