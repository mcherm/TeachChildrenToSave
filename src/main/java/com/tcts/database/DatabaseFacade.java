package com.tcts.database;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.tcts.common.PrettyPrintingDate;
import com.tcts.datamodel.ApprovalStatus;
import com.tcts.datamodel.Bank;
import com.tcts.datamodel.BankAdmin;
import com.tcts.datamodel.Document;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.School;
import com.tcts.datamodel.SiteStatistics;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.User;
import com.tcts.datamodel.UserType;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.AllowedValueAlreadyInUseException;
import com.tcts.exception.BankHasVolunteersException;
import com.tcts.exception.EmailAlreadyInUseException;
import com.tcts.exception.EventAlreadyHasAVolunteerException;
import com.tcts.exception.InconsistentDatabaseException;
import com.tcts.exception.NoSuchAllowedValueException;
import com.tcts.exception.NoSuchBankException;
import com.tcts.exception.NoSuchEventException;
import com.tcts.exception.NoSuchSchoolException;
import com.tcts.exception.NoSuchUserException;
import com.tcts.exception.TeacherHasEventsException;
import com.tcts.exception.VolunteerHasEventsException;
import com.tcts.formdata.AddAllowedDateFormData;
import com.tcts.formdata.CreateBankFormData;
import com.tcts.formdata.CreateEventFormData;
import com.tcts.formdata.CreateSchoolFormData;
import com.tcts.formdata.EditBankFormData;
import com.tcts.formdata.EditEventFormData;
import com.tcts.formdata.EditPersonalDataFormData;
import com.tcts.formdata.EditSchoolFormData;
import com.tcts.formdata.EditVolunteerPersonalDataFormData;
import com.tcts.formdata.EventRegistrationFormData;
import com.tcts.formdata.NewBankAdminFormData;
import com.tcts.formdata.SetBankSpecificFieldLabelFormData;
import com.tcts.formdata.TeacherRegistrationFormData;
import com.tcts.formdata.VolunteerRegistrationFormData;

/**
 * Methods for accessing the database. It should probably be refactored somehow.
 */
public interface DatabaseFacade {


    /**
     * Passed the name of a table and field, this returns the maximum allowed length
     * for that field.
     */
    public int getFieldLength(DatabaseField field);

    /**
     * Return the user with this userId, or null if there is none. The actual class
     * returned will be an appropriate subclass of User.
     */
    public User getUserById(String userId) throws InconsistentDatabaseException;

    /** Return the user with this email, or null if there is none. Whether the emails are treated as case
     * insensitive or not is determined by the database implementation .  The Dynamo Db implementation
     * only allows one user per email regardless of case, and will ignore case of the email when
     * doing the user lookup
     */
    public User getUserByEmail(String email) throws InconsistentDatabaseException;

    /**
     * Modify certain fields of a User. The user with the userId given in formData
     * will be edited to set their email, firstName, lastName, and phoneNumber to
     * the values given in the other fields of formData. Other properties of a user
     * (such as password and userType) are not modified.
     */
    public void modifyUserPersonalFields(EditPersonalDataFormData formData)
            throws EmailAlreadyInUseException, InconsistentDatabaseException;


    /**
     * Modify normal user fields of a volunteer, and ALSO the bank specific data field.
     */
    public void modifyVolunteerPersonalFields(EditVolunteerPersonalDataFormData formData)
            throws EmailAlreadyInUseException, InconsistentDatabaseException;

    /**
     *   Modifies the schoolId of a teacher in the User table
     * @param userId userId of user to be modified
     * @param organizationId new organzation Id
     */
    void modifyTeacherSchool(String userId, String organizationId)
            throws NoSuchSchoolException, NoSuchUserException;

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
            throws NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException;


    /**
     *   Returns a list of events created by the passed in teacher.  This list does not have the linked fields filled in.
     * @param teacherId
     * @return A sorted list of Events created by this teacher.  Each event does not have the linked bank and volunteer.
     */
    public List<Event> getEventsByTeacher(String teacherId);

