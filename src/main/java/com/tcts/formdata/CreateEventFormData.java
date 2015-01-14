package com.tcts.formdata;

import java.sql.SQLException;
import java.util.Date;

/**
 * Data fields in the form used to create a new event.
 */
public class CreateEventFormData extends ValidatedFormData<SQLException> {
    private Date eventDate;
    private String eventTime;
    private String grade;
    private String numberStudents;
    private String notes;


    @Override
    public void validationRules(Errors errors) throws SQLException {
        if (!database.getAllowedDates().contains(eventDate)) {
            errors.addError("You must select a valid date.");
        }
        if (!database.getAllowedTimes().contains(eventTime)) {
            errors.addError("You must select a time from the list.");
        }
        if (grade == null || grade.length() == 0) {
            errors.addError("You must specify the grade.");
        } else {
            if (!(grade.equals("3") || grade.equals("4"))) {
                errors.addError("That is not a valid grade.");
            }
        }
        if (numberStudents == null || numberStudents.length() == 0) {
            errors.addError("Please specify the approximate number of students so the volunteers can ensure they have enough materials.");
        } else {
            try {
                int num = Integer.parseInt(numberStudents);
                if (num < 1) {
                    throw new NumberFormatException();
                }
            } catch(NumberFormatException err) {
                errors.addError("Please enter a valid number of students so the volunteers can ensure they have enough materials.");
            }
        }
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getNumberStudents() {
        return numberStudents;
    }

    public void setNumberStudents(String numberStudents) {
        this.numberStudents = numberStudents;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
