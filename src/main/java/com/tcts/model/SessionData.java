package com.tcts.model;

import com.tcts.model2.User;

/**
 * An instance of this class will be stored in the session. It contains all
 * of the data that will potentially be passed around while navigating the
 * the site.
 */
public class SessionData {
	private Volunteer volunteer;
	private boolean isAuthenticated;
    private User user;

	public Volunteer getVolunteer() {
		return volunteer;
	}
	public void setVolunteer(Volunteer volunteer) {
		this.volunteer = volunteer;
	}
	public boolean isAuthenticated() {
		return isAuthenticated;
	}
	public void setAuthenticated(boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
