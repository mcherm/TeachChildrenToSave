package com.tcts.datamodel;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a single volunteer. Contains data from one row of the Users table.
 */
public class Volunteer extends User {
    // --- Basic data fields ---
    private String bankId;
    private ApprovalStatus approvalStatus;  /*denotes whether a volunteer has been approved by the bank admin*/
    private String bankSpecificData; /* An optional field that may contain extra information for a specific bank. */
    /*Volunteers Address*/
    private String streetAddress;
    private String suiteOrFloorNumber;
    private String mailCode;  //Internal address used by some banks
    private String city;
    private String state;
    private String zip;


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
        setApprovalStatus(ApprovalStatus.fromDBValue(resultSet.getInt(prefix + "user_status")));
        setBankSpecificData(resultSet.getString(prefix + "bank_specific_data"));
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

    public String getBankSpecificData() {
        return bankSpecificData;
    }

    public void setBankSpecificData(String bankSpecificData) {
        this.bankSpecificData = bankSpecificData;
    }


    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getSuiteOrFloorNumber() {
        return suiteOrFloorNumber;
    }

    public void setSuiteOrFloorNumber(String suiteOrFloorNumber) {
        this.suiteOrFloorNumber = suiteOrFloorNumber;
    }

    public String getMailCode() {
        return mailCode;
    }

    public void setMailCode(String mailCode) {
        this.mailCode = mailCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

}
