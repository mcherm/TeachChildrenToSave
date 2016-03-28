package com.tcts.formdata;

/**
 * A class containing the fields needed for email announcement formdata.
 */
public class EmailAnnouncementFormData {
    private String matchedTeachers;
    private String unmatchedTeachers;
    private String matchedVolunteer;
    private String unmatchedVolunteers;
    private String bankAdmins;
    private String message;
    
	public String getMatchedTeachers() {
		return matchedTeachers;
	}
	public void setMatchedTeachers(String matchedTeachers) {
		this.matchedTeachers = matchedTeachers;
	}
	public String getUnmatchedTeachers() {
		return unmatchedTeachers;
	}
	public void setUnmatchedTeachers(String unmatchedTeachers) {
		this.unmatchedTeachers = unmatchedTeachers;
	}
	public String getMatchedVolunteer() {
		return matchedVolunteer;
	}
	public void setMatchedVolunteer(String matchedVolunteer) {
		this.matchedVolunteer = matchedVolunteer;
	}
	public String getUnmatchedVolunteers() {
		return unmatchedVolunteers;
	}
	public void setUnmatchedVolunteers(String unmatchedVolunteers) {
		this.unmatchedVolunteers = unmatchedVolunteers;
	}
	public String getBankAdmins() {
		return bankAdmins;
	}
	public void setBankAdmins(String bankAdmins) {
		this.bankAdmins = bankAdmins;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
    

}
