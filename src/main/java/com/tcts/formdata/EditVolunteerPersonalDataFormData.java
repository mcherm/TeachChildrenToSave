package com.tcts.formdata;

import com.tcts.database.DatabaseField;

/**
 * Volunteers have a couple of extra fields in addition to those found on a normal user
 */
public class EditVolunteerPersonalDataFormData extends EditPersonalDataFormData {
    private String bankSpecificData;
    /*Volunteers Address*/
    private String streetAddress;
    private String suiteOrFloorNumber;
    private String city;
    private String state;
    private String zip;


    public void validateAddressFilledIn(Errors errors){
        if (isEmpty(streetAddress)) {
            errors.addError("You must provide a street address.");
        }
        if (isEmpty(city)) {
            errors.addError("You must provide a city.");
        }
        if (isEmpty(zip)) {
            errors.addError("You must provide a zip code.");
        }
        if (isEmpty(state)) {
            errors.addError("You must provide a state.");
        } else {
            if (state.length() != 2 || !Character.isLetter(state.charAt(0)) || !Character.isLetter(state.charAt(1))) {
                errors.addError("Please enter a valid 2 character state (e.g. \"DE\")");
            }
        }

    }

    @Override
    // validationRules validate that the address lengths are not too long but do not
    // require that the address is filled in.  In order to require that the address is filled the validateAddressFilledIn
    // method must be called in addition to validationRules
    public void validationRules(Errors errors) throws RuntimeException {

        super.validationRules(errors);

        validateLength(streetAddress, DatabaseField.user_street_address, errors);
        validateLength(suiteOrFloorNumber, DatabaseField.user_suite_or_floor_number, errors);
        validateLength(city, DatabaseField.user_city, errors);
        validateLength(state, DatabaseField.user_state, errors);
        validateLength(zip, DatabaseField.user_zip, errors);
        validateLength(bankSpecificData, DatabaseField.user_bank_specific_data, errors);
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
