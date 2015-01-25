package com.tcts.formdata;

/**
 * A class containing the fields needed for email announcement formdata.
 */
public class EmailAnnouncementFormData {
    private String matchedTeachers;
    private String unmachedTeachers;
    private String matchedVolunteer;
    private String unmatchedvolunteers;
    private String bankAdmins;
    private String message;
    
	public String getMatchedTeachers() {
		return matchedTeachers;
	}
	public void setMatchedTeachers(String matchedTeachers) {
		this.matchedTeachers = matchedTeachers;
	}
	public String getUnmachedTeachers() {
		return unmachedTeachers;
	}
	public void setUnmachedTeachers(String unmachedTeachers) {
		this.unmachedTeachers = unmachedTeachers;
	}
	public String getMatchedVolunteer() {
		return matchedVolunteer;
	}
	public void setMatchedVolunteer(String matchedVolunteer) {
		this.matchedVolunteer = matchedVolunteer;
	}
	public String getUnmatchedvolunteers() {
		return unmatchedvolunteers;
	}
	public void setUnmatchedvolunteers(String unmatchedvolunteers) {
		this.unmatchedvolunteers = unmatchedvolunteers;
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
