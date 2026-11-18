# FinSight – Personal Finance Management System

## About the Project

FinSight is a Java-based Personal Finance Management System developed to help users manage their personal finances.

The application allows users to record income and expenses, monitor their financial balance, view transaction history, analyse financial activities and manage monthly budgets.

## Features

- User registration and login
- Personal finance dashboard
- Add income and expense transactions
- View transaction history
- Monthly budget management
- Financial summaries and analysis
- Currency preferences

## Technologies Used

- **Programming Language:** Java
- **GUI:** Java Swing
- **Database:** MySQL
- **Database Connectivity:** JDBC
- **IDE:** IntelliJ IDEA

## Database

The project uses a MySQL database named `finsight_db`.

The database contains the following tables:

- `users` – Stores user account information.
- `categories` – Stores transaction categories.
- `transactions` – Stores income and expense records.
- `budgets` – Stores monthly budget information.

## Project Structure

- `LoginGUI.java` – Handles user login.
- `FinSightGUI.java` – Provides the main application interface.
- `DatabaseConnection.java` – Handles database connectivity and operations.
- `TransactionOperations.java` – Defines transaction operations through an interface.

## Requirements

- Java Development Kit (JDK)
- IntelliJ IDEA
- MySQL Server
- MySQL Connector/J JDBC driver

## How to Run

1. Clone or download this repository.
2. Open the project in IntelliJ IDEA.
3. Create the MySQL database named `finsight_db`.
4. Create the required database tables.
5. Configure the database connection details in the application.
6. Ensure the MySQL JDBC driver is added to the project.
7. Run `LoginGUI.java` to start the application.

## Project Objective

The objective of FinSight is to provide a simple and organised desktop application for tracking personal income, expenses and budgets.

## Future Enhancements

- Recurring transaction management
- Advanced financial reports
- Budget notifications
- Cloud-based data synchronisation

---

**Developed as an academic project**

B.Tech Computer Science and Engineering – Big Data Analytics

SRM Institute of Science and Technology
