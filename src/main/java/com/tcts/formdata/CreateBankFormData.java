package com.tcts.formdata;

import java.sql.SQLException;


/**
 * The data fields needed to create a bank and its associated bank admin.
 */
public class CreateBankFormData extends ValidatedFormData<RuntimeException> {
    private String bankName;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    private boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    @Override
    public void validationRules(Errors errors) {
        if (isEmpty(bankName)) {
            errors.addError("Please provide a name for the bank.");
        }
        if (isEmpty(firstName) && isEmpty(lastName) && isEmpty(email) && isEmpty(phoneNumber)) {
            // No Bank Admin at all... this is valid.
        } else {
            if (isEmpty(firstName)) {
                errors.addError("Provide a first name for the bank administrator who can approve or reject volunteers.");
            }
            if (isEmpty(lastName)) {
                errors.addError("Provide a last name for the bank administrator who can approve or reject volunteers.");
            }
            if (isEmpty(email)) {
                errors.addError("Provide an email adddress for the bank administrator who can approve or reject volunteers.");
            }
        }
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
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
