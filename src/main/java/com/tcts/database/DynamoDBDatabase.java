package com.tcts.database;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.tcts.common.PrettyPrintingDate;
import com.tcts.datamodel.ApprovalStatus;
import com.tcts.datamodel.Bank;
import com.tcts.datamodel.BankAdmin;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.School;
import com.tcts.datamodel.SiteAdmin;
import com.tcts.datamodel.SiteStatistics;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.User;
import com.tcts.datamodel.UserType;
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
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

import static com.tcts.database.DatabaseField.*;


/**
 * A facade that implements the database functionality using DynamoDB.
 */
public class DynamoDBDatabase implements DatabaseFacade {
    // FIXME: Need to add sorting in lots of places

    // ========== Static Variables ==========

    /**
     * Since bank admins ARE volunteers, when searching for volunteers we must check two different user types;
     * this set contains the database values to check against.
     */
    final static Set<String> volunteerUserTypes = new TreeSet<String>() {{
        add(UserType.VOLUNTEER.getDBValue());
        add(UserType.BANK_ADMIN.getDBValue());
    }};



    // ========== Instance Variables and Constructor ==========

    private final Tables tables;


    /**
     * Constructor.
     *
     * @param delegate the underlying database to delegate to -- this argument will go away
     *                 once the class is fully functional. Pass null to get an exception for
     *                 any calls to methods not yet implemented.
     */
    public DynamoDBDatabase(DatabaseFacade delegate) {
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
    public static String createUniqueId() {
        long randomNonNegativeLong = ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE);
        return Long.toString(randomNonNegativeLong);
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

    /**
     * Helper that creates an AttributeUpdate for setting a particular field to a
     * particular string value. If the string is "" or null, then the attribute
     * will be deleted; if it has any other value then it will be set to that.
     *
     * @param field the DatabaseField to update
     * @param attributeValue the value to set it to, including "" or null.
     */
    private AttributeUpdate attributeUpdate(DatabaseField field, String attributeValue) {
        if (attributeValue == null || attributeValue.isEmpty()) {
            return new AttributeUpdate(field.name()).delete();
        } else {
            return new AttributeUpdate(field.name()).put(attributeValue);
        }
    }

    /**
     * Helper that creates an AttributeUpdate for setting a particular field to a
     * particular string value. If the string is "" or null, then the attribute
     * will be deleted; if it has any other value then it will be set to that.
     *
     * @param field the DatabaseField to update
     * @param attributeValue the value to set it to, including "" or null.
     */
    private AttributeUpdate intAttributeUpdate(DatabaseField field, int attributeValue) {
        return new AttributeUpdate(field.name()).put(attributeValue);
    }


    /**
     * This retrieves a field which is a string from an Item. If the field is missing
     * (null) it will return an empty string ("") instead of null. This mirrors what
     * we do when storing the value, and is done because DynamoDB is not able to store
     * empty string values.
     */
    private String getStringField(Item item, DatabaseField field) {
        String stringValue = item.getString(field.name());
        return stringValue == null ? "" : stringValue;
    }

    /**
     * This retrieves a field which is an int from an Item.
     *
     * @throws NumberFormatException if the field is null or is not an integer
     */
    private int getIntField(Item item, DatabaseField field) {
        return item.getInt(field.name());
    }

    /* Constants used for the field lengths. Only has the fields of type String, not int or ID. */
    private final Map<DatabaseField,Integer> FIELD_LENGTHS = new HashMap<DatabaseField,Integer>() {{
        put(site_setting_name, 30);
        put(site_setting_value, 100);
        put(event_time, 30);
        put(event_grade, 8);
        put(event_notes, 1000);
        put(bank_name, 45);
        put(user_email, 50);
        put(user_first_name, 50);
        put(user_last_name, 50);
        put(user_phone_number, 45);
        put(user_bank_specific_data, 500);
        put(school_name, 80);
        put(school_addr1, 60);
        put(school_city, 45);
        put(school_zip, 10);
        put(school_county, 45);
        put(school_district, 45);
        put(school_state, 2);
        put(school_phone, 45);
        put(school_slc, 10);
    }};


    // ========== Methods for populating objects ==========


    /**
     * Creates a School object from the corresponding Item retrieved from DynamoDB. If passed
     * null, it returns null.
     */
    private School createSchoolFromDynamoDBItem(Item item) {
        if (item == null) {
            return null;
        }
        School school = new School();
        school.setSchoolId(getStringField(item, school_id));
        school.setName(getStringField(item, school_name));
        school.setAddressLine1(getStringField(item, school_addr1));
        school.setCity(getStringField(item, school_city));
        school.setState(getStringField(item, school_state));
        school.setZip(getStringField(item, school_zip));
        school.setCounty(getStringField(item, school_county));
        school.setSchoolDistrict(getStringField(item, school_district));
        school.setPhone(getStringField(item, school_phone));
        school.setLmiEligible(getIntField(item, school_lmi_eligible));
        school.setSLC(getStringField(item, school_slc));
        return school;
    }


    /**
     * Creates a Bank object from the corresponding Item retrieved from DynamoDB. If passed
     * null, it returns null.
     */
    private Bank createBankFromDynamoDBItem(Item item) {
        if (item == null) {
            return null;
        }
        Bank bank = new Bank();
        bank.setBankId(getStringField(item, bank_id));
        bank.setBankName(getStringField(item, bank_name));
        if (item.get(min_lmi_for_cra.name()) == null) {
            bank.setMinLMIForCRA(null); // An int field that nevertheless can store null
        } else {
            bank.setMinLMIForCRA(getIntField(item, min_lmi_for_cra));
        }
        if (getStringField(item, bank_specific_data_label) == null) {
            bank.setBankSpecificDataLabel(""); // Use "" when there is a null in the DB
        } else {
            bank.setBankSpecificDataLabel(getStringField(item, bank_specific_data_label));
        }
        return bank;
    }

    /**
     * Creates a User object from the corresponding Item retrieved from DynamoDB. It will
     * be of the appropriate concrete sub-type of User. If passed null, it returns null.
     * None of the linked data is filled in.
     */
    private User createUserFromDynamoDBItem(Item item) {
        if (item == null) {
            return null;
        }
        UserType userType = UserType.fromDBValue(getStringField(item, user_type));
        User user;
        switch(userType) {
            case TEACHER: {
                Teacher teacher = new Teacher();
                teacher.setSchoolId(getStringField(item, user_organization_id));
                user = teacher;
            } break;
            case VOLUNTEER: {
                Volunteer volunteer = new Volunteer();
                volunteer.setBankId(getStringField(item, user_organization_id));
                volunteer.setApprovalStatus(ApprovalStatus.fromDBValue(getIntField(item,user_approval_status)));
                volunteer.setBankSpecificData(getStringField(item, user_bank_specific_data));
                user = volunteer;
            } break;
            case BANK_ADMIN: {
                BankAdmin bankAdmin = new BankAdmin();
                bankAdmin.setBankId(getStringField(item, user_organization_id));
                bankAdmin.setApprovalStatus(ApprovalStatus.fromDBValue(getIntField(item,user_approval_status)));
                bankAdmin.setBankSpecificData(getStringField(item, user_bank_specific_data));
                user = bankAdmin;
            } break;
            case SITE_ADMIN: {
                SiteAdmin siteAdmin = new SiteAdmin();
                user = siteAdmin;
            } break;
            default: {
                throw new RuntimeException("Invalid type in case statement.");
            }
        }
        user.setUserId(getStringField(item, user_id));
        user.setEmail(getStringField(item, user_email));
        user.setHashedPassword(getStringField(item, user_hashed_password));
        user.setSalt(getStringField(item, user_password_salt));
        user.setFirstName(getStringField(item, user_first_name));
        user.setLastName(getStringField(item, user_last_name));
        user.setPhoneNumber(getStringField(item, user_phone_number));
        user.setResetPasswordToken(getStringField(item, user_reset_password_token));
        user.setUserType(userType);
        return user;
    }


    private Event createEventFromDynamoDBItem(Item item) {
        if (item == null) {
            return null;
        }
        Event event = new Event();
        event.setEventId(getStringField(item, event_id));
        event.setTeacherId(getStringField(item, event_teacher_id));
        try {
            event.setEventDate(PrettyPrintingDate.fromParsableDate(getStringField(item, event_date)));
        } catch(ParseException err) {
            throw new InconsistentDatabaseException("Date '" + getStringField(item, event_date) + "' not parsable.");
        }
        event.setEventTime(getStringField(item, event_time));
        event.setGrade(Integer.toString(getIntField(item, event_grade)));
        event.setNumberStudents(getIntField(item, event_number_students));
        event.setNotes(getStringField(item, event_notes));
        String volunteerString = getStringField(item, event_volunteer_id);
        event.setVolunteerId(volunteerString.length() == 0 ? null : volunteerString);
        return event;
    }

    // ========== Methods of DatabaseFacade Class ==========

    @Override
    public int getFieldLength(DatabaseField field) {
        return FIELD_LENGTHS.get(field);
    }

    @Override
    public User getUserById(String userId) throws SQLException, InconsistentDatabaseException {
        Item item = tables.userTable.getItem(new PrimaryKey(user_id.name(), userId));
        return createUserFromDynamoDBItem(item);
    }

    @Override
    public User getUserByEmail(String email) throws SQLException, InconsistentDatabaseException {
        Index userByEmail = tables.userTable.getIndex("byEmail");
        ItemCollection<QueryOutcome> users = userByEmail.query(new KeyAttribute(user_email.name(), email));
        User user = null;
        int numItems = 0;
        for (Item item : users) {
            user = createUserFromDynamoDBItem(item);
            numItems += 1;
        }
        if (numItems == 0) {
            return null;
        } else if (numItems == 1) {
            return user;
        } else {
            throw new InconsistentDatabaseException("More than one user with email address '" + email + "'.");
        }
    }

    @Override
    public void modifyUserPersonalFields(EditPersonalDataFormData formData) throws SQLException, EmailAlreadyInUseException, InconsistentDatabaseException {
        // This approach will CREATE the user if it doesn't exist. I THINK that behavior is fine.
        tables.userTable.updateItem(
                new PrimaryKey(user_id.name(), formData.getUserId()),
                attributeUpdate(user_email, formData.getEmail()),
                attributeUpdate(user_first_name, formData.getFirstName()),
                attributeUpdate(user_last_name, formData.getLastName()),
                attributeUpdate(user_phone_number, formData.getPhoneNumber()));
    }

    @Override
    public void modifyVolunteerPersonalFields(EditVolunteerPersonalDataFormData formData) throws SQLException, EmailAlreadyInUseException, InconsistentDatabaseException {
        // This approach will CREATE the user if it doesn't exist. I THINK that behavior is fine.
        tables.userTable.updateItem(
                new PrimaryKey(user_id.name(), formData.getUserId()),
                attributeUpdate(user_email, formData.getEmail()),
                attributeUpdate(user_first_name, formData.getFirstName()),
                attributeUpdate(user_last_name, formData.getLastName()),
                attributeUpdate(user_phone_number, formData.getPhoneNumber()),
                attributeUpdate(user_bank_specific_data, formData.getBankSpecificData()));
    }

    @Override
    public void modifyTeacherSchool(String userId, String organizationId) throws SQLException, NoSuchSchoolException, NoSuchUserException {
        // This approach will CREATE the user if it doesn't exist. I THINK that behavior is fine.
        // It also does not verify that the organization ID actually exists in the database.
        tables.userTable.updateItem(
                new PrimaryKey(user_id.name(), userId),
                attributeUpdate(user_organization_id, organizationId));
    }

    @Override
    public Teacher insertNewTeacher(TeacherRegistrationFormData formData, String hashedPassword, String salt) throws SQLException, NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException {
        // NOTE: I'm choosing NOT to verify that the school ID is actually present in the database
        // FIXME: I *must* verify that the email is unique, and I don't do that yet.
        String newTeacherId = createUniqueId();
        tables.userTable.putItem(new Item()
                .withPrimaryKey(new PrimaryKey(user_id.name(), newTeacherId))
                .withString(user_type.name(), UserType.TEACHER.getDBValue())
                .withString(user_email.name(), formData.getEmail())
                .withString(user_first_name.name(), formData.getFirstName())
                .withString(user_last_name.name(), formData.getLastName())
                .withString(user_phone_number.name(), formData.getPhoneNumber())
                .withString(user_organization_id.name(), formData.getSchoolId())
                .withString(user_hashed_password.name(), hashedPassword)
                .withString(user_password_salt.name(), salt));
        Teacher result = new Teacher();
        result.setUserId(newTeacherId);
        result.setUserType(UserType.TEACHER);
        result.setEmail(formData.getEmail());
        result.setFirstName(formData.getFirstName());
        result.setLastName(formData.getLastName());
        result.setPhoneNumber(formData.getPhoneNumber());
        result.setSchoolId(formData.getSchoolId());
        result.setHashedPassword(hashedPassword);
        result.setSalt(salt);
        return result;
    }

    @Override
    public List<Event> getEventsByTeacher(String teacherId) throws SQLException {
        // FIXME: This needs a search criteria, and perhaps even an index to make it faster.
        List<Event> result = new ArrayList<Event>();
        for (Item item : tables.eventTable.scan()) {
            if (teacherId.equals(item.getString(event_teacher_id.name()))) {
                result.add(createEventFromDynamoDBItem(item));
            }
        }
        return result;
    }

    @Override
    public List<Event> getAllAvailableEvents() throws SQLException {
        // FIXME: This needs a search criteria, and perhaps even an index to make it faster.
        // FIXME: The requirements state that this must pre-populate the linkedTeacher and
        //    those teachers' linkedSchool. But I haven't done that yet.
        List<Event> result = new ArrayList<Event>();
        for (Item item : tables.eventTable.scan()) {
            if (item.getString(event_volunteer_id.name()) == null) {
                result.add(createEventFromDynamoDBItem(item));
            }
        }
        return result;
    }

    @Override
    public List<Event> getEventsByVolunteer(String volunteerId) throws SQLException {
        // FIXME: This needs a search criteria, and perhaps even an index to make it faster.
        List<Event> result = new ArrayList<Event>();
        for (Item item : tables.eventTable.scan()) {
            if (volunteerId.equals(item.getString(event_volunteer_id.name()))) {
                result.add(createEventFromDynamoDBItem(item));
            }
        }
        return result;
    }

    @Override
    public List<Event> getEventsByVolunteerWithTeacherAndSchool(String volunteerId) throws SQLException {
        Map<String,School> schoolsById = new HashMap<String,School>();
        Map<String,Teacher> teachersById = new HashMap<String,Teacher>();
        List<Event> events = getEventsByVolunteer(volunteerId);
        for (Event event : events) {
            String teacherId = event.getTeacherId();
            Teacher teacher = teachersById.get(teacherId);
            if (teacher == null) {
                teacher = (Teacher) getUserById(teacherId);
                teachersById.put(teacherId, teacher);
            }
            String schoolId = teacher.getSchoolId();
            School school = schoolsById.get(schoolId);
            if (school == null) {
                school = getSchoolById(schoolId);
                schoolsById.put(schoolId, school);
            }
            teacher.setLinkedSchool(school);
            event.setLinkedTeacher(teacher);
        }
        return events;
    }

    @Override
    public void insertEvent(String teacherId, CreateEventFormData formData) throws SQLException {
        String newEventId = createUniqueId();
        tables.eventTable.putItem(new Item()
                .withPrimaryKey(new PrimaryKey(event_id.name(), newEventId))
                .withString(event_teacher_id.name(), teacherId)
                .withString(event_date.name(), PrettyPrintingDate.fromJavaUtilDate(formData.getEventDate()).getParseable())
                .withString(event_time.name(), formData.getEventTime())
                .withInt(event_grade.name(), Integer.parseInt(formData.getGrade()))
                .withInt(event_number_students.name(), Integer.parseInt(formData.getNumberStudents()))
                .withString(event_notes.name(), formData.getNotes()));
    }

    @Override
    public void volunteerForEvent(String eventId, String volunteerId) throws SQLException, NoSuchEventException {
        // FIXME: Really should verify on this update that the volunteer is null before we update it!
        tables.eventTable.updateItem(
                new PrimaryKey(event_id.name(), eventId),
                attributeUpdate(event_volunteer_id, volunteerId));
    }

    @Override
    public List<Volunteer> getVolunteersByBank(String bankId) throws SQLException {
        // FIXME: Needs a query criterion or index to speed it up.
        List<Volunteer> result = new ArrayList<Volunteer>();
        for (Item item : tables.userTable.scan()) {
            if (volunteerUserTypes.contains(item.getString(user_type.name()))
                && bankId.equals(item.getString(user_organization_id.name()))) {
                result.add((Volunteer) createUserFromDynamoDBItem(item));
            }
        }
        return result;
    }

    @Override
    public BankAdmin getBankAdminByBank(String bankId) throws SQLException {
        // FIXME: This really needs to have an index to speed it up
        BankAdmin result = null;
        List<BankAdmin> bankAdmins = getBankAdmins();
        for (BankAdmin bankAdmin : bankAdmins) {
            if (bankId.equals(bankAdmin.getBankId())) {
                result = bankAdmin;
                break;
            }
        }
        if (result == null) {
            throw new InconsistentDatabaseException("No bank admin for bank with ID " + bankId);
        } else {
            return result;
        }
    }

    @Override
    public Volunteer insertNewVolunteer(VolunteerRegistrationFormData formData, String hashedPassword, String salt) throws SQLException, NoSuchBankException, EmailAlreadyInUseException {
        // NOTE: I'm choosing NOT to verify that the bank ID is actually present in the database
        // FIXME: I *must* verify that the email is unique, and I don't do that yet.
        String newVolunteerId = createUniqueId();
        tables.userTable.putItem(new Item()
                .withPrimaryKey(new PrimaryKey(user_id.name(), newVolunteerId))
                .withString(user_type.name(), UserType.VOLUNTEER.getDBValue())
                .withString(user_email.name(), formData.getEmail())
                .withString(user_first_name.name(), formData.getFirstName())
                .withString(user_last_name.name(), formData.getLastName())
                .withString(user_phone_number.name(), formData.getPhoneNumber())
                .withString(user_organization_id.name(), formData.getBankId())
                .withInt(user_approval_status.name(), ApprovalStatus.INITIAL_APPROVAL_STATUS.getDbValue())
                .withString(user_bank_specific_data.name(), formData.getBankSpecificData())
                .withString(user_hashed_password.name(), hashedPassword)
                .withString(user_password_salt.name(), salt));
        Volunteer result = new Volunteer();
        result.setUserId(newVolunteerId);
        result.setUserType(UserType.VOLUNTEER);
        result.setEmail(formData.getEmail());
        result.setFirstName(formData.getFirstName());
        result.setLastName(formData.getLastName());
        result.setPhoneNumber(formData.getPhoneNumber());
        result.setBankId(formData.getBankId());
        result.setBankSpecificData(formData.getBankSpecificData());
        result.setHashedPassword(hashedPassword);
        result.setSalt(salt);
        return result;
    }

    @Override
    public Bank getBankById(String bankId) throws SQLException {
        Item item = tables.bankTable.getItem(new PrimaryKey(bank_id.name(), bankId));
        return createBankFromDynamoDBItem(item);
    }

    @Override
    public School getSchoolById(String schoolId) throws SQLException {
        Item item = tables.schoolTable.getItem(new PrimaryKey(school_id.name(), schoolId));
        return createSchoolFromDynamoDBItem(item);
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
        List<Bank> result = new ArrayList<Bank>();
        // -- Get the banks --
        for (Item item : tables.bankTable.scan()) {
            result.add(createBankFromDynamoDBItem(item));
        }
        // -- Sort by name --
        Collections.sort(result, new Comparator<Bank>() {
            @Override
            public int compare(Bank bank1, Bank bank2) {
                return bank1.getBankName().compareTo(bank2.getBankName());
            }
        });
        // -- Return the result --
        return result;
    }

    @Override
    public List<PrettyPrintingDate> getAllowedDates() throws SQLException {
        List<PrettyPrintingDate> result = new ArrayList<PrettyPrintingDate>();
        for (Item scanOutcome : tables.allowedDatesTable.scan()) {
            String dateStr = scanOutcome.getString(event_date_allowed.name());
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
        List<TimeAndSortKey> sortableTimes = new ArrayList<TimeAndSortKey>();
        for (Item scanOutcome : tables.allowedTimesTable.scan()) {
            sortableTimes.add(new TimeAndSortKey(
                    scanOutcome.getString(event_time_allowed.name()),
                    scanOutcome.getInt(event_time_sort_key.name())));
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
        // Note: Does NOT verify whether the school exists and throw NoSuchSchoolException where appropriate
        tables.schoolTable.deleteItem(new PrimaryKey(school_id.name(), schoolId));
    }

    @Override
    public void deleteBank(String bankId) throws SQLException, NoSuchBankException {
        // Does not verify that the bank exists and throw NoSuchBankException
        // FIXME: Does not currently delete the bank admin and all volunteers
        tables.bankTable.deleteItem(new PrimaryKey(bank_id.name(), bankId));
    }

    @Override
    public void deleteVolunteer(String volunteerId) throws SQLException, NoSuchUserException, VolunteerHasEventsException {
        // FIXME: Needs to validate that the volunteer has no events and raise an exception if it does.
        tables.userTable.deleteItem(new PrimaryKey(user_id.name(), volunteerId));
    }

    @Override
    public void deleteTeacher(String teacherId) throws SQLException, NoSuchUserException, TeacherHasEventsException {
        // FIXME: Needs to validate that the teacher has no events and raise an exception if it does.
        tables.userTable.deleteItem(new PrimaryKey(user_id.name(), teacherId));
    }

    @Override
    public void deleteEvent(String eventId) throws SQLException, NoSuchEventException {
        tables.eventTable.deleteItem(new PrimaryKey(event_id.name(), eventId));
    }

    @Override
    public List<Event> getAllEvents() throws SQLException, InconsistentDatabaseException {
        List<Event> result = new ArrayList<Event>();
        // -- Get the events --
        for (Item item : tables.eventTable.scan()) {
            result.add(createEventFromDynamoDBItem(item));
        }
        // FIXME: Do these need to be sorted?
        // -- Return the result --
        return result;
    }

    @Override
    public Event getEventById(String eventId) throws SQLException {
        Item item = tables.eventTable.getItem(new PrimaryKey(event_id.name(), eventId));
        return createEventFromDynamoDBItem(item);
    }

    @Override
    public void modifySchool(EditSchoolFormData school) throws SQLException, NoSuchSchoolException {
        // This approach will CREATE the school if it doesn't exist. I THINK that behavior doesn't break anything.
        tables.schoolTable.updateItem(
                new PrimaryKey(school_id.name(), school.getSchoolId()),
                attributeUpdate(school_name, school.getSchoolName()),
                attributeUpdate(school_addr1, school.getSchoolAddress1()),
                attributeUpdate(school_city, school.getCity()),
                attributeUpdate(school_state, school.getState()),
                attributeUpdate(school_zip, school.getZip()),
                attributeUpdate(school_county, school.getCounty()),
                attributeUpdate(school_district, school.getDistrict()),
                attributeUpdate(school_phone, school.getPhone()),
                attributeUpdate(school_lmi_eligible, school.getLmiEligible()),
                attributeUpdate(school_slc, school.getSLC()));
    }

    @Override
    public void insertNewBankAndAdmin(CreateBankFormData formData) throws SQLException, EmailAlreadyInUseException {
        // FIXME: I *must* verify that the email is unique, and I don't do that yet.
        String bankAdminId = createUniqueId();
        String bankId = createUniqueId();
        tables.bankTable.putItem(new Item()
                .withPrimaryKey(bank_id.name(), bankId)
                .withString(bank_name.name(), formData.getBankName()));
    }

    @Override
    public void modifyBankAndBankAdmin(EditBankFormData formData) throws SQLException, EmailAlreadyInUseException, NoSuchBankException {
        // -- Find the bankAdminId --
        BankAdmin bankAdmin = getBankAdminByBank(formData.getBankId());

        // -- Update the bank admin --
        // FIXME: I *must* verify that the email is unique, and I don't do that yet.
        tables.userTable.updateItem(
                new PrimaryKey(user_id.name(), bankAdmin.getUserId()),
                attributeUpdate(user_first_name, formData.getFirstName()),
                attributeUpdate(user_last_name, formData.getLastName()),
                attributeUpdate(user_email, formData.getEmail()),
                attributeUpdate(user_phone_number, formData.getPhoneNumber()));

        // -- Update the bank --
        tables.bankTable.updateItem(
                new PrimaryKey(bank_id.name(), formData.getBankId()),
                attributeUpdate(bank_name, formData.getBankName()),
                attributeUpdate(min_lmi_for_cra, formData.getMinLMIForCRA()));
    }

    @Override
    public void setBankSpecificFieldLabel(SetBankSpecificFieldLabelFormData formData) throws SQLException, NoSuchBankException {
        // This approach will CREATE the bank if it doesn't exist. I THINK that behavior doesn't break anything.
        tables.bankTable.updateItem(
                new PrimaryKey(bank_id.name(), formData.getBankId()),
                attributeUpdate(bank_specific_data_label, formData.getBankSpecificFieldLabel()));
    }

    @Override
    public void insertNewSchool(CreateSchoolFormData school) throws SQLException {
        Item item = new Item()
                .withPrimaryKey(school_id.name(), createUniqueId())
                .withString(school_name.name(), school.getSchoolName())
                .withString(school_addr1.name(), school.getSchoolAddress1())
                .withString(school_city.name(), school.getCity())
                .withString(school_state.name(), school.getState())
                .withString(school_zip.name(), school.getZip())
                .withString(school_county.name(), school.getCounty())
                .withString(school_district.name(), school.getDistrict())
                .withString(school_phone.name(), school.getPhone())
                .withString(school_lmi_eligible.name(), school.getLmiEligible())
                .withString(school_slc.name(), school.getSLC());
        tables.schoolTable.putItem(item);
    }

    @Override
    public void insertNewAllowedDate(AddAllowedDateFormData formData) throws SQLException, AllowedDateAlreadyInUseException {
        tables.allowedDatesTable.putItem(new Item()
                .withPrimaryKey(event_date_allowed.name(), formData.getParsableDateStr()));
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
                        .withPrimaryKey(event_time_allowed.name(), formData.getAllowedTime())
                        .withInt(event_time_sort_key.name(), sortKey));
                sortKey += 1;
            }
            // - Now we insert the one from the list -
            tables.allowedTimesTable.putItem(new Item()
                    .withPrimaryKey(event_time_allowed.name(), allowedTime)
                    .with(event_time_sort_key.name(), sortKey));
            sortKey += 1;
        }
        if (formData.getTimeToInsertBefore().isEmpty()) {
            // - Add the new one at the end -
            tables.allowedTimesTable.putItem(new Item()
                    .withPrimaryKey(event_time_allowed.name(), formData.getAllowedTime())
                    .with(event_time_sort_key.name(), sortKey));
        }
    }

