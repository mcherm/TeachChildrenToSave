package com.tcts.formdata;

/**
 * A container for the fields used when making the call to update a bank-specific field label.
 */
public class SetBankSpecificFieldLabelFormData {
    private String bankId;
    private String bankSpecificFieldLabel;

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankSpecificFieldLabel() {
        return bankSpecificFieldLabel;
    }

    public void setBankSpecificFieldLabel(String bankSpecificFieldLabel) {
        this.bankSpecificFieldLabel = bankSpecificFieldLabel;
    }
}
