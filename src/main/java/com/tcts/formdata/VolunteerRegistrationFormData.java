package com.tcts.formdata;

/**
 * Data that will be entered on the volunteer registration form.
 */
public class VolunteerRegistrationFormData extends UserRegistrationFormData {
    private String bankId;

    @Override
    protected void validationRules(Errors errors) throws RuntimeException {
        super.validationRules(errors);
        if (bankId == null || bankId.trim().length() == 0 || bankId.equals("0")) {
            errors.addError("You must select a bank.");
        }
    }


    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
}