    @Override
    public void modifyEvent(EventRegistrationFormData formData) throws SQLException, NoSuchEventException {
        tables.eventTable.updateItem(
                new PrimaryKey(event_id.name(), formData.getEventId()),
                attributeUpdate(event_date, PrettyPrintingDate.fromJavaUtilDate(formData.getEventDate()).getParseable()),
                attributeUpdate(event_time, formData.getEventTime()),
                intAttributeUpdate(event_grade, Integer.parseInt(formData.getGrade())),
                intAttributeUpdate(event_number_students, Integer.parseInt(formData.getNumberStudents())),
                attributeUpdate(event_notes, formData.getNotes()));
    }

    @Override
    public void updateUserCredential(String userId, String hashedPassword, String salt) throws SQLException {
        // This approach will CREATE the user if it doesn't exist. I THINK that behavior doesn't break anything.
        tables.userTable.updateItem(
                new PrimaryKey(user_id.name(), userId),
                attributeUpdate(user_hashed_password, hashedPassword),
                attributeUpdate(user_password_salt, salt));
    }

    @Override
    public void updateResetPasswordToken(String userId, String resetPasswordToken) throws SQLException {
        // This approach will CREATE the user if it doesn't exist. I THINK that behavior doesn't break anything.
        tables.userTable.updateItem(
                new PrimaryKey(user_id.name(), userId),
                attributeUpdate(user_reset_password_token, resetPasswordToken));
    }