    /**
     * Return the list of all events that have null for a volunteer.
     * <p>
     * The performance of this call is key to the performance of the system as a whole,
     * so it has been special-cased a bit. This version promises to return Event objects
     * in which the linkedTeacher has been set, and those teachers will have the
     * linkedSchool set, so that a single call will retrieve all the data needed to
     * display data.
     */
    public List<Event> getAllAvailableEvents();

    /**
     * Returns a list of events that the passed in volunteer is volunteering for.  Does not fill in the linked fields bank and school.
     * @param volunteerId
     * @return
     */
    public List<Event> getEventsByVolunteer(String volunteerId);

    /**Return the list of events that have a particular volunteer.  Fills in the linked teacher and school object */
    public List<Event> getEventsByVolunteerWithTeacherAndSchool (String volunteerId);


    /**
     * Insert a new Teacher in the database, and return it. Expects that all
     * fields have been checked for containing valid values.
     */
    public void insertEvent(String teacherId, CreateEventFormData formData);
    
    /**
     * Register the specified volunteer as volunteering for the specified event, or unregister the
     * current volunteer if null is passed for volunteerId. If the event does not
     * exist, a NoSuchEventException is thrown. Otherwise, if the volunteerId is not null but
     * the event in question already has a volunteer then an EventAlreadyHasAVolunteerException
     * is thrown.
     * 
     * @param eventId the event to be modified
     * @param volunteerId the volunteer to register for the event, or null to deregister the
     *                    current volunteer (if any).
     * @throws NoSuchEventException if the eventId does not represent a valid event
     * @throws EventAlreadyHasAVolunteerException if volunteerId is non-null but the event
     *     already has a volunteer.
     */
    
    public void volunteerForEvent(String eventId, String volunteerId) throws NoSuchEventException, EventAlreadyHasAVolunteerException;

    /** Return the list of volunteers that have a particular bank. Includes BankAdmins at that bank. */
    public List<Volunteer> getVolunteersByBank(String bankId);

    /** Returns the list of all Bank Admins for the given bank. */
    public List<BankAdmin> getBankAdminsByBank(String bankId);

    /**
     * Insert a new Volunteer in the database, and return it. Expects that all
     * fields have been checked for containing valid values. Will throw an
     * exception if the email is not unique or if the bank is not found.
     * @throws EmailAlreadyInUseException
     * @throws NoSuchBankException
     */
    public Volunteer insertNewVolunteer(VolunteerRegistrationFormData formData, String hashedPassword, String salt)
            throws NoSuchBankException, EmailAlreadyInUseException;

    /** Return the bank with this bankId, or null if there is none. */
    public Bank getBankById(String bankId);

    /** Return the school with this schoolId, or null if there is none. */
    public School getSchoolById(String schoolId);

    /** Returns the full list of all schools. */
    public List<School> getAllSchools();
    
    /** Returns the full list of all banks. */
    public List<Bank> getAllBanks();

    /** Returns the full list of users (with only user data). */
    public List<User> getAllUsers();

    /** Returns the allowed dates. */
    public List<PrettyPrintingDate> getAllowedDates();

    /** Returns the allowed times. */
    public List<String> getAllowedTimes();

    /** Returns the allowed grades. */
    public List<String> getAllowedGrades();

    /** Returns the allowed delivery methods. */
    public List<String> getAllowedDeliveryMethods();


    public void deleteSchool(String schoolId) throws NoSuchSchoolException;

    /**
     * This will delete a bank, the bank admin AND ALL Volunteers belonging to that bank. It will fail if
     * any volunteer from that bank is currently signed up for any classes.
     */
    public void deleteBankAndBankVolunteers(String bankId) throws NoSuchBankException, BankHasVolunteersException, VolunteerHasEventsException;
    
    /**
     * Delete volunteer from database. If the volunteer has signed up for any events,
     * it will throw an exception.
     */
    public void deleteVolunteer(String volunteerId) throws NoSuchUserException, VolunteerHasEventsException;

