package com.tcts.datamodel;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An object that corresponds to the "Users" table in the database.
 */
public abstract class User {
    private String userId;
    private String email;
    private String hashedPassword;
    private String salt;
    private String firstName;
    private String lastName;
    private UserType userType;
    private String phoneNumber;

    /**
     * This can be called to populate fields from the current row of a resultSet.
     */
    protected void populateFieldsFromResultSetRow(ResultSet resultSet) throws SQLException {
        setUserId(resultSet.getString("user_id"));
        setEmail(resultSet.getString("email"));
        setHashedPassword(resultSet.getString("password_hash"));
        setSalt(resultSet.getString("password_salt"));
        setFirstName(resultSet.getString("first_name"));
        setLastName(resultSet.getString("last_name"));
        setPhoneNumber(resultSet.getString("phone_number"));
        setUserType(UserType.fromDBValue(resultSet.getString("access_type")));
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String password) {
        this.hashedPassword = password;
    }

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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
    
    
}
