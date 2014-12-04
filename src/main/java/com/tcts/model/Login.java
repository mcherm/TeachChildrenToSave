package com.tcts.model;

public class Login {
	private StringBuffer userID;
	private StringBuffer password;
	private String userType;
	
	public StringBuffer getUserID() {
		return userID;
	}
	public void setUserID(StringBuffer userID) {
		this.userID = userID;
	}
	public StringBuffer getPassword() {
		return password;
	}
	public void setPassword(StringBuffer password) {
		this.password = password;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	

}
