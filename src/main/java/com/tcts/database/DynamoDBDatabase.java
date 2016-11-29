package com.tcts.database;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.tcts.common.PrettyPrintingDate;
import com.tcts.datamodel.Bank;
import com.tcts.datamodel.BankAdmin;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.School;
import com.tcts.datamodel.SiteStatistics;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.User;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.AllowedDateAlreadyInUseException;
import com.tcts.exception.AllowedTimeAlreadyInUseException;
import com.tcts.exception.EmailAlreadyInUseException;
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
import com.tcts.formdata.SetBankSpecificFieldLabelFormData;
import com.tcts.formdata.TeacherRegistrationFormData;
import com.tcts.formdata.VolunteerRegistrationFormData;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FIXME: Doc this.
 */
public class DynamoDBDatabase implements DatabaseFacade {

    // ========== Instance Variables and Constructor ==========

    private final DatabaseFacade delegate; // FIXME: Only temporary. Delegate some calls here until fully implemented
    private final Tables tables;


    public DynamoDBDatabase(DatabaseFacade delegate) {
        this.delegate = delegate;
        this.tables = getTables(connectToDB());
    }

    // ========== Static Methods Shared by DynamoDBSetup ==========

    static DynamoDB connectToDB() {
        AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient();
        dynamoDBClient.withEndpoint("http://localhost:8000"); // FIXME: Make this a config property.
        return new DynamoDB(dynamoDBClient);
    }

    static class Tables {
        final Table siteSettingsTable;
        final Table allowedDatesTable;
        final Table allowedTimesTable;
        final Table eventTable;
        final Table bankTable;
        final Table userTable;
        final Table schoolTable;

        /**
         * Constructor.
         */
        public Tables(
                final Table siteSettingsTable,
                final Table allowedDatesTable,
                final Table allowedTimesTable,
                final Table eventTable,
                final Table bankTable,
                final Table userTable,
                final Table schoolTable
        ) {
            this.siteSettingsTable = siteSettingsTable;
            this.allowedDatesTable = allowedDatesTable;
            this.allowedTimesTable = allowedTimesTable;
            this.eventTable = eventTable;
            this.bankTable = bankTable;
            this.userTable = userTable;
            this.schoolTable = schoolTable;
        }
    }

    static DynamoDBDatabase.Tables getTables(DynamoDB dynamoDB) {
        dynamoDB.getTable("SiteSettings");
        Table siteSettingsTable = dynamoDB.getTable("SiteSettings");
        Table allowedDatesTable = dynamoDB.getTable("AllowedDates");
        Table allowedTimesTable = dynamoDB.getTable("AllowedTimes");
        Table eventTable = dynamoDB.getTable("Event");
        Table bankTable = dynamoDB.getTable("Bank");
        Table userTable = dynamoDB.getTable("User");
        Table schoolTable = dynamoDB.getTable("School");
        return new DynamoDBDatabase.Tables(siteSettingsTable, allowedDatesTable, allowedTimesTable, eventTable, bankTable, userTable, schoolTable);
    }

    // ========== Methods of DatabaseFacade Class ==========

    @Override
    public int getFieldLength(DatabaseField field) {
        return delegate.getFieldLength(field);
    }

    @Override
    public User getUserById(String userId) throws SQLException, InconsistentDatabaseException {
        return delegate.getUserById(userId);
    }

    @Override
    public User getUserByEmail(String email) throws SQLException, InconsistentDatabaseException {
        return delegate.getUserByEmail(email);
    }

    @Override
    public User modifyUserPersonalFields(EditPersonalDataFormData formData) throws SQLException, EmailAlreadyInUseException, InconsistentDatabaseException {
        return delegate.modifyUserPersonalFields(formData);
    }

    @Override
    public Volunteer modifyVolunteerPersonalFields(EditVolunteerPersonalDataFormData formData) throws SQLException, EmailAlreadyInUseException, InconsistentDatabaseException {
        return delegate.modifyVolunteerPersonalFields(formData);
    }

    @Override
    public void modifyTeacherSchool(String userId, String organizationId) throws SQLException, NoSuchSchoolException, NoSuchUserException {
        delegate.modifyTeacherSchool(userId, organizationId);
    }

