package com.tcts.formdata;

import com.tcts.database.DatabaseField;

/**
 * A parent class with fields that are common to multiple registrations. Created
 * so that they can share the same validation logic.
 */
public abstract class UserRegistrationFormData extends ValidatedFormData<RuntimeException> {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    @Override
    protected void validationRules(Errors errors) throws RuntimeException {
        if (email == null || email.trim().length()==0) {
            errors.addError("You must provide a valid email.");
        }
        if (firstName == null || firstName.trim().length()==0) {
            errors.addError("You must provide a first name.");
        }
        if (lastName == null || lastName.trim().length()==0) {
            errors.addError("You must provide a last name.");
        }
        if (password == null || password.trim().length()==0) {
            errors.addError("You must select a password.");
        }
        validateLength(email, DatabaseField.user_email, errors);
        validateLength(firstName, DatabaseField.user_first_name, errors);
        validateLength(lastName, DatabaseField.user_last_name, errors);
        validateLength(phoneNumber, DatabaseField.user_phone_number, errors);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
