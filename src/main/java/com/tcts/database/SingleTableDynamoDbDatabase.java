package com.tcts.database;

import com.tcts.common.Configuration;
import com.tcts.common.PrettyPrintingDate;
import com.tcts.database.dynamodb.DynamoDBHelper;
import com.tcts.database.dynamodb.ItemBuilder;
import com.tcts.database.dynamodb.UpdateItemBuilder;
import com.tcts.datamodel.ApprovalStatus;
import com.tcts.datamodel.Bank;
import com.tcts.datamodel.BankAdmin;
import com.tcts.datamodel.Document;
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
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tcts.database.SingleTableDbField.*;


// FIXME: Still under development
public class SingleTableDynamoDbDatabase implements DatabaseFacade {

    // ========== main() - TEMPORARY ==========

    // FIXME: Remove
    public static void main(String[] args) throws Exception {
        final Configuration configuration = new Configuration();
        final SingleTableDynamoDbDatabase instance = new SingleTableDynamoDbDatabase(configuration);
        final User user = instance.getUserById("2075337998040712579");
        System.out.println("user: " + user + ": " + user.getFirstName());
    }

    // ========== Constants ==========

    /** An indicator value for event_volunteer_id that means no volunteer; used in place of null. */
    private final String NO_VOLUNTEER = "0";

    /* Constants used for the field lengths. Only has the fields of type String, not int or ID. */
    private final Map<DatabaseField,Integer> FIELD_LENGTHS = new HashMap<>() {{
        put(DatabaseField.site_setting_name, 30);
        put(DatabaseField.site_setting_value, 100);
        put(DatabaseField.event_time, 30);
        put(DatabaseField.event_grade, 8);
        put(DatabaseField.event_delivery_method, 1);
        put(DatabaseField.event_notes, 1000);
        put(DatabaseField.bank_name, 45);
        put(DatabaseField.user_email, 50);
        put(DatabaseField.user_original_email, 50);
        put(DatabaseField.user_first_name, 50);
        put(DatabaseField.user_last_name, 50);
        put(DatabaseField.user_street_address, 60);
        put(DatabaseField.user_suite_or_floor_number, 20);
        put(DatabaseField.user_city, 45);
        put(DatabaseField.user_zip, 10);
        put(DatabaseField.user_state, 2);
        put(DatabaseField.user_phone_number, 45);
        put(DatabaseField.user_bank_specific_data, 500);
        put(DatabaseField.school_name, 80);
        put(DatabaseField.school_addr1, 60);
        put(DatabaseField.school_city, 45);
        put(DatabaseField.school_zip, 10);
        put(DatabaseField.school_county, 45);
        put(DatabaseField.school_district, 45);
        put(DatabaseField.school_state, 2);
        put(DatabaseField.school_phone, 45);
        put(DatabaseField.school_slc, 10);
    }};

    // ========== Instance Variables ==========
    private final DynamoDbClient dynamoDbClient;
    private final String tableName;
    private final DynamoDBHelper dynamoDBHelper;

    // ========== Constructor ==========

    /**
     * Constructor.
     */
    public SingleTableDynamoDbDatabase(Configuration configuration) {
        dynamoDbClient = connectToDB(configuration);
        tableName = getTableName(configuration);
        dynamoDBHelper = new DynamoDBHelper();
    }

    // ========== Static Methods for Use in Constructor ==========

