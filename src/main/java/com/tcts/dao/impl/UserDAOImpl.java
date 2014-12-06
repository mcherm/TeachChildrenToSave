package com.tcts.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.tcts.dao.UserDAO;
import com.tcts.dao.UserDAO;
import com.tcts.model.User;
import com.tcts.model.Volunteer;

public class UserDAOImpl implements UserDAO {
	private Connection connection;
    private PreparedStatement statement;
	
    static StringBuffer addUserSQL = new StringBuffer("INSERT INTO teachkidsdb.Users"
			+ "(User_ID,Email_1,Email_2,Password,First_Name,Last_Name,Access_Type,Organization_ID,Phone_Number_1,Phone_number_2,Active,Created,User_Status) VALUES"
			+ "(?,?,?,?,?,?,?,?,?,?,?,?,?);");
    
    static StringBuffer selectUserSQL = new StringBuffer("INSERT INTO teachkidsdb.Users"
			+ "(User_ID,Email_1,Email_2,Password,First_Name,Last_Name,Access_Type,Organization_ID,Phone_Number_1,Phone_number_2,Active,Created,User_Status) VALUES"
			+ "(?,?,?,?,?,?,?,?,?,?,?,?,?);");
    
    public UserDAOImpl() {
		// TODO Auto-generated constructor stub
	}
    public User getUser(String userId) throws SQLException {
		return null;
	}
    
    public boolean addUser(User user) {
		return true;
    }
	@Override
	public List<User> getAllUser() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void updateUser(User user) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteUser(User user) {
		// TODO Auto-generated method stub
		
	}
    
    

}
