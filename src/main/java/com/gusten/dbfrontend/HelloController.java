package com.gusten.dbfrontend;

import com.gusten.dbfrontend.listeners.DatabaseManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class HelloController {
    @FXML
    public TableView<Employee> tableView;
    @FXML
    public TableColumn<Employee, Number> idColumn;
    @FXML
    public TableColumn<Employee, String> nameColumn;
    @FXML
    public TextField nameField;
    @FXML
    public Button addButton;

    private DatabaseManager databaseManager;

    public HelloController() {

    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    databaseManager = new DatabaseManager("jdbc:postgresql://localhost:5432/postgres", "postgres", "password");
                    setupDatabaseListener();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        task.setOnSucceeded(event -> loadEmployees());

        new Thread(task).start();
    }

    private void loadEmployees() {
        try {
            tableView.setItems(FXCollections.observableArrayList(databaseManager.getEmployees()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupDatabaseListener() {
        databaseManager.startListening(() -> Platform.runLater(this::loadEmployees));
    }

    @FXML
    public void handleAddEmployee(ActionEvent actionEvent) {
        String name = nameField.getText();
        nameField.clear();
        if (!name.isEmpty()) {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() {
                    try {
                        databaseManager.addEmployee(name);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };

            task.setOnSucceeded(event -> loadEmployees());

            new Thread(task).start();
        }
    }
}