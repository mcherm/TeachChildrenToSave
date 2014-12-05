package com.tcts.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.tcts.dao.EventDAO;
import com.tcts.model.Event;
import com.tcts.model.Volunteer;

public class EventDAOImpl implements EventDAO {
	private Connection connection;
    private PreparedStatement statement;
	
    static StringBuffer addEventSQL = new StringBuffer("INSERT INTO teachkidsdb.Users"
			+ "(User_ID,Email_1,Email_2,Password,First_Name,Last_Name,Access_Type,Organization_ID,Phone_Number_1,Phone_number_2,Active,Created,User_Status) VALUES"
			+ "(?,?,?,?,?,?,?,?,?,?,?,?,?);");
    
    static StringBuffer selectEventSQL = new StringBuffer("INSERT INTO teachkidsdb.Users"
			+ "(User_ID,Email_1,Email_2,Password,First_Name,Last_Name,Access_Type,Organization_ID,Phone_Number_1,Phone_number_2,Active,Created,User_Status) VALUES"
			+ "(?,?,?,?,?,?,?,?,?,?,?,?,?);");
    
    public EventDAOImpl() {
		// TODO Auto-generated constructor stub
	}
    public Event getEvent(String eventId) throws SQLException {
		return null;
	}
    
    public boolean addEvent(Event event) {
		return true;
    }
	@Override
	public List<Event> getAllEvent() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void updateEvent(Event event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteEvent(Event event) {
		// TODO Auto-generated method stub
		
	}
    
    

}
