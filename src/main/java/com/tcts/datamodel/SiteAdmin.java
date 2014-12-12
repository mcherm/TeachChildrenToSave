package com.tcts.datamodel;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a single site administrator. Contains data from one row of the Users table.
 */
public class SiteAdmin extends User {
    /**
     * This can be called to populate fields from the current row of a resultSet.
     */
    public void populateFieldsFromResultSetRow(ResultSet resultSet) throws SQLException {
        super.populateFieldsFromResultSetRow(resultSet);
    }

}