    @Override
    public void updateApprovalStatusById(String volunteerId, ApprovalStatus approvalStatus) throws SQLException {
        // This approach will CREATE the user if it doesn't exist. I THINK that behavior doesn't break anything.
        tables.userTable.updateItem(
                new PrimaryKey(user_id.name(), volunteerId),
                intAttributeUpdate(user_approval_status, approvalStatus.getDbValue()));
    }

    @Override
    public void deleteAllowedTime(String time) throws SQLException, NoSuchAllowedTimeException {
        tables.allowedTimesTable.deleteItem(new PrimaryKey(event_time_allowed.name(), time));
    }

    @Override
    public void deleteAllowedDate(PrettyPrintingDate date) throws SQLException, NoSuchAllowedDateException {
        tables.allowedDatesTable.deleteItem(new PrimaryKey(event_date_allowed.name(), date.getParseable()));
    }

    @Override
    public SiteStatistics getSiteStatistics() throws SQLException {
        // FIXME: This returns just dummy values for now.
        SiteStatistics siteStatistics = new SiteStatistics();
        siteStatistics.setNumEvents(0);
        siteStatistics.setNumMatchedEvents(0);
        siteStatistics.setNumUnmatchedEvents(0);
        siteStatistics.setNum3rdGradeEvents(0);
        siteStatistics.setNum4thGradeEvents(0);
        siteStatistics.setNumVolunteers(0);
        siteStatistics.setNumParticipatingTeachers(0);
        siteStatistics.setNumParticipatingSchools(0);
        return siteStatistics;
    }

