package com.tcts.formdata;

import com.tcts.database.DatabaseField;

import java.sql.SQLException;
import java.util.List;

/**
 * Fields needed to create an allowed time.
 */
public class AddAllowedTimeFormData extends ValidatedFormData<SQLException> {
    public String allowedTime;

    /** Gives a particular time this should be inserted before. An empty
     * string ("") means add it to the end of the list of times.
     */
    public String timeToInsertBefore;

    @Override
    protected void validationRules(Errors errors) throws SQLException {
        if (allowedTime == null || allowedTime.isEmpty()) {
            errors.addError("You must enter a time.");
        }
        validateLength(allowedTime, DatabaseField.event_time, errors);
        List<String> allowedTimes = database.getAllowedTimes();
        if (!"".equals(timeToInsertBefore) && !allowedTimes.contains(timeToInsertBefore)) {
            errors.addError("You must specify where in the list to insert the new time.");
        }
    }

    public String getAllowedTime() {
        return allowedTime;
    }

    public void setAllowedTime(String allowedTime) {
        this.allowedTime = allowedTime;
    }

    public String getTimeToInsertBefore() {
        return timeToInsertBefore;
    }

    public void setTimeToInsertBefore(String timeToInsertBefore) {
        this.timeToInsertBefore = timeToInsertBefore;
    }
}
