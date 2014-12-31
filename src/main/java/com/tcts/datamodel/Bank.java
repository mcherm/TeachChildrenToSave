package com.tcts.datamodel;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a single bank. Contains data from one row of the Bank table.
 */
public class Bank {
    private String bankId;
    private String bankName;

    // --- Linked data - loaded only when needed ---
    private BankAdmin linkedBankAdmin;


    /**
     * This can be called to populate fields from the current row of a resultSet.
     */
    public void populateFieldsFromResultSetRow(ResultSet resultSet) throws SQLException {
        setBankId(resultSet.getString("bank_id"));
        setBankName(resultSet.getString("bank_name"));
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

    public BankAdmin getLinkedBankAdmin() {
        return linkedBankAdmin;
    }

    public void setLinkedBankAdmin(BankAdmin linkedBankAdmin) {
        this.linkedBankAdmin = linkedBankAdmin;
    }
}
