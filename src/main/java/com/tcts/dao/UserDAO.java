package com.tcts.dao;

import java.sql.SQLException;
import java.util.List;

import com.tcts.model.User;

public interface UserDAO {
	public List<User> getAllUser() throws SQLException;
	public User getUser(String userId) throws SQLException;
	public void updateUser(User user) throws SQLException;
	public void deleteUser(User user) throws SQLException;
	public boolean addUser(User user) throws SQLException;

}
