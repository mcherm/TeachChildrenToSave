package com.tcts.database;

import com.tcts.common.Configuration;
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
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;


// FIXME: Still under development
public class SingleTableDynamoDBDatabase implements DatabaseFacade {
    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    // FIXME: Remove
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        String connectURL = configuration.getProperty("dynamoDB.connect");
        String signingRegion = configuration.getProperty("dynamoDB.signingRegion");
        String accessKey = configuration.getProperty("aws.access_key");
        String accessSecret = configuration.getProperty("aws.secret_access_key");
        String proxyHost = configuration.getProperty("dynamodb.proxyhost");
        Region region = Region.US_EAST_1;
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, accessSecret);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();
        ScanRequest scanRequest = ScanRequest.builder().tableName("TCTS.dev.SiteSettings").build();
        ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        for (Map<String, AttributeValue> item: scanResponse.items()) {
            System.out.println("site_setting_name: " + item.get("site_setting_name").s());
            System.out.println("site_setting_value: " + item.get("site_setting_value").s());
        }
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

    // ========== Methods of DatabaseFacade Class ==========

    @Override
    public int getFieldLength(DatabaseField field) {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public User getUserById(String userId) throws SQLException, InconsistentDatabaseException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public User getUserByEmail(String email) throws SQLException, InconsistentDatabaseException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
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
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
    }

    @Override
    public List<String> getAllowedTimes() throws SQLException {
        throw new RuntimeException("Not implemented yet"); // FIXME: Implement
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
        final Map<String,AttributeValue> key = Map.of("key", AttributeValue.builder().s("siteSettings").build());
        final GetItemRequest getItemRequest = GetItemRequest.builder()
            .tableName(tableName)
            .key(key)
            .build();
        final GetItemResponse getItemResponse = dynamoDbClient.getItem(getItemRequest);
        final Map<String,AttributeValue> item = getItemResponse.item();
        final AttributeValue keyvalues = item.get("keyvalues");
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