    /**
     * Static method to get a DB connection. Made public to use in SingleTableDynamoDBSetup.
     */
    public static DynamoDbClient connectToDB(Configuration configuration) {
        String accessKey = configuration.getProperty("aws.access_key");
        String accessSecret = configuration.getProperty("aws.secret_access_key");
        Region region = Region.US_EAST_1;
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, accessSecret);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
        return DynamoDbClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();
    }

    /**
     * Static method to create the table name. Made public for use in SingleTableDynamoDBSetup.
     */
    public static String getTableName(Configuration configuration) {
        final String environment = configuration.getProperty("dynamoDB.environment", "dev");
        return "TCTS1." + environment;
    }

    // ========== Create Object Functions ==========

    /**
     * This retrieves a field which is a string from an Item. If the field is missing
     * (null) it will return an empty string ("") instead of null. This mirrors what
     * we do when storing the value, and is done because DynamoDB is not able to store
     * empty string values.
     */
    private String getStringField(Map<String,AttributeValue> item, SingleTableDbField field) {
        final AttributeValue fieldValue = item.get(field.name());
        return fieldValue == null ? "" : fieldValue.s();
    }

    /**
     * This retrieves a field which is an int from an Item.
     *
     * @throws NumberFormatException if the field is null or is not an integer
     */
    private int getIntField(Map<String,AttributeValue> item, SingleTableDbField field) {
        final AttributeValue attributeValue = item.get(field.name());
        if (attributeValue == null) {
            throw new NullPointerException("Numeric field " + field.name() + " is null");
        } else {
            return Integer.parseInt(attributeValue.n());
        }
    }

    /**
     * This retrieves a field which is a BigDecimal from an Item.
     *
     * @throws NumberFormatException if the field is not an integer
     */
    private BigDecimal getDecimalField(Map<String,AttributeValue> item, SingleTableDbField field) {
        final AttributeValue attributeValue = item.get(field.name());
        return attributeValue == null ? null : new BigDecimal(attributeValue.s());
    }

    /** An interface for passing a method reference a function to build an object from a DB item. */
    private interface CreateFunction<T> {
        T create(Map<String,AttributeValue> item);
    }

    /**
     * Creates a Bank object from the corresponding Item retrieved from DynamoDB. If passed
     * null, it returns null.
     */
    private Bank createBankFromDynamoDbItem(Map<String,AttributeValue> item) {
        if (item == null) {
            return null;
        }
        Bank bank = new Bank();
        bank.setBankId(getStringField(item, bank_id));
        bank.setBankName(getStringField(item, bank_name));
        bank.setMinLMIForCRA(getDecimalField(item, min_lmi_for_cra));
        if (getStringField(item, bank_specific_data_label) == null) {
            bank.setBankSpecificDataLabel(""); // Use "" when there is a null in the DB
        } else {
            bank.setBankSpecificDataLabel(getStringField(item, bank_specific_data_label));
        }
        return bank;
    }

    /**
     * Creates a School object from the corresponding Item retrieved from DynamoDB. If passed
     * null, it returns null.
     */
    private School createSchoolFromDynamoDbItem(Map<String,AttributeValue> item) {
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
        if (item.containsKey(school_lmi_eligible.name())) {
            school.setLmiEligible(getDecimalField(item, school_lmi_eligible));
        }
        school.setSLC(getStringField(item, school_slc));
        return school;
    }

    private Event createEventFromDynamoDbItem(Map<String,AttributeValue> item) {
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
        event.setDeliveryMethod(getStringField(item, event_delivery_method));
        event.setNumberStudents(getIntField(item, event_number_students));
        event.setNotes(getStringField(item, event_notes));
        String volunteerString = getStringField(item, event_volunteer_id);
        event.setVolunteerId(volunteerString.equals(NO_VOLUNTEER) ? null : volunteerString);
        return event;
    }

    /**
     * Creates a User object from the corresponding Item retrieved from DynamoDB. It will
     * be of the appropriate concrete sub-type of User. If passed null, it returns null.
     * None of the linked data is filled in.
     */
    private User createUserFromDynamoDbItem(Map<String,AttributeValue> item) {
        if (item == null) {
            return null;
        }
        final UserType userType = UserType.fromDBValue(getStringField(item, user_type));
        final User user = switch(userType) {
            case TEACHER -> {
                Teacher teacher = new Teacher();
                teacher.setSchoolId(getStringField(item, user_organization_id));
                yield teacher;
            }
            case VOLUNTEER -> {
                Volunteer volunteer = new Volunteer();
                volunteer.setBankId(getStringField(item, user_organization_id));
                volunteer.setApprovalStatus(ApprovalStatus.fromDBValue(getIntField(item,user_approval_status)));
                volunteer.setBankSpecificData(getStringField(item, user_bank_specific_data));
                volunteer.setStreetAddress(getStringField(item, user_street_address));
                volunteer.setSuiteOrFloorNumber(getStringField(item, user_suite_or_floor_number));
                volunteer.setCity(getStringField(item, user_city));
                volunteer.setState(getStringField(item, user_state));
                volunteer.setZip(getStringField(item, user_zip));
                yield volunteer;
            }
            case BANK_ADMIN -> {
                BankAdmin bankAdmin = new BankAdmin();
                bankAdmin.setBankId(getStringField(item, user_organization_id));
                bankAdmin.setApprovalStatus(ApprovalStatus.fromDBValue(getIntField(item,user_approval_status)));
                bankAdmin.setBankSpecificData(getStringField(item, user_bank_specific_data));
                bankAdmin.setStreetAddress(getStringField(item, user_street_address));
                bankAdmin.setSuiteOrFloorNumber(getStringField(item, user_suite_or_floor_number));
                bankAdmin.setCity(getStringField(item, user_city));
                bankAdmin.setState(getStringField(item,user_state));
                bankAdmin.setZip(getStringField(item, user_zip));
                yield bankAdmin;
            }
            case SITE_ADMIN -> {
                SiteAdmin siteAdmin = new SiteAdmin();
                yield siteAdmin;
            }
        };
        user.setUserId(getStringField(item, user_id));
        user.setEmail(getStringField(item, user_original_email));  //gets the email with case preserved as the user originally typed it
        user.setHashedPassword(getStringField(item, user_hashed_password));
        user.setSalt(getStringField(item, user_password_salt));
        user.setFirstName(getStringField(item, user_first_name));
        user.setLastName(getStringField(item, user_last_name));
        user.setPhoneNumber(getStringField(item, user_phone_number));
        user.setResetPasswordToken(getStringField(item, user_reset_password_token));
        user.setUserType(userType);
        return user;
    }

    // ========== Helper Functions for Common DB Queries ==========

    /**
     * We have some singleton items in our single-table design: allowedDates, allowedTimes, documents,
     * and siteSettings. This retrieves the (single) item for one of those. If the item does not
     * exist, this throws a RuntimeException.
     *
     * @param singletonItemKey the name of the key entry for the singleton item
     * @return the value of the item.
     */
    private Map<String,AttributeValue> getSingletonItem(String singletonItemKey) {
        final GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(table_key.name(), AttributeValue.builder().s(singletonItemKey).build()))
                .build();
        final GetItemResponse getItemResponse = dynamoDbClient.getItem(getItemRequest);
        if (!getItemResponse.hasItem()) {
            throw new RuntimeException("No " + singletonItemKey + " found. DB may not be initialized.");
        }
        return getItemResponse.item();
    }

    /**
     * Used when we do a lookup in an index and now need to retrieve the individual
     * item from the table using the table_key.
     */
    private Map<String,AttributeValue> getItemAfterIndexLookup(Map<String,AttributeValue> indexItem) {
        final AttributeValue tableKey = indexItem.get(table_key.name());
        final GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(table_key.name(), tableKey))
                .build();
        final GetItemResponse getItemResponse = dynamoDbClient.getItem(getItemRequest);
        return getItemResponse.item();
    }

    /**
     * This is used to look up an object from the database using its unique ID. It works on any objects that
     * use the "&lt;item-type>:&lt;unique-id>" pattern for their primary key in the table. Returns null if
     * no object of that type is found with this ID.
     *
     * @param keyPrefix the prefix eg: "school:"
     * @param createFunction the function for constructing an object from the database item
     * @param id the id of the object to return
     * @return the object or null if no object of that type is found with the given id
     * @param <T> the type of the object to return (eg: School)
     */
    private <T> T getObjectByUniqueId(String keyPrefix, CreateFunction<T> createFunction, String id) {
        // --- First, we look in the index to find out the key ---
        final String tableKey = keyPrefix + id;
        final GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(table_key.name(), AttributeValue.builder().s(tableKey).build()))
                .build();
        final GetItemResponse getItemResponse = dynamoDbClient.getItem(getItemRequest);
        if (getItemResponse.hasItem()) {
            return createFunction.create(getItemResponse.item());
        } else {
            return null; // That ID wasn't found, so return null
        }
    }

    /**
     * This looks in an index to find all the items that match the primary key of that index,
     * then finds the actual item for each, converts it into an object, sorts them, and
     * returns it.
     *
     * @param indexName the index to search in (eg: "ByUserOrganizationId")
     * @param keyField the field which is the key for that index (eg: user_organization_id)
     * @param keyValue the value of the key (eg: "122834382608201305")
     * @param createFunction the function to create the object from a database item
     * @param comparator the comparator to use for sorting
     * @return a list of matching items (which can be of length 0)
     * @param <T> the type of the objects in the list
     */
    private <T> List<T> getObjectsByIndexLookup(
            String indexName, SingleTableDbField keyField, String keyValue, CreateFunction<T> createFunction, Comparator<T> comparator
    ) {
        final QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .indexName(indexName)
                .keyConditionExpression(keyField.name() + " = :key_val")
                .expressionAttributeValues(Map.of(":key_val", AttributeValue.builder().s(keyValue).build()))
                .build();
        final QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

        // For each one found in the index, we have to do a getItem from the table to get the fields ---
        return queryResponse.items().stream()
                .map(indexItem -> createFunction.create(getItemAfterIndexLookup(indexItem)))
                .sorted(comparator)
                .toList();
    }

    /**
     * Deletes an item that is listed with a key of "&lt;type>:&lt;id>". This will NOT verify whether
     * the item exists beforehand -- if it doesn't exist this will complete with no errors.
     *
     * @param keyPrefix the prefix, eg: "school:"
     * @param id the id to delete
     */
    private void deleteItem(String keyPrefix, String id) {
        final String tableKey = keyPrefix + id;
        final DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(table_key.name(), AttributeValue.builder().s(tableKey).build()))
                .build();
        dynamoDbClient.deleteItem(deleteItemRequest);
    }

    /**
     * For a given type which is in the table using keys like "foo:83333323", this will return
     * all the objects of that type. It will EITHER use a table scan OR an index to do so.
     *
     * @param indexName the index to use, eg "ByBankId"
     * @param keyPrefix the prefix for keys, eg "bank:"
     * @param createFunction this is a function to create an instance from a dynamodb record
     * @param comparator this is used to sort the list before returning
     * @return the list of objects
     * @param <T> the type to return, Event, School, Bank, or User.
     */
    private <T> List<T> getAllUsingIndexOrScan(
            String indexName, String keyPrefix, CreateFunction<T> createFunction, Comparator<T> comparator
    ) {
        // DESIGN NOTE:
        // There are 2 ways we could query this. In approach 1, we use an index. It is
        // either an index by the field we want to sort on (eg: ByUserType for finding
        // users of a certain type) or an index containing only the items we want (eg:
        // the BySchoolId index for finding a school). This avoids having to do scan of
        // the full table -- the index directly contains an entry for every individual
        // record. HOWEVER, since we do not project all the fields into our indexes, we
        // will need to perform a getItem() for each individual item after looking it
        // up in the index.
        //
        // In approach 2, we do a full table scan using a filter specifying the items
        // we want including a prefix for the table_key (like "school:"). This avoids
        // having to make a second series of calls BUT it performs a full table scan.
        //
        // The best approach to use depends on the fraction of the table entries that
        // are schools. If that fraction is very small then we should use approach 1; if
        // it is even a moderate fraction of the table then we should use approach 2.
        //
        // Based on existing measurements it seems like approach 2 is better for our
        // actual data. ALSO NOTE - if we projected all the fields into the index then
        // we could read from the index quickly... but that's a good bit more data to
        // store (and there would be several copies of it).
        //
        // SO, this method implements BOTH approaches, but is hard-coded to always use
        // approach 2.
        final boolean USE_INDEX = false;
        if (USE_INDEX) {
            // Scan over the SchoolId index to get all the schools
            final ScanRequest scanRequest = ScanRequest.builder()
                    .tableName(tableName)
                    .indexName(indexName)
                    .build();
            final ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);

            // For each item, we have to look it up in the real table to get all the fields
            return scanResponse.items().stream()
                    .map(indexItem -> createFunction.create(getItemAfterIndexLookup(indexItem)))
                    .sorted(comparator)
                    .toList();
        } else {
            final ScanRequest scanRequest = ScanRequest.builder()
                    .tableName(tableName)
                    .filterExpression("begins_with( " + table_key.name() + ", :keyPrefix )")
                    .expressionAttributeValues(Map.of(":keyPrefix", AttributeValue.builder().s(keyPrefix).build()))
                    .build();
            final ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
            return scanResponse.items().stream()
                    .map(createFunction::create)
                    .sorted(comparator)
                    .toList();
        }
    }

    /**
     * This returns all users of a given type by doing a full table scan and finding the records
     * that match. It is intended for use only in getUsersByType().
     *
     * @param userType the user type to return. Note that this does NOT include BANK_ADMINs when
     *                 asked for Volunteers; the 4 types are distinct.
     * @return a List of the users. They will all be of the appropriate subtype of User.
     */
    private List<User> getUsersByTypeUsingScan(UserType userType) {
        final ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .filterExpression(
                        "begins_with( " + table_key.name() + ", :keyPrefix ) and " + user_type.name() + " = :userName")
                .expressionAttributeValues(Map.of(
                        ":keyPrefix", AttributeValue.builder().s("user:").build(),
                        ":userName", AttributeValue.builder().s(userType.getDBValue()).build()))
                .build();
        final ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        return scanResponse.items().stream()
                .map(this::createUserFromDynamoDbItem)
                .sorted(compareUsersByName)
                .toList();
    }

    /**
     * This returns all users of a given type by doing a lookup in the ByUserType index, then
     * looking in the table for that key. It is intended for use only in getUsersByType().
     *
     * @param userType the user type to return. Note that this does NOT include BANK_ADMINs when
     *                 asked for Volunteers; the 4 types are distinct.
     * @return a List of the users. They will all be of the appropriate subtype of User.
     */
    private List<User> getUsersByTypeUsingIndex(UserType userType) {
        return getObjectsByIndexLookup("ByUserType", user_type, userType.getDBValue(),
                this::createUserFromDynamoDbItem, compareUsersByName);
    }

    /**
     * This returns all users of a given type.
     *
     * @param userType the user type to return. Note that this does NOT include BANK_ADMINs when
     *                 asked for Volunteers; the 4 types are distinct.
     * @return a List of the users. They will all be of the appropriate subtype of User.
     */
    private List<User> getUsersByType(UserType userType) {
        // DESIGN NOTE:
        //
        // We use a different approach for the common types of users (a scan)
        // and the rare types of users (an index lookup).
        return switch(userType) {
            case VOLUNTEER, TEACHER -> getUsersByTypeUsingScan(userType);
            case BANK_ADMIN, SITE_ADMIN -> getUsersByTypeUsingIndex(userType);
        };
    }

    /**
     * Called in the services that insert a user; throws an exception if the email is in use by
     * anyone other than the indicated userId. Email comparison ignores case (so it doesn't matter
     * what case the passed in email has).
     *
     * @param userId the userId of the user who is allowed to be using this email or NULL if no one should be using it
     * @param email email to check if already in use
     */
    private void verifyEmailNotInUseByAnyoneElse(String userId, String email) throws SQLException, EmailAlreadyInUseException {
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Not a valid email: '" + email + "'.");
        }
        User otherUserWithSameEmail = getUserByEmail(email);
        if (otherUserWithSameEmail != null && !otherUserWithSameEmail.getUserId().equals(userId)) {
            throw new EmailAlreadyInUseException();
        }
    }

    // ========== Comparators for sorting ==========

    /** Comparator for sorting banks. */
    private final Comparator<Bank> compareBanks = Comparator.comparing(Bank::getBankName);

    /** Comparator for sorting schools. */
    private final Comparator<School> compareSchools = Comparator.comparing(School::getName);

    /** Comparator for sorting events. */
    private final Comparator<Event> compareEvents =
            Comparator.comparing(Event::getEventDate).thenComparing(Event::getEventId);

    private final Comparator<User> compareUsersByName =
            Comparator.comparing(User::getLastName).thenComparing(User::getFirstName);

    // ========== Methods of DatabaseFacade Class ==========

    @Override
    public int getFieldLength(DatabaseField field) {
        // FIXME: We made a new enum, and now this takes the wrong type! I should probably fix that,
        //   but I'm not sure how.
        return FIELD_LENGTHS.get(field);
    }

    @Override
    public User getUserById(String userId) throws SQLException, InconsistentDatabaseException {
        return getObjectByUniqueId("user:", this::createUserFromDynamoDbItem, userId);
    }

    @Override
    public User getUserByEmail(String email) throws SQLException, InconsistentDatabaseException {
        // --- First, we look in the index to find out the key ---
        final QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .indexName("ByUserEmail")
                .keyConditionExpression("user_email = :email")
                .expressionAttributeValues(Map.of(":email", AttributeValue.builder().s(email.toLowerCase()).build()))
                .build();
        final QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

        final AttributeValue keyValue = switch (queryResponse.count()) {
            case 0 -> null;
            case 1 -> queryResponse.items().get(0).get(table_key.name());
            default -> throw new InconsistentDatabaseException(
                    "More than one user with email address '" + email + "'.");
        };

        // --- If there was no user, return null ---
        if (keyValue == null) {
            return null;
        }

        // --- Then we do the actual getItem from the table to get the fields ---
        final GetItemRequest getItemRequest = GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(table_key.name(), keyValue))
                .build();
        final GetItemResponse getItemResponse = dynamoDbClient.getItem(getItemRequest);
        return createUserFromDynamoDbItem(getItemResponse.item());
    }

    @Override
    public void modifyUserPersonalFields(EditPersonalDataFormData formData) throws SQLException, EmailAlreadyInUseException, InconsistentDatabaseException {
        verifyEmailNotInUseByAnyoneElse(formData.getUserId(), formData.getEmail());
        final String itemKey = "user:" + formData.getUserId();
        // Because emails are commonly case-insensitive, we store a lower-case version of the email
        // ("user_email") in addition to the original format ("user_original_email") and enforce
        // uniqueness on the lower-case field.
        final String userEmail = formData.getEmail().toLowerCase();

        final UpdateItemRequest updateItemRequest = new UpdateItemBuilder(tableName, itemKey)
                .withString(user_email, userEmail)
                .withString(user_original_email, formData.getEmail())
                .withString(user_first_name, formData.getFirstName())
                .withString(user_last_name, formData.getLastName())
                .withString(user_phone_number, formData.getPhoneNumber())
                .build();
        dynamoDbClient.updateItem(updateItemRequest);
    }

    @Override
    public void modifyVolunteerPersonalFields(EditVolunteerPersonalDataFormData formData) throws SQLException, EmailAlreadyInUseException, InconsistentDatabaseException {
        verifyEmailNotInUseByAnyoneElse(formData.getUserId(), formData.getEmail());
        final String itemKey = "user:" + formData.getUserId();
        // Because emails are commonly case-insensitive, we store a lower-case version of the email
        // ("user_email") in addition to the original format ("user_original_email") and enforce
        // uniqueness on the lower-case field.
        final String userEmail = formData.getEmail().toLowerCase();
        final UpdateItemRequest updateItemRequest = new UpdateItemBuilder(tableName, itemKey)
                .withString(user_email, userEmail)
                .withString(user_original_email, formData.getEmail())
                .withString(user_first_name, formData.getFirstName())
                .withString(user_last_name, formData.getLastName())
                .withString(user_phone_number, formData.getPhoneNumber())
                .withString(user_bank_specific_data, formData.getBankSpecificData())
                .withString(user_street_address, formData.getStreetAddress())
                .withString(user_suite_or_floor_number, formData.getSuiteOrFloorNumber())
                .withString(user_city, formData.getCity())
                .withString(user_state, formData.getState())
                .withString(user_zip, formData.getZip())
                .build();
        dynamoDbClient.updateItem(updateItemRequest);
    }

    @Override
    public void modifyTeacherSchool(String userId, String organizationId) throws SQLException, NoSuchSchoolException, NoSuchUserException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public Teacher insertNewTeacher(TeacherRegistrationFormData formData, String hashedPassword, String salt) throws SQLException, NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<Event> getEventsByTeacher(String teacherId) throws SQLException {
        return getObjectsByIndexLookup("ByEventTeacherId", event_teacher_id, teacherId,
                this::createEventFromDynamoDbItem, compareEvents);
    }

    @Override
    public List<Event> getAllAvailableEvents() throws SQLException {
        // NOTE: This is incredibly similar to getAllEvents(). Normally, I would try to make
        // the two of them share code. HOWEVER, this is the single most important function
        // in the whole interface -- it is the slow step that powers the signup process.
        // So a little duplication is acceptable for performance here. It even duplicates
        // code from getAllUsingIndexOrScan(). Basically, all the normal "try not to
        // duplicate code too much" rules are disregarded for this function.

        // --- get the schools ---
        Map<String,School> schools = getAllSchools().stream()
                .collect(Collectors.toMap(
                        School::getSchoolId,
                        x -> x
                ));

        // --- get the banks ---
        Map<String,Bank> banks = getAllBanks().stream()
                .collect(Collectors.toMap(
                        Bank::getBankId,
                        x -> x
                ));

        // -- get the users --
        Map<String,User> users = getAllUsers().stream()
                .collect(Collectors.toMap(
                        User::getUserId,
                        x -> x
                ));

        // --- get the events and populate data in them ---
        final ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .filterExpression(
                        "begins_with( " + table_key.name() + ", :keyPrefix ) AND " +
                        "( attribute_not_exists(" + event_volunteer_id.name() + ") OR " +
                        event_volunteer_id.name() + " = :zero )"
                )
                .expressionAttributeValues(Map.of(
                        ":keyPrefix", AttributeValue.builder().s("event:").build(),
                        ":zero", AttributeValue.builder().s("0").build()
                ))
                .build();
        final ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        return scanResponse.items().stream()
                .map(item -> {
                    // --- make the event ---
                    final Event event = createEventFromDynamoDbItem(item);

                    // --- populate the teacher ---
                    final Teacher teacher = (Teacher) users.get(event.getTeacherId());
                    // if LinkedSchool is not already set for the teacher, set it
                    if (teacher.getLinkedSchool() == null) {
                        teacher.setLinkedSchool(schools.get(teacher.getSchoolId()));
                    }
                    event.setLinkedTeacher(teacher);

                    // --- populate the volunteer ---
                    if (event.getVolunteerId() != null) {
                        final Volunteer volunteer = (Volunteer) users.get(event.getVolunteerId());
                        // if linkedBank is not already set for the volunteer, set it
                        if (volunteer.getLinkedBank() == null) {
                            volunteer.setLinkedBank(banks.get(volunteer.getBankId()));
                        }
                        event.setLinkedVolunteer(volunteer);
                    }

                    return event;
                })
                .sorted(compareEvents)
                .toList();
    }

    @Override
    public List<Event> getEventsByVolunteer(String volunteerId) throws SQLException {
        if (volunteerId == null) {
            throw new RuntimeException("This method doesn't handle null for volunteerId.");
            // NOTE: It *could* handle that if we wanted it to, but for now that's just basically an assert
        }
        return getObjectsByIndexLookup("ByEventVolunteerId", event_volunteer_id, volunteerId,
                this::createEventFromDynamoDbItem, compareEvents);
    }

    @Override
    public List<Event> getEventsByVolunteerWithTeacherAndSchool(String volunteerId) throws SQLException {
        // --- Load the volunteer, since we know it'll be needed if there are ANY events ---
        final Volunteer volunteer = (Volunteer) getUserById(volunteerId);
        volunteer.setLinkedBank(getBankById(volunteer.getBankId()));

        // --- Now get the events ---
        return getEventsByVolunteer(volunteerId).stream()
                .map(event -> {
                    try {
                        // --- populate the teacher ---
                        final Teacher teacher = (Teacher) getUserById(event.getTeacherId());
                        teacher.setLinkedSchool(getSchoolById(teacher.getSchoolId()));
                        event.setLinkedTeacher(teacher);

                        // --- populate the volunteer ---
                        assert event.getVolunteerId().equals(volunteerId);
                        event.setLinkedVolunteer(volunteer);

                        // --- the event is ready now ---
                        return event;
                    } catch(SQLException err) {
                        throw new RuntimeException(err); // these things won't throw SQLException ANYWAY.
                    }
                })
                .sorted(compareEvents)
                .toList();
    }

    @Override
    public void insertEvent(String teacherId, CreateEventFormData formData) throws SQLException {
        final String uniqueId = dynamoDBHelper.createUniqueId();
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(new ItemBuilder("event", event_id, uniqueId)
                        .withString(event_teacher_id, teacherId)
                        .withString(event_date, PrettyPrintingDate.fromJavaUtilDate(formData.getEventDate()).getParseable())
                        .withString(event_time, formData.getEventTime())
                        .withInt(event_grade, Integer.parseInt(formData.getGrade()))
                        .withString(event_delivery_method, formData.getDeliveryMethod())
                        .withInt(event_number_students, Integer.parseInt(formData.getNumberStudents()))
                        .withString(event_notes, formData.getNotes())
                        .withString(event_volunteer_id, NO_VOLUNTEER)
                        .build())
                .conditionExpression("attribute_not_exists(" + table_key.name() + ")") // verify it is unique
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public void volunteerForEvent(String eventId, String volunteerId) throws SQLException, NoSuchEventException, EventAlreadyHasAVolunteerException {
        final String volunteerIdToUse = volunteerId == null ? NO_VOLUNTEER : volunteerId;

        final UpdateItemBuilder builder = new UpdateItemBuilder(tableName, "event:" + eventId)
                .withString(event_volunteer_id, volunteerIdToUse);
        if (volunteerId == null) {
            builder.withStringFieldNotEqualsCondition(event_volunteer_id, NO_VOLUNTEER);
        } else {
            builder.withStringFieldEqualsCondition(event_volunteer_id, NO_VOLUNTEER);
        }
        final UpdateItemRequest updateItemRequest = builder.build();
        dynamoDbClient.updateItem(updateItemRequest);
    }

    @Override
    public List<Volunteer> getVolunteersByBank(String bankId) throws SQLException {
        // We look for users with this organization id
        final QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .indexName("ByUserOrganizationId")
                .keyConditionExpression("user_organization_id = :bank_id")
                .expressionAttributeValues(Map.of(":bank_id", AttributeValue.builder().s(bankId).build()))
                .build();
        final QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

        // For each one found in the index, we have to do a getItem from the table to get the fields ---
        return queryResponse.items().stream()
                .map(indexItem -> (Volunteer) createUserFromDynamoDbItem(getItemAfterIndexLookup(indexItem)))
                .sorted(compareUsersByName)
                .toList();
    }

    @Override
    public List<BankAdmin> getBankAdminsByBank(String bankId) throws SQLException {
        return getVolunteersByBank(bankId).stream()
                .filter(volunteer -> volunteer instanceof BankAdmin)
                .map(volunteer -> (BankAdmin) volunteer)
                .toList();
    }

    @Override
    public Volunteer insertNewVolunteer(VolunteerRegistrationFormData formData, String hashedPassword, String salt) throws SQLException, NoSuchBankException, EmailAlreadyInUseException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public Bank getBankById(String bankId) throws SQLException {
        return getObjectByUniqueId("bank:", this::createBankFromDynamoDbItem, bankId);
    }

    @Override
    public School getSchoolById(String schoolId) throws SQLException {
        return getObjectByUniqueId("school:", this::createSchoolFromDynamoDbItem, schoolId);
    }

    @Override
    public List<School> getAllSchools() throws SQLException {
        return getAllUsingIndexOrScan("BySchoolId", "school:", this::createSchoolFromDynamoDbItem, compareSchools);
    }

    @Override
    public List<Bank> getAllBanks() throws SQLException {
        return getAllUsingIndexOrScan("ByBankId", "bank:", this::createBankFromDynamoDbItem, compareBanks);
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        return getAllUsingIndexOrScan("ByUserEmail", "user:", this::createUserFromDynamoDbItem, compareUsersByName);
    }

    @Override
    public List<PrettyPrintingDate> getAllowedDates() throws SQLException {
        final Map<String,AttributeValue> item = getSingletonItem("allowedDates");
        final AttributeValue allowedDateValues = item.get(allowed_date_values.name());
        if (allowedDateValues == null) {
            throw new RuntimeException("No allowed dates found. DB may not be initialized.");
        }
        final List<PrettyPrintingDate> result = allowedDateValues.ss().stream()
                .map(dateStr -> {
                    try {
                        return PrettyPrintingDate.fromParsableDate(dateStr);
                    } catch (ParseException err) {
                        throw new RuntimeException("Invalid date in the database: '" + dateStr + "'.", err);
                    }
                })
                .sorted()
                .toList();
        return result;
    }

    @Override
    public List<String> getAllowedTimes() throws SQLException {
        final Map<String,AttributeValue> item = getSingletonItem("allowedTimes");
        final AttributeValue allowedTimeValuesWithSort = item.get(allowed_time_values_with_sort.name());
        if (allowedTimeValuesWithSort == null) {
            throw new RuntimeException("No allowed times found. DB may not be initialized.");
        }

        // Create a record type we can sort on
        record SortKeyAndTimeValue(int sortKey, String timeValue) implements Comparable<SortKeyAndTimeValue> {
            @Override
            public int compareTo(SortKeyAndTimeValue o) {
                return Integer.compare(this.sortKey, o.sortKey);
            }
        }
        return allowedTimeValuesWithSort.ss().stream()
                .map(x -> {
                    String[] pieces = x.split("\\|",2); // split on first vertical-bar
                    return new SortKeyAndTimeValue(Integer.parseInt(pieces[0]), pieces[1]);
                })
                .sorted()
                .map(x -> x.timeValue)
                .toList();
    }

    @Override
    public void deleteSchool(String schoolId) throws SQLException, NoSuchSchoolException {
        // Note: Does NOT verify whether the school exists and throw NoSuchSchoolException where appropriate
        // Note: Does not verify whether the school is referenced anywhere.
        deleteItem("school:", schoolId);
    }

    @Override
    public void deleteBankAndBankVolunteers(String bankId) throws SQLException, NoSuchBankException, BankHasVolunteersException, VolunteerHasEventsException {
        // First, delete all the volunteers
        List<Volunteer> volunteers = getVolunteersByBank(bankId);
        for (Volunteer volunteer : volunteers) {
            try {
                deleteVolunteer(volunteer.getUserId());
            } catch (NoSuchUserException err) {
                throw new RuntimeException ("Volunteer found but then could not be deleted.");
            }
        }
        // Then delete the bank
        deleteItem("bank:", bankId);
    }

    @Override
    public void deleteVolunteer(String volunteerId) throws SQLException, NoSuchUserException, VolunteerHasEventsException {
        List<Event> events = getEventsByVolunteer(volunteerId);
        if (!events.isEmpty()) {
            throw new VolunteerHasEventsException();
        }
        deleteItem("user:", volunteerId);
    }

    @Override
    public void deleteTeacher(String teacherId) throws SQLException, NoSuchUserException, TeacherHasEventsException {
        // Note: Does NOT verify whether the teacher exists and throw NoSuchSchoolException where appropriate
        if (!getEventsByTeacher(teacherId).isEmpty()) {
            throw new TeacherHasEventsException();
        }
        deleteItem("user:", teacherId);
    }

    @Override
    public void deleteEvent(String eventId) throws SQLException, NoSuchEventException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<Event> getAllEvents() throws SQLException, InconsistentDatabaseException {
        // --- get the schools ---
        Map<String,School> schools = getAllSchools().stream()
                .collect(Collectors.toMap(
                        School::getSchoolId,
                        x -> x
                ));

        // --- get the banks ---
        Map<String,Bank> banks = getAllBanks().stream()
                .collect(Collectors.toMap(
                        Bank::getBankId,
                        x -> x
                ));

        // -- get the users --
        Map<String,User> users = getAllUsers().stream()
                .collect(Collectors.toMap(
                        User::getUserId,
                        x -> x
                ));

        // --- get the events and populate data in them ---
        return getAllUsingIndexOrScan("ByEventId", "event:", this::createEventFromDynamoDbItem, compareEvents).stream()
                .map(event -> {
                    final Teacher teacher = (Teacher) users.get(event.getTeacherId());
                    // if LinkedSchool is not already set for the teacher, set it
                    if (teacher.getLinkedSchool() == null) {
                        teacher.setLinkedSchool(schools.get(teacher.getSchoolId()));
                    }
                    event.setLinkedTeacher(teacher);
                    if (event.getVolunteerId() != null) {
                        final Volunteer volunteer = (Volunteer) users.get(event.getVolunteerId());
                        // if linkedBank is not already set for the volunteer, set it
                        if (volunteer.getLinkedBank() == null) {
                            volunteer.setLinkedBank(banks.get(volunteer.getBankId()));
                        }
                        event.setLinkedVolunteer(volunteer);
                    }
                    return event;
                })
                .sorted(compareEvents)
                .toList();
    }

    @Override
    public Event getEventById(String eventId) throws SQLException {
        return getObjectByUniqueId("event:", this::createEventFromDynamoDbItem, eventId);
    }

    @Override
    public void modifySchool(EditSchoolFormData school) throws SQLException, NoSuchSchoolException {
        // FIXME: This might be wrong because it's doing a PUT not a MODIFY!!
        // This approach will CREATE the school if it doesn't exist.
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(new ItemBuilder("school", school_id, school.getSchoolId())
                        .withString(school_name, school.getSchoolName())
                        .withString(school_addr1, school.getSchoolAddress1())
                        .withString(school_city, school.getCity())
                        .withString(school_state, school.getState())
                        .withString(school_zip, school.getZip())
                        .withString(school_county, school.getCounty())
                        .withString(school_district, school.getDistrict())
                        .withString(school_phone, school.getPhone())
                        .withString(school_lmi_eligible, school.getLmiEligible())
                        .withString(school_slc, school.getSLC())
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public void insertNewBankAndAdmin(CreateBankFormData formData) throws SQLException, EmailAlreadyInUseException {
        // FIXME: It might be nice to enforce that the bank name is unique
        // -- Insert bank --
        final String uniqueBankId = dynamoDBHelper.createUniqueId();
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(new ItemBuilder("bank", bank_id, uniqueBankId)
                        .withString(bank_name, formData.getBankName())
                        .build())
                .conditionExpression("attribute_not_exists(" + table_key.name() + ")") // verify it is unique
                .build();
        dynamoDbClient.putItem(putItemRequest);
        // -- Insert bank admin --
        // If it has an email we presume it has a bank admin, and if not we assume it doesn't
        final boolean formHasBankAdmin = ! (formData.getEmail() == null || formData.getEmail().isEmpty());
        if (formHasBankAdmin) {
            NewBankAdminFormData newFormData = new NewBankAdminFormData();
            newFormData.setBankId(uniqueBankId);
            newFormData.setFirstName(formData.getFirstName());
            newFormData.setLastName(formData.getLastName());
            newFormData.setEmail(formData.getEmail());
            newFormData.setPhoneNumber(formData.getPhoneNumber());
            insertNewBankAdmin(newFormData);
        }
    }

    @Override
    public void insertNewBankAdmin(NewBankAdminFormData formData) throws SQLException, EmailAlreadyInUseException {
        verifyEmailNotInUseByAnyoneElse(null, formData.getEmail());
        final String uniqueId = dynamoDBHelper.createUniqueId();
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(new ItemBuilder("user", user_id, uniqueId)
                        .withString(user_type, UserType.BANK_ADMIN.getDBValue())
                        .withString(user_email, formData.getEmail().toLowerCase())   //for purposes of user lookup only allow one user per email regardless of case
                        .withString(user_original_email,formData.getEmail())  //preserves case for purposes of sending email
                        .withString(user_first_name, formData.getFirstName())
                        .withString(user_last_name, formData.getLastName())
                        .withString(user_phone_number, formData.getPhoneNumber())
                        .withString(user_organization_id, formData.getBankId())
                        .withInt(user_approval_status, ApprovalStatus.CHECKED.getDbValue())
                        .build())
                .conditionExpression("attribute_not_exists(" + table_key.name() + ")") // verify it is unique
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public void modifyBank(EditBankFormData formData) throws SQLException, NoSuchBankException {
        // This approach will CREATE the bank if it doesn't exist instead of throwing an exception
        // FIXME: This does a put, instead of a modify. Is that right?
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(new ItemBuilder("bank", bank_id, formData.getBankId())
                        .withString(bank_name, formData.getBankName())
                        .withString(min_lmi_for_cra, formData.getMinLMIForCRA())
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public void setUserType(String userId, UserType userType) throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void setBankSpecificFieldLabel(SetBankSpecificFieldLabelFormData formData) throws SQLException, NoSuchBankException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void insertNewSchool(CreateSchoolFormData school) throws SQLException {
        final String uniqueId = dynamoDBHelper.createUniqueId();
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(new ItemBuilder("school", school_id, uniqueId)
                        .withString(school_name, school.getSchoolName())
                        .withString(school_addr1, school.getSchoolAddress1())
                        .withString(school_city, school.getCity())
                        .withString(school_state, school.getState())
                        .withString(school_zip, school.getZip())
                        .withString(school_county, school.getCounty())
                        .withString(school_district, school.getDistrict())
                        .withString(school_phone, school.getPhone())
                        .withString(school_lmi_eligible, school.getLmiEligible())
                        .withString(school_slc, school.getSLC())
                        .build())
                .conditionExpression("attribute_not_exists(" + table_key.name() + ")") // verify it is unique
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public void insertNewAllowedDate(AddAllowedDateFormData formData) throws SQLException, AllowedDateAlreadyInUseException {
        // NOTE: Instead of checking for AllowedDateAlreadyInUseException, we will simply leave as-is if already in use
        // --- get the existing value ---
        final List<PrettyPrintingDate> oldAllowedDates = getAllowedDates();
        // --- tweak it as needed ---
        final String parseableToAdd = formData.getParsableDateStr();
        // add it at the end without duplicates by dropping any that match before adding it at the end
        final String[] newAllowedDates = Stream.concat(
                oldAllowedDates.stream()
                    .map(PrettyPrintingDate::getParseable)
                    .filter(s -> !s.equals(parseableToAdd)),
                Stream.of(parseableToAdd))
                .toArray(String[]::new);
        // --- write it out (overwriting the existing one) ---
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(new ItemBuilder("allowedDates")
                        .withStrings(allowed_date_values, newAllowedDates)
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public void insertNewAllowedTime(AddAllowedTimeFormData formData) throws SQLException, AllowedTimeAlreadyInUseException, NoSuchAllowedTimeException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void modifyEvent(EventRegistrationFormData formData) throws SQLException, NoSuchEventException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void updateUserCredential(String userId, String hashedPassword, String salt) throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void updateResetPasswordToken(String userId, String resetPasswordToken) throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void updateApprovalStatusById(String volunteerId, ApprovalStatus approvalStatus) throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void deleteAllowedTime(String time) throws SQLException, NoSuchAllowedTimeException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void deleteAllowedDate(PrettyPrintingDate date) throws SQLException, NoSuchAllowedDateException {
        // --- get the existing value ---
        final List<PrettyPrintingDate> oldAllowedDates = getAllowedDates();
        // --- tweak it as needed ---
        final String parseableToDelete = date.getParseable();
        final String[] newAllowedDates = oldAllowedDates.stream()
                .map(PrettyPrintingDate::getParseable)
                .filter(s -> !s.equals(parseableToDelete))
                .toArray(String[]::new);
        // --- write it out (overwriting the existing one) ---
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(new ItemBuilder("allowedDates")
                        .withStrings(allowed_date_values, newAllowedDates)
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public SiteStatistics getSiteStatistics() throws SQLException {
        int numEvents = 0;
        int numMatchedEvents = 0;
        int numUnmatchedEvents = 0;
        int num3rdGradeEvents = 0;
        int num4thGradeEvents = 0;
        int numInPersonEvents = 0;
        int numVirtualEvents = 0;
        final Set<String> volunteerIdsActuallySignedUp = new HashSet<>();
        final Set<String> teacherIdsWithClassesThatHaveVolunteers = new HashSet<>();

        for (final Event event : getAllEvents()) {
            numEvents += 1;
            if (event.getVolunteerId() == null) {
                numUnmatchedEvents += 1;
            } else {
                numMatchedEvents += 1;
                volunteerIdsActuallySignedUp.add(event.getVolunteerId());
                teacherIdsWithClassesThatHaveVolunteers.add(event.getTeacherId());
            }
            if (event.getGrade().equals("3")) {
                num3rdGradeEvents += 1;
            } else if (event.getGrade().equals("4")) {
                num4thGradeEvents += 1;
            }
            if (event.getDeliveryMethod().equals("P")) {
                numInPersonEvents += 1;
            } else if (event.getDeliveryMethod().equals("V")) {
                numVirtualEvents += 1;
            }
        }
        final int numVolunteers = volunteerIdsActuallySignedUp.size();
        final int numParticipatingTeachers = teacherIdsWithClassesThatHaveVolunteers.size();
        // Loop through the teachers separately to count schools (more efficient than querying each one separately)
        Set<String> schoolIdsWithClassesThatHaveVolunteers = new HashSet<String>();
        for (User user : getUsersByType(UserType.TEACHER)) {
            final Teacher teacher = (Teacher) user;
            if (teacherIdsWithClassesThatHaveVolunteers.contains(teacher.getUserId())) {
                schoolIdsWithClassesThatHaveVolunteers.add(teacher.getSchoolId());
            }
        }
        final int numParticipatingSchools = schoolIdsWithClassesThatHaveVolunteers.size();

        final SiteStatistics siteStatistics = new SiteStatistics();
        siteStatistics.setNumEvents(numEvents);
        siteStatistics.setNumMatchedEvents(numMatchedEvents);
        siteStatistics.setNumUnmatchedEvents(numUnmatchedEvents);
        siteStatistics.setNum3rdGradeEvents(num3rdGradeEvents);
        siteStatistics.setNum4thGradeEvents(num4thGradeEvents);
        siteStatistics.setNumInPersonEvents(numInPersonEvents);
        siteStatistics.setNumVirtualEvents(numVirtualEvents);
        siteStatistics.setNumVolunteers(numVolunteers);
        siteStatistics.setNumParticipatingTeachers(numParticipatingTeachers);
        siteStatistics.setNumParticipatingSchools(numParticipatingSchools);
        return siteStatistics;
    }

    @Override
    public List<Teacher> getTeachersWithSchoolData() throws SQLException {
        // Step 1: read in all the schools so we can easily add them in.
        //   (This would be inefficient if there were lots of schools with
        //   no teachers, but we expect instead that most schools have
        //   multiple teachers, and at least one.)
        final Map<String,School> schoolsById = getAllSchools().stream()
                .collect(Collectors.toMap(
                        School::getSchoolId,
                        x -> x
                ));

        // Step 2: read in the teachers, then set the school for each one.
        return getUsersByType(UserType.TEACHER).stream()
                .map(user -> {
                    final Teacher teacher = (Teacher) user;
                    teacher.setLinkedSchool(schoolsById.get(teacher.getSchoolId()));
                    return teacher;
                })
                .toList();
    }

    @Override
    public List<Teacher> getTeachersBySchool(String schoolId) throws SQLException {
        return getObjectsByIndexLookup("ByUserOrganizationId", user_organization_id, schoolId,
                item -> (Teacher) createUserFromDynamoDbItem(item),
                compareUsersByName::compare
                );
    }

    @Override
    public List<Volunteer> getVolunteersWithBankData() throws SQLException {
        // Step 1: read in all the Banks so we can easily add them in.
        //   (This would be inefficient if there were lots of Banks with
        //   no volunteers, but we expect instead that most banks have
        //   multiple volunteers, and at least one.)
        final Map<String,Bank> banksById = getAllBanks().stream()
                .collect(Collectors.toMap(
                        Bank::getBankId,
                        x -> x
                ));

        // Step 2: read in the Volunteers (including BankAdmins), then set the bank
        //   for each one.
        return Stream.concat(
                getUsersByType(UserType.VOLUNTEER).stream(),
                getUsersByType(UserType.BANK_ADMIN).stream())
                .map(user -> {
                    final Volunteer volunteer = (Volunteer) user;
                    volunteer.setLinkedBank(banksById.get(volunteer.getBankId()));
                    return volunteer;
                })
                .sorted(compareUsersByName)
                .toList();
    }

    @Override
    public List<Teacher> getMatchedTeachers() throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<Teacher> getUnMatchedTeachers() throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<Volunteer> getMatchedVolunteers() throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<Volunteer> getUnMatchedVolunteers() throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<BankAdmin> getBankAdmins() throws SQLException {
        return getUsersByType(UserType.BANK_ADMIN).stream()
                .map(x -> (BankAdmin) x)
                .toList();
    }

    @Override
    public Map<String, String> getSiteSettings() throws SQLException {
        final Map<String,AttributeValue> item = getSingletonItem("siteSettings");
        final AttributeValue keyvalues = item.get(site_setting_entries.name());
        if (keyvalues == null) {
            throw new RuntimeException("No site settings found. DB may not be initialized.");
        }
        final Map<String,String> result = new HashMap<>();
        for (String entry : keyvalues.ss()) {
            final String[] keyAndValue = entry.split("=",2);
            switch (keyAndValue.length) {
                case 1: result.put(keyAndValue[0], ""); break;
                case 2: result.put(keyAndValue[0], keyAndValue[1]); break;
                default: throw new RuntimeException("Invalid data in site settings.");
            }
        }
        return result;
    }

    @Override
    public void modifySiteSetting(String settingName, String settingValue) throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public SortedSet<Document> getDocuments() throws SQLException {
        final Map<String,AttributeValue> item = getSingletonItem("documents");
        if (item == null) {
            throw new RuntimeException("No documents found. DB may not be initialized.");
        }
        final AttributeValue entries = item.get(documents_values.name());
        final Function<String,Boolean> readBoolLetter = s -> switch(s) {
            case "T" -> true;
            case "F" -> false;
            default -> throw new RuntimeException("Invalid data in documents.");
        };
        SortedSet<Document> result = new TreeSet<>();
        for (String documentEntry : entries.ss()) {
            final String[] fields = documentEntry.split("\\|",4);
            if (fields.length != 4) {
                throw new RuntimeException("Invalid data in documents.");
            }
            final boolean showToTeacher = readBoolLetter.apply(fields[0]);
            final boolean showToVolunteer = readBoolLetter.apply(fields[1]);
            final boolean showToBankAdmin = readBoolLetter.apply(fields[2]);
            final String docName = fields[3];
            final Document document = new Document(docName, showToTeacher, showToVolunteer, showToBankAdmin);
            result.add(document);
        }
        return result;
    }

    @Override
    public void createOrModifyDocument(Document document) throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void deleteDocument(String documentName) throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }
}
