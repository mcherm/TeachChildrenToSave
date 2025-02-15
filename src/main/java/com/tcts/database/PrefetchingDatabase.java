package com.tcts.database;


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
import com.tcts.exception.AllowedDateAlreadyInUseException;
import com.tcts.exception.AllowedTimeAlreadyInUseException;
import com.tcts.exception.BankHasVolunteersException;
import com.tcts.exception.EmailAlreadyInUseException;
import com.tcts.exception.EventAlreadyHasAVolunteerException;
import com.tcts.exception.InconsistentDatabaseException;
import com.tcts.exception.NoSuchAllowedDateException;
import com.tcts.exception.NoSuchAllowedTimeException;
import com.tcts.exception.NoSuchBankException;
import com.tcts.exception.NoSuchEventException;
import com.tcts.exception.NoSuchSchoolException;
import com.tcts.exception.NoSuchUserException;
import com.tcts.exception.TeacherHasEventsException;
import com.tcts.exception.VolunteerHasEventsException;
import com.tcts.formdata.AddAllowedDateFormData;
import com.tcts.formdata.AddAllowedTimeFormData;
import com.tcts.formdata.CreateBankFormData;
import com.tcts.formdata.CreateEventFormData;
import com.tcts.formdata.CreateSchoolFormData;
import com.tcts.formdata.EditBankFormData;
import com.tcts.formdata.EditPersonalDataFormData;
import com.tcts.formdata.EditSchoolFormData;
import com.tcts.formdata.EditVolunteerPersonalDataFormData;
import com.tcts.formdata.EventRegistrationFormData;
import com.tcts.formdata.NewBankAdminFormData;
import com.tcts.formdata.SetBankSpecificFieldLabelFormData;
import com.tcts.formdata.TeacherRegistrationFormData;
import com.tcts.formdata.VolunteerRegistrationFormData;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;


/**
 * This is a database implementation which simply wraps another implementation
 * but makes sure to prefetch two different pieces of data so they will be
 * available as quickly as possible. The two pieces of data are the site settings
 * (very frequently accessed and very rarely modified), and the list of available events
 * (frequently accessed and very slow to retrieve).
 */
public class PrefetchingDatabase implements DatabaseFacade {

    private final DatabaseFacade database;
    private final ExecutorService threadPool;
    private final Object siteSettingsLock; // always hold this lock when reading/writing prefetchedSiteSettings.
    private Future<Map<String,String>> prefetchedSiteSettings;
    private final Object eventListLock; // always hold this lock when reading/writing prefetchedEventList.
    private Future<List<Event>> prefetchedEventList;
    private final Object documentsLock;  //always hold this lock when reading/writing prefetched Documents
    private Future<SortedSet<Document>> prefetchedDocuments;


