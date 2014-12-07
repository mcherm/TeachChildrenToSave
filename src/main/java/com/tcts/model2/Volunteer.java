package com.tcts.model2;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a single volunteer. Contains data from one row of the Users table.
 */
public class Volunteer extends User {
    // --- Basic data fields ---
    private String bankId;
    private boolean isApproved;
    private BigDecimal craHours;

    // --- Linked data - loaded only when needed ---
    private Bank linkedBank;

    /**
     * This can be called to populate fields from the current row of a resultSet.
     */
    public void populateFieldsFromResultSetRow(ResultSet resultSet) throws SQLException {
        super.populateFieldsFromResultSetRow(resultSet);
        setBankId(resultSet.getString("organization_id"));
        setApproved(resultSet.getInt("user_status") == 1);
        setCraHours(new BigDecimal(0)); // FIXME: Need column in DB for this, right?
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public BigDecimal getCraHours() {
        return craHours;
    }

    public void setCraHours(BigDecimal craHours) {
        this.craHours = craHours;
    }

    public Bank getLinkedBank() {
        return linkedBank;
    }

    public void setLinkedBank(Bank linkedBank) {
        this.linkedBank = linkedBank;
    }
}
