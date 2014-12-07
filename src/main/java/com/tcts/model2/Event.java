package com.tcts.model2;

import java.util.Date;

/**
 * An object that corresponds to the "Event2" table in the database.
 */
public class Event {
    // --- Basic data fields ---
    private String eventId;
    private String teacherId;
    private Date eventDate;
    private String eventTime;
    private String grade;
    private int numberStudents;
    private String notes;
    private String volunteerId;

    // --- Linked data - loaded only when needed ---
    private Teacher linkedTeacher;
    private Volunteer linkedVolunteer;


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
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

    public int getNumberStudents() {
        return numberStudents;
    }

    public void setNumberStudents(Integer numberStudents) {
        this.numberStudents = numberStudents;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(String volunteerId) {
        this.volunteerId = volunteerId;
    }


    public Teacher getLinkedTeacher() {
        return linkedTeacher;
    }

    public void setLinkedTeacher(Teacher linkedTeacher) {
        this.linkedTeacher = linkedTeacher;
    }

    public Volunteer getLinkedVolunteer() {
        return linkedVolunteer;
    }

    public void setLinkedVolunteer(Volunteer linkedVolunteer) {
        this.linkedVolunteer = linkedVolunteer;
    }
}
