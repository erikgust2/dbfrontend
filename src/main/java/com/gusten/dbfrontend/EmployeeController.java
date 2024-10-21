package com.gusten.dbfrontend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class EmployeeController {
    private final EmployeeService employeeService;
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
    private ObservableList<Employee> employeeData = FXCollections.observableArrayList();

    public EmployeeController() {
        this.employeeService = new EmployeeService();
        this.databaseManager = DatabaseManager.getInstance();
        databaseManager.subscribeToTable("employees", this::loadEmployees);
    }

    private void loadEmployees(String tableName) {
       if ("employees".equals(tableName)) {
           Task<List<Employee>> loadTask = new Task<>() {
               @Override
               protected List<Employee> call() throws SQLException {
                   return databaseManager.getColumnsFromTable("employees", new String[]{"id", "name"}, Employee.class);
               }
           };

           loadTask.setOnSucceeded(event -> {
               employeeData.setAll(loadTask.getValue());
               tableView.setItems(employeeData);
           });

           loadTask.setOnFailed(event -> {
               Throwable exception = loadTask.getException();
               showErrorMessage("Failed to load employees: " + exception.getMessage());
           });

           new Thread(loadTask).start();
       }
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        loadEmployees("employees");
    }

    @FXML
    public void handleAddEmployee() {
        String name = nameField.getText();

        if (name == null || name.trim().isEmpty()) {
            showErrorMessage("Name cannot be empty");
            return;
        }

        try {
            employeeService.addEmployee(name);
            nameField.clear();
        } catch (Exception e) {
            showErrorMessage("Failed to add employee: " + e.getMessage());
        }

    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
