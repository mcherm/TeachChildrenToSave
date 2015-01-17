package com.tcts.formdata;

import com.tcts.exception.InvalidParameterFromGUIException;

import java.sql.SQLException;

/**
 * The data fields needed to edit a bank.
 */
public class EditBankFormData extends CreateBankFormData {
    private String bankId;

    @Override
    public void validationRules(Errors errors) {
        super.validationRules(errors);
        if (bankId == null || bankId.isEmpty()) {
            throw new InvalidParameterFromGUIException("The bankId should always be present.");
        }
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
}
