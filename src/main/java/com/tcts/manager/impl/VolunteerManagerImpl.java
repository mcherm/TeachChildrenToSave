package com.tcts.manager.impl;

import com.tcts.database.impl.AWSConnectionManagerImpl;
import com.tcts.manager.VolunteerManager;
import com.tcts.model.Volunteer;

public class VolunteerManagerImpl implements VolunteerManager{
	
 public boolean addVolunteer(Volunteer volunteer) {
	 int result;
	 AWSConnectionManagerImpl connectionManager = new AWSConnectionManagerImpl();
	 result = connectionManager.addVolunteer(volunteer);
	 if (result == 0)
		 return true;
	 else return false;
 }
}
