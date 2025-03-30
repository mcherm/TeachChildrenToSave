package com.tcts.database;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.tcts.common.CachedList;
import com.tcts.common.CachedValue;
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


/**
 * This is a database implementation which is intended to wrap a real database
 * implementation. It caches some of the most frequent calls, invalidating the
 * caches when the data has become invalid.
 */
public class CachingDatabase implements DatabaseFacade {

    private DatabaseFacade database;

    /** Refresh values every 4 hours even if we think they're still accurate. */
    private static long REFRESH_IN_MILLIS = 4 * 60 * 60 * 1000;

    private final CachedValue<List<PrettyPrintingDate>,SQLException> allowedDates =
            new CachedValue<List<PrettyPrintingDate>,SQLException>(REFRESH_IN_MILLIS) {
                @Override
                public List<PrettyPrintingDate> generateValue() throws SQLException {
                    return Collections.unmodifiableList(database.getAllowedDates());
                }
            };

    private final CachedValue<List<String>,SQLException> allowedTimes =
            new CachedValue<List<String>,SQLException>(REFRESH_IN_MILLIS) {
                @Override
                public List<String> generateValue() throws SQLException {
                    return Collections.unmodifiableList(database.getAllowedTimes());
                }
            };

    private final CachedValue<List<String>,SQLException> allowedGrades =
            new CachedValue<>(REFRESH_IN_MILLIS) {
                @Override
                public List<String> generateValue() throws SQLException {
                    return Collections.unmodifiableList(database.getAllowedGrades());
                }
            };

    private final CachedValue<List<String>,SQLException> allowedDeliveryMethods =
            new CachedValue<>(REFRESH_IN_MILLIS) {
                @Override
                public List<String> generateValue() throws SQLException {
                    return Collections.unmodifiableList(database.getAllowedDeliveryMethods());
                }
            };

    private final CachedValue<List<Bank>,SQLException> allBanks =
            new CachedValue<List<Bank>,SQLException>(REFRESH_IN_MILLIS) {
                @Override
                public List<Bank> generateValue() throws SQLException {
                    return Collections.unmodifiableList(database.getAllBanks());
                }
            };

    private final CachedValue<List<School>,SQLException> allSchools =
            new CachedValue<List<School>,SQLException>(REFRESH_IN_MILLIS) {
                @Override
                public List<School> generateValue() throws SQLException {
                    return Collections.unmodifiableList(database.getAllSchools());
                }
            };

    private final CachedList<Event, SQLException> availableEvents =
            new CachedList<Event, SQLException>(REFRESH_IN_MILLIS) {
                @Override
                public List<Event> generateValue() throws SQLException {
                    return database.getAllAvailableEvents();
                }
            };

    private final CachedValue<Map<String,String>, SQLException> siteSettings =
            new CachedValue<Map<String,String>, SQLException>(REFRESH_IN_MILLIS) {
                @Override
                public Map<String, String> generateValue() throws SQLException {
                    return database.getSiteSettings();
                }
            };
            

    /** Constructor requires you to provide an actual database. */
    public CachingDatabase(DatabaseFacade database) {
        this.database = database;
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
        return availableEvents.getCachedValue();
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
        return allSchools.getCachedValue();
    }

    @Override
    public List<Bank> getAllBanks() throws SQLException {
        return allBanks.getCachedValue();
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        return database.getAllUsers();
    }

    @Override
    public List<PrettyPrintingDate> getAllowedDates() throws SQLException {
        return allowedDates.getCachedValue();
    }

    @Override
    public List<String> getAllowedTimes() throws SQLException {
        return allowedTimes.getCachedValue();
    }

    @Override
    public List<String> getAllowedGrades() throws SQLException {
        return allowedGrades.getCachedValue();
    }

    @Override
    public List<String> getAllowedDeliveryMethods() throws SQLException {
        return allowedDeliveryMethods.getCachedValue();
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
        availableEvents.refreshNow();
    }

    @Override
    public void modifyVolunteerPersonalFields(EditVolunteerPersonalDataFormData formData) throws SQLException, EmailAlreadyInUseException, InconsistentDatabaseException {
        database.modifyVolunteerPersonalFields(formData);
        availableEvents.refreshNow();
    }

    @Override
    public void modifyTeacherSchool(String userId, String schoolId) throws SQLException, NoSuchSchoolException, NoSuchUserException
    {
       database.modifyTeacherSchool(userId, schoolId);
    }


    @Override
    public Teacher insertNewTeacher(TeacherRegistrationFormData formData, String hashedPassword, String salt) throws SQLException, NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException {
        return database.insertNewTeacher(formData, hashedPassword, salt);
    }

