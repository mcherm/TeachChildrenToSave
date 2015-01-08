package com.tcts.database;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.tcts.datamodel.Bank;
import com.tcts.datamodel.BankAdmin;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.School;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.User;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.EmailAlreadyInUseException;
import com.tcts.exception.InconsistentDatabaseException;
import com.tcts.exception.NoSuchAllowedDateException;
import com.tcts.exception.NoSuchBankException;
import com.tcts.exception.NoSuchEventException;
import com.tcts.exception.NoSuchSchoolException;
import com.tcts.exception.NoSuchAllowedTimeException;
import com.tcts.exception.NoSuchUserException;
import com.tcts.formdata.CreateBankFormData;
import com.tcts.formdata.CreateEventFormData;
import com.tcts.formdata.CreateSchoolFormData;
import com.tcts.formdata.EditAllowedDateTimeData;
import com.tcts.formdata.EditBankFormData;
import com.tcts.formdata.EditPersonalDataFormData;
import com.tcts.formdata.EditSchoolFormData;
import com.tcts.formdata.EventRegistrationFormData;
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
            throws SQLException, NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException;

    /** Return the list of events that have a particular teacher. */
    public List<Event> getEventsByTeacher(String teacherId) throws SQLException;

    /**
     * Return the list of all events that have null for a volunteer.
     * <p>
     * The performance of this call is key to the performance of the system as a whole,
     * so it has been special-cased a bit. This version promises to return Event objects
     * in which the linkedTeacher has been set, and those teachers will have the
     * linkedSchool set, so that a single call will retrieve all the data needed to
     * display data.
     */
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

    /** Returns the Bank Admin for the given bank, or null if that bank has no Bank Admin. */
    public BankAdmin getBankAdminByBank(String bankId)
            throws SQLException;

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
    
    /** Returns the full list of all banks. */
    public List<Bank> getAllBanks() throws SQLException;

    /** Returns the allowed dates. */
    public List<Date> getAllowedDates() throws SQLException;

    /** Returns the allowed times. */
    public List<String> getAllowedTimes() throws SQLException;
    
    public List<? super User> getUsersByType(String userType) throws SQLException, InconsistentDatabaseException;

    public boolean deleteSchool(String schoolId) throws SQLException, NoSuchSchoolException;

    /**
     * This will delete a bank. It ALSO deletes the bank admin AND ALL Volunteers belonging
     * to that bank.
     */
    public void deleteBank(String bankId) throws SQLException, NoSuchBankException;
    
     
    public boolean deleteVolunteer(String volunteerId) throws SQLException, NoSuchUserException;

    /**
     * This deletes the indicated event, or throws NoSuchEventException if it does not exist.
     */
    public void deleteEvent(String eventId) throws SQLException, NoSuchEventException;

    /**
     * Return the list of all events.
     * <p>
     * To make things faster, this has been special-cased a bit. This call promises
     * to return Event objects in which the linkedTeacher has been set and those
     * teachers have the linkedSchool set; also if the volunteerId for the event is
     * not null then the linkedVolunteer will be set it will have the linkedBank
     * set. This way a single call returns all the data that is needed.
     */
    public List<Event> getAllEvents() throws SQLException, InconsistentDatabaseException;

    /** Returns the indicated event object, or null if it does not exist. */
    public Event getEventById(String eventId) throws SQLException;

    // FIXME: All this stuff shouldn't throw InconsistentDatabaseException.
    
    /**
     * Modifies fields of an existing school. 
     */

	public void modifySchool(EditSchoolFormData school) throws SQLException, NoSuchSchoolException;

    /** Inserts a new bank and the corresponding bank admin. Password is set to null. */
	void insertNewBankAndAdmin(CreateBankFormData formData) throws SQLException, EmailAlreadyInUseException;

    /**
     * Modifies fields of an existing bank. Either modifies the existing bank admin
     * (if there is one) or creates a new Bank Admin (if there wasn't one).
     */
    public void modifyBankAndBankAdmin(EditBankFormData formData)
            throws SQLException, EmailAlreadyInUseException, NoSuchBankException;

    public void insertNewSchool(CreateSchoolFormData school) throws SQLException;

	public void modifyEvent(EventRegistrationFormData formData) throws SQLException, NoSuchEventException;

    /** This can be used to upate certain fields of a Volunteer. */
	User updateVolunteer(Volunteer volunteer) throws SQLException,
			EmailAlreadyInUseException;
	
	/** Changes the password (and salt) for an existing user. */
	void updateUserCredential(String userId, String hashedPassword, String salt) throws SQLException;
	
	/** This will update reset password link token in db. */
	
	void updateResetPasswordToken(String userId, String resetPasswordToken) throws SQLException;
	
	/** Update allowed time **/
	
	public void modifyAllowedTime(EditAllowedDateTimeData time) throws SQLException, NoSuchAllowedTimeException;
	
	/**
     *		Delete allowed time
     */
    public void deleteAllowedTime(String time) throws SQLException, NoSuchAllowedTimeException;

    /**
     *		Delete allowed date
     */
	public void deleteAllowedDate(String date) throws SQLException, NoSuchAllowedDateException;

	/** Update allowed date **/
	public void modifyAllowedDate(EditAllowedDateTimeData date) throws SQLException, NoSuchAllowedDateException;
	
}
