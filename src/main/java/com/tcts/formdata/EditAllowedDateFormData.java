package com.tcts.formdata;

/**
 * Fields needed for editing an allowed date.
 */
public class EditAllowedDateFormData extends AddAllowedDateFormData {
    public String previousDate;

    @Override
    protected void validationRules(Errors errors) throws RuntimeException {
        super.validationRules(errors);
        // FIXME: Make sure previous date exists and that date matches previous date OR is different from all valid dates
    }

    public String getPreviousDate() {
        return previousDate;
    }

    public void setPreviousDate(String previousDate) {
        this.previousDate = previousDate;
    }
}
