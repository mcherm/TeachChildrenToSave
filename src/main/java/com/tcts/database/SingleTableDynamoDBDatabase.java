package com.tcts.database;

import com.tcts.common.Configuration;
import com.tcts.common.PrettyPrintingDate;
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
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import static com.tcts.database.SingleTableDbField.*;


// FIXME: Still under development
public class SingleTableDynamoDBDatabase implements DatabaseFacade {
    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    // FIXME: Remove
    public static void main(String[] args) throws Exception {
        final Configuration configuration = new Configuration();
        final SingleTableDynamoDBDatabase instance = new SingleTableDynamoDBDatabase(configuration);
        final List<String> allowedTimes = instance.getAllowedTimes();
        System.out.println("allowedTimes: " + allowedTimes);
    }

    /**
     * Constructor.
     */
    public SingleTableDynamoDBDatabase(Configuration configuration) {
        dynamoDbClient = connectToDB(configuration);
        tableName = getTableName(configuration);
    }

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

    // ========== Methods of DatabaseFacade Class ==========

    @Override
    public int getFieldLength(DatabaseField field) {
        // FIXME: We made a new enum, and now this takes the wrong type! No sure what to do about that.
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public User getUserById(String userId) throws SQLException, InconsistentDatabaseException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
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
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void modifyVolunteerPersonalFields(EditVolunteerPersonalDataFormData formData) throws SQLException, EmailAlreadyInUseException, InconsistentDatabaseException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
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
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<Event> getAllAvailableEvents() throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<Event> getEventsByVolunteer(String volunteerId) throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<Event> getEventsByVolunteerWithTeacherAndSchool(String volunteerId) throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void insertEvent(String teacherId, CreateEventFormData formData) throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void volunteerForEvent(String eventId, String volunteerId) throws SQLException, NoSuchEventException, EventAlreadyHasAVolunteerException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<Volunteer> getVolunteersByBank(String bankId) throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<BankAdmin> getBankAdminsByBank(String bankId) throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public Volunteer insertNewVolunteer(VolunteerRegistrationFormData formData, String hashedPassword, String salt) throws SQLException, NoSuchBankException, EmailAlreadyInUseException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public Bank getBankById(String bankId) throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public School getSchoolById(String schoolId) throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<School> getAllSchools() throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<Bank> getAllBanks() throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
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
                .toList();
        Collections.sort(result);
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
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void deleteBankandBankVolunteers(String bankId) throws SQLException, NoSuchBankException, BankHasVolunteersException, VolunteerHasEventsException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void deleteVolunteer(String volunteerId) throws SQLException, NoSuchUserException, VolunteerHasEventsException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void deleteTeacher(String teacherId) throws SQLException, NoSuchUserException, TeacherHasEventsException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void deleteEvent(String eventId) throws SQLException, NoSuchEventException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<Event> getAllEvents() throws SQLException, InconsistentDatabaseException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public Event getEventById(String eventId) throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void modifySchool(EditSchoolFormData school) throws SQLException, NoSuchSchoolException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void insertNewBankAndAdmin(CreateBankFormData formData) throws SQLException, EmailAlreadyInUseException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void insertNewBankAdmin(NewBankAdminFormData formData) throws SQLException, EmailAlreadyInUseException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void modifyBank(EditBankFormData formData) throws SQLException, EmailAlreadyInUseException, NoSuchBankException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
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
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public void insertNewAllowedDate(AddAllowedDateFormData formData) throws SQLException, AllowedDateAlreadyInUseException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
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
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public SiteStatistics getSiteStatistics() throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<Teacher> getTeachersWithSchoolData() throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<Teacher> getTeachersBySchool(String schoolId) throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<Volunteer> getVolunteersWithBankData() throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
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
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public Map<String, String> getSiteSettings() throws SQLException {
        final Map<String,AttributeValue> item = getSingletonItem("siteSettings");
        final AttributeValue keyvalues = item.get(SingleTableDbField.site_setting_entries.name());
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
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
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
