package com.tcts.formdata;

import com.tcts.database.DatabaseField;

/**
 * Data that will be entered on the volunteer registration form.
 */
public class VolunteerRegistrationFormData extends UserRegistrationFormData {
    private String bankId;
    private String bankSpecificData;

    @Override
    protected void validationRules(Errors errors) throws RuntimeException {
        super.validationRules(errors);
        if (bankId == null || bankId.trim().length() == 0 || bankId.equals("0")) {
            errors.addError("You must select a bank.");
        }
        validateLength(bankSpecificData, DatabaseField.user_bank_specific_data, errors);
    }


    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getBankSpecificData() {
        return bankSpecificData;
    }

    public void setBankSpecificData(String bankSpecificData) {
        this.bankSpecificData = bankSpecificData;
    }
}
