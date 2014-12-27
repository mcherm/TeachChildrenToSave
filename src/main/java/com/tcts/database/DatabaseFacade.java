package com.tcts.database;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.tcts.datamodel.Bank;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.School;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.User;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.EmailAlreadyInUseException;
import com.tcts.exception.InconsistentDatabaseException;
import com.tcts.exception.NoSuchBankException;
import com.tcts.exception.NoSuchEventException;
import com.tcts.exception.NoSuchSchoolException;
import com.tcts.formdata.CreateEventFormData;
import com.tcts.formdata.EditPersonalDataFormData;
import com.tcts.formdata.TeacherRegistrationFormData;
import com.tcts.formdata.VolunteerRegistrationFormData;

/**
 * Methods for accessing the database. It should probably be refactored somehow.
 */
public interface DatabaseFacade {
    /**
     * Return the user with this userId, or null if there is none. The actual class
     * returned will be an appropriate subclass of User.
     */
    public User getUserById(String userId) throws SQLException, InconsistentDatabaseException;

    /** Return the user with this email, or null if there is none. */
    public User getUserByEmail(String email) throws SQLException, InconsistentDatabaseException;

    /**
     * Modify certain fields of a User. The user with the userId given will be edited
     * to set their email, firstName, lastName, and phoneNumber to the values given
     * in this data structure. Other fields are not modified. The newly modified
     * user is returned.
     */
    public User modifyUserPersonalFields(String userId, EditPersonalDataFormData formData)
            throws SQLException, EmailAlreadyInUseException;

    /**
     * Insert a new Teacher in the database, and return it. Expects that all
     * fields have been checked for containing valid values. Will throw an
     * exception if the email is not unique or if the school is not found.
     * @throws NoSuchSchoolException 
     * @throws com.tcts.exception.EmailAlreadyInUseException
     * @throws UnsupportedEncodingException 
     * @throws NoSuchAlgorithmException 
     */
    public Teacher insertNewTeacher(TeacherRegistrationFormData formData, String hashedPassword, String salt)
            throws SQLException, NoSuchSchoolException, EmailAlreadyInUseException, NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException;

    /** Return the list of events that have a particular teacher. */
    public List<Event> getEventsByTeacher(String teacherId) throws SQLException;
    
    /** Return the list of all events that have null for a volunteer. */
    public List<Event> getAllAvailableEvents() throws SQLException;

    /** Return the list of events that have a particular volunteer. */
    public List<Event> getEventsByVolunteer(String volunteerId) throws SQLException;

    /**
     * Insert a new Teacher in the database, and return it. Expects that all
     * fields have been checked for containing valid values.
     */
    public void insertEvent(String teacherId, CreateEventFormData formData) throws SQLException;
    
    public void volunteerForEvent(String eventId, String volunteerId) throws SQLException, NoSuchEventException;

    /** Return the list of volunteers that have a particular bank. */
    public List<Volunteer> getVolunteersByBank(String bankId) throws SQLException;
    
    /**
     * Insert a new Volunteer in the database, and return it. Expects that all
     * fields have been checked for containing valid values. Will throw an
     * exception if the email is not unique or if the bank is not found.
     * @throws EmailAlreadyInUseException
     * @throws NoSuchBankException
     */
    public Volunteer insertNewVolunteer(VolunteerRegistrationFormData formData, String hashedPassword, String salt)
            throws SQLException, NoSuchBankException, EmailAlreadyInUseException;

    /** Return the bank with this bankId, or null if there is none. */
    public Bank getBankById(String bankId) throws SQLException;

    /** Return the school with this schoolId, or null if there is none. */
    public School getSchoolById(String schoolId) throws SQLException;

    /** Returns the full list of all schools. */
    public List<School> getAllSchools() throws SQLException;
    
    /** Returns the full list of all schools. */
    public List<Bank> getAllBanks() throws SQLException;

    /** Returns the allowed dates. */
    public List<Date> getAllowedDates() throws SQLException;

    /** Returns the allowed times. */
    public List<String> getAllowedTimes() throws SQLException;
    
    public List<? super User> getUsersByType(String userType) throws SQLException, InconsistentDatabaseException;
    
    /** Return the bank list  or null if there is none. */
    public List<Bank> getBankList() throws SQLException;
    
    public boolean deleteSchool(String schoolId) throws SQLException, InconsistentDatabaseException;
    
    public boolean deleteBank(String bankId) throws SQLException, InconsistentDatabaseException;
    
     
    public boolean deleteVolunteer(String volunteerId) throws SQLException, InconsistentDatabaseException;
    
    public boolean deleteEvent(String eventId) throws SQLException, InconsistentDatabaseException;
    
    public List<Event> getEvents() throws SQLException, InconsistentDatabaseException;
    
    public Event getEventById(String eventId) throws SQLException;

    // FIXME: All this stuff shouldn't throw InconsistentDatabaseException.

	School updateSchool(School school) throws SQLException,
			InconsistentDatabaseException;

	Bank updateBank(Bank bank) throws SQLException, EmailAlreadyInUseException;
	
	void insertBank(Bank bank) throws SQLException, EmailAlreadyInUseException;
	
	void insertSchool(School school) throws SQLException, InconsistentDatabaseException;

	Event updateEvent(Event event) throws SQLException, InconsistentDatabaseException;

    /** This can be used to upate certain fields of a Volunteer. */
	User updateVolunteer(Volunteer volunteer) throws SQLException,
			EmailAlreadyInUseException;
	
	public List<? super User> getAllUsers() throws SQLException, InconsistentDatabaseException;

	void updateUserCredential(String userId, String hashedPassword, String salt) throws SQLException,
			InconsistentDatabaseException;
}
