package com.tcts.datamodel;

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
    private List<BankAdmin> linkedBankAdmins;

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

    public List<BankAdmin> getLinkedBankAdmins() {
        return linkedBankAdmins;
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
