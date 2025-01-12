package com.tcts.formdata;

/**
 * Data fields needed to be passed to mark a Volunteer as a BankAdmin OR to mark
 * a BankAdmin as a Volunteer. It really only needs the userId, but we pass
 * around the bankId also so we can navigate back to what we were doing (editing
 * the bank).
 */
public class MarkAsBankAdminFormData  {
    private String userId;
    private String bankId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
}
