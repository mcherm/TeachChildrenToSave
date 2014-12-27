package com.tcts.formdata;

import java.util.Date;

/**
 * Data fields in the form used to creae a new event.
 */
public class CreateEventFormData {
    private Date eventDate;
    private String eventTime;
    private String grade;
    private String numberStudents;
    private String notes;

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
