package com.gusten.dbfrontend;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Employee implements DatabaseModel {
    private final IntegerProperty id;
    private final StringProperty name;

    public Employee() {
        this(0, "");
    }

    public Employee(int id, String name) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
    }

    public int getId() {
        return id.get();
    }
    public void setId(int id) {
        this.id.set(id);
    }
    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }
    public void setName(String name) {
        this.name.set(name);
    }
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public void fromResultSet(ResultSet rs) throws SQLException {
        this.id.setValue(rs.getInt("id"));
        this.name.setValue(rs.getString("name"));
    }
}
