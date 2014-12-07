package com.tcts.dao2;

import com.tcts.model.TeacherRegistrationFormData;
import com.tcts.model2.Bank;
import com.tcts.model2.Event;
import com.tcts.model2.School;
import com.tcts.model2.Teacher;
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

    /** Return the user with this login, or null if there is none. */
    public User getUserByLogin(String login) throws SQLException, InconsistentDatabaseException;

    /** Return the list of events that have a particular teacher. */
    public List<Event> getEventsByTeacher(String teacherId) throws SQLException;

    /** Return the list of events that have a particular volunteer. */
    public List<Event> getEventsByVolunteer(String volunteerId) throws SQLException;

    /** Return the list of volunteers that have a particular bank. */
    public List<Volunteer> getVolunteersByBank(String bankId) throws SQLException;

    /** Return the bank with this bankId, or null if there is none. */
    public Bank getBankById(String bankId) throws SQLException;

    /** Return the school with this schoolId, or null if there is none. */
    public School getSchoolById(String schoolId) throws SQLException;

    // FIXME: Will throw other things too.
    /**
     * Insert a new Teacher in the database, and return it. Expects that all
     * fields have been checked for containing valid values. Will throw an
     * exception if the login is not unique or if the school is not found.
     */
    public Teacher insertNewTeacher(TeacherRegistrationFormData formData)
            throws SQLException, NoSuchSchoolException, LoginAlreadyInUseException;
}