    @Override
    public Teacher insertNewTeacher(TeacherRegistrationFormData formData, String hashedPassword, String salt) throws SQLException, NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException {
        return delegate.insertNewTeacher(formData, hashedPassword, salt);
    }

    @Override
    public List<Event> getEventsByTeacher(String teacherId) throws SQLException {
        return delegate.getEventsByTeacher(teacherId);
    }

    @Override
    public List<Event> getAllAvailableEvents() throws SQLException {
        return delegate.getAllAvailableEvents();
    }

    @Override
    public List<Event> getEventsByVolunteer(String volunteerId) throws SQLException {
        return delegate.getEventsByVolunteer(volunteerId);
    }

    @Override
    public List<Event> getEventsByVolunteerWithTeacherAndSchool(String volunteerId) throws SQLException {
        return delegate.getEventsByVolunteerWithTeacherAndSchool(volunteerId);
    }

    @Override
    public void insertEvent(String teacherId, CreateEventFormData formData) throws SQLException {
        delegate.insertEvent(teacherId, formData);
    }

    @Override
    public void volunteerForEvent(String eventId, String volunteerId) throws SQLException, NoSuchEventException {
        delegate.volunteerForEvent(eventId, volunteerId);
    }

    @Override
    public List<Volunteer> getVolunteersByBank(String bankId) throws SQLException {
        return delegate.getVolunteersByBank(bankId);
    }

    @Override
    public BankAdmin getBankAdminByBank(String bankId) throws SQLException {
        return delegate.getBankAdminByBank(bankId);
    }

    @Override
    public Volunteer insertNewVolunteer(VolunteerRegistrationFormData formData, String hashedPassword, String salt) throws SQLException, NoSuchBankException, EmailAlreadyInUseException {
        return delegate.insertNewVolunteer(formData, hashedPassword, salt);
    }

    @Override
    public Bank getBankById(String bankId) throws SQLException {
        return delegate.getBankById(bankId);
    }

    @Override
    public School getSchoolById(String schoolId) throws SQLException {
        return delegate.getSchoolById(schoolId);
    }

    @Override
    public List<School> getAllSchools() throws SQLException {
        return delegate.getAllSchools();
    }

    @Override
    public List<Bank> getAllBanks() throws SQLException {
        return delegate.getAllBanks();
    }

