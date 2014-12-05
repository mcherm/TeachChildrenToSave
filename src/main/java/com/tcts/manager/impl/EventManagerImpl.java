package com.tcts.manager.impl;

import java.sql.SQLException;

import com.tcts.dao.EventDAO;
import com.tcts.dao.impl.EventDAOImpl;
import com.tcts.manager.EventManager;
import com.tcts.model.Event;

public class EventManagerImpl implements EventManager{
	
 public boolean addEvent(Event event) {
	 EventDAO eventDAO = new EventDAOImpl();
	 try {
		return eventDAO.addEvent(event);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return false;
	}
	}
 
 public Event getEvent(String eventId) {
	 EventDAO eventDAO= new EventDAOImpl();
	 try {
		return eventDAO.getEvent(eventId);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
 }
}
