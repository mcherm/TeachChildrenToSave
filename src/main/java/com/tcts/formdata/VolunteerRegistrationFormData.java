package com.tcts.formdata;

import com.tcts.database.DatabaseField;

/**
 * Data that will be entered on the volunteer registration form.
 */
public class VolunteerRegistrationFormData extends UserRegistrationFormData {
    private String bankId;
    private String bankSpecificData;
    /*Volunteers Address*/
    private String streetAddress;
    private String suiteOrFloorNumber;
    private String mailCode;  //Internal address used by some banks
    private String city;
    private String state;
    private String zip;

    /** Just a utility to quickly test a string. */
    private boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    @Override
    protected void validationRules(Errors errors) throws RuntimeException {
        super.validationRules(errors);
        if (bankId == null || bankId.trim().length() == 0 || bankId.equals("0")) {
            errors.addError("You must select a bank.");
        }
        if (isEmpty(streetAddress)) {
            errors.addError("You must provide a street address.");
        }
        if (isEmpty(city)) {
            errors.addError("You must provide a city for your address.");
        }
        if (isEmpty(zip)) {
            errors.addError("You must provide a zip code for your address.");
        }
        if (isEmpty(state)) {
            errors.addError("You must provide a state for your address.");
        } else {
            if (state.length() != 2 || !Character.isLetter(state.charAt(0)) || !Character.isLetter(state.charAt(1))) {
                errors.addError("Please enter a valid 2 character state (e.g. \"DE\")");
            }
        }

        validateLength(streetAddress, DatabaseField.user_street_address,errors);
        validateLength(suiteOrFloorNumber, DatabaseField.user_suite_or_floor_number,errors);
        validateLength(mailCode, DatabaseField.user_mail_code,errors);
        validateLength(city, DatabaseField.user_city,errors);
        validateLength(state, DatabaseField.user_state,errors);
        validateLength(zip, DatabaseField.user_zip,errors);

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

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getSuiteOrFloorNumber() {
        return suiteOrFloorNumber;
    }

    public void setSuiteOrFloorNumber(String suiteOrFloorNumber) {
        this.suiteOrFloorNumber = suiteOrFloorNumber;
    }

    public String getMailCode() {
        return mailCode;
    }

    public void setMailCode(String mailCode) {
        this.mailCode = mailCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
