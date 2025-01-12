package com.tcts.formdata;

import com.tcts.database.DatabaseField;
import com.tcts.exception.InvalidParameterFromGUIException;

/**
 * The data fields needed to add a new (not yet existing) person as a Bank Admin for a bank.
 */
public class NewBankAdminFormData extends ValidatedFormData<RuntimeException> {
    private String bankId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    @Override
    protected void validationRules(Errors errors) throws RuntimeException {
        if (isEmpty(bankId)) {
            throw new InvalidParameterFromGUIException("The bankId should always be present.");
        }
        if (isEmpty(firstName)) {
            errors.addError("Provide a first name for the bank administrator who can approve or reject volunteers.");
        }
        if (isEmpty(lastName)) {
            errors.addError("Provide a last name for the bank administrator who can approve or reject volunteers.");
        }
        if (isEmpty(email)) {
            errors.addError("Provide an email address for the bank administrator who can approve or reject volunteers.");
        }
        validateLength(firstName, DatabaseField.user_first_name, errors);
        validateLength(lastName, DatabaseField.user_last_name, errors);
        validateLength(email, DatabaseField.user_email, errors);
        validateLength(phoneNumber, DatabaseField.user_phone_number, errors);
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
