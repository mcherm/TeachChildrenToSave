package com.tcts.model;

import java.sql.Timestamp;

public class Volunteer {
	private String volunteerID;
	private String accessType;
	private boolean active;
	private String organizatiom;
	private String userStatus;
	private Timestamp createdTimeStamp;
	private Timestamp updatedTimeStamp;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String confirmEmailAddress;
	private StringBuffer password;
	private StringBuffer confirmPassword;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String zipcode;
	private String workPhoneNumber;
	private String mobilePhoneNumber;
	private String employerInfo;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public StringBuffer getPassword() {
		return password;
	}
	public void setPassword(StringBuffer password) {
		this.password = password;
	}
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getAddressLine2() {
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getWorkPhoneNumber() {
		return workPhoneNumber;
	}
	public void setWorkPhoneNumber(String workPhoneNumber) {
		this.workPhoneNumber = workPhoneNumber;
	}
	public String getMobilePhoneNumber() {
		return mobilePhoneNumber;
	}
	public void setMobilePhoneNumber(String mobilePhoneNumber) {
		this.mobilePhoneNumber = mobilePhoneNumber;
	}
	public String getEmployerInfo() {
		return employerInfo;
	}
	public void setEmployerInfo(String employerInfo) {
		this.employerInfo = employerInfo;
	}
	public String getVolunteerID() {
		return volunteerID;
	}
	public void setVolunteerID(String volunteerID) {
		this.volunteerID = volunteerID;
	}
	public String getAccessType() {
		return accessType;
	}
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getOrganizatiom() {
		return organizatiom;
	}
	public void setOrganizatiom(String organizatiom) {
		this.organizatiom = organizatiom;
	}
	public String getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	public Timestamp getCreatedTimeStamp() {
		return createdTimeStamp;
	}
	public void setCreatedTimeStamp(Timestamp createdTimeStamp) {
		this.createdTimeStamp = createdTimeStamp;
	}
	public Timestamp getUpdatedTimeStamp() {
		return updatedTimeStamp;
	}
	public void setUpdatedTimeStamp(Timestamp updatedTimeStamp) {
		this.updatedTimeStamp = updatedTimeStamp;
	}
	public String getConfirmEmailAddress() {
		return confirmEmailAddress;
	}
	public void setConfirmEmailAddress(String confirmEmailAddress) {
		this.confirmEmailAddress = confirmEmailAddress;
	}
	public StringBuffer getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(StringBuffer confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	

}