    @Override
    public void insertEvent(String teacherId, CreateEventFormData formData) throws SQLException {
        database.insertEvent(teacherId, formData);
        availableEvents.refreshNow();
    }

    @Override
    public Volunteer insertNewVolunteer(VolunteerRegistrationFormData formData, String hashedPassword, String salt) throws SQLException, NoSuchBankException, EmailAlreadyInUseException {
        return database.insertNewVolunteer(formData, hashedPassword, salt);
    }

    @Override
    public void insertNewAllowedDate(AddAllowedDateFormData formData) throws SQLException, AllowedDateAlreadyInUseException {
        database.insertNewAllowedDate(formData);
        allowedDates.refreshNow();
    }

    @Override
    public void insertNewAllowedTime(AddAllowedTimeFormData formData)
            throws SQLException, AllowedTimeAlreadyInUseException, NoSuchAllowedTimeException
    {
        database.insertNewAllowedTime(formData);
        allowedTimes.refreshNow();
    }

    @Override
    public void volunteerForEvent(final String eventId, String volunteerId) throws SQLException, NoSuchEventException, EventAlreadyHasAVolunteerException {
        database.volunteerForEvent(eventId, volunteerId);
        if (volunteerId == null) {
            // Withdrew someone from an event so we need to reload the list
            availableEvents.refreshNow();
        } else {
            availableEvents.deleteItems(new CachedList.Filter<Event>() {
                @Override
                public boolean keep(Event item) {
                    return !item.getEventId().equals(eventId);
                }
            });
        }
    }

    @Override
    public void deleteSchool(String schoolId) throws SQLException, NoSuchSchoolException {
        database.deleteSchool(schoolId);
        allSchools.refreshNow();
        availableEvents.refreshNow();
    }

    @Override
    public void deleteBankAndBankVolunteers(String bankId) throws SQLException, NoSuchBankException, BankHasVolunteersException, VolunteerHasEventsException {
        database.deleteBankAndBankVolunteers(bankId);
        allBanks.refreshNow();
        availableEvents.refreshNow();
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
        availableEvents.refreshNow();
    }

    @Override
    public void modifySchool(EditSchoolFormData school) throws SQLException, NoSuchSchoolException {
        database.modifySchool(school);
        availableEvents.refreshNow();
        allSchools.refreshNow();
    }

    @Override
    public void insertNewBankAndAdmin(CreateBankFormData formData) throws SQLException, EmailAlreadyInUseException {
        database.insertNewBankAndAdmin(formData);
        allBanks.refreshNow();
    }

    @Override
    public void insertNewBankAdmin(NewBankAdminFormData formData) throws SQLException, EmailAlreadyInUseException {
        database.insertNewBankAdmin(formData);
        allBanks.refreshNow();
    }

    @Override
    public void modifyBank(EditBankFormData formData) throws SQLException, NoSuchBankException {
        database.modifyBank(formData);
        allBanks.refreshNow();
    }

    @Override
    public void setUserType(String userId, UserType userType) throws SQLException {
        database.setUserType(userId, userType);
        allBanks.refreshNow();
    }

    @Override
    public void setBankSpecificFieldLabel(SetBankSpecificFieldLabelFormData formData) throws SQLException, NoSuchBankException {
        database.setBankSpecificFieldLabel(formData);
        allBanks.refreshNow();
    }

    @Override
    public void insertNewSchool(CreateSchoolFormData school) throws SQLException {
        database.insertNewSchool(school);
        allSchools.refreshNow();
    }

    @Override
    public void modifyEvent(EventRegistrationFormData formData) throws SQLException, NoSuchEventException {
        database.modifyEvent(formData);
        availableEvents.refreshNow();
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
        allowedTimes.refreshNow();
        database.deleteAllowedTime(time);
    }

    @Override
    public void deleteAllowedDate(PrettyPrintingDate date) throws SQLException, NoSuchAllowedDateException {
        allowedDates.refreshNow();
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
        return siteSettings.getCachedValue();
    }

    @Override
    public void modifySiteSetting(String settingName, String settingValue) throws SQLException {
        siteSettings.refreshNow();
        database.modifySiteSetting(settingName, settingValue);
    }

    @Override
    public SortedSet<Document> getDocuments() throws SQLException{
        return database.getDocuments();
    }

    @Override
    public void createOrModifyDocument (Document document) throws SQLException{
        database.createOrModifyDocument(document);
    }

    @Override
    public void deleteDocument(String documentName) throws SQLException{
        database.deleteDocument(documentName);
    }

}
