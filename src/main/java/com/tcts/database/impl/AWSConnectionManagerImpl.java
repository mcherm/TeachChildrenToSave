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
	
	static String dbUrl = "";
	static String dbClass = "com.mysql.jdbc.Driver";
	static String dbName ="";
	static String dbUsername="";
	static String dbPassword ="";
	static StringBuffer addVolunteerSQL = new StringBuffer("INSERT INTO teachkidsdb.Users"
			+ "(User_ID,Email_1,Email_2,Password,First_Name,Last_Name,Access_Type,Organization_ID,Phone_Number_1,Phone_number_2,Active,Created,User_Status) VALUES"
			+ "(?,?,?,?,?,?,?,?,?,?,?,?,?);");
	

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
    		System.out.println("AWSConnectio");
			return DriverManager.getConnection (dbUrl,dbUsername,dbPassword);
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
		addVolunteerStatement.setString(3, volunteer.getEmailAddress());
		addVolunteerStatement.setString(4, volunteer.getPassword().toString());
		addVolunteerStatement.setString(5, volunteer.getFirstName());
		addVolunteerStatement.setString(6, volunteer.getLastName());
		addVolunteerStatement.setString(7, volunteer.getAccessType());
		addVolunteerStatement.setInt(8, 1);
		addVolunteerStatement.setString(9, volunteer.getWorkPhoneNumber());
		addVolunteerStatement.setString(10, volunteer.getWorkPhoneNumber());
		addVolunteerStatement.setInt(11, 0);
		addVolunteerStatement.setTimestamp(12, new Timestamp(new Date().getTime()));
		addVolunteerStatement.setInt(13, 0);
		
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
