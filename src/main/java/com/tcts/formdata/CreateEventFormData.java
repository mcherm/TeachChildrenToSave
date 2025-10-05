package com.tcts.formdata;

import com.tcts.common.PrettyPrintingDate;
import com.tcts.database.DatabaseField;


/**
 * Data fields in the form used to create a new event.
 */
public class CreateEventFormData extends ValidatedFormData<RuntimeException> {
    private PrettyPrintingDate eventDate;
    private String eventTime;
    private String grade;
    private String deliveryMethod;
    private String numberStudents;
    private String notes;
    private String teacherId;



    @Override
    public void validationRules(Errors errors) {
        if ((teacherId == null) || (teacherId.isEmpty())) {
            errors.addError("You must select a teacher from the list.");
        }
        if (!database.getAllowedDates().contains(eventDate)) {
            errors.addError("You must select a valid date.");
        }
        if (!database.getAllowedTimes().contains(eventTime)) {
            errors.addError("You must select a time from the list.");
        }
        if (!database.getAllowedGrades().contains(grade)) {
            errors.addError("You must select a grade from the list.");
        }
        if (!database.getAllowedDeliveryMethods().contains(deliveryMethod)) {
            errors.addError("You must select a delivery method from the list.");
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
        validateLength(notes, DatabaseField.event_notes, errors);
    }

    public PrettyPrintingDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(PrettyPrintingDate eventDate) {
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

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
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

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

}