    @Override
    public List<Teacher> getTeachersWithSchoolData() throws SQLException {
        // FIXME: An index or select might allow me to get just the teachers more effectively
        Map<String,School> schoolsById = new HashMap<String,School>();
        List<Teacher> teachers = new ArrayList<Teacher>();
        for (Item item : tables.userTable.scan()) {
            if (UserType.TEACHER.getDBValue().equals(getStringField(item, user_type))) {
                Teacher teacher = (Teacher) createUserFromDynamoDBItem(item);
                String schoolId = teacher.getSchoolId();
                School school = schoolsById.get(schoolId);
                if (school == null) {
                    school = getSchoolById(schoolId);
                    schoolsById.put(schoolId, school);
                }
                teacher.setLinkedSchool(school);
                teachers.add(teacher);
            }
        }
        return teachers;
    }

    @Override
    public List<Teacher> getTeachersBySchool(String schoolId) throws SQLException {
        // NOTE: This is a very rare operation (only used for deleting a school) so there is
        // no need for efficiency. Therefore we will NOT use an index, but a full table scan.
        List<Teacher> result = new ArrayList<Teacher>();
        for (Item item : tables.userTable.scan()) {
            UserType userType = UserType.fromDBValue(getStringField(item, user_type));
            String organizationId = getStringField(item, user_organization_id);
            if (userType == UserType.TEACHER && schoolId.equals(organizationId)) {
                result.add((Teacher) createUserFromDynamoDBItem(item));
            }
        }
        return result;
    }

