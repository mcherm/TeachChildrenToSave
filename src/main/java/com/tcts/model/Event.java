package com.tcts.model;

import java.util.Date;

@Deprecated
public class Event {
	private String eventID;
	private String schoolID;
	private String teacherUserID;
	private String volunteerUserID;
	private String grade;
	private String subject;
	private Integer numStudents;
	private Date eventDate;
	private String eventTime;
	private String eventNotes;
	private boolean volunteerAssigned;
	
	public String getEventID() {
		return eventID;
	}
	public void setEventID(String eventID) {
		this.eventID = eventID;
	}
	public String getSchoolID() {
		return schoolID;
	}
	public void setSchoolID(String schooldID) {
		this.schoolID = schooldID;
	}
	public String getTeacherUserID() {
		return teacherUserID;
	}
	public void setTeacherUserID(String teacherUserID) {
		this.teacherUserID = teacherUserID;
	}
	public String getVolunteerUserID() {
		return volunteerUserID;
	}
	public void setVolunteerUserID(String volunteerUserID) {
		this.volunteerUserID = volunteerUserID;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Integer getNumStudents() {
		return numStudents;
	}
	public void setNumStudents(Integer numStudents) {
		this.numStudents = numStudents;
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
	public String getEventNotes() {
		return eventNotes;
	}
	public void setEventNotes(String eventNotes) {
		this.eventNotes = eventNotes;
	}
	public boolean isVolunteerAssigned() {
		return volunteerAssigned;
	}
	public void setVolunteerAssigned(boolean volunteerAssigned) {
		this.volunteerAssigned = volunteerAssigned;
	}
	

}
