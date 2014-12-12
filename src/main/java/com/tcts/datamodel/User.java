package com.tcts.datamodel;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An object that corresponds to the "Users" table in the database.
 */
public abstract class User {
    private String userId;
    private String login;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private UserType userType;
    private String phoneNumber;

    /**
     * This can be called to populate fields from the current row of a resultSet.
     */
    protected void populateFieldsFromResultSetRow(ResultSet resultSet) throws SQLException {
        setUserId(resultSet.getString("user_id"));
        setLogin(resultSet.getString("user_login"));
        setEmail(resultSet.getString("email"));
        setPassword(resultSet.getString("password_hash"));
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
