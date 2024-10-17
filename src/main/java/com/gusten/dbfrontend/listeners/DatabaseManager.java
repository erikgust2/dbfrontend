package com.gusten.dbfrontend.listeners;

import com.gusten.dbfrontend.DataChangeListener;
import com.gusten.dbfrontend.Employee;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private final Connection connection;
    private DataChangeListener onDataChangeListener;

    public DatabaseManager(String url, String user, String password) throws SQLException {
        // Open the database connection
        connection = DriverManager.getConnection(url, user, password);

        // Cast to PGConnection to access PostgreSQL-specific functionality
        PGConnection pgConnection = connection.unwrap(PGConnection.class);

        // Start listening on the notification channel
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("LISTEN data_update_channel;");
        }

        // Start a background thread to listen for notifications
        new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    // Wait for up to 100ms for notifications
                    PGNotification[] notifications = pgConnection.getNotifications(100);
                    if (notifications != null) {
                        for (PGNotification notification : notifications) {
                            // Notify listener when the "employees" table changes
                            if (notification.getParameter().split(" ")[0].equals("employees")) {
                                onDataChangeListener.onDataChange();
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public List<Employee> getEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT id, name FROM employees";
        try (Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                employees.add(new Employee(rs.getInt("id"), rs.getString("name")));
            }
        }
        return employees;
    }

    public void addEmployee(String name) throws SQLException {
        String query = "INSERT INTO employees (name) VALUES (?)";
        try (var stmt = connection.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
        }
    }

    public void startListening(DataChangeListener listener) {
       this.onDataChangeListener = listener;
    }

    public void stopListening() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
