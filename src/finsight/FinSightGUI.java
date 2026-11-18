package finsight;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class FinSightGUI {

    // ================= COLORS =================

    static final Color BACKGROUND = new Color(250, 245, 245);
    static final Color BURGUNDY = new Color(82, 45, 55);
    static final Color DARK_BURGUNDY = new Color(100, 45, 60);
    static final Color LIGHT_PINK = new Color(255, 238, 243);
    static final Color SOFT_PINK = new Color(248, 232, 235);
    static final Color LIGHT_ROSE = new Color(235, 218, 222);
    static final Color BORDER = new Color(235, 215, 220);
    static final Color TEXT = new Color(120, 90, 95);

    // ================= MAIN =================

    public static void main(String[] args) {
        int userId = LoginGUI.currentUserId;

        if (userId == -1) {
            JOptionPane.showMessageDialog(
                    null, "Please log in first."
            );
            return;
        }

        double[] totals = loadTotals(userId);

        final double[] totalIncome = {totals[0]};
        final double[] totalExpense = {totals[1]};
        double displayIncome = totalIncome[0];
        double displayExpense = totalExpense[0];

        try {
            displayIncome = CurrencyConverter.convert(
                    totalIncome[0],
                    "INR",
                    DatabaseConnection.preferredCurrency
            );

            displayExpense = CurrencyConverter.convert(
                    totalExpense[0],
                    "INR",
                    DatabaseConnection.preferredCurrency
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    null,
                    "Unable to fetch exchange rates. Please check your internet connection."
            );
        }

        double displayBalance = displayIncome - displayExpense;

        JFrame frame = new JFrame("FinSight");
        frame.setSize(1200, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);

        // =========================================================
        // SIDEBAR
        // =========================================================

        JPanel sidebar = new JPanel();
        sidebar.setBackground(BURGUNDY);
        sidebar.setPreferredSize(new Dimension(240, 750));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("  FINSIGHT");
        logo.setFont(new Font("Serif", Font.BOLD, 22));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(50));

        JButton dashboardButton = new JButton("♡  Dashboard");
        JButton transactionButton = new JButton("＋  Add Transaction");
        JButton analysisButton = new JButton("◌  Analysis");
        JButton historyButton = new JButton("♡  History");
        JButton budgetButton = new JButton("▣  Budget");
        JButton settingsButton = new JButton("⚙  Settings");
        JButton logoutButton = new JButton("↪  Logout");

        JButton[] buttons = {
                dashboardButton,
                transactionButton,
                analysisButton,
                historyButton,
                budgetButton,
                settingsButton,
                logoutButton
        };

        for (JButton button : buttons) {

            button.setFont(new Font("SansSerif", Font.PLAIN, 15));
            button.setForeground(Color.WHITE);
            button.setBackground(BURGUNDY);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setMaximumSize(new Dimension(190, 45));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            sidebar.add(button);
            sidebar.add(Box.createVerticalStrut(10));
        }

        // =========================================================
        // HEADER
        // =========================================================

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(BACKGROUND);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("FINSIGHT");
        title.setFont(new Font("Serif", Font.BOLD, 32));
        title.setForeground(DARK_BURGUNDY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Personal Finance Analyzer");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(TEXT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(Box.createVerticalStrut(20));
        headerPanel.add(title);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitle);
        headerPanel.add(Box.createVerticalStrut(20));

        // =========================================================
        // CARD LAYOUT
        // =========================================================

        CardLayout cardLayout = new CardLayout();

        JPanel contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BACKGROUND);

        // =========================================================
        // DASHBOARD
        // =========================================================

        JPanel dashboard = new JPanel();
        dashboard.setBackground(BACKGROUND);
        dashboard.setLayout(new BoxLayout(dashboard, BoxLayout.Y_AXIS));

        // ---------------- CARDS ----------------

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setBackground(BACKGROUND);
        cardsPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 30, 10, 30)
        );

        // Income

        JPanel incomeCard = createCard(LIGHT_PINK);

        JLabel incomeTitle = createCardTitle("INCOME");

        JLabel incomeAmount = new JLabel(
                DatabaseConnection.preferredCurrency + " "
                        + String.format("%.2f", displayIncome)
        );

        incomeAmount.setFont(new Font("Serif", Font.BOLD, 26));
        incomeAmount.setForeground(BURGUNDY);
        incomeAmount.setAlignmentX(Component.CENTER_ALIGNMENT);

        incomeCard.add(incomeTitle);
        incomeCard.add(Box.createVerticalStrut(10));
        incomeCard.add(incomeAmount);

        // Expense

        JPanel expenseCard = createCard(SOFT_PINK);

        JLabel expenseTitle = createCardTitle("EXPENSE");

        JLabel expenseAmount = new JLabel(
                DatabaseConnection.preferredCurrency + " "
                        + String.format("%.2f", displayExpense)
        );
        expenseAmount.setFont(new Font("Serif", Font.BOLD, 26));
        expenseAmount.setForeground(BURGUNDY);
        expenseAmount.setAlignmentX(Component.CENTER_ALIGNMENT);

        expenseCard.add(expenseTitle);
        expenseCard.add(Box.createVerticalStrut(10));
        expenseCard.add(expenseAmount);

        // Balance

        JPanel balanceCard = createCard(LIGHT_ROSE);

        JLabel balanceTitle = createCardTitle("BALANCE");

        JLabel balanceAmount = new JLabel(
                DatabaseConnection.preferredCurrency + " "
                        + String.format("%.2f", displayBalance)
        );

        balanceAmount.setFont(new Font("Serif", Font.BOLD, 26));
        balanceAmount.setForeground(BURGUNDY);
        balanceAmount.setAlignmentX(Component.CENTER_ALIGNMENT);

        balanceCard.add(balanceTitle);
        balanceCard.add(Box.createVerticalStrut(10));
        balanceCard.add(balanceAmount);

        cardsPanel.add(incomeCard);
        cardsPanel.add(expenseCard);
        cardsPanel.add(balanceCard);

        dashboard.add(cardsPanel);

        // =========================================================
        // ADD TRANSACTION
        // =========================================================

        JPanel transactionPanel = new JPanel(new GridBagLayout());

        transactionPanel.setBackground(Color.WHITE);

        transactionPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        BorderFactory.createEmptyBorder(
                                10, 25, 10, 25
                        )
                )
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel transactionTitle = new JLabel("Add Transaction");

        transactionTitle.setFont(
                new Font("Serif", Font.BOLD, 22)
        );

        transactionTitle.setForeground(DARK_BURGUNDY);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        transactionPanel.add(transactionTitle, gbc);

        // Amount

        JLabel amountLabel = new JLabel("Amount");

        JTextField amountField = new JTextField();

        amountField.setPreferredSize(
                new Dimension(200, 35)
        );

        styleTextField(amountField);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;

        transactionPanel.add(amountLabel, gbc);

        gbc.gridx = 1;

        transactionPanel.add(amountField, gbc);

        // Type

        JLabel typeLabel = new JLabel("Type");

        JComboBox<String> typeBox =
                new JComboBox<>(
                        new String[]{"Income", "Expense"}
                );

        typeBox.setPreferredSize(
                new Dimension(200, 35)
        );

        gbc.gridx = 0;
        gbc.gridy = 2;

        transactionPanel.add(typeLabel, gbc);

        gbc.gridx = 1;

        transactionPanel.add(typeBox, gbc);

        // Category

        JLabel categoryLabel = new JLabel("Category");

        JComboBox<String> categoryBox =
                new JComboBox<>(
                        new String[]{
                                "Food",
                                "Transport",
                                "Shopping",
                                "Education",
                                "Entertainment",
                                "Bills",
                                "Health",
                                "Other"
                        }
                );

        categoryBox.setPreferredSize(
                new Dimension(200, 35)
        );

        gbc.gridx = 0;
        gbc.gridy = 3;

        transactionPanel.add(categoryLabel, gbc);

        gbc.gridx = 1;

        transactionPanel.add(categoryBox, gbc);

        // Description

        JLabel descriptionLabel =
                new JLabel("Description");

        JTextField descriptionField =
                new JTextField();

        descriptionField.setPreferredSize(
                new Dimension(200, 35)
        );

        styleTextField(descriptionField);

        gbc.gridx = 0;
        gbc.gridy = 4;

        transactionPanel.add(descriptionLabel, gbc);

        gbc.gridx = 1;

        transactionPanel.add(descriptionField, gbc);

        // Button

        JButton addButton =
                new JButton("+  ADD TRANSACTION");

        addButton.setFont(
                new Font("SansSerif", Font.BOLD, 14)
        );

        addButton.setForeground(Color.WHITE);
        addButton.setBackground(DARK_BURGUNDY);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);

        addButton.setPreferredSize(
                new Dimension(220, 40)
        );

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;

        transactionPanel.add(addButton, gbc);

        // =========================================================
        // SAVINGS
        // =========================================================

        JPanel middlePanel =
                new JPanel(
                        new GridLayout(1, 2, 15, 0)
                );

        middlePanel.setBackground(BACKGROUND);

        middlePanel.add(transactionPanel);

        JPanel savingsPanel = new JPanel();

        savingsPanel.setBackground(
                new Color(255, 248, 249)
        );

        savingsPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER, 1
                        ),
                        BorderFactory.createEmptyBorder(
                                20, 20, 20, 20
                        )
                )
        );

        savingsPanel.setLayout(
                new BoxLayout(
                        savingsPanel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel savingsTitle =
                new JLabel("SAVINGS RATE");

        savingsTitle.setFont(
                new Font("SansSerif", Font.BOLD, 15)
        );

        savingsTitle.setForeground(DARK_BURGUNDY);
        savingsTitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JLabel savingsValue =
                new JLabel("0.0%");

        savingsValue.setFont(
                new Font("Serif", Font.BOLD, 38)
        );

        savingsValue.setForeground(BURGUNDY);

        savingsValue.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JLabel savingsText =
                new JLabel(
                        "<html>You're saving a good<br>" +
                                "portion of your income.</html>"
                );

        savingsText.setFont(
                new Font("SansSerif", Font.PLAIN, 14)
        );

        savingsText.setForeground(TEXT);
        savingsText.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        savingsText.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        savingsPanel.add(Box.createVerticalGlue());

        savingsPanel.add(savingsTitle);

        savingsPanel.add(
                Box.createVerticalStrut(15)
        );

        savingsPanel.add(savingsValue);

        savingsPanel.add(
                Box.createVerticalStrut(10)
        );

        savingsPanel.add(savingsText);

        savingsPanel.add(Box.createVerticalGlue());

        middlePanel.add(savingsPanel);

        dashboard.add(
                Box.createVerticalStrut(10)
        );

        dashboard.add(middlePanel);

        // =========================================================
        // RECENT TRANSACTIONS
        // =========================================================

        JLabel recentTitle =
                new JLabel("Recent Transactions");

        recentTitle.setFont(
                new Font("Serif", Font.BOLD, 22)
        );

        recentTitle.setForeground(DARK_BURGUNDY);

        recentTitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        dashboard.add(
                Box.createVerticalStrut(10)
        );

        dashboard.add(recentTitle);

        dashboard.add(
                Box.createVerticalStrut(8)
        );

        String[] columns = {
                "Date",
                "Type",
                "Category",
                "Amount",
                "Description"
        };

        DefaultTableModel tableModel =
                new DefaultTableModel(
                        columns,
                        0
                );
        loadRecentTransactions(tableModel);

        JTable transactionTable =
                new JTable(tableModel);

        styleTable(transactionTable);

        JScrollPane tableScrollPane =
                new JScrollPane(transactionTable);

        tableScrollPane.setPreferredSize(
                new Dimension(850, 320)
        );

        tableScrollPane.setBorder(
                BorderFactory.createLineBorder(
                        BORDER, 1
                )
        );

        dashboard.add(tableScrollPane);

        JScrollPane dashboardScroll =
                new JScrollPane(dashboard);

        dashboardScroll.setBorder(null);

        contentPanel.add(
                dashboardScroll,
                "Dashboard"
        );

        // =========================================================
        // ANALYSIS PAGE
        // =========================================================

        JPanel analysisPage =
                createPagePanel();

        JLabel analysisTitle =
                createPageTitle("Financial Analysis");

        analysisPage.add(analysisTitle);

        JLabel analysisSubtitle =
                new JLabel(
                        "Your spending breakdown by category"
                );

        analysisSubtitle.setForeground(TEXT);

        analysisPage.add(analysisSubtitle);

        analysisPage.add(
                Box.createVerticalStrut(20)
        );

        JPanel chartPanel =
                new SpendingChartPanel();

        chartPanel.setPreferredSize(
                new Dimension(800, 350)
        );

        analysisPage.add(chartPanel);

        analysisPage.add(
                Box.createVerticalStrut(20)
        );

        JLabel analysisInfo =
                new JLabel(
                        "Analysis is calculated from your MySQL transactions."
                );

        analysisInfo.setForeground(TEXT);

        analysisPage.add(analysisInfo);

        contentPanel.add(
                new JScrollPane(analysisPage),
                "Analysis"
        );

        // =========================================================
        // HISTORY PAGE
        // =========================================================
// BUDGET PAGE
        JPanel budgetPage = createPagePanel();

        JLabel budgetTitle = createPageTitle("Monthly Budget");

        budgetPage.add(budgetTitle);

        budgetPage.add(
                Box.createVerticalStrut(20)
        );

        JLabel budgetInfo = new JLabel(
                "Set and track your monthly spending limits by category."
        );

        budgetInfo.setForeground(TEXT);

        budgetPage.add(budgetInfo);
        java.util.Map<Integer, String> budgetCategories =
                DatabaseConnection.getCategories();

        JComboBox<String> budgetCategory = new JComboBox<>();

        for (String category : budgetCategories.values()) {
            budgetCategory.addItem(category);
        }
        JTextField budgetAmount = new JTextField(15);
        budgetAmount.setBorder(
                BorderFactory.createTitledBorder("Monthly Budget Amount")
        );
        JButton saveBudgetButton = new JButton("SAVE BUDGET");

        styleButton(saveBudgetButton);
        JTextField budgetMonth = new JTextField("2026-11", 10);
        budgetMonth.setBorder(
                BorderFactory.createTitledBorder("Month (YYYY-MM)")
        );

        budgetPage.add(budgetCategory);
        budgetPage.add(budgetAmount);
        budgetPage.add(budgetMonth);
        budgetPage.add(saveBudgetButton);

        String[] budgetColumns = {
                "Category",
                "Budget",
                "Spent",
                "Remaining"
        };

        DefaultTableModel budgetModel =
                new DefaultTableModel(budgetColumns, 0);

        JTable budgetTable = new JTable(budgetModel);

        styleTable(budgetTable);

        JScrollPane budgetScroll = new JScrollPane(budgetTable);

        budgetScroll.setPreferredSize(new Dimension(850, 300));

        budgetPage.add(budgetScroll);

        saveBudgetButton.addActionListener(e -> {

            try {
                int budgetUserId = LoginGUI.currentUserId;
                String selectedCategory =
                        (String) budgetCategory.getSelectedItem();

                if (selectedCategory == null) {
                    JOptionPane.showMessageDialog(frame, "Please select a category.");
                    return;
                }

                int categoryId = -1;

                for (java.util.Map.Entry<Integer, String> entry
                        : budgetCategories.entrySet()) {

                    if (entry.getValue().equals(selectedCategory)) {
                        categoryId = entry.getKey();
                        break;
                    }
                }

                double amount = Double.parseDouble(budgetAmount.getText().trim());
                String monthYear = budgetMonth.getText().trim();

                if (amount <= 0 || !monthYear.matches("\\d{4}-(0[1-9]|1[0-2])")) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Enter a valid positive amount and month (YYYY-MM)."
                    );
                    return;
                }

                if (categoryId == -1) {
                    JOptionPane.showMessageDialog(frame, "Invalid category.");
                    return;
                }

                boolean saved = DatabaseConnection.saveBudget(
                        budgetUserId, categoryId, amount, monthYear
                );
                if (saved) {

                    budgetModel.setRowCount(0);

                    for (Object[] row : DatabaseConnection.getSavedBudgets(
                            budgetUserId, monthYear)) {

                        double savedAmount = ((Number) row[2]).doubleValue();
                        double spentAmount = ((Number) row[3]).doubleValue();

                        String currency = DatabaseConnection.preferredCurrency;

                        double convertedBudget =
                                CurrencyConverter.convert(savedAmount, "INR", currency);

                        double convertedSpent =
                                CurrencyConverter.convert(spentAmount, "INR", currency);

                        budgetModel.addRow(new Object[]{
                                row[1],
                                currency + " " + String.format("%.2f", convertedBudget),
                                currency + " " + String.format("%.2f", convertedSpent),
                                currency + " " + String.format("%.2f",
                                        convertedBudget - convertedSpent)
                        });
                    }

                    JOptionPane.showMessageDialog(frame, "Budget saved successfully!");

                } else {
                    JOptionPane.showMessageDialog(frame, "Could not save budget.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter a valid budget amount.");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Currency conversion failed: " + ex.getMessage());
            }
        });

        JPanel historyPage =
                createPagePanel();

        JLabel historyTitle =
                createPageTitle("Transaction History");

        historyPage.add(historyTitle);

        historyPage.add(
                Box.createVerticalStrut(20)
        );

        String[] historyColumns = {
                "ID",
                "Date",
                "Type",
                "Category",
                "Amount",
                "Description"
        };

        DefaultTableModel historyModel =
                new DefaultTableModel(
                        historyColumns,
                        0
                );

        JTable historyTable =
                new JTable(historyModel);

        styleTable(historyTable);

        JScrollPane historyScroll =
                new JScrollPane(historyTable);

        historyScroll.setPreferredSize(
                new Dimension(850, 450)
        );

        historyPage.add(historyScroll);

        JButton refreshHistory =
                new JButton("↻  REFRESH HISTORY");

        styleButton(refreshHistory);

        historyPage.add(
                Box.createVerticalStrut(15)
        );

        historyPage.add(refreshHistory);

        contentPanel.add(
                new JScrollPane(historyPage),
                "History"
        );
        contentPanel.add(
                new JScrollPane(budgetPage),
                "Budget"
        );

        // =========================================================
        // SETTINGS PAGE
        // =========================================================

        JPanel settingsPage =
                createPagePanel();

        JLabel settingsTitle =
                createPageTitle("Settings");

        settingsPage.add(settingsTitle);

        settingsPage.add(
                Box.createVerticalStrut(30)
        );

        JPanel settingsCard =
                new JPanel();

        settingsCard.setBackground(Color.WHITE);

        settingsCard.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDER
                        ),
                        BorderFactory.createEmptyBorder(
                                30, 50, 30, 50
                        )
                )
        );

        settingsCard.setLayout(
                new BoxLayout(
                        settingsCard,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel account =
                new JLabel("Account: " + LoginGUI.currentUsername);

        account.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        16
                )
        );

        JLabel database =
                new JLabel(
                        "Database: finsight_db"
                );

        database.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        16
                )
        );

        JLabel currencyLabel =
                new JLabel("Preferred Currency:");

        currencyLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        16
                )
        );

        JComboBox<String> currencySelector =
                new JComboBox<>(
                        new String[]{
                                "INR", "USD", "EUR",
                                "GBP", "JPY", "AED"
                        }
                );

        currencySelector.setSelectedItem(
                DatabaseConnection.preferredCurrency
        );

        currencySelector.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        16
                )
        );
        currencySelector.setMaximumSize(
                new Dimension(120, 35)
        );

        currencySelector.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel technology =
                new JLabel(
                        "Technology: Java Swing + JDBC + MySQL"
                );

        technology.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        16
                )
        );

        settingsCard.add(account);

        settingsCard.add(
                Box.createVerticalStrut(15)
        );

        settingsCard.add(database);

        settingsCard.add(
                Box.createVerticalStrut(15)
        );

        settingsCard.add(currencyLabel);

        settingsCard.add(
                Box.createVerticalStrut(10)
        );

        settingsCard.add(currencySelector);
        currencySelector.addActionListener(e -> {

            String selectedCurrency =
                    (String) currencySelector.getSelectedItem();

            boolean updated =
                    DatabaseConnection.updatePreferredCurrency(
                            LoginGUI.currentUserId,
                            selectedCurrency
                    );

            if (updated) {
                DatabaseConnection.preferredCurrency = selectedCurrency;
                try {
                    double convertedIncome =
                            CurrencyConverter.convert(
                                    totalIncome[0], "INR", selectedCurrency
                            );

                    double convertedExpense =
                            CurrencyConverter.convert(
                                    totalExpense[0], "INR", selectedCurrency
                            );

                    double convertedBalance =
                            convertedIncome - convertedExpense;

                    incomeAmount.setText(
                            selectedCurrency + " "
                                    + String.format("%.2f", convertedIncome)
                    );

                    expenseAmount.setText(
                            selectedCurrency + " "
                                    + String.format("%.2f", convertedExpense)
                    );

                    balanceAmount.setText(
                            selectedCurrency + " "
                                    + String.format("%.2f", convertedBalance)
                    );
                    loadRecentTransactions(tableModel);
                    JOptionPane.showMessageDialog(
                            settingsCard,
                            "Currency updated successfully!"
                    );

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            settingsCard,
                            "Currency conversion failed. Check your internet connection."
                    );
                }

            } else {
                JOptionPane.showMessageDialog(
                        settingsCard,
                        "Could not save currency. Please try again."
                );
            }
        });
        settingsCard.add(
                Box.createVerticalStrut(15)
        );

        settingsCard.add(technology);

        settingsCard.add(
                Box.createVerticalStrut(25)
        );

        JButton testConnection =
                new JButton(
                        "TEST DATABASE CONNECTION"
                );

        styleButton(testConnection);

        settingsCard.add(testConnection);

        settingsPage.add(settingsCard);

        contentPanel.add(
                new JScrollPane(settingsPage),
                "Settings"
        );

        // =========================================================
        // ADD TRANSACTION BUTTON
        // =========================================================

        addButton.addActionListener(e -> {

            String amount =
                    amountField.getText().trim();

            String type =
                    (String) typeBox.getSelectedItem();

            String category =
                    (String) categoryBox.getSelectedItem();

            String description =
                    descriptionField
                            .getText()
                            .trim();

            if (amount.isEmpty()
                    || description.isEmpty()) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please fill in all the details."
                );

                return;
            }

            double amountValue;

            try {

                amountValue =
                        Double.parseDouble(amount);

                if (amountValue <= 0) {

                    JOptionPane.showMessageDialog(
                            frame,
                            "Please enter an amount greater than 0."
                    );

                    return;
                }

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please enter a valid amount."
                );

                return;
            }

            if (type.equals("Income")) {

                totalIncome[0] += amountValue;

            } else {

                totalExpense[0] += amountValue;
            }

            double balance =
                    totalIncome[0]
                            - totalExpense[0];

            try {

                double newIncomeDisplay = CurrencyConverter.convert(
                        totalIncome[0],
                        "INR",
                        DatabaseConnection.preferredCurrency
                );

                double newExpenseDisplay = CurrencyConverter.convert(
                        totalExpense[0],
                        "INR",
                        DatabaseConnection.preferredCurrency
                );

                double newBalanceDisplay = newIncomeDisplay - newExpenseDisplay;

                incomeAmount.setText(
                        DatabaseConnection.preferredCurrency + " "
                                + String.format("%.2f", newIncomeDisplay)
                );

                expenseAmount.setText(
                        DatabaseConnection.preferredCurrency + " "
                                + String.format("%.2f", newExpenseDisplay)
                );

                balanceAmount.setText(
                        DatabaseConnection.preferredCurrency + " "
                                + String.format("%.2f", newBalanceDisplay)
                );

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Currency conversion failed. Check your internet connection."
                );
            }

            double savingsRate = 0;

            if (totalIncome[0] > 0) {

                savingsRate =
                        (balance
                                / totalIncome[0])
                                * 100;
            }

            savingsValue.setText(
                    String.format(
                            "%.1f%%",
                            savingsRate
                    )
            );

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern(
                            "dd MMM"
                    );

            String currentDate =
                    LocalDate.now()
                            .format(formatter);

            // Save to MySQL

            TransactionOperations transactionOperations = new DatabaseConnection();

            boolean saved = transactionOperations.addTransaction(
                    userId,
                    amountValue,
                    type,
                    category,
                    description
            );

            // Add to current table

            // Convert transaction amount to preferred currency

            String displayAmount;

            try {
                double convertedAmount = CurrencyConverter.convert(
                        amountValue,
                        "INR",
                        DatabaseConnection.preferredCurrency
                );

                displayAmount = DatabaseConnection.preferredCurrency + " "
                        + String.format("%.2f", convertedAmount);

            } catch (Exception ex) {
                displayAmount = "INR " + String.format("%.2f", amountValue);
            }