    /**
     * Deletes a teacher from the database. If the teacher has any events then it will
     * throw an exception.
     */
    public void deleteTeacher(String teacherId) throws NoSuchUserException, TeacherHasEventsException;

    /**
     * This deletes the indicated event, or throws NoSuchEventException if it does not exist.
     */
    public void deleteEvent(String eventId) throws NoSuchEventException;

    /**
     * Return the list of all events.
     * <p>
     * To make things faster, this has been special-cased a bit. This call promises
     * to return Event objects in which the linkedTeacher has been set and those
     * teachers have the linkedSchool set; also if the volunteerId for the event is
     * not null then the linkedVolunteer will be set it will have the linkedBank
     * set. This way a single call returns all the data that is needed.
     */
    public List<Event> getAllEvents() throws InconsistentDatabaseException;

    /** Returns the indicated event object, or null if it does not exist. */
    public Event getEventById(String eventId);

    // FIXME: All this stuff shouldn't throw InconsistentDatabaseException.
    
    /**
     * Modifies fields of an existing school. 
     */

	public void modifySchool(EditSchoolFormData school) throws NoSuchSchoolException;

    /** Inserts a new bank and the corresponding bank admin. Password is set to null. */
	void insertNewBankAndAdmin(CreateBankFormData formData) throws EmailAlreadyInUseException;

    /** Insert a new Bank Admin for a bank. Password is set to null. */
    void insertNewBankAdmin(NewBankAdminFormData formData) throws EmailAlreadyInUseException;

    /**
     * Modifies fields of an existing bank.
     */
    void modifyBank(EditBankFormData formData)
            throws NoSuchBankException;

    /**
     * Sets the user type for a user. This can be safely used to convert someone between
     * being a Volunteer and a BankAdmin, but other changes may not be valid if the
     * user is in certain states (eg: changing a teacher with a class to anything else).
     */
    void setUserType(String userId, UserType userType);

    /**
     * Sets a particular field on a bank.
     */
    public void setBankSpecificFieldLabel(SetBankSpecificFieldLabelFormData formData)
            throws NoSuchBankException;

    /**
     * Insert new school in database
     * @param school
     */
    public void insertNewSchool(CreateSchoolFormData school);

    /** Inserts a new allowed date. */
    public void insertNewAllowedDate(AddAllowedDateFormData formData) throws AllowedValueAlreadyInUseException;

    /**
     * Inserts a new allowed time. The name of the time must be unique, and is contained in the
     * allowedTime field. The timeToInsertBefore field must either
     * contain an empty string (in which case the sort_order will place this after the last
     * existing time) or the name of an existing time (in which case that and all subsequent
     * sort_orders will be incremented to make space for this new one).
     *
     * @param newAllowedTime the new time to add
     * @param timeToInsertBefore the time it should be inserted before, OR "" to place it at the end
     * @throws AllowedValueAlreadyInUseException if an existing time has the same string as this one
     * @throws NoSuchAllowedValueException if the timeToInsertBefore field has something that is not
     *   either an existing time or "".
     */
    public void insertNewAllowedTime(String newAllowedTime, String timeToInsertBefore)
            throws AllowedValueAlreadyInUseException, NoSuchAllowedValueException;

    public void insertNewAllowedGrade(String newAllowedGrade, String gradeToInsertBefore)
        throws AllowedValueAlreadyInUseException, NoSuchAllowedValueException;

    public void insertNewAllowedDeliveryMethod(String newAllowedDeliveryMethod, String deliveryMethodToInsertBefore)
            throws AllowedValueAlreadyInUseException, NoSuchAllowedValueException;

    /** Add or remove a volunteer from an event. */
    public void modifyEventRegistration(EventRegistrationFormData formData) throws NoSuchEventException;

    /** Modify the fields of an event. */
    public void modifyEvent(EditEventFormData formData) throws NoSuchEventException;


	/** Changes the password (and salt) for an existing user. */
	void updateUserCredential(String userId, String hashedPassword, String salt);
	
