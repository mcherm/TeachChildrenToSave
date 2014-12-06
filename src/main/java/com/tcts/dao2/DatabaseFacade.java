package com.tcts.dao2;

import com.tcts.model2.Event;
import com.tcts.model2.User;
import com.tcts.model2.Volunteer;

import java.sql.SQLException;
import java.util.List;

/**
 * Methods for accessing the database. It should probably be refactored somehow.
 */
public interface DatabaseFacade {
    /**
     * Return the user with this userId, or null if there is none. The actual class
     * returned will be an appropriate subclass of User.
     */
    public User getUserById(String userId) throws SQLException, InconsistentDatabaseException;

    /** Return the list of events that have a particular teacher. */
    public List<Event> getEventsByTeacher(String teacherId) throws SQLException;

    /** Return the list of events that have a particular volunteer. */
    public List<Event> getEventsByVolunteer(String volunteerId) throws SQLException;

    /** Return the list of volunteers that have a particular bank. */
    public List<Volunteer> getVolunteersByBank(String bankId) throws SQLException;
}
