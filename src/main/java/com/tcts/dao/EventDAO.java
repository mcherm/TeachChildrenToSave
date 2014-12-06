package com.tcts.dao;

import java.sql.SQLException;
import java.util.List;

import com.tcts.model.Event;

public interface EventDAO {
	public List<Event> getAllEvent() throws SQLException;
	public Event getEvent(String eventId) throws SQLException;
	public void updateEvent(Event event) throws SQLException;
	public void deleteEvent(Event event) throws SQLException;
	public boolean addEvent(Event event) throws SQLException;

}
