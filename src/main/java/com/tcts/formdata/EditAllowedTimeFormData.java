package com.tcts.formdata;

import java.sql.SQLException;

/**
 * Fields needed to edit an allowed time.
 */
public class EditAllowedTimeFormData extends AddAllowedTimeFormData {
    public String previousTime;

    @Override
    protected void validationRules(Errors errors) throws SQLException {
        super.validationRules(errors);
        // FIXME: Make sure previous time exists and that time matches previous time OR is different from all valid time
    }

    public String getPreviousTime() {
        return previousTime;
    }

    public void setPreviousTime(String previousTime) {
        this.previousTime = previousTime;
    }
}
