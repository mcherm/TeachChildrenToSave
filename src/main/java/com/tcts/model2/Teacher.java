package com.tcts.model2;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a single teacher. Contains data from one row of the Users table.
 */
public class Teacher extends User {
    private String schoolId;

    /**
     * This can be called to populate fields from the current row of a resultSet.
     */
    public void populateFieldsFromResultSetRow(ResultSet resultSet) throws SQLException {
        super.populateFieldsFromResultSetRow(resultSet);
        setSchoolId(resultSet.getString("organization_id"));
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }
}
