package com.tcts.model;

import com.tcts.model2.User;

import javax.servlet.http.HttpSession;

/**
 * An instance of this class will be stored in the session. It contains all
 * of the data that will potentially be passed around while navigating the
 * the site.
 */
public class SessionData {
    private final static String sessionKey = "sessionData";

    /**
     * If there is a logged-in user session, this retrieves it from the
     * HttpSession object. If not, then it raises a "NotLoggedInException".
     * Use this at the top of any controller method that expects the user
     * to be already logged in.
     */
    public static SessionData fromSession(HttpSession session) {
        Object sessionData = session.getAttribute(sessionKey);
        if (sessionData == null) {
            throw new NotLoggedInException();
        } else {
            return (SessionData) sessionData;
        }
    }

    /**
     * This is used to put a SessionData into the session which should be
     * done after a user has successfully logged in.
     */
    public static SessionData beginNewSession(HttpSession session) {
        SessionData sessionData = new SessionData();
        session.setAttribute(sessionKey, sessionData);
        return sessionData;
    }


	private Volunteer volunteer;
	private boolean isAuthenticated;
    private User user;

    /**
     * Constructor is private; use static methods beginNewSession() and/or
     * fromSession() to obtain an instance.
     */
    private SessionData() {
    }

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
