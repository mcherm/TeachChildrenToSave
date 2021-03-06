package com.tcts.datamodel;

import com.tcts.database.DatabaseFacade;
import com.tcts.exception.NoSuchEventException;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Represents a single teacher. Contains data from one row of the Users table.
 */
public class Teacher extends User {
    @Autowired
    private DatabaseFacade database;

    // --- Basic data fields ---
    private String schoolId;

    // --- Linked data - loaded only when needed ---
    private School linkedSchool;


    /**
     * This can be called to populate fields from the current row of a resultSet.
     */
    @Override
    public void populateFieldsFromResultSetRow(ResultSet resultSet) throws SQLException {
        populateFieldsFromResultSetRowWithPrefix(resultSet, "");
    }

    @Override
    public void populateFieldsFromResultSetRowWithPrefix(ResultSet resultSet, String prefix) throws SQLException {
        super.populateFieldsFromResultSetRowWithPrefix(resultSet, prefix);
        setSchoolId(resultSet.getString(prefix + "organization_id"));
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public School getLinkedSchool() {
        return linkedSchool;
    }

    public void setLinkedSchool(School linkedSchool) {
        this.linkedSchool = linkedSchool;
    }

}
