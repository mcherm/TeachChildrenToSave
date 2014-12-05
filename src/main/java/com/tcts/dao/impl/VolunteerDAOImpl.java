package com.tcts.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.tcts.dao.VolunteerDAO;
import com.tcts.database.ConnectionFactory;
import com.tcts.model.Volunteer;
import com.tcts.util.DBUtil;

public class VolunteerDAOImpl implements VolunteerDAO{
	private Connection connection;
    private PreparedStatement statement;
	
    static StringBuffer addVolunteerSQL = new StringBuffer("INSERT INTO teachkidsdb.Users"
			+ "(User_ID,Email_1,Email_2,Password,First_Name,Last_Name,Access_Type,Organization_ID,Phone_Number_1,Phone_number_2,Active,Created,User_Status) VALUES"
			+ "(?,?,?,?,?,?,?,?,?,?,?,?,?);");
 
    public VolunteerDAOImpl() {	}
    
    public Volunteer getEmployee(int volunteerId) throws SQLException {
        String query = "SELECT * FROM USERS WHERE user_id=" + volunteerId;
        ResultSet rs = null;
        Volunteer volunteer = null;
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery(query);
        } finally {
            DBUtil.close(rs);
            DBUtil.close(statement);
            DBUtil.close(connection);
        }
        return volunteer;
    }
    
    public boolean addVolunteer(Volunteer volunteer) {
		try{
		connection = ConnectionFactory.getConnection();
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
		return addVolunteerStatement.executeUpdate() == 0?false:true;
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Volunteer> getAllVolunteer() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Volunteer getVolunteer(String volunteerId) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateVolunteer(Volunteer volunteer) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteVolunteer(Volunteer volunteer) throws SQLException {
		// TODO Auto-generated method stub
		
	}
    
    
    
}
