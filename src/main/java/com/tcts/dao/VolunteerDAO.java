package com.tcts.dao;

import java.sql.SQLException;
import java.util.List;

import com.tcts.model.Volunteer;

public interface VolunteerDAO {
	public List<Volunteer> getAllVolunteer() throws SQLException;
	public Volunteer getVolunteer(String volunteerId) throws SQLException;
	public void updateVolunteer(Volunteer volunteer) throws SQLException;
	public void deleteVolunteer(Volunteer volunteer) throws SQLException;
	public boolean addVolunteer(Volunteer volunteer) throws SQLException;

}
