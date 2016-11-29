package com.tcts.database;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


/**
 * A facade that implements the database functionality using DynamoDB.
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

    // ========== Special Plumbing ==========

    /**
     * When this is called, it will create a single, unique ID.
     * <p>
     * We happen to be using the following approach: pick a random
     * positive long. Count on luck for it to never collide. It's
     * not the most perfect algorithm in the world, but using the
     * birthday problem formula, we would need to issue about 430
     * million IDs to have a 1% chance of encountering a collision.
     */
    private String createUniqueId() {
        long randomNonNegativeLong = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        return Long.toString(randomNonNegativeLong);
    }


    // ========== Methods for populating objects ==========


    /**
     * Creates a School object from the corresponding Item retrieved from DynamoDB.
     */
    private School createSchoolFromDynamoDBItem(Item item) {
        School school = new School();
        school.setSchoolId(item.getString(DatabaseField.school_id.name()));
        school.setName(item.getString(DatabaseField.school_name.name()));
        school.setAddressLine1(item.getString(DatabaseField.school_addr1.name()));
        school.setCity(item.getString(DatabaseField.school_city.name()));
        school.setState(item.getString(DatabaseField.school_state.name()));
        school.setZip(item.getString(DatabaseField.school_zip.name()));
        school.setCounty(item.getString(DatabaseField.school_county.name()));
        school.setSchoolDistrict(item.getString(DatabaseField.school_district.name()));
        school.setPhone(item.getString(DatabaseField.school_phone.name()));
        school.setLmiEligible(item.getInt(DatabaseField.school_lmi_eligible.name()));
        school.setSLC(item.getString(DatabaseField.school_slc.name()));
        return school;
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
        Item item = tables.schoolTable.getItem(new PrimaryKey(DatabaseField.school_id.name(), schoolId));
        if (item == null) {
            return null;
        }
        else {
            return createSchoolFromDynamoDBItem(item);
        }
    }

    @Override
    public List<School> getAllSchools() throws SQLException {
        List<School> result = new ArrayList<School>();
        // -- Get the schools --
        for (Item item : tables.schoolTable.scan()) {
            result.add(createSchoolFromDynamoDBItem(item));
        }
        // -- Sort by name --
        Collections.sort(result, new Comparator<School>() {
            @Override
            public int compare(School school1, School school2) {
                return school1.getName().compareTo(school2.getName());
            }
        });
        // -- Return the result --
        return result;
    }

    @Override
    public List<Bank> getAllBanks() throws SQLException {
        return delegate.getAllBanks();
    }

    @Override
    public List<PrettyPrintingDate> getAllowedDates() throws SQLException {
        List<PrettyPrintingDate> result = new ArrayList<PrettyPrintingDate>();
        for (Item scanOutcome : tables.allowedDatesTable.scan()) {
            String dateStr = scanOutcome.getString(DatabaseField.event_date_allowed.name());
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

    /** Static class used when getting allowed times in the proper sort order. */
    private static class TimeAndSortKey implements Comparable<TimeAndSortKey> {
        final String timeStr;
        final int sortKey;

        /** Constructor. */
        TimeAndSortKey(String timeStr, int sortKey) {
            this.timeStr = timeStr;
            this.sortKey = sortKey;
        }

        @Override
        public int compareTo(TimeAndSortKey o) {
            return Integer.compare(this.sortKey, o.sortKey);
        }

        @Override
        public String toString() {
            return "TimeAndSortKey{" +
                    "timeStr='" + timeStr + '\'' +
                    ", sortKey=" + sortKey +
                    '}';
        }
    }

    @Override
    public List<String> getAllowedTimes() throws SQLException {
        List<TimeAndSortKey> sortableTimes = new ArrayList<TimeAndSortKey>();
        for (Item scanOutcome : tables.allowedTimesTable.scan()) {
            sortableTimes.add(new TimeAndSortKey(
                    scanOutcome.getString(DatabaseField.event_time_allowed.name()),
                    scanOutcome.getInt(DatabaseField.event_time_sort_key.name())));
        }
        Collections.sort(sortableTimes);
        List<String> result = new ArrayList<String>(sortableTimes.size());
        for (TimeAndSortKey sortableTime : sortableTimes) {
            result.add(sortableTime.timeStr);
        }
        return result;
    }

    @Override
    public void deleteSchool(String schoolId) throws SQLException, NoSuchSchoolException {
        DeleteItemOutcome outcome = tables.schoolTable.deleteItem(new PrimaryKey(DatabaseField.school_id.name(), schoolId));
        if (outcome.getItem() == null) {
            throw new NoSuchSchoolException();
        }
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
        // This approach will CREATE the school if it doesn't exist. I THINK that behavior doesn't break anything.
        tables.schoolTable.updateItem(
                new PrimaryKey(DatabaseField.school_id.name(), school.getSchoolId()),
                new AttributeUpdate(DatabaseField.school_name.name()).put(school.getSchoolName()),
                new AttributeUpdate(DatabaseField.school_addr1.name()).put(school.getSchoolAddress1()),
                new AttributeUpdate(DatabaseField.school_city.name()).put(school.getCity()),
                new AttributeUpdate(DatabaseField.school_state.name()).put(school.getState()),
                new AttributeUpdate(DatabaseField.school_zip.name()).put(school.getZip()),
                new AttributeUpdate(DatabaseField.school_county.name()).put(school.getCounty()),
                new AttributeUpdate(DatabaseField.school_district.name()).put(school.getDistrict()),
                new AttributeUpdate(DatabaseField.school_phone.name()).put(school.getPhone()),
                new AttributeUpdate(DatabaseField.school_lmi_eligible.name()).put(school.getLmiEligible()),
                new AttributeUpdate(DatabaseField.school_slc.name()).put(school.getSLC()));
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
        Item item = new Item()
                .withPrimaryKey(DatabaseField.school_id.name(), createUniqueId())
                .withString(DatabaseField.school_name.name(), school.getSchoolName())
                .withString(DatabaseField.school_addr1.name(), school.getSchoolAddress1())
                .withString(DatabaseField.school_city.name(), school.getCity())
                .withString(DatabaseField.school_state.name(), school.getState())
                .withString(DatabaseField.school_zip.name(), school.getZip())
                .withString(DatabaseField.school_county.name(), school.getCounty())
                .withString(DatabaseField.school_district.name(), school.getDistrict())
                .withString(DatabaseField.school_phone.name(), school.getPhone())
                .withString(DatabaseField.school_lmi_eligible.name(), school.getLmiEligible())
                .withString(DatabaseField.school_slc.name(), school.getSLC());
        tables.schoolTable.putItem(item);
    }

    @Override
    public void insertNewAllowedDate(AddAllowedDateFormData formData) throws SQLException, AllowedDateAlreadyInUseException {
        tables.allowedDatesTable.putItem(new Item()
                .withPrimaryKey(DatabaseField.event_date_allowed.name(), formData.getParsableDateStr()));
    }

    @Override
    public void insertNewAllowedTime(AddAllowedTimeFormData formData) throws SQLException, AllowedTimeAlreadyInUseException, NoSuchAllowedTimeException {
        // -- Get the existing list of times so we can ensure they are properly sorted --
        List<String> allowedTimes = getAllowedTimes();
        // -- Make sure it's OK to insert --
        if (!formData.getTimeToInsertBefore().isEmpty() && !allowedTimes.contains(formData.getTimeToInsertBefore())) {
            throw new NoSuchAllowedTimeException();
        }
        if (allowedTimes.contains(formData.getAllowedTime())) {
            throw new AllowedTimeAlreadyInUseException();
        }
        // -- Delete existing values from the database --
        // NOTE: not even slightly threadsafe. Won't be a problem in practice.
        for (String allowedTime : allowedTimes) {
            deleteAllowedTime(allowedTime);
        }
        // -- Now insert the new values --
        int sortKey = 0;
        for (String allowedTime : allowedTimes) {
            if (!formData.getTimeToInsertBefore().isEmpty() && formData.getTimeToInsertBefore().equals(allowedTime)) {
                // - Now we insert the new one -
                tables.allowedTimesTable.putItem(new Item()
                        .withPrimaryKey(DatabaseField.event_time_allowed.name(), formData.getAllowedTime())
                        .withInt(DatabaseField.event_time_sort_key.name(), sortKey));
                sortKey += 1;
            }
            // - Now we insert the one from the list -
            tables.allowedTimesTable.putItem(new Item()
                    .withPrimaryKey(DatabaseField.event_time_allowed.name(), allowedTime)
                    .with(DatabaseField.event_time_sort_key.name(), sortKey));
            sortKey += 1;
        }
        if (formData.getTimeToInsertBefore().isEmpty()) {
            // - Add the new one at the end -
            tables.allowedTimesTable.putItem(new Item()
                    .withPrimaryKey(DatabaseField.event_time_allowed.name(), formData.getAllowedTime())
                    .with(DatabaseField.event_time_sort_key.name(), sortKey));
        }
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
        tables.allowedTimesTable.deleteItem(new PrimaryKey(DatabaseField.event_time_allowed.name(), time));
    }

    @Override
    public void deleteAllowedDate(PrettyPrintingDate date) throws SQLException, NoSuchAllowedDateException {
        tables.allowedDatesTable.deleteItem(new PrimaryKey(DatabaseField.event_date_allowed.name(), date.getParseable()));
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
            result.put(
                    scanOutcome.getString(DatabaseField.site_setting_name.name()),
                    scanOutcome.getString(DatabaseField.site_setting_value.name()));
        }
        return result;
    }

    @Override
    public void modifySiteSetting(String settingName, String settingValue) throws SQLException {
        tables.siteSettingsTable.putItem(new Item()
                .withPrimaryKey(DatabaseField.site_setting_name.name(), settingName)
                .withString(DatabaseField.site_setting_value.name(), settingValue));
    }
}
