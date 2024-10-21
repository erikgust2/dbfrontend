module com.gusten.dbfrontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.zaxxer.hikari;

    opens com.gusten.dbfrontend to javafx.fxml;
    exports com.gusten.dbfrontend;
}