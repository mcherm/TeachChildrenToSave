package com.tcts.formdata;

/**
 * The data fields needed to edit a bank.
 */
public class EditBankFormData extends CreateBankFormData {
    private String bankId;

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
}