	/** This will update reset password link token in db. */
	void updateResetPasswordToken(String userId, String resetPasswordToken);

    /** Update the userStatus field of a specific volunteer. */
    public void updateApprovalStatusById(String volunteerId, ApprovalStatus approvalStatus);

	/**
     * Delete allowed time. Will remove this particular allowed time from the list of allowed times if
     * it is there. If the given time is not in the list this will throw NoSuchAllowedTimeException.
     */
    public void deleteAllowedTime(String time) throws NoSuchAllowedValueException;

    /**
     * Delete allowed grade. Will remove this particular allowed grade from the list of allowed grades if
     * it is there. If the given grade is not in the list this will throw NoSuchAllowedGradeException.
     */
    public void deleteAllowedGrade(String grade) throws NoSuchAllowedValueException;

    /**
     * Delete allowed delivery method. Will remove this particular allowed delivery method from the list of
     * allowed delivery methods if it is there. If the given delivery method is not in the list this will throw
     * NoSuchAllowedDeliveryMethodException.
     */
    public void deleteAllowedDeliveryMethod(String deliveryMethod) throws NoSuchAllowedValueException;

    /**
     *		Delete allowed date
     */
	public void deleteAllowedDate(PrettyPrintingDate date) throws NoSuchAllowedValueException;

    /** Retrieves a bunch of basic statistics about the database. */
    public SiteStatistics getSiteStatistics();

    /**
     * Get list of teachers with their school details.
     * 
     * @return List<Teacher>
     */
	public List<Teacher> getTeachersWithSchoolData();

    /**
     *  Get list of teachers from the specified school
     * @param schoolId
     * @return
     */
    public List<Teacher> getTeachersBySchool(String schoolId);


    /**
     * Get list of volunteers with their bank details.
     *
     * @return List<Volunteer>
     */
	public List<Volunteer> getVolunteersWithBankData();
	
	/**
	 * Get list of all the teachers who has at least one class which has a volunteer.  If there are none returns an empty list.
	 * @return
	 */
	List<Teacher> getMatchedTeachers();
	
	/**
	 * Get list of all the teachers who has at least one class which dooesn't have a volunteer.  If there are no teachers like this returns an empty list.
     * Implementation are permitted to return multiple instances of the same teacher
	 * @return
	 */

	List<Teacher> getUnMatchedTeachers();
	
	/**
	 * Get list of all the volunteers who have signed-up for classes.  If there are no volunteers returns an empty list.
     * Implementations are permitted to return multiple instances of the same teacher
	 * @return List<Volunteer>
	 */

	List<Volunteer> getMatchedVolunteers();
	
	/**
	 * Get list of all the volunteers who are not signed up for classes.  If there are no volunteers returns an empty list.
     * Implementations are permitted to return multiple instances of the same volunteer
	 * @return
	 */

	List<Volunteer> getUnMatchedVolunteers();

    /**
     * Returns a list of all the bank admins.  If there are no bankadmins returns an empty list.
     * Implementations are permitted to return multiple instances of the same volunteer
     *
     * @return
     */
    List<BankAdmin> getBankAdmins();

    /**
     * Retrieves the full collection of all site settings.
     * @return
     */
    Map<String, String> getSiteSettings();

    /**
     * This modifies a specific site setting. If the existing setting name already
     * exists, then this modifies it; if it does not exist then this creates a
     * new setting.
     *
     * @param settingName the new site setting to modify or create
     * @param settingName the new value for the site setting
     */
    void modifySiteSetting(String settingName, String settingValue);

    /**
     * Retrieves a Set of the documents in the database
     * @return A Sorted Set of the documents in the database
     */
    public SortedSet<Document> getDocuments();

    /**
     * This creates or modifies the passed in document.
     * @param document - document to created or modified.
     */
    public void createOrModifyDocument (Document document);

    /**
     * Deletes the passed in document, the document name is the unique key.
     *
     * @param documentName name of document to delete
     */
    public void deleteDocument(String documentName);
}


