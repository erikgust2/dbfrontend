package com.gusten.dbfrontend;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DatabaseModel {
    /**
     * This method will map the ResultSet row to the model object.
     * @param rs The ResultSet to map from
     * @throws SQLException If an error occurs while reading the ResultSet
     */
    void fromResultSet(ResultSet rs) throws SQLException;
}
