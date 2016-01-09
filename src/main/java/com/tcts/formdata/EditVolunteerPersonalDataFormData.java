package com.tcts.formdata;

/**
 * Volunteers have a couple of extra fields in addition to those found on a normal user
 */
public class EditVolunteerPersonalDataFormData extends EditPersonalDataFormData {
    private String bankSpecificData;

    public String getBankSpecificData() {
        return bankSpecificData;
    }

    public void setBankSpecificData(String bankSpecificData) {
        this.bankSpecificData = bankSpecificData;
    }
}
