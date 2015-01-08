package com.tcts.datamodel;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a single bank administrator. Contains data from one row of the Users table.
 */
public class BankAdmin extends User {
    private String bankId;

    /**
     * This can be called to populate fields from the current row of a resultSet.
     */
    @Override
    public void populateFieldsFromResultSetRow(ResultSet resultSet) throws SQLException {
        super.populateFieldsFromResultSetRow(resultSet);
        setBankId(resultSet.getString("organization_id"));
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
}
