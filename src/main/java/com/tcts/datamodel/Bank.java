package com.tcts.datamodel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a single bank. Contains data from one row of the Bank table.
 */
public class Bank {
    private String bankId;
    private String bankName;
    private BigDecimal minLMIForCRA;
    private String bankSpecificDataLabel;

    // --- Linked data - loaded only when needed ---
    private BankAdmin linkedBankAdmin; // FIXME: The old one-admin-per-bank model
    private List<BankAdmin> linkedBankAdmins; // FIXME: thew new, multiple-admins model


    /**
     * This can be called to populate fields from the current row of a resultSet.
     */
    public void populateFieldsFromResultSetRow(ResultSet resultSet) throws SQLException {
        setBankId(resultSet.getString("bank_id"));
        setBankName(resultSet.getString("bank_name"));
        setMinLMIForCRA(resultSet.getBigDecimal("min_lmi_for_cra"));
        setBankSpecificDataLabel(resultSet.getString("bank_specific_data_label"));
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

    public BankAdmin getLinkedBankAdmin() { // FIXME: Old version; get rid of it
        return linkedBankAdmin;
    }

    public List<BankAdmin> getLinkedBankAdmins() {
        return linkedBankAdmins;
    }

    public void setLinkedBankAdmin(BankAdmin linkedBankAdmin) {
        this.linkedBankAdmin = linkedBankAdmin;
    }

    public void setLinkedBankAdmins(List<BankAdmin> linkedBankAdmins) {
        this.linkedBankAdmins = linkedBankAdmins;
    }

    public BigDecimal getMinLMIForCRA() {
        return minLMIForCRA;
    }

    public void setMinLMIForCRA(BigDecimal minLMIForCRA) {
        this.minLMIForCRA = minLMIForCRA;
    }

    public String getBankSpecificDataLabel() {
        return bankSpecificDataLabel;
    }

    public void setBankSpecificDataLabel(String bankSpecificDataLabel) {
        this.bankSpecificDataLabel = bankSpecificDataLabel;
    }
}