    @Override
    public List<Volunteer> getVolunteersWithBankData() throws SQLException {
        // FIXME: Should have a search criteria or index to make this faster
        List<Volunteer> result = new ArrayList<Volunteer>();
        Map<String,Bank> banksById = new HashMap<String,Bank>();

        for (Item item : tables.userTable.scan()) {
            if (volunteerUserTypes.contains(getStringField(item, user_type))) {
                Volunteer volunteer = (Volunteer) createUserFromDynamoDBItem(item);
                String bankId = volunteer.getBankId();
                Bank bank = banksById.get(bankId);
                if (bank == null) {
                    bank = getBankById(bankId);
                    banksById.put(bankId, bank);
                }
                // Note: makes all the volunteers from the same bank share the same Bank object. I think that's OK.
                volunteer.setLinkedBank(bank);
                result.add(volunteer);
            }
        }
        return result;
    }

    @Override
    public List<Teacher> getMatchedTeachers() throws SQLException {
        // FIXME: Needs to be written, but is only used for email announcements.
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Teacher> getUnMatchedTeachers() throws SQLException {
        // FIXME: Needs to be written, but is only used for email announcements.
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Volunteer> getMatchedVolunteers() throws SQLException {
        // FIXME: Needs to be written, but is only used for email announcements.
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Volunteer> getUnMatchedVolunteers() throws SQLException {
        // FIXME: Needs to be written, but is only used for email announcements.
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<BankAdmin> getBankAdmins() throws SQLException {
        List<BankAdmin> result = new ArrayList<BankAdmin>();
        // FIXME: Could be much more efficient if the scan expression excluded all but admins
        for (Item item : tables.userTable.scan()) {
            if (UserType.fromDBValue(item.getString(user_type.name())) == UserType.BANK_ADMIN) {
                BankAdmin bankAdmin = (BankAdmin) createUserFromDynamoDBItem(item);
                result.add(bankAdmin);
            }
        }
        return result;
    }

    @Override
    public Map<String, String> getSiteSettings() throws SQLException {
        Map<String,String> result = new HashMap<String,String>();
        for (Item scanOutcome : tables.siteSettingsTable.scan()) {
            result.put(
                    scanOutcome.getString(site_setting_name.name()),
                    scanOutcome.getString(site_setting_value.name()));
        }
        return result;
    }

    @Override
    public void modifySiteSetting(String settingName, String settingValue) throws SQLException {
        tables.siteSettingsTable.putItem(new Item()
                .withPrimaryKey(site_setting_name.name(), settingName)
                .withString(site_setting_value.name(), settingValue));
    }
}
