package com.tcts.datamodel;

import com.tcts.common.PrettyPrintingDate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * An object that corresponds to the "Event" table in the database.
 */
public class Event {
    // --- Basic data fields ---
    private String eventId;
    private String teacherId;
    private PrettyPrintingDate eventDate;
    private String eventTime;
    private String grade;
    private String presence;
    private int numberStudents;
    private String notes;
    private String volunteerId;

    // --- Linked data - loaded only when needed ---
    private Teacher linkedTeacher;
    private Volunteer linkedVolunteer;


    /**
     * This can be called to populate fields from the current row of a resultSet.
     */
    public void populateFieldsFromResultSetRow(ResultSet resultSet) throws SQLException {
        setEventId(resultSet.getString("event_id"));
        setTeacherId(resultSet.getString("teacher_id"));
        setEventDate(new PrettyPrintingDate(resultSet.getDate("event_date")));
        setEventTime(resultSet.getString("event_time"));
        setGrade(resultSet.getString("grade"));
        setPresence(resultSet.getString("presence"));
        setNumberStudents(resultSet.getInt("number_students"));
        setNotes(resultSet.getString("notes"));
        setVolunteerId(resultSet.getString("volunteer_id"));
    }

    public void populateFieldsFromResultSetRowWithTeacherAndSchool (ResultSet resultSet) throws SQLException {
        populateFieldsFromResultSetRow(resultSet);
        Teacher teacher = new Teacher();
        teacher.populateFieldsFromResultSetRow(resultSet);
        setLinkedTeacher(teacher);
        School school = new School();
        school.populateFieldsFromResultSetRow(resultSet);
        teacher.setLinkedSchool(school);
    }

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

    public String getPresence() {
        return presence;
    }

    public String getPresenceString() {
        if (presence.equals("P")) {
            return "In person";
        } else if (presence.equals("V")) {
            return "Virtual";
        } else {
            return ""; // This should never happen!
        }
    }

    public void setPresence(String presence) {
        this.presence = presence;
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