// Add to current table

            tableModel.addRow(
                    new Object[]{
                            currentDate,
                            type,
                            category,
                            displayAmount,
                            description
                    }
            );

            amountField.setText("");

            descriptionField.setText("");

            JOptionPane.showMessageDialog(
                    frame,
                    "Transaction added successfully!"
            );
        });

        // =========================================================
        // SIDEBAR ACTIONS
        // =========================================================

        dashboardButton.addActionListener(e -> {

            cardLayout.show(
                    contentPanel,
                    "Dashboard"
            );
        });

        transactionButton.addActionListener(e -> {

            cardLayout.show(
                    contentPanel,
                    "Dashboard"
            );

            SwingUtilities.invokeLater(() -> {

                amountField.requestFocusInWindow();

            });
        });

        analysisButton.addActionListener(e -> {

            cardLayout.show(
                    contentPanel,
                    "Analysis"
            );

            chartPanel.repaint();
        });

        historyButton.addActionListener(e -> {

            loadHistory(historyModel);

            cardLayout.show(
                    contentPanel,
                    "History"
            );
        });
        budgetButton.addActionListener(e -> {

            int budgetUserId = LoginGUI.currentUserId;
            String monthYear = budgetMonth.getText().trim();

            budgetModel.setRowCount(0);

            try {
                String currency = DatabaseConnection.preferredCurrency;

                for (Object[] row : DatabaseConnection.getSavedBudgets(
                        budgetUserId, monthYear)) {

                    double savedAmount = ((Number) row[2]).doubleValue();
                    double spentAmount = ((Number) row[3]).doubleValue();

                    double convertedBudget =
                            CurrencyConverter.convert(savedAmount, "INR", currency);

                    double convertedSpent =
                            CurrencyConverter.convert(spentAmount, "INR", currency);

                    budgetModel.addRow(new Object[]{
                            row[1],
                            currency + " " + String.format("%.2f", convertedBudget),
                            currency + " " + String.format("%.2f", convertedSpent),
                            currency + " " + String.format("%.2f",
                                    convertedBudget - convertedSpent)
                    });
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                        "Could not load budgets: " + ex.getMessage());
            }

            cardLayout.show(contentPanel, "Budget");
        });
        settingsButton.addActionListener(e -> {

            cardLayout.show(
                    contentPanel,
                    "Settings"
            );
        });
        logoutButton.addActionListener(e -> {

            int choice = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                frame.dispose();
                new LoginGUI().setVisible(true);
            }
        });

        refreshHistory.addActionListener(e -> {

            loadHistory(historyModel);

            JOptionPane.showMessageDialog(
                    frame,
                    "History refreshed!"
            );
        });

        testConnection.addActionListener(e -> {

            if (DatabaseConnection.getConnection()
                    != null) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Database connection successful!"
                );

            } else {

                JOptionPane.showMessageDialog(
                        frame,
                        "Database connection failed."
                );
            }
        });

        // =========================================================
        // MAIN WINDOW
        // =========================================================

        mainPanel.add(
                sidebar,
                BorderLayout.WEST
        );

        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );

        mainPanel.add(
                contentPanel,
                BorderLayout.CENTER
        );

        frame.add(mainPanel);

        frame.setVisible(true);
    }
    private static double[] loadTotals(int userId) {

        double income = 0;
        double expense = 0;

        String sql = """
            SELECT transaction_type, COALESCE(SUM(amount), 0) AS total
            FROM transactions
            WHERE user_id = ?
            GROUP BY transaction_type
            """;

        try (java.sql.Connection connection =
                     DatabaseConnection.getConnection()) {

            if (connection == null) {
                JOptionPane.showMessageDialog(
                        null, "Database connection failed."
                );
                return new double[]{0, 0};
            }

            try (java.sql.PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, userId);

                try (ResultSet result = statement.executeQuery()) {

                    while (result.next()) {

                        String type = result.getString("transaction_type");
                        double total = result.getDouble("total");

                        if ("Income".equalsIgnoreCase(type)) {
                            income = total;
                        } else if ("Expense".equalsIgnoreCase(type)) {
                            expense = total;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null, "Could not load your financial totals."
            );
        }

        return new double[]{income, expense};
    }
    // =============================================================
    // HELPER METHODS
    // =============================================================

    private static JPanel createCard(Color color) {

        JPanel panel = new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setBackground(color);

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        12, 20, 12, 20
                )
        );

        return panel;
    }

    private static JLabel createCardTitle(
            String text
    ) {

        JLabel label =
                new JLabel(text);

        label.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        label.setForeground(DARK_BURGUNDY);

        label.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        return label;
    }

    private static JLabel createPageTitle(
            String text
    ) {

        JLabel label =
                new JLabel(text);

        label.setFont(
                new Font(
                        "Serif",
                        Font.BOLD,
                        30
                )
        );

        label.setForeground(DARK_BURGUNDY);

        label.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        return label;
    }

    private static JPanel createPagePanel() {

        JPanel panel = new JPanel();

        panel.setBackground(BACKGROUND);

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 40, 30, 40
                )
        );

        return panel;
    }

    private static void styleTextField(
            JTextField field
    ) {

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 195, 202)
                        ),
                        BorderFactory.createEmptyBorder(
                                5, 8, 5, 8
                        )
                )
        );
    }

    private static void styleButton(
            JButton button
    ) {

        button.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(Color.WHITE);
        button.setBackground(DARK_BURGUNDY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setPreferredSize(
                new Dimension(220, 40)
        );

        button.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );
    }

    private static void styleTable(
            JTable table
    ) {

        table.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        table.setRowHeight(38);

        table.getTableHeader().setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
        );

        table.getTableHeader().setBackground(
                DARK_BURGUNDY
        );

        table.getTableHeader().setForeground(
                Color.WHITE
        );

        table.setGridColor(BORDER);

        table.setShowVerticalLines(false);
    }

    // =============================================================
    // LOAD HISTORY FROM DATABASE
    // =============================================================



    private static void loadHistory(DefaultTableModel model) {

        model.setRowCount(0);

        int userId = LoginGUI.currentUserId;

        String sql = """
        SELECT
            t.transaction_id,
            t.transaction_date,
            t.transaction_type,
            c.category_name,
            t.amount,
            t.description
        FROM transactions t
        LEFT JOIN categories c
            ON t.category_id = c.category_id
        WHERE t.user_id = ?
        ORDER BY
            t.transaction_date DESC,
            t.transaction_id DESC
        """;

        try (java.sql.Connection connection =
                     DatabaseConnection.getConnection()) {

            if (connection == null) {
                JOptionPane.showMessageDialog(
                        null, "Database connection failed."
                );
                return;
            }

            try (java.sql.PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, userId);

                try (ResultSet result = statement.executeQuery()) {

                    while (result.next()) {

                        double originalAmount =
                                result.getDouble("amount");

                        String displayAmount;

                        try {
                            double convertedAmount =
                                    CurrencyConverter.convert(
                                            originalAmount,
                                            "INR",
                                            DatabaseConnection.preferredCurrency
                                    );

                            displayAmount =
                                    DatabaseConnection.preferredCurrency + " "
                                            + String.format("%.2f", convertedAmount);

                        } catch (Exception ex) {
                            displayAmount =
                                    "INR " + String.format("%.2f", originalAmount);
                        }

                        model.addRow(new Object[]{
                                model.getRowCount() + 1,
                                result.getDate("transaction_date"),
                                result.getString("transaction_type"),
                                result.getString("category_name"),
                                displayAmount,
                                result.getString("description")
                        });
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null, "Could not load transaction history."
            );
            e.printStackTrace();
        }
    }


    private static void loadRecentTransactions(DefaultTableModel model) {

        model.setRowCount(0);

        String sql = """
        SELECT
            t.transaction_date,
            t.transaction_type,
            c.category_name,
            t.amount,
            t.description
        FROM transactions t
        LEFT JOIN categories c
            ON t.category_id = c.category_id
        WHERE t.user_id = ?
        ORDER BY
            t.transaction_date DESC,
            t.transaction_id DESC
        LIMIT 5
        """;

        try (java.sql.Connection connection =
                     DatabaseConnection.getConnection()) {

            if (connection == null) {
                JOptionPane.showMessageDialog(
                        null, "Database connection failed."
                );
                return;
            }

            try (java.sql.PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, LoginGUI.currentUserId);

                try (ResultSet result = statement.executeQuery()) {

                    while (result.next()) {

                        double originalAmount =
                                result.getDouble("amount");

                        String displayAmount;

                        try {
                            double convertedAmount =
                                    CurrencyConverter.convert(
                                            originalAmount,
                                            "INR",
                                            DatabaseConnection.preferredCurrency
                                    );

                            displayAmount =
                                    DatabaseConnection.preferredCurrency + " "
                                            + String.format("%.2f", convertedAmount);

                        } catch (Exception ex) {
                            displayAmount =
                                    "INR " + String.format("%.2f", originalAmount);
                        }

                        model.addRow(new Object[]{
                                result.getDate("transaction_date"),
                                result.getString("transaction_type"),
                                result.getString("category_name"),
                                displayAmount,
                                result.getString("description")
                        });
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null, "Could not load recent transactions."
            );
            e.printStackTrace();
        }
    }




    // =============================================================
    // SPENDING CHART
    // =============================================================

    static class SpendingChartPanel
            extends JPanel {

        private final Map<String, Double>
                categoryData =
                new LinkedHashMap<>();

        SpendingChartPanel() {

            setBackground(Color.WHITE);

            setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(
                                    BORDER
                            ),
                            BorderFactory.createEmptyBorder(
                                    20, 20, 20, 20
                            )
                    )
            );

            loadData();
        }


        private void loadData() {

            categoryData.clear();

            String sql =
                    """
                    SELECT
                        c.category_name,
                        SUM(t.amount) AS total
                    FROM transactions t
                    JOIN categories c
                        ON t.category_id = c.category_id
                    WHERE t.transaction_type = 'Expense'
                      AND t.user_id = ?
                    GROUP BY c.category_name
                    ORDER BY total DESC
                    """;

            try (
                    java.sql.Connection connection =
                            DatabaseConnection.getConnection();

                    java.sql.PreparedStatement statement =
                            connection.prepareStatement(sql)
            ) {

                statement.setInt(1, LoginGUI.currentUserId);

                try (ResultSet result = statement.executeQuery()) {

                    while (result.next()) {

                        double originalAmount =
                                result.getDouble("total");

                        double convertedAmount;

                        try {
                            convertedAmount = CurrencyConverter.convert(
                                    originalAmount,
                                    "INR",
                                    DatabaseConnection.preferredCurrency
                            );
                        } catch (Exception ex) {
                            convertedAmount = originalAmount;
                        }

                        categoryData.put(
                                result.getString("category_name"),
                                convertedAmount
                        );
                    }
                }

            } catch (SQLException e) {

                e.printStackTrace();
            }
        }


        @Override
        protected void paintComponent(
                Graphics g
        ) {

            super.paintComponent(g);

            loadData();

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (categoryData.isEmpty()) {

                g2.setColor(TEXT);

                g2.setFont(
                        new Font(
                                "SansSerif",
                                Font.PLAIN,
                                15
                        )
                );

                g2.drawString(
                        "No expense data available yet.",
                        250,
                        150
                );

                g2.dispose();

                return;
            }

            double max =
                    categoryData.values()
                            .stream()
                            .max(Double::compare)
                            .orElse(1.0);

            int chartHeight =
                    getHeight() - 100;

            int barWidth = 45;

            int gap = 25;

            int x = 60;

            for (Map.Entry<String, Double> entry
                    : categoryData.entrySet()) {

                double value =
                        entry.getValue();

                int barHeight =
                        (int)
                                ((value / max)
                                        * chartHeight);

                int y =
                        getHeight()
                                - barHeight
                                - 50;

                g2.setColor(
                        new Color(
                                180,
                                120,
                                135
                        )
                );

                g2.fillRoundRect(
                        x,
                        y,
                        barWidth,
                        barHeight,
                        12,
                        12
                );

                g2.setColor(BURGUNDY);

                g2.setFont(
                        new Font(
                                "SansSerif",
                                Font.BOLD,
                                11
                        )
                );

                String amount =
                        DatabaseConnection.preferredCurrency + " " +
                                String.format(
                                        "%.2f",
                                        value
                                );

                g2.drawString(
                        amount,
                        x,
                        y - 8
                );

                g2.setFont(
                        new Font(
                                "SansSerif",
                                Font.PLAIN,
                                11
                        )
                );

                String category =
                        entry.getKey();

                g2.drawString(
                        category,
                        x - 5,
                        getHeight() - 25
                );

                x += barWidth + gap;
            }

            g2.dispose();
        }
    }
}