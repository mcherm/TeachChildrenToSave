package com.tcts.model2;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a single bank. Contains data from one row of the Bank2 table.
 */
public class Bank {
    private String bankId;
    private String bankName;
    private String bankAdminId;

    /**
     * This can be called to populate fields from the current row of a resultSet.
     */
    public void populateFieldsFromResultSetRow(ResultSet resultSet) throws SQLException {
        setBankId(resultSet.getString("bank_id"));
        setBankName(resultSet.getString("bank_name"));
        setBankAdminId(resultSet.getString("bank_admin"));
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAdminId() {
        return bankAdminId;
    }

    public void setBankAdminId(String bankAdminId) {
        this.bankAdminId = bankAdminId;
    }
}