    /** Constructor requires you to provide an actual database. */
    public PrefetchingDatabase(DatabaseFacade database) {
        this.database = database;
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread result = new Thread(r, "PrefetchThread");
                result.setDaemon(true); // allow app to exit without this thread exiting
                return result;
            }
        };
        this.threadPool = Executors.newFixedThreadPool(1, threadFactory);
        this.siteSettingsLock = new Object();
        this.eventListLock = new Object();
        this.documentsLock = new Object();

        refreshSiteSettings();
        refreshEventList();
        refreshDocuments();
    }

    /**
     * Call this to invalidate the current site settings and fetch a new one on a background thread.
     */
    private void refreshSiteSettings() {
        synchronized (siteSettingsLock) {
            prefetchedSiteSettings = threadPool.submit(new Callable<Map<String,String>>() {
                @Override
                public Map<String,String> call() throws Exception {
                    return database.getSiteSettings();
                }
            });
        }
    }

    /**
     * Call this to invalidate the current documents and fetch a new one on a background thread.
     */
    private void refreshDocuments() {
        synchronized (documentsLock) {
            prefetchedDocuments = threadPool.submit(new Callable<SortedSet<Document>>() {
                @Override
                public SortedSet<Document> call() throws Exception {
                    return database.getDocuments();
                }
            });
        }
    }

    /**
     * Call this to invalidate the current event list and fetch a new one on a background thread.
     */
    private void refreshEventList() {
        synchronized (eventListLock) {
            prefetchedEventList = threadPool.submit(new Callable<List<Event>>() {
                @Override
                public List<Event> call() throws Exception {
                    return database.getAllAvailableEvents();
                }
            });
        }
    }


    @Override
    public int getFieldLength(DatabaseField field) {
        return database.getFieldLength(field);
    }

    @Override
    public User getUserById(String userId) throws SQLException, InconsistentDatabaseException {
        return database.getUserById(userId);
    }

    @Override
    public User getUserByEmail(String email) throws SQLException, InconsistentDatabaseException {
        return database.getUserByEmail(email);
    }

    @Override
    public List<Event> getEventsByTeacher(String teacherId) throws SQLException {
        return database.getEventsByTeacher(teacherId);
    }

    @Override
    public List<Event> getAllAvailableEvents() throws SQLException {
        synchronized (eventListLock) {
            try {
                return prefetchedEventList.get();
            } catch(InterruptedException err) {
                throw new RuntimeException(err);
            } catch(ExecutionException err) {
                Throwable cause = err.getCause();
                if (cause instanceof SQLException) {
                    throw (SQLException) cause;
                } else if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else {
                    throw new RuntimeException(cause);
                }
            }
        }
    }

    @Override
    public List<Event> getEventsByVolunteer(String volunteerId) throws SQLException {
        return database.getEventsByVolunteer(volunteerId);
    }

    @Override
    public List<Event> getEventsByVolunteerWithTeacherAndSchool (String volunteerId) throws SQLException{
        return database.getEventsByVolunteerWithTeacherAndSchool(volunteerId);
    }

    @Override
    public List<Volunteer> getVolunteersByBank(String bankId) throws SQLException {
        return database.getVolunteersByBank(bankId);
    }

    @Override
    public List<BankAdmin> getBankAdminsByBank(String bankId) throws SQLException {
        return database.getBankAdminsByBank(bankId);
    }

    @Override
    public Bank getBankById(String bankId) throws SQLException {
        return database.getBankById(bankId);
    }

    @Override
    public School getSchoolById(String schoolId) throws SQLException {
        return database.getSchoolById(schoolId);
    }

    @Override
    public List<School> getAllSchools() throws SQLException {
        return database.getAllSchools();
    }

    @Override
    public List<Bank> getAllBanks() throws SQLException {
        return database.getAllBanks();
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        return database.getAllUsers();
    }

    @Override
    public List<PrettyPrintingDate> getAllowedDates() throws SQLException {
        return database.getAllowedDates();
    }

    @Override
    public List<String> getAllowedTimes() throws SQLException {
        return database.getAllowedTimes();
    }

    @Override
    public List<BankAdmin> getBankAdmins() throws SQLException {
        return database.getBankAdmins();
    }

    @Override
    public List<Event> getAllEvents() throws SQLException, InconsistentDatabaseException {
        return database.getAllEvents();
    }

    @Override
    public Event getEventById(String eventId) throws SQLException {
        return database.getEventById(eventId);
    }

    @Override
    public SiteStatistics getSiteStatistics() throws SQLException {
        return database.getSiteStatistics();
    }

    @Override
    public void modifyUserPersonalFields(EditPersonalDataFormData formData) throws SQLException, EmailAlreadyInUseException {
        database.modifyUserPersonalFields(formData);
        refreshEventList();
    }

    @Override
    public void modifyVolunteerPersonalFields(EditVolunteerPersonalDataFormData formData) throws SQLException, EmailAlreadyInUseException, InconsistentDatabaseException {
        database.modifyVolunteerPersonalFields(formData);
    }

    @Override
    public void modifyTeacherSchool(String userId, String schoolId) throws SQLException, NoSuchSchoolException, NoSuchUserException {
        database.modifyTeacherSchool(userId, schoolId);
        refreshEventList();
    }


    @Override
    public Teacher insertNewTeacher(TeacherRegistrationFormData formData, String hashedPassword, String salt) throws SQLException, NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException {
        return database.insertNewTeacher(formData, hashedPassword, salt);
    }

    @Override
    public void insertEvent(String teacherId, CreateEventFormData formData) throws SQLException {
        database.insertEvent(teacherId, formData);
        refreshEventList();
    }

    @Override
    public Volunteer insertNewVolunteer(VolunteerRegistrationFormData formData, String hashedPassword, String salt) throws SQLException, NoSuchBankException, EmailAlreadyInUseException {
        return database.insertNewVolunteer(formData, hashedPassword, salt);
    }

    @Override
    public void insertNewAllowedDate(AddAllowedDateFormData formData) throws SQLException, AllowedDateAlreadyInUseException {
        database.insertNewAllowedDate(formData);
    }

    @Override
    public void insertNewAllowedTime(AddAllowedTimeFormData formData)
            throws SQLException, AllowedTimeAlreadyInUseException, NoSuchAllowedTimeException
    {
        database.insertNewAllowedTime(formData);
    }

    @Override
    public void volunteerForEvent(final String eventId, String volunteerId) throws SQLException, NoSuchEventException, EventAlreadyHasAVolunteerException {
        database.volunteerForEvent(eventId, volunteerId);
        refreshEventList();
    }

    @Override
    public void deleteSchool(String schoolId) throws SQLException, NoSuchSchoolException {
        database.deleteSchool(schoolId);
        refreshEventList();
    }

    @Override
    public void deleteBankandBankVolunteers(String bankId) throws SQLException, NoSuchBankException, BankHasVolunteersException, VolunteerHasEventsException {
        database.deleteBankandBankVolunteers(bankId);
        refreshEventList();
    }

    @Override
    public void deleteVolunteer(String volunteerId) throws SQLException, NoSuchUserException, VolunteerHasEventsException {
        database.deleteVolunteer(volunteerId);
    }

    @Override
    public void deleteTeacher(String teacherId) throws SQLException, NoSuchUserException, TeacherHasEventsException {
        database.deleteTeacher(teacherId);
    }

    @Override
    public void deleteEvent(String eventId) throws SQLException, NoSuchEventException {
        database.deleteEvent(eventId);
        refreshEventList();
    }

    @Override
    public void modifySchool(EditSchoolFormData school) throws SQLException, NoSuchSchoolException {
        database.modifySchool(school);
        refreshEventList();
    }

    @Override
    public void insertNewBankAndAdmin(CreateBankFormData formData) throws SQLException, EmailAlreadyInUseException {
        database.insertNewBankAndAdmin(formData);
    }

    @Override
    public void insertNewBankAdmin(NewBankAdminFormData formData) throws SQLException, EmailAlreadyInUseException {
        database.insertNewBankAdmin(formData);
    }

    @Override
    public void modifyBank(EditBankFormData formData) throws SQLException, EmailAlreadyInUseException, NoSuchBankException {
        database.modifyBank(formData);
        refreshEventList();
    }

    @Override
    public void setUserType(String userId, UserType userType) throws SQLException {
        database.setUserType(userId, userType);
    }

    @Override
    public void setBankSpecificFieldLabel(SetBankSpecificFieldLabelFormData formData) throws SQLException, NoSuchBankException {
        database.setBankSpecificFieldLabel(formData);
    }

    @Override
    public void insertNewSchool(CreateSchoolFormData school) throws SQLException {
        database.insertNewSchool(school);
    }

    @Override
    public void modifyEvent(EventRegistrationFormData formData) throws SQLException, NoSuchEventException {
        database.modifyEvent(formData);
        refreshEventList();
    }

    @Override
    public void updateUserCredential(String userId, String hashedPassword, String salt) throws SQLException {
        database.updateUserCredential(userId, hashedPassword, salt);
    }

    @Override
    public void updateResetPasswordToken(String userId, String resetPasswordToken) throws SQLException {
        database.updateResetPasswordToken(userId, resetPasswordToken);
    }

    @Override
    public void updateApprovalStatusById(String volunteerId, ApprovalStatus approvalStatus) throws SQLException {
        database.updateApprovalStatusById(volunteerId, approvalStatus);
    }

    @Override
    public void deleteAllowedTime(String time) throws SQLException, NoSuchAllowedTimeException {
        database.deleteAllowedTime(time);
    }

    @Override
    public void deleteAllowedDate(PrettyPrintingDate date) throws SQLException, NoSuchAllowedDateException {
        database.deleteAllowedDate(date);
    }

    @Override
    public List<Volunteer> getVolunteersWithBankData() throws SQLException {
        return database.getVolunteersWithBankData();
    }

    @Override
    public List<Teacher> getTeachersWithSchoolData() throws SQLException {
        return database.getTeachersWithSchoolData();
    }

    @Override
    public List<Teacher> getTeachersBySchool(String schoolId) throws SQLException {
        return database.getTeachersBySchool(schoolId);
    }

    @Override
    public List<Teacher> getMatchedTeachers() throws SQLException {
        return database.getMatchedTeachers();
    }

    @Override
    public List<Teacher> getUnMatchedTeachers() throws SQLException {
        return database.getUnMatchedTeachers();
    }

    @Override
    public List<Volunteer> getMatchedVolunteers() throws SQLException {
        return database.getMatchedVolunteers();
    }

    @Override
    public List<Volunteer> getUnMatchedVolunteers() throws SQLException {
        return database.getUnMatchedVolunteers();
    }

    @Override
    public Map<String, String> getSiteSettings() throws SQLException {
        synchronized (siteSettingsLock) {
            try {
                return prefetchedSiteSettings.get();
            } catch(InterruptedException err) {
                throw new RuntimeException(err);
            } catch(ExecutionException err) {
                Throwable cause = err.getCause();
                if (cause instanceof SQLException) {
                    throw (SQLException) cause;
                } else if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else {
                    throw new RuntimeException(cause);
                }
            }
        }
    }

    @Override
    public void modifySiteSetting(String settingName, String settingValue) throws SQLException {
        database.modifySiteSetting(settingName, settingValue);
        refreshSiteSettings();
    }


    //Overrides the getDocuments function to get it from the prefetched List of documents
    @Override
    public SortedSet<Document> getDocuments() throws SQLException{
        synchronized (documentsLock) {
            try {
                return prefetchedDocuments.get();
            } catch(InterruptedException err) {
                throw new RuntimeException(err);
            } catch(ExecutionException err) {
                Throwable cause = err.getCause();
                if (cause instanceof SQLException) {
                    throw (SQLException) cause;
                } else if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else {
                    throw new RuntimeException(cause);
                }
            }
        }
    }

    @Override
    public void createOrModifyDocument(Document document) throws SQLException{
        database.createOrModifyDocument(document);
        refreshDocuments();
    }

    @Override
    public void deleteDocument(String documentName) throws SQLException{
        database.deleteDocument(documentName);
        refreshDocuments();
    }

}
