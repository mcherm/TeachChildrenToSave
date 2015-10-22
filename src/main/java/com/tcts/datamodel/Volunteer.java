package com.tcts.datamodel;

import com.tcts.database.MySQLDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a single volunteer. Contains data from one row of the Users table.
 */
public class Volunteer extends User {
    // --- Basic data fields ---
    private String bankId;
    private ApprovalStatus approvalStatus;  /*denotes whether a volunteer has been approved by the bank admin*/


    // --- Linked data - loaded only when needed ---
    private Bank linkedBank;

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
        setBankId(resultSet.getString(prefix + "organization_id"));
        int userStatus = resultSet.getInt(prefix + "user_status");
        if (userStatus == MySQLDatabase.APPROVAL_STATUS_UNCHECKED){
            setApprovalStatus(ApprovalStatus.Unchecked);
        } else if (userStatus == MySQLDatabase.APPROVAL_STATUS_CHECKED) {
            setApprovalStatus(ApprovalStatus.Checked);
        } else if (userStatus == MySQLDatabase.APPROVAL_STATUS_SUSPENDED) {
            setApprovalStatus(ApprovalStatus.Suspended);
        } else {
            throw new RuntimeException("Invalid User Status");
        }
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }


    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }


    public Bank getLinkedBank() {
        return linkedBank;
    }

    public void setLinkedBank(Bank linkedBank) {
        this.linkedBank = linkedBank;
    }
}
