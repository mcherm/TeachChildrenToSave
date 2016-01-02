package com.tcts.database;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.tcts.common.CachedList;
import com.tcts.common.CachedValue;
import com.tcts.common.PrettyPrintingDate;
import com.tcts.datamodel.Bank;
import com.tcts.datamodel.BankAdmin;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.School;
import com.tcts.datamodel.SiteStatistics;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.User;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.*;
import com.tcts.formdata.AddAllowedDateFormData;
import com.tcts.formdata.AddAllowedTimeFormData;
import com.tcts.formdata.CreateBankFormData;
import com.tcts.formdata.CreateEventFormData;
import com.tcts.formdata.CreateSchoolFormData;
import com.tcts.formdata.EditBankFormData;
import com.tcts.formdata.EditPersonalDataFormData;
import com.tcts.formdata.EditSchoolFormData;
import com.tcts.formdata.EventRegistrationFormData;
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
            

    /** Constructor requires you to provide an actual database. */
    public CachingDatabase(DatabaseFacade database) {
        this.database = database;
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
    public List<Volunteer> getVolunteersByBank(String bankId) throws SQLException {
        return database.getVolunteersByBank(bankId);
    }

    @Override
    public BankAdmin getBankAdminByBank(String bankId) throws SQLException {
        return database.getBankAdminByBank(bankId);
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
    public List<PrettyPrintingDate> getAllowedDates() throws SQLException {
        return allowedDates.getCachedValue();
    }

    @Override
    public List<String> getAllowedTimes() throws SQLException {
        return allowedTimes.getCachedValue();
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
    public User modifyUserPersonalFields(EditPersonalDataFormData formData) throws SQLException, EmailAlreadyInUseException {
        User result = database.modifyUserPersonalFields(formData);
        availableEvents.refreshNow();
        return result;
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
    public void volunteerForEvent(final String eventId, String volunteerId) throws SQLException, NoSuchEventException {
        database.volunteerForEvent(eventId, volunteerId);
        availableEvents.deleteItems(new CachedList.Filter<Event>() {
            @Override
            public boolean keep(Event item) {
                return !item.getEventId().equals(eventId);
            }
        });
    }

    @Override
    public void deleteSchool(String schoolId) throws SQLException, NoSuchSchoolException {
        database.deleteSchool(schoolId);
        allSchools.refreshNow();
        availableEvents.refreshNow();
    }

    @Override
    public void deleteBank(String bankId) throws SQLException, NoSuchBankException {
        database.deleteBank(bankId);
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
    public void modifyBankAndBankAdmin(EditBankFormData formData) throws SQLException, EmailAlreadyInUseException, NoSuchBankException {
        database.modifyBankAndBankAdmin(formData);
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
    public void updateUserStatusById(String userId, int userStatus) throws SQLException {
        database.updateUserStatusById(userId, userStatus);
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
    public List<Volunteer> getVolunteerWithBankData() throws SQLException {
        return database.getVolunteerWithBankData();
    }
    
    @Override
    public List<Teacher> getTeacherWithSchoolData() throws SQLException {
        return database.getTeacherWithSchoolData();
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
}