    @Override
    public List<PrettyPrintingDate> getAllowedDates() throws SQLException {
        List<PrettyPrintingDate> result = new ArrayList<PrettyPrintingDate>();
        for (Item scanOutcome : tables.allowedDatesTable.scan()) {
            String dateStr = scanOutcome.getString("event_date");
            try {
                result.add(PrettyPrintingDate.fromParsableDate(dateStr));
            }
            catch(ParseException err) {
                throw new RuntimeException("Invalid date in the database: '" + dateStr + "'.", err);
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public List<String> getAllowedTimes() throws SQLException {
        return delegate.getAllowedTimes();
    }

    @Override
    public void deleteSchool(String schoolId) throws SQLException, NoSuchSchoolException {
        delegate.deleteSchool(schoolId);
    }

    @Override
    public void deleteBank(String bankId) throws SQLException, NoSuchBankException {
        delegate.deleteBank(bankId);
    }

    @Override
    public void deleteVolunteer(String volunteerId) throws SQLException, NoSuchUserException, VolunteerHasEventsException {
        delegate.deleteVolunteer(volunteerId);
    }

    @Override
    public void deleteTeacher(String teacherId) throws SQLException, NoSuchUserException, TeacherHasEventsException {
        delegate.deleteTeacher(teacherId);
    }

    @Override
    public void deleteEvent(String eventId) throws SQLException, NoSuchEventException {
        delegate.deleteEvent(eventId);
    }

    @Override
    public List<Event> getAllEvents() throws SQLException, InconsistentDatabaseException {
        return delegate.getAllEvents();
    }

    @Override
    public Event getEventById(String eventId) throws SQLException {
        return delegate.getEventById(eventId);
    }

    @Override
    public void modifySchool(EditSchoolFormData school) throws SQLException, NoSuchSchoolException {
        delegate.modifySchool(school);
    }

    @Override
    public void insertNewBankAndAdmin(CreateBankFormData formData) throws SQLException, EmailAlreadyInUseException {
        delegate.insertNewBankAndAdmin(formData);
    }

    @Override
    public void modifyBankAndBankAdmin(EditBankFormData formData) throws SQLException, EmailAlreadyInUseException, NoSuchBankException {
        delegate.modifyBankAndBankAdmin(formData);
    }

    @Override
    public void setBankSpecificFieldLabel(SetBankSpecificFieldLabelFormData formData) throws SQLException, NoSuchBankException {
        delegate.setBankSpecificFieldLabel(formData);
    }

    @Override
    public void insertNewSchool(CreateSchoolFormData school) throws SQLException {
        delegate.insertNewSchool(school);
    }

    @Override
    public void insertNewAllowedDate(AddAllowedDateFormData formData) throws SQLException, AllowedDateAlreadyInUseException {
        tables.allowedDatesTable.putItem(new Item()
                .withPrimaryKey("event_date", formData.getParsableDateStr()));
    }

    @Override
    public void insertNewAllowedTime(AddAllowedTimeFormData formData) throws SQLException, AllowedTimeAlreadyInUseException, NoSuchAllowedTimeException {
        delegate.insertNewAllowedTime(formData);
    }

    @Override
    public void modifyEvent(EventRegistrationFormData formData) throws SQLException, NoSuchEventException {
        delegate.modifyEvent(formData);
    }

    @Override
    public void updateUserCredential(String userId, String hashedPassword, String salt) throws SQLException {
        delegate.updateUserCredential(userId, hashedPassword, salt);
    }

    @Override
    public void updateResetPasswordToken(String userId, String resetPasswordToken) throws SQLException {
        delegate.updateResetPasswordToken(userId, resetPasswordToken);
    }

    @Override
    public void updateUserStatusById(String userId, int userStatus) throws SQLException {
        delegate.updateUserStatusById(userId, userStatus);
    }

    @Override
    public void deleteAllowedTime(String time) throws SQLException, NoSuchAllowedTimeException {
        delegate.deleteAllowedTime(time);
    }

    @Override
    public void deleteAllowedDate(PrettyPrintingDate date) throws SQLException, NoSuchAllowedDateException {
        tables.allowedDatesTable.deleteItem(new PrimaryKey("event_date", date.getParseable()));
    }

    @Override
    public SiteStatistics getSiteStatistics() throws SQLException {
        return delegate.getSiteStatistics();
    }

    @Override
    public List<Teacher> getTeacherWithSchoolData() throws SQLException {
        return delegate.getTeacherWithSchoolData();
    }

    @Override
    public List<Teacher> getTeachersBySchool(String schoolId) throws SQLException {
        return delegate.getTeachersBySchool(schoolId);
    }

    @Override
    public List<Volunteer> getVolunteersWithBankData() throws SQLException {
        return delegate.getVolunteersWithBankData();
    }

    @Override
    public List<Teacher> getMatchedTeachers() throws SQLException {
        return delegate.getMatchedTeachers();
    }

    @Override
    public List<Teacher> getUnMatchedTeachers() throws SQLException {
        return delegate.getUnMatchedTeachers();
    }

    @Override
    public List<Volunteer> getMatchedVolunteers() throws SQLException {
        return delegate.getMatchedVolunteers();
    }

    @Override
    public List<Volunteer> getUnMatchedVolunteers() throws SQLException {
        return delegate.getUnMatchedVolunteers();
    }

    @Override
    public List<BankAdmin> getBankAdmins() throws SQLException {
        return delegate.getBankAdmins();
    }

    @Override
    public Map<String, String> getSiteSettings() throws SQLException {
        Map<String,String> result = new HashMap<String,String>();
        for (Item scanOutcome : tables.siteSettingsTable.scan()) {
            result.put(scanOutcome.getString("name"), scanOutcome.getString("value"));
        }
        return result;
    }

    @Override
    public void modifySiteSetting(String settingName, String settingValue) throws SQLException {
        tables.siteSettingsTable.putItem(new Item()
                .withPrimaryKey("name", settingName)
                .with("value", settingValue));
    }
}
