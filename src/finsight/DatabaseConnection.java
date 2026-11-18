
package finsight;

import java.sql.*;

public class DatabaseConnection implements TransactionOperations {
    public static String preferredCurrency = "INR";

    private static final String URL =
            "jdbc:mysql://localhost:3306/finsight_db";

    private static final String USER = "root";

    // Enter the same local MySQL password you used previously.
    private static final String PASSWORD = "Irap@irap7";

    public static Connection getConnection() {

        try {
            return DriverManager.getConnection(
                    URL, USER, PASSWORD
            );
        } catch (SQLException e) {
            System.out.println("Database connection failed: "
                    + e.getMessage());
            return null;
        }
    }

    // REGISTER NEW USER
    public static boolean registerUser(
            String username,
            String password
    ) {

        String sql =
                "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection connection = getConnection()) {

            if (connection == null) return false;

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setString(1, username);
                statement.setString(
                        2, PasswordUtil.hashPassword(password)
                );

                return statement.executeUpdate() == 1;
            }

        } catch (SQLException e) {
            System.out.println("Registration error: "
                    + e.getMessage());
            return false;
        }
    }

    // LOGIN USER

    public static int loginUser(
            String username,
            String password
    ) {

        String sql =
                "SELECT user_id, password, preferred_currency FROM users WHERE username = ?";

        try (Connection connection = getConnection()) {

            if (connection == null) return -1;

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setString(1, username);

                try (ResultSet result = statement.executeQuery()) {

                    if (result.next()) {

                        String storedHash =
                                result.getString("password");

                        if (PasswordUtil.verifyPassword(
                                password, storedHash)) {

                            preferredCurrency =
                                    result.getString("preferred_currency");

                            return result.getInt("user_id");
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Login error: "
                    + e.getMessage());
        }

        return -1;
    }

    public static boolean updatePreferredCurrency(
            int userId,
            String currency
    ) {

        String sql =
                "UPDATE users SET preferred_currency = ? WHERE user_id = ?";

        try (Connection connection = getConnection()) {

            if (connection == null) return false;

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setString(1, currency);
                statement.setInt(2, userId);

                int rowsUpdated = statement.executeUpdate();

                if (rowsUpdated > 0) {
                    preferredCurrency = currency;
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println("Currency update error: "
                    + e.getMessage());
        }

        return false;
    }


    // SAVE TRANSACTION FOR LOGGED-IN USER
    public static boolean saveTransaction(
            int userId,
            double amount,
            String type,
            String category,
            String description
    ) {

        String sql = """
                INSERT INTO transactions
                (user_id, category_id, transaction_type,
                 amount, description, transaction_date)
                VALUES (
                    ?, 
                    (SELECT category_id FROM categories
                     WHERE category_name = ?),
                    ?, ?, ?, CURDATE()
                )
                """;

        try (Connection connection = getConnection()) {

            if (connection == null) return false;

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setInt(1, userId);
                statement.setString(2, category);
                statement.setString(3, type);
                statement.setDouble(4, amount);
                statement.setString(5, description);

                return statement.executeUpdate() == 1;
            }

        } catch (SQLException e) {
            System.out.println("Transaction error: "
                    + e.getMessage());
            return false;
        }
    }
    @Override
    public boolean addTransaction(
            int userId,
            double amount,
            String type,
            String category,
            String description
    ) {
        return saveTransaction(userId, amount, type, category, description);
    }
    // SAVE OR UPDATE MONTHLY BUDGET
    public static boolean saveBudget(
            int userId,
            int categoryId,
            double amount,
            String monthYear
    ) {
        String checkSql = """
            SELECT budget_id FROM budgets
            WHERE user_id = ? AND category_id = ? AND month_year = ?
            """;

        String updateSql = """
            UPDATE budgets SET amount = ?
            WHERE user_id = ? AND category_id = ? AND month_year = ?
            """;

        String insertSql = """
            INSERT INTO budgets (user_id, category_id, amount, month_year)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection connection = getConnection()) {

            if (connection == null) return false;

            try (PreparedStatement check = connection.prepareStatement(checkSql)) {
                check.setInt(1, userId);
                check.setInt(2, categoryId);
                check.setString(3, monthYear);

                try (ResultSet result = check.executeQuery()) {

                    if (result.next()) {
                        try (PreparedStatement update =
                                     connection.prepareStatement(updateSql)) {

                            update.setDouble(1, amount);
                            update.setInt(2, userId);
                            update.setInt(3, categoryId);
                            update.setString(4, monthYear);

                            return update.executeUpdate() > 0;
                        }
                    }
                }
            }

            try (PreparedStatement insert =
                         connection.prepareStatement(insertSql)) {

                insert.setInt(1, userId);
                insert.setInt(2, categoryId);
                insert.setDouble(3, amount);
                insert.setString(4, monthYear);

                return insert.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.out.println("Budget save error: " + e.getMessage());
            return false;
        }
    }
    // GET ALL CATEGORIES
    public static java.util.Map<Integer, String> getCategories() {

        java.util.Map<Integer, String> categories = new java.util.LinkedHashMap<>();

        String sql = "SELECT category_id, category_name FROM categories";

        try (Connection connection = getConnection()) {

            if (connection == null) return categories;

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    categories.put(
                            result.getInt("category_id"),
                            result.getString("category_name")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Category retrieval error: " + e.getMessage());
        }

        return categories;
    }
    // GET TOTAL EXPENSES FOR A CATEGORY IN A MONTH
    public static double getMonthlyExpenses(
            int userId,
            int categoryId,
            String monthYear
    ) {
        String sql = """
            SELECT COALESCE(SUM(amount), 0)
            FROM transactions
            WHERE user_id = ?
            AND category_id = ?
            AND transaction_type = 'Expense'
            AND DATE_FORMAT(transaction_date, '%Y-%m') = ?
            """;

        try (Connection connection = getConnection()) {

            if (connection == null) return 0;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, userId);
                statement.setInt(2, categoryId);
                statement.setString(3, monthYear);

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return result.getDouble(1);
                    }
                }

            }

        } catch (SQLException e) {
            System.out.println("Monthly expense error: " + e.getMessage());
        }

        return 0;
    }
    // GET SAVED BUDGETS
    public static java.util.List<Object[]> getSavedBudgets(
            int userId,
            String monthYear
    ) {
        java.util.List<Object[]> budgets = new java.util.ArrayList<>();

        String sql = """
            SELECT
                b.category_id,
                c.category_name,
                b.amount,
                COALESCE(SUM(t.amount), 0) AS spent
            FROM budgets b
            JOIN categories c ON b.category_id = c.category_id
            LEFT JOIN transactions t
                ON t.user_id = b.user_id
                AND t.category_id = b.category_id
                AND t.transaction_type = 'Expense'
                AND DATE_FORMAT(t.transaction_date, '%Y-%m') = b.month_year
            WHERE b.user_id = ?
            AND b.month_year = ?
            GROUP BY b.category_id, c.category_name, b.amount
            """;

        try (Connection connection = getConnection()) {

            if (connection == null) return budgets;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, userId);
                statement.setString(2, monthYear);

                try (ResultSet result = statement.executeQuery()) {

                    while (result.next()) {
                        budgets.add(new Object[]{
                                result.getInt("category_id"),
                                result.getString("category_name"),
                                result.getDouble("amount"),
                                result.getDouble("spent")
                        });
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Saved budget retrieval error: " + e.getMessage());
        }

        return budgets;
    }
}
