package com.gusten.dbfrontend;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {
    private static DatabaseManager databaseManager;
    private final Map<String, List<DataChangeListener>> tableListeners = new HashMap<>();

    private final String url;
    private final String user;
    private final String password;

    public DatabaseManager(String url, String user, String password) throws SQLException {
        this.url = url;
        this.user = user;
        this.password = password;

        startListeningForNotifications();
        databaseManager = this;
    }

    private void startListeningForNotifications() {
        new Thread(() -> {
            try (Connection connection = getConnection()) {
                PGConnection pgConnection = connection.unwrap(PGConnection.class);

                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("LISTEN data_update_channel");
                }

                while (!Thread.interrupted()) {
                    PGNotification[] notifications = pgConnection.getNotifications(100);
                    if (notifications != null) {
                        for (PGNotification notification : notifications) {
                            String tableName = notification.getParameter().split(" ")[0];
                            notifySubscribers(tableName);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private void notifySubscribers(String tableName) {
        List<DataChangeListener> listeners = tableListeners.get(tableName);
        if (listeners != null) {
            for (DataChangeListener listener : listeners) {
                listener.onDataChanged(tableName);
            }
        }
    }

    public static DatabaseManager getInstance() {
        return databaseManager;
    }

    public void subscribeToTable(String tableName, DataChangeListener listener) {
        tableListeners.computeIfAbsent(tableName, k -> new ArrayList<>()).add(listener);
    }

    public void unsubscribeFromTable(String tableName, DataChangeListener listener) {
        List<DataChangeListener> listeners = tableListeners.get(tableName);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public <T extends DatabaseModel> List<T> getColumnsFromTable(String tableName, String[] columns, Class<T> modelClass) throws SQLException {
        List<T> records = new ArrayList<>();
        String query = "SELECT " + String.join(", ", columns) + " FROM " + tableName;

        try (Connection connection = getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                try {
                    T record = modelClass.getDeclaredConstructor().newInstance();
                    record.fromResultSet(rs);
                    records.add(record);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return records;
    }

}
