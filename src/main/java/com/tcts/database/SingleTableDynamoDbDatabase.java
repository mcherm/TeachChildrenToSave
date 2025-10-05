package com.tcts.database;

import com.tcts.common.Configuration;
import com.tcts.common.PrettyPrintingDate;
import com.tcts.common.SitesConfig;
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
import com.tcts.exception.*;  //fixme
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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tcts.database.DatabaseField.*;


/**
 * An implementation of the facade that stores everything in a single DynamoDB Database (with
 * records of different types).
 */
public class SingleTableDynamoDbDatabase implements DatabaseFacade {

    // ========== Constants ==========

    /** An indicator value for event_volunteer_id that means no volunteer; used in place of null. */
    private final static String NO_VOLUNTEER = "0";
    private static final SitesConfig sitesConfig = new SitesConfig();
    private static final Configuration configuration = new Configuration();

    /* Constants used for the field lengths. Only has the fields of type String, not int or ID. */
    private final Map<DatabaseField,Integer> FIELD_LENGTHS = new HashMap<>() {{
        put(DatabaseField.event_time, 30);
        put(DatabaseField.event_grade, 30);
        put(DatabaseField.event_delivery_method, 30);
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
    private final DynamoDBHelper dynamoDBHelper;


    // ========== Constructor ==========

    /**
     * Constructor.
     */
    public SingleTableDynamoDbDatabase(Configuration configuration) {
        dynamoDbClient = connectToDB(configuration);
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
    public static String getTableName() {
        final String site = sitesConfig.getSite();
        final String environment = configuration.getProperty("dynamoDB.environment", "dev");
        return "TCTS." + site + "." + environment;
    }

    // ========== Create Object Functions ==========

    /**
     * This retrieves a field which is a string from an Item. If the field is missing
     * (null) it will return an empty string ("") instead of null. This mirrors what
     * we do when storing the value, and is done because DynamoDB is not able to store
     * empty string values.
     */
    private static String getStringField(Map<String,AttributeValue> item, DatabaseField field) {
        final AttributeValue fieldValue = item.get(field.name());
        return fieldValue == null ? "" : fieldValue.s();
    }

    /**
     * This retrieves a field which is an int from an Item.
     *
     * @throws NumberFormatException if the field is null or is not an integer
     */
    private static int getIntField(Map<String,AttributeValue> item, DatabaseField field) {
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
    private static BigDecimal getDecimalField(Map<String,AttributeValue> item, DatabaseField field) {
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
    static Bank createBankFromDynamoDbItem(Map<String,AttributeValue> item) {
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
    static School createSchoolFromDynamoDbItem(Map<String,AttributeValue> item) {
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

    static Event createEventFromDynamoDbItem(Map<String,AttributeValue> item) {
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
        // in the old singleTable event_grade was an int. If reading normally fails we'll try that.
        String grade = getStringField(item, event_grade);
        if (grade == null) {
            try {
                int intGrade = getIntField(item, event_grade);
                grade = switch (intGrade) {
                    case 3 -> "3rd Grade";
                    case 4 -> "4th Grade";
                    default -> Integer.toString(intGrade);
                };
            } catch(NumberFormatException err) {
                // leave grade as null
            }
        }
        event.setGrade(grade);
        event.setDeliveryMethod(getStringField(item, event_delivery_method));
        event.setNumberStudents(getIntField(item, event_number_students));
        event.setNotes(getStringField(item, event_notes));
        final String volunteerStringField = getStringField(item, event_volunteer_id);
        final String volunteerString = switch(volunteerStringField) {
            case NO_VOLUNTEER -> null;
            case "" -> null;
            default -> volunteerStringField;
        };
        event.setVolunteerId(volunteerString);
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
                .tableName(getTableName())
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
                .tableName(getTableName())
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
                .tableName(getTableName())
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
            String indexName, DatabaseField keyField, String keyValue, CreateFunction<T> createFunction, Comparator<T> comparator
    ) {
        final QueryRequest queryRequest = QueryRequest.builder()
                .tableName(getTableName())
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
                .tableName(getTableName())
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
                    .tableName(getTableName())
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
                    .tableName(getTableName())
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
                .tableName(getTableName())
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
    private void verifyEmailNotInUseByAnyoneElse(String userId, String email) throws EmailAlreadyInUseException {
        if (email == null || email.isEmpty()) {
            throw new RuntimeException("Not a valid email: '" + email + "'.");
        }
        User otherUserWithSameEmail = getUserByEmail(email);
        if (otherUserWithSameEmail != null && !otherUserWithSameEmail.getUserId().equals(userId)) {
            throw new EmailAlreadyInUseException();
        }
    }

    /**
     * A common body for all of the methods that return a List&lt;String&gt; from a singleton item which
     * is sorted using a vertical-bar.
     *
     * @param singletonItemName the name of the singleton item
     * @param valuesWithSort the DatabaseField for the value
     * @param nameOfItemInErrors a name for the singleton item in error messages
     * @return the List (in the correct order).
     */
    private List<String> getSortedStrings(
            String singletonItemName,
            DatabaseField valuesWithSort,
            String nameOfItemInErrors
    ) {
        final Map<String,AttributeValue> item = getSingletonItem(singletonItemName);
        final AttributeValue allowedValuesWithSort = item.get(valuesWithSort.name());
        if (allowedValuesWithSort == null) {
            throw new RuntimeException("No " + nameOfItemInErrors + " found. DB may not be initialized.");
        }

        // Create a record type we can sort on
        record SortKeyAndValue(int sortKey, String value) implements Comparable<SortKeyAndValue> {
            @Override
            public int compareTo(SortKeyAndValue o) {
                return Integer.compare(this.sortKey, o.sortKey);
            }
        }
        return readSetOfStrings(allowedValuesWithSort.ss()).stream()
                .map(x -> {
                    String[] pieces = x.split("\\|",2); // split on first vertical-bar
                    return new SortKeyAndValue(Integer.parseInt(pieces[0]), pieces[1]);
                })
                .sorted()
                .map(x -> x.value)
                .toList();
    }

    /**
     * A common body for all of the methods that delete an entry from a singleton item which
     * is sorted using a vertical-bar.
     *
     * @param valueToDelete the value to be deleted
     * @param oldAllowedValues the existing list of values (which must have just been retrieved)
     * @param singletonItemName the name of the singleton item
     * @param valuesWithSort  the SingelTableDbField for the value
     */
    private void deleteFromSortedStrings(
            final String valueToDelete,
            final List<String> oldAllowedValues,
            final String singletonItemName,
            final DatabaseField valuesWithSort
    ) throws NoSuchAllowedValueException {
        // --- check if the value is missing ---
        if (!oldAllowedValues.contains(valueToDelete)) {
            throw new NoSuchAllowedValueException();
        }
        // --- mark with order while also skipping the one we should delete ---
        final String[] newAllowedValuesWithSort = new String[oldAllowedValues.size() - 1];
        int index = 0;
        for (String oldAllowedValue : oldAllowedValues) {
            if (!oldAllowedValue.equals(valueToDelete)) {
                newAllowedValuesWithSort[index] = index + "|" + oldAllowedValue;
                index += 1;
            }
        }
        // --- write it out (overwriting the existing one) ---
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(getTableName())
                .item(new ItemBuilder(singletonItemName)
                        .withStrings(valuesWithSort, storeSetOfStrings(newAllowedValuesWithSort))
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    /**
     * A common body for all of the methods that insert an entry into a singleton item which
     * is sorted using a vertical-bar.
     *
     * @param newValue the value to be inserted
     * @param valueToInsertBefore the existing value that the new value should be inserted before,
     *                            or "" to insert at the end of the list
     * @param oldAllowedValues the existing list of values (which must have just been retrieved)
     * @param singletonItemName the name of the singleton item
     * @param valuesWithSort  the SingelTableDbField for the value
     */
    private void insertNewSortedString(
            final String newValue,
            final String valueToInsertBefore,
            final List<String> oldAllowedValues,
            final String singletonItemName,
            final DatabaseField valuesWithSort
    ) throws AllowedValueAlreadyInUseException, NoSuchAllowedValueException {
        // --- check for invalid times ---
        if (newValue.equals("") || oldAllowedValues.contains(newValue)) {
            throw new AllowedValueAlreadyInUseException();
        }
        // --- find the spot to insert OR that the insert-before is invalid ---
        final int insertBefore = valueToInsertBefore.equals("")
                ? oldAllowedValues.size()
                : oldAllowedValues.indexOf(valueToInsertBefore);
        if (insertBefore == -1) {
            throw new NoSuchAllowedValueException();
        }
        // --- insert new value and mark with order ---
        final String[] newAllowedValuesWithSort = new String[oldAllowedValues.size() + 1];
        int index = 0;
        for (String oldAllowedValue : oldAllowedValues) {
            if (index == insertBefore) {
                newAllowedValuesWithSort[index] = index + "|" + newValue;
                index += 1;
            }
            newAllowedValuesWithSort[index] = index + "|" + oldAllowedValue;
            index += 1;
        }
        if (index == insertBefore) {
            newAllowedValuesWithSort[index] = index + "|" + newValue;
        }
        // --- write it out (overwriting the existing one) ---
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(getTableName())
                .item(new ItemBuilder(singletonItemName)
                        .withStrings(valuesWithSort, storeSetOfStrings(newAllowedValuesWithSort))
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
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
    public User getUserById(String userId) throws InconsistentDatabaseException {
        return getObjectByUniqueId("user:", this::createUserFromDynamoDbItem, userId);
    }

    @Override
    public User getUserByEmail(String email) throws InconsistentDatabaseException {
        // --- First, we look in the index to find out the key ---
        final QueryRequest queryRequest = QueryRequest.builder()
                .tableName(getTableName())
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
                .tableName(getTableName())
                .key(Map.of(table_key.name(), keyValue))
                .build();
        final GetItemResponse getItemResponse = dynamoDbClient.getItem(getItemRequest);
        return createUserFromDynamoDbItem(getItemResponse.item());
    }

    @Override
    public void modifyUserPersonalFields(EditPersonalDataFormData formData) throws EmailAlreadyInUseException, InconsistentDatabaseException {
        verifyEmailNotInUseByAnyoneElse(formData.getUserId(), formData.getEmail());
        final String itemKey = "user:" + formData.getUserId();
        // Because emails are commonly case-insensitive, we store a lower-case version of the email
        // ("user_email") in addition to the original format ("user_original_email") and enforce
        // uniqueness on the lower-case field.
        final String userEmail = formData.getEmail().toLowerCase();

        final UpdateItemRequest updateItemRequest = new UpdateItemBuilder(getTableName(), itemKey)
                .withString(user_email, userEmail)
                .withString(user_original_email, formData.getEmail())
                .withString(user_first_name, formData.getFirstName())
                .withString(user_last_name, formData.getLastName())
                .withString(user_phone_number, formData.getPhoneNumber())
                .build();
        dynamoDbClient.updateItem(updateItemRequest);
    }

    @Override
    public void modifyVolunteerPersonalFields(EditVolunteerPersonalDataFormData formData) throws EmailAlreadyInUseException, InconsistentDatabaseException {
        verifyEmailNotInUseByAnyoneElse(formData.getUserId(), formData.getEmail());
        final String itemKey = "user:" + formData.getUserId();
        // Because emails are commonly case-insensitive, we store a lower-case version of the email
        // ("user_email") in addition to the original format ("user_original_email") and enforce
        // uniqueness on the lower-case field.
        final String userEmail = formData.getEmail().toLowerCase();
        final UpdateItemRequest updateItemRequest = new UpdateItemBuilder(getTableName(), itemKey)
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
    public void modifyTeacherSchool(String userId, String organizationId) throws NoSuchSchoolException, NoSuchUserException {
        // NOTE: At the moment, this is NOT checking whether the user exists (it will create it if not, but with
        //   all the other fields missing) and it is not checking whether the school exists. Except in the case
        //   of bugs elsewhere, that should be fine.
        final UpdateItemRequest updateItemRequest = new UpdateItemBuilder(getTableName(), "user:" + userId)
                .withString(user_organization_id, organizationId)
                .build();
        dynamoDbClient.updateItem(updateItemRequest);
    }

    @Override
    public Teacher insertNewTeacher(TeacherRegistrationFormData formData, String hashedPassword, String salt) throws NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException {
        // NOTE: I'm choosing NOT to verify that the school ID is actually present in the database
        // --- check for uniqueness ---
        verifyEmailNotInUseByAnyoneElse(null, formData.getEmail());
        // --- generate the new ID ---
        final String newTeacherId = dynamoDBHelper.createUniqueId();
        // --- store it ---
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(getTableName())
                .item(new ItemBuilder("user", user_id, newTeacherId)
                        .withString(user_type, UserType.TEACHER.getDBValue())
                        .withInt(user_approval_status, ApprovalStatus.INITIAL_APPROVAL_STATUS.getDbValue())
                        .withString(user_email, formData.getEmail().toLowerCase()) // in canonical form
                        .withString(user_original_email, formData.getEmail()) // in original form
                        .withString(user_first_name, formData.getFirstName())
                        .withString(user_last_name, formData.getLastName())
                        .withString(user_phone_number, formData.getPhoneNumber())
                        .withString(user_organization_id, formData.getSchoolId())
                        .withString(user_hashed_password, hashedPassword)
                        .withString(user_password_salt, salt)
                        .build()
                )
                .conditionExpression("attribute_not_exists(" + table_key.name() + ")") // verify it is unique
                .build();
        dynamoDbClient.putItem(putItemRequest);
        // --- retrieve it and return it ---
        return (Teacher) getUserById(newTeacherId);
    }

    @Override
    public List<Event> getEventsByTeacher(String teacherId) {
        return getObjectsByIndexLookup("ByEventTeacherId", event_teacher_id, teacherId,
                SingleTableDynamoDbDatabase::createEventFromDynamoDbItem, compareEvents);
    }

    @Override
    public List<Event> getAllAvailableEvents() {
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
                .tableName(getTableName())
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
    public List<Event> getEventsByVolunteer(String volunteerId) {
        if (volunteerId == null) {
            throw new RuntimeException("This method doesn't handle null for volunteerId.");
            // NOTE: It *could* handle that if we wanted it to, but for now that's just basically an assert
        }
        return getObjectsByIndexLookup("ByEventVolunteerId", event_volunteer_id, volunteerId,
                SingleTableDynamoDbDatabase::createEventFromDynamoDbItem, compareEvents);
    }

    @Override
    public List<Event> getEventsByVolunteerWithTeacherAndSchool(String volunteerId) {
        // --- Load the volunteer, since we know it'll be needed if there are ANY events ---
        final Volunteer volunteer = (Volunteer) getUserById(volunteerId);
        volunteer.setLinkedBank(getBankById(volunteer.getBankId()));

        // --- Now get the events ---
        return getEventsByVolunteer(volunteerId).stream()
                .map(event -> {
                    // --- populate the teacher ---
                    final Teacher teacher = (Teacher) getUserById(event.getTeacherId());
                    teacher.setLinkedSchool(getSchoolById(teacher.getSchoolId()));
                    event.setLinkedTeacher(teacher);

                    // --- populate the volunteer ---
                    assert event.getVolunteerId().equals(volunteerId);
                    event.setLinkedVolunteer(volunteer);

                    // --- the event is ready now ---
                    return event;
                })
                .sorted(compareEvents)
                .toList();
    }

    @Override
    public void insertEvent(String teacherId, CreateEventFormData formData) {
        final String uniqueId = dynamoDBHelper.createUniqueId();
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(getTableName())
                .item(new ItemBuilder("event", event_id, uniqueId)
                        .withString(event_teacher_id, teacherId)
                        .withString(event_date, PrettyPrintingDate.fromJavaUtilDate(formData.getEventDate()).getParseable())
                        .withString(event_time, formData.getEventTime())
                        .withString(event_grade, formData.getGrade())
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
    public void volunteerForEvent(String eventId, String volunteerId) throws NoSuchEventException, EventAlreadyHasAVolunteerException {
        final String volunteerIdToUse = volunteerId == null ? NO_VOLUNTEER : volunteerId;

        final UpdateItemBuilder builder = new UpdateItemBuilder(getTableName(), "event:" + eventId)
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
    public List<Volunteer> getVolunteersByBank(String bankId) {
        // We look for users with this organization id
        final QueryRequest queryRequest = QueryRequest.builder()
                .tableName(getTableName())
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
    public List<BankAdmin> getBankAdminsByBank(String bankId) {
        return getVolunteersByBank(bankId).stream()
                .filter(volunteer -> volunteer instanceof BankAdmin)
                .map(volunteer -> (BankAdmin) volunteer)
                .toList();
    }

    @Override
    public Volunteer insertNewVolunteer(VolunteerRegistrationFormData formData, String hashedPassword, String salt) throws NoSuchBankException, EmailAlreadyInUseException {
        // NOTE: I'm choosing NOT to verify that the school ID is actually present in the database
        // --- check for uniqueness ---
        verifyEmailNotInUseByAnyoneElse(null, formData.getEmail());
        // --- generate the new ID ---
        final String newVolunteerId = dynamoDBHelper.createUniqueId();
        // --- store it ---
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(getTableName())
                .item(new ItemBuilder("user", user_id, newVolunteerId)
                        .withString(user_type, UserType.VOLUNTEER.getDBValue())
                        .withInt(user_approval_status, ApprovalStatus.INITIAL_APPROVAL_STATUS.getDbValue())
                        .withString(user_email, formData.getEmail().toLowerCase()) // in canonical form
                        .withString(user_original_email, formData.getEmail()) // in original form
                        .withString(user_first_name, formData.getFirstName())
                        .withString(user_last_name, formData.getLastName())
                        .withString(user_phone_number, formData.getPhoneNumber())
                        .withString(user_bank_specific_data, formData.getBankSpecificData())
                        .withString(user_organization_id, formData.getBankId())
                        .withString(user_hashed_password, hashedPassword)
                        .withString(user_password_salt, salt)
                        .withString(user_street_address, formData.getStreetAddress())
                        .withString(user_suite_or_floor_number, formData.getSuiteOrFloorNumber())
                        .withString(user_city, formData.getCity())
                        .withString(user_state, formData.getState())
                        .withString(user_zip, formData.getZip())
                        .build()
                )
                .conditionExpression("attribute_not_exists(" + table_key.name() + ")") // verify it is unique
                .build();
        dynamoDbClient.putItem(putItemRequest);
        // --- retrieve it and return it ---
        return (Volunteer) getUserById(newVolunteerId);
    }

    @Override
    public Bank getBankById(String bankId) {
        return getObjectByUniqueId("bank:", SingleTableDynamoDbDatabase::createBankFromDynamoDbItem, bankId);
    }

    @Override
    public School getSchoolById(String schoolId) {
        return getObjectByUniqueId(
                "school:", SingleTableDynamoDbDatabase::createSchoolFromDynamoDbItem, schoolId);
    }

    @Override
    public List<School> getAllSchools() {
        return getAllUsingIndexOrScan(
                "BySchoolId", "school:", SingleTableDynamoDbDatabase::createSchoolFromDynamoDbItem, compareSchools);
    }

    @Override
    public List<Bank> getAllBanks() {
        return getAllUsingIndexOrScan(
                "ByBankId", "bank:", SingleTableDynamoDbDatabase::createBankFromDynamoDbItem, compareBanks);
    }

    @Override
    public List<User> getAllUsers() {
        return getAllUsingIndexOrScan("ByUserEmail", "user:", this::createUserFromDynamoDbItem, compareUsersByName);
    }

    @Override
    public List<PrettyPrintingDate> getAllowedDates() {
        final Map<String,AttributeValue> item = getSingletonItem("allowedDates");
        final AttributeValue allowedDateValues = item.get(allowed_date_values.name());
        if (allowedDateValues == null) {
            throw new RuntimeException("No allowed dates found. DB may not be initialized.");
        }
        final List<PrettyPrintingDate> result = readSetOfStrings(allowedDateValues.ss()).stream()
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
    public List<String> getAllowedTimes() {
        return getSortedStrings("allowedTimes", allowed_time_values_with_sort, "allowed times");
    }

    @Override
    public List<String> getAllowedGrades() {
        return getSortedStrings("allowedGrades", allowed_grade_values_with_sort, "allowed grades");
    }

    @Override
    public List<String> getAllowedDeliveryMethods() {
        return getSortedStrings("allowedDeliveryMethods", allowed_delivery_method_values_with_sort, "allowed delivery methods");
    }

    @Override
    public void deleteSchool(String schoolId) throws NoSuchSchoolException {
        // Note: Does NOT verify whether the school exists and throw NoSuchSchoolException where appropriate
        // Note: Does not verify whether the school is referenced anywhere.
        deleteItem("school:", schoolId);
    }

    @Override
    public void deleteBankAndBankVolunteers(String bankId) throws NoSuchBankException, BankHasVolunteersException, VolunteerHasEventsException {
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
    public void deleteVolunteer(String volunteerId) throws NoSuchUserException, VolunteerHasEventsException {
        List<Event> events = getEventsByVolunteer(volunteerId);
        if (!events.isEmpty()) {
            throw new VolunteerHasEventsException();
        }
        deleteItem("user:", volunteerId);
    }

    @Override
    public void deleteTeacher(String teacherId) throws NoSuchUserException, TeacherHasEventsException {
        // Note: Does NOT verify whether the teacher exists and throw NoSuchSchoolException where appropriate
        if (!getEventsByTeacher(teacherId).isEmpty()) {
            throw new TeacherHasEventsException();
        }
        deleteItem("user:", teacherId);
    }

    @Override
    public void deleteEvent(String eventId) throws NoSuchEventException {
        deleteItem("event:", eventId);
    }

    @Override
    public List<Event> getAllEvents() throws InconsistentDatabaseException {
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
        return getAllUsingIndexOrScan(
                "ByEventId",
                "event:",
                SingleTableDynamoDbDatabase::createEventFromDynamoDbItem,
                compareEvents
        ).stream()
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
    public Event getEventById(String eventId) {
        return getObjectByUniqueId("event:", SingleTableDynamoDbDatabase::createEventFromDynamoDbItem, eventId);
    }

    @Override
    public void modifySchool(EditSchoolFormData school) throws NoSuchSchoolException {
        // This approach will CREATE the school if it doesn't exist.
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(getTableName())
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
    public void insertNewBankAndAdmin(CreateBankFormData formData) throws EmailAlreadyInUseException {
        // FIXME: It might be nice to enforce that the bank name is unique
        // -- Insert bank --
        final String uniqueBankId = dynamoDBHelper.createUniqueId();
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(getTableName())
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
    public void insertNewBankAdmin(NewBankAdminFormData formData) throws EmailAlreadyInUseException {
        verifyEmailNotInUseByAnyoneElse(null, formData.getEmail());
        final String uniqueId = dynamoDBHelper.createUniqueId();
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(getTableName())
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
    public void modifyBank(EditBankFormData formData) throws NoSuchBankException {
        // This approach will CREATE the bank if it doesn't exist instead of throwing an exception
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(getTableName())
                .item(new ItemBuilder("bank", bank_id, formData.getBankId())
                        .withString(bank_name, formData.getBankName())
                        .withString(min_lmi_for_cra, formData.getMinLMIForCRA())
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public void setUserType(String userId, UserType userType) {
        final UpdateItemRequest updateItemRequest = new UpdateItemBuilder(getTableName(), "user:" + userId)
                .withString(user_type, userType.getDBValue())
                .build();
        dynamoDbClient.updateItem(updateItemRequest);
    }

    @Override
    public void setBankSpecificFieldLabel(SetBankSpecificFieldLabelFormData formData) throws NoSuchBankException {
        final UpdateItemRequest updateItemRequest = new UpdateItemBuilder(getTableName(), "bank:" + formData.getBankId())
                .withString(bank_specific_data_label, formData.getBankSpecificFieldLabel())
                .build();
        dynamoDbClient.updateItem(updateItemRequest);
    }

    @Override
    public void insertNewSchool(CreateSchoolFormData school) {
        final String uniqueId = dynamoDBHelper.createUniqueId();
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(getTableName())
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
    public void insertNewAllowedDate(AddAllowedDateFormData formData) throws AllowedValueAlreadyInUseException {
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
                .tableName(getTableName())
                .item(new ItemBuilder("allowedDates")
                        .withStrings(allowed_date_values, storeSetOfStrings(newAllowedDates))
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public void insertNewAllowedTime(String newAllowedTime, String timeToInsertBefore) throws AllowedValueAlreadyInUseException, NoSuchAllowedValueException {
        insertNewSortedString(newAllowedTime, timeToInsertBefore, getAllowedTimes(), "allowedTimes", allowed_time_values_with_sort);
    }

    @Override
    public void insertNewAllowedGrade(String newAllowedGrade, String gradeToInsertBefore) throws AllowedValueAlreadyInUseException, NoSuchAllowedValueException {
        insertNewSortedString(newAllowedGrade, gradeToInsertBefore, getAllowedGrades(), "allowedGrades", allowed_grade_values_with_sort);
    }

    @Override
    public void insertNewAllowedDeliveryMethod(String newAllowedDeliveryMethod, String deliveryMethodToInsertBefore) throws AllowedValueAlreadyInUseException, NoSuchAllowedValueException {
        insertNewSortedString(newAllowedDeliveryMethod, deliveryMethodToInsertBefore, getAllowedDeliveryMethods(), "allowedDeliveryMethods", allowed_delivery_method_values_with_sort);
    }

    /** Convert a volunteerId (using null to indicate no volunteer) to the database format (using NO_VOLUNTEER). */
    private String dbFormatVolunteerId(String volunteerId) {
        if (volunteerId == null) {
            return NO_VOLUNTEER;
        } else {
            return volunteerId;
        }
    }

    @Override
    public void modifyEventRegistration(EventRegistrationFormData formData) throws NoSuchEventException {
        final String tableKey = "event:" + formData.getEventId();
        final UpdateItemRequest updateItemRequest = new UpdateItemBuilder(getTableName(), tableKey)
                .withString(event_volunteer_id, dbFormatVolunteerId(formData.getVolunteerId()))
                .withStringFieldEqualsCondition(table_key, tableKey) // confirm it exists
                .build();
        try {
            dynamoDbClient.updateItem(updateItemRequest);
        } catch(ConditionalCheckFailedException err) {
            throw new NoSuchEventException();
        }
    }

    @Override
    public void modifyEvent(EditEventFormData formData) throws NoSuchEventException {
        final String tableKey = "event:" + formData.getEventId();
        final UpdateItemRequest updateItemRequest = new UpdateItemBuilder(getTableName(), tableKey)
                .withString(event_date, PrettyPrintingDate.fromJavaUtilDate(formData.getEventDate()).getParseable())
                .withString(event_time, formData.getEventTime())
                .withString(event_grade, formData.getGrade())
                .withString(event_delivery_method, formData.getDeliveryMethod())
                .withInt(event_number_students, Integer.parseInt(formData.getNumberStudents()))
                .withString(event_notes, formData.getNotes())
                .withStringFieldEqualsCondition(table_key, tableKey) // confirm it exists
                .build();
        try {
            dynamoDbClient.updateItem(updateItemRequest);
        } catch(ConditionalCheckFailedException err) {
            throw new NoSuchEventException();
        }
    }

    @Override
    public void updateUserCredential(String userId, String hashedPassword, String salt) {
        final UpdateItemRequest updateItemRequest = new UpdateItemBuilder(getTableName(), "user:" + userId)
                .withString(user_hashed_password, hashedPassword)
                .withString(user_password_salt, salt)
                .withString(user_reset_password_token, null)
                .withStringFieldEqualsCondition(table_key, "user:" + userId)
                .build();
        dynamoDbClient.updateItem(updateItemRequest);
    }

    @Override
    public void updateResetPasswordToken(String userId, String resetPasswordToken) {
        final UpdateItemRequest updateItemRequest = new UpdateItemBuilder(getTableName(), "user:" + userId)
                .withString(user_reset_password_token, resetPasswordToken)
                .withStringFieldEqualsCondition(table_key, "user:" + userId)
                .build();
        dynamoDbClient.updateItem(updateItemRequest);
    }

    @Override
    public void updateApprovalStatusById(String volunteerId, ApprovalStatus approvalStatus) {
        final UpdateItemRequest updateItemRequest = new UpdateItemBuilder(getTableName(), "user:" + volunteerId)
                .withInt(user_approval_status, approvalStatus.getDbValue())
                .withStringFieldEqualsCondition(table_key, "user:" + volunteerId)
                .build();
        dynamoDbClient.updateItem(updateItemRequest);
    }

    @Override
    public void deleteAllowedTime(String time) throws NoSuchAllowedValueException {
        deleteFromSortedStrings(time, getAllowedTimes(), "allowedTimes", allowed_time_values_with_sort);
    }

    @Override
    public void deleteAllowedGrade(String grade) throws NoSuchAllowedValueException {
        deleteFromSortedStrings(grade, getAllowedGrades(), "allowedGrades", allowed_grade_values_with_sort);
    }

    @Override
    public void deleteAllowedDeliveryMethod(String deliveryMethod) throws NoSuchAllowedValueException {
        deleteFromSortedStrings(deliveryMethod, getAllowedDeliveryMethods(), "allowedDeliveryMethods", allowed_delivery_method_values_with_sort);
    }

    @Override
    public void deleteAllowedDate(PrettyPrintingDate date) throws NoSuchAllowedValueException {
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
                .tableName(getTableName())
                .item(new ItemBuilder("allowedDates")
                        .withStrings(allowed_date_values, storeSetOfStrings(newAllowedDates))
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public SiteStatistics getSiteStatistics() {
        int numEvents = 0;
        int numMatchedEvents = 0;
        int numUnmatchedEvents = 0;
        int num3rdGradeEvents = 0;
        int num4thGradeEvents = 0;
        int numInPersonEvents = 0;
        int numVirtualEvents = 0;
        final Set<String> volunteerIdsActuallySignedUp = new HashSet<>();
        final Set<String> teacherIdsWithClassesThatHaveVolunteers = new HashSet<>();
        final SortedMap<PrettyPrintingDate, Integer> numEventsByEventDate = new TreeMap<>();
        final SortedMap<String, Integer> numEventsByGrade = new TreeMap<>();
        final SortedMap<String, Integer> numEventsByDeliveryMethod = new TreeMap<>();
        final SortedMap<String, Integer> numEventsByEventTime = new TreeMap<>();

        for (final Event event : getAllEvents()) {
            numEvents += 1;
            if (event.getVolunteerId() == null) {
                numUnmatchedEvents += 1;
            } else {
                numMatchedEvents += 1;
                volunteerIdsActuallySignedUp.add(event.getVolunteerId());
                teacherIdsWithClassesThatHaveVolunteers.add(event.getTeacherId());
            }
            // FIXME: This way of calculating grades is invalid now that the list of grades isn't fixed
            if (event.getGrade().equals("3rd Grade")) {
                num3rdGradeEvents += 1;
            } else if (event.getGrade().equals("4th Grade")) {
                num4thGradeEvents += 1;
            }
            // FIXME: This way of calculating delivery methods is invalid now that the list of grades isn't fixed
            if (event.getDeliveryMethod().equals("In-Person")) {
                numInPersonEvents += 1;
            } else if (event.getDeliveryMethod().equals("Virtual")) {
                numVirtualEvents += 1;
            }
            numEventsByEventDate.put(event.getEventDate(), numEventsByEventDate.getOrDefault(event.getEventDate(), 0) + 1);
            numEventsByGrade.put(event.getGrade(), numEventsByGrade.getOrDefault(event.getGrade(), 0) + 1);
            numEventsByDeliveryMethod.put(event.getDeliveryMethod(), numEventsByDeliveryMethod.getOrDefault(event.getDeliveryMethod(), 0) + 1);
            numEventsByEventTime.put(event.getEventTime(), numEventsByEventTime.getOrDefault(event.getEventTime(), 0) + 1);
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

        return new SiteStatistics(
                numEvents,
                numMatchedEvents,
                numUnmatchedEvents,
                num3rdGradeEvents,
                num4thGradeEvents,
                numInPersonEvents,
                numVirtualEvents,
                numVolunteers,
                numParticipatingTeachers,
                numParticipatingSchools,
                numEventsByEventDate,
                numEventsByGrade,
                numEventsByDeliveryMethod,
                numEventsByEventTime
        );
    }

    @Override
    public List<Teacher> getTeachersWithSchoolData() {
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
    public List<Teacher> getTeachersBySchool(String schoolId) {
        return getObjectsByIndexLookup("ByUserOrganizationId", user_organization_id, schoolId,
                item -> (Teacher) createUserFromDynamoDbItem(item),
                compareUsersByName::compare
                );
    }

    @Override
    public List<Volunteer> getVolunteersWithBankData() {
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
    public List<Teacher> getMatchedTeachers() {
        return getAllEvents().stream()
                .filter(event -> event.getLinkedVolunteer() != null)
                .map(Event::getLinkedTeacher)
                .distinct()
                .toList();
    }

    @Override
    public List<Teacher> getUnMatchedTeachers() {
        return getAllAvailableEvents().stream()
                .map(Event::getLinkedTeacher)
                .distinct()
                .toList();
    }

    @Override
    public List<Volunteer> getMatchedVolunteers() {
        return getAllEvents().stream()
                .map(Event::getLinkedVolunteer)
                .filter(x -> x != null)
                .distinct()
                .toList();
    }

    @Override
    public List<Volunteer> getUnMatchedVolunteers() {
        final Set<Volunteer> matchedVolunteers = new HashSet<>(getMatchedVolunteers());
        return Stream.concat(
                getUsersByType(UserType.VOLUNTEER).stream(),
                getUsersByType(UserType.BANK_ADMIN).stream())
                .filter(x -> !matchedVolunteers.contains(x))
                .map(x -> (Volunteer) x)
                .distinct()
                .toList();
    }

    @Override
    public List<BankAdmin> getBankAdmins() {
        return getUsersByType(UserType.BANK_ADMIN).stream()
                .map(x -> (BankAdmin) x)
                .toList();
    }

    @Override
    public Map<String, String> getSiteSettings() {
        final Map<String,AttributeValue> item = getSingletonItem("siteSettings");
        final AttributeValue keyvalues = item.get(site_setting_entries.name());
        if (keyvalues == null) {
            throw new RuntimeException("No site settings found. DB may not be initialized.");
        }
        final SortedMap<String,String> result = new TreeMap<>();
        for (String entry : readSetOfStrings(keyvalues.ss())) {
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
    public void modifySiteSetting(String settingName, String settingValue) {
        // --- get existing values ---
        final Map<String,String> oldSiteSettings = getSiteSettings();
        // --- update it ---
        oldSiteSettings.put(settingName, settingValue);
        // --- convert to the form for output ---
        final String[] siteSettingKeyValues = oldSiteSettings.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .toArray(String[]::new);
        // --- write it out (overwriting the existing one) ---
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(getTableName())
                .item(new ItemBuilder("siteSettings")
                        .withStrings(site_setting_entries, storeSetOfStrings(siteSettingKeyValues))
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public SortedSet<Document> getDocuments() {
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
        for (String documentEntry : readSetOfStrings(entries.ss())) {
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
    public void createOrModifyDocument(Document document) {
        // --- get existing documents ---
        final SortedSet<Document> documents = getDocuments();
        // --- modify as desired ---
        // NOTE: This counts on the fact that createOrModifyDocument gives us back a
        //   *mutable* SortedSet. If not, we'd need to copy it to a new mutable one.
        documents.removeIf(doc -> doc.getName().equals(document.getName()));
        documents.add(document);
        // --- write it out ---
        final String[] documentsValues = documents.stream()
                .map(doc ->
                        (doc.getShowToTeacher() ? "T" : "F") + "|" +
                        (doc.getShowToVolunteer() ? "T" : "F") + "|" +
                        (doc.getShowToBankAdmin() ? "T" : "F") + "|" +
                        doc.getName())
                .toArray(String[]::new);
        // --- write it out (overwriting the existing one) ---
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(getTableName())
                .item(new ItemBuilder("documents")
                        .withStrings(documents_values, storeSetOfStrings(documentsValues))
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    @Override
    public void deleteDocument(String documentName) {
        // --- get existing documents ---
        final SortedSet<Document> documents = getDocuments();
        // --- modify as desired ---
        // NOTE: This counts on the fact that createOrModifyDocument gives us back a
        //   *mutable* SortedSet. If not, we'd need to copy it to a new mutable one.
        documents.removeIf(doc -> doc.getName().equals(documentName));
        // --- write it out ---
        final String[] documentsValues = documents.stream()
                .map(doc ->
                        (doc.getShowToTeacher() ? "T" : "F") + "|" +
                                (doc.getShowToVolunteer() ? "T" : "F") + "|" +
                                (doc.getShowToBankAdmin() ? "T" : "F") + "|" +
                                doc.getName())
                .toArray(String[]::new);
        // --- write it out (overwriting the existing one) ---
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(getTableName())
                .item(new ItemBuilder("documents")
                        .withStrings(documents_values, storeSetOfStrings(documentsValues))
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    // ========== Helpers to Overcome DynamoDB Design Flaws ==========

    // In several of these we use DynamoDB's StringSet type. But that type has a fatal flaw.
    // It cannot store an empty set. So if we ever have an empty set of values and try to
    // store that things will simply crash and fail to write -- or they would if we didn't
    // have this workaround.
    //
    // Every place where we attempt to write or read a StringSet type we will run it through
    // this pair of functions. If it detects an attempt to write an empty StringSet we will
    // replace it with a dummy value that MEANS "empty". On reads, if it reads this special
    // value then we will convert that dummy value to an empty list before returning it.
    //
    // The only remaining issue is the choice of dummy value. We SHOULD use some "out of band"
    // value which would otherwise be illegal. I was considering using {""} (a set containing
    // an empty string) which technically isn't valid for any of our use cases. But on
    // second thought that's just ITCHING to create a problem later on when it gets
    // misinterpreted by someone because it SEEMS like it would be valid. So instead we will
    // use the value {"this-set-is-empty-but-dynamodb-can-not-store-an-empty-set"} which will
    // NOT lead future developers to wonder what is happening. Technically this means that
    // you can't use that particular string as a document name, a course time, or several
    // other things, but we can live with that.

    private final static String emptyStringSetDummyValue = "this-set-is-empty-but-dynamodb-can-not-store-an-empty-set";

    /** Call this on a setOfStrings before writing it to DynamoDB. */
    private static String[] storeSetOfStrings(String[] setOfStrings) {
        if (setOfStrings.length == 0) {
            return new String[]{emptyStringSetDummyValue};
        } else {
            return setOfStrings;
        }
    }

    /** Call this on a setOfStrings after reading it from DynamoDB. */
    private static List<String> readSetOfStrings(List<String> setOfStrings) {
        if (setOfStrings.size() == 1 && setOfStrings.get(0).equals(emptyStringSetDummyValue)) {
            return new ArrayList<String>();
        } else {
            return setOfStrings;
        }
    }

}
