package com.tcts.formdata;

import com.tcts.common.PrettyPrintingDate;

import java.sql.Date;

/**
 * Fields needed for creating a new allowed date.
 */
public class AddAllowedDateFormData extends ValidatedFormData<RuntimeException> {
    private String parsableDateStr;
    private PrettyPrintingDate date;

    /** As a side effect, this may set date using the field parsableDateStr. */
    @Override
    protected void validationRules(Errors errors) throws RuntimeException {
        if (parsableDateStr == null || parsableDateStr.length() == 0) {
            errors.addError("You must enter a date.");
        } else {
            try {
                date = new PrettyPrintingDate(Date.valueOf(parsableDateStr));
            } catch(IllegalArgumentException err) {
                errors.addError("Please enter the date in the form \"yyyy-mm-dd\".");
            }
        }
    }

    public String getParsableDateStr() {
        return parsableDateStr;
    }

    public void setParsableDateStr(String parsableDateStr) {
        this.parsableDateStr = parsableDateStr;
    }

    public PrettyPrintingDate getDate() {
        return date;
    }

    public void setDate(PrettyPrintingDate date) {
        this.date = date;
    }
}
