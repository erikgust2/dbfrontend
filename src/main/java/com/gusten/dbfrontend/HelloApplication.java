package com.gusten.dbfrontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        initializeDatabaseManager();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("employee-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    private void initializeDatabaseManager() {
        String url = "jdbc:postgresql://172.26.200.64:5432/postgres";
        String user = "myuser";
        String password = "password";

        try {
            DatabaseManager databaseManager = new DatabaseManager(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}