package com.tcts.database.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.tcts.database.AWSConnectionManager;
import com.tcts.model.Volunteer;

public class AWSConnectionManagerImpl implements AWSConnectionManager{
	
	static String dbUrl = "jdbc:mysql://mysqldb.cl0on6rirrkn.us-east-1.rds.amazonaws.com:3306/teachkidsde";
	static String dbClass = "com.mysql.jdbc.Driver";
	static String dbName ="teachkidsdb";
	static String dbUsername="teachkidsde";
	static String dbPassword ="Winter123";
	static StringBuffer addVolunteerSQL = new StringBuffer("INSERT INTO users"
			+ "(UserID,EmailID,Password,FirstName,LastName,AccessType,Organization,PhoneNumber,Active,Created_TS,Modified_TS,UserStatus) VALUES"
			+ "(?,?,?,?,?,?,?,?,?,?,?,?)");
	

    static {
        	try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }

    public static Connection getConnection() {
    	try {	
			return DriverManager.getConnection (dbUrl);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    }


	@Override
	public void connect() {
		// AWS 
	}
	
	public Integer addVolunteer(Volunteer volunteer) {
		Integer success = 0;
		Connection connection = getConnection();
		try{ 
		PreparedStatement addVolunteerStatement = connection.prepareStatement(addVolunteerSQL.toString());
		addVolunteerStatement.setString(1, volunteer.getVolunteerID());
		addVolunteerStatement.setString(2, volunteer.getEmailAddress());
		addVolunteerStatement.setString(3, volunteer.getPassword().toString());
		addVolunteerStatement.setString(4, volunteer.getFirstName());
		addVolunteerStatement.setString(5, volunteer.getLastName());
		addVolunteerStatement.setString(6, volunteer.getAccessType());
		addVolunteerStatement.setString(7, volunteer.getOrganizatiom());
		addVolunteerStatement.setString(8, volunteer.getWorkPhoneNumber());
		addVolunteerStatement.setString(9, "Y");
		addVolunteerStatement.setTimestamp(10, new Timestamp(new Date().getTime()));
		addVolunteerStatement.setTimestamp(11, new Timestamp(new Date().getTime()));
		addVolunteerStatement.setString(12, "Locked");
		
		// execute insert SQL stetement
		
			success = addVolunteerStatement.executeUpdate();
			return success;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return success;	
	}
	
	
		
	
	

}
