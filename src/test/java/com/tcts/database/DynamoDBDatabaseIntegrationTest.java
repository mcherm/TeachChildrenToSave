package com.tcts.database;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.tcts.common.PrettyPrintingDate;
import com.tcts.datamodel.ApprovalStatus;
import com.tcts.datamodel.Bank;
import com.tcts.datamodel.BankAdmin;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.School;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.User;
import com.tcts.datamodel.UserType;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.AllowedDateAlreadyInUseException;
import com.tcts.exception.AllowedTimeAlreadyInUseException;
import com.tcts.exception.EmailAlreadyInUseException;
import com.tcts.exception.NoSuchAllowedDateException;
import com.tcts.exception.NoSuchAllowedTimeException;
import com.tcts.exception.NoSuchBankException;
import com.tcts.exception.NoSuchSchoolException;
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
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


/**
 * Unit tests for DynamoDBDatabase.
 * <p>
 * WARNING: I'm using this while developing the code, but the tests are NOT set up to
 * reliably run in any environment. There's no decent mocking, and it won't work if
 * you don't have DynamoDB running locally on the right port. All of that MIGHT get
 * fixed later; but if this comment is still here then run these at your own peril.
 */
public class DynamoDBDatabaseIntegrationTest {
    private DynamoDB dynamoDB;
    private DynamoDBDatabase dynamoDBDatabase;


    @Before
    public void initialize() throws InterruptedException {
        dynamoDB = DynamoDBDatabase.connectToDB();
        try {
            DynamoDBSetup.deleteAllDatabaseTables(dynamoDB);
        } catch(ResourceNotFoundException err) {
            // It's fine if the deletions failed.
        }
        DynamoDBSetup.createAllDatabaseTables(dynamoDB);
        dynamoDBDatabase = new DynamoDBDatabase(null);
    }



    @Test
    public void testGetFieldLengths() throws Exception {
        assertEquals(8, dynamoDBDatabase.getFieldLength(DatabaseField.event_grade));
        assertEquals(50, dynamoDBDatabase.getFieldLength(DatabaseField.user_last_name));
    }

    @Test
    public void testWriteOneSettingAndReadIt() throws SQLException {
        dynamoDBDatabase.modifySiteSetting("TestSetting", "TestValue");
        Map<String,String> siteSettings = dynamoDBDatabase.getSiteSettings();
        assertEquals("TestValue", siteSettings.get("TestSetting"));
    }

    private PrettyPrintingDate insertDateAndReturnIt() throws SQLException, AllowedDateAlreadyInUseException {
        String parsableDateStr = "2016-12-19";
        AddAllowedDateFormData addAllowedDateFormData = new AddAllowedDateFormData();
        addAllowedDateFormData.setParsableDateStr(parsableDateStr);
        dynamoDBDatabase.insertNewAllowedDate(addAllowedDateFormData);
        try {
            return PrettyPrintingDate.fromParsableDate(parsableDateStr);
        } catch(ParseException err) {
            throw new RuntimeException(err);
        }
    }

    @Test
    public void testWriteOneDateAndReturnIt() throws SQLException, AllowedDateAlreadyInUseException, ParseException {
        insertDateAndReturnIt();
        List<PrettyPrintingDate> allowedDates = dynamoDBDatabase.getAllowedDates();
        assertEquals(Arrays.asList(PrettyPrintingDate.fromParsableDate("2016-12-19")), allowedDates);
    }

    @Test
    public void testWriteThreeDatesOutOfOrderAndReadThem() throws SQLException, AllowedDateAlreadyInUseException, ParseException {
        AddAllowedDateFormData addAllowedDateFormData1 = new AddAllowedDateFormData();
        addAllowedDateFormData1.setParsableDateStr("2016-12-21");
        dynamoDBDatabase.insertNewAllowedDate(addAllowedDateFormData1);
        AddAllowedDateFormData addAllowedDateFormData2 = new AddAllowedDateFormData();
        addAllowedDateFormData2.setParsableDateStr("2016-12-19");
        dynamoDBDatabase.insertNewAllowedDate(addAllowedDateFormData2);
        AddAllowedDateFormData addAllowedDateFormData3 = new AddAllowedDateFormData();
        addAllowedDateFormData3.setParsableDateStr("2016-12-23");
        dynamoDBDatabase.insertNewAllowedDate(addAllowedDateFormData3);
        List<PrettyPrintingDate> allowedDates = dynamoDBDatabase.getAllowedDates();
        assertEquals(
                Arrays.asList(
                        PrettyPrintingDate.fromParsableDate("2016-12-19"),
                        PrettyPrintingDate.fromParsableDate("2016-12-21"),
                        PrettyPrintingDate.fromParsableDate("2016-12-23")),
                allowedDates);
    }

    @Test
    public void testWriteSameDateTwiceAndReadIt() throws SQLException, AllowedDateAlreadyInUseException, ParseException {
        AddAllowedDateFormData addAllowedDateFormData1 = new AddAllowedDateFormData();
        addAllowedDateFormData1.setParsableDateStr("2016-12-19");
        dynamoDBDatabase.insertNewAllowedDate(addAllowedDateFormData1);
        AddAllowedDateFormData addAllowedDateFormData2 = new AddAllowedDateFormData();
        addAllowedDateFormData2.setParsableDateStr("2016-12-19");
        dynamoDBDatabase.insertNewAllowedDate(addAllowedDateFormData2);
        List<PrettyPrintingDate> allowedDates = dynamoDBDatabase.getAllowedDates();
        assertEquals(
                Arrays.asList(PrettyPrintingDate.fromParsableDate("2016-12-19")),
                allowedDates);
    }

    @Test
    public void testInsertDateDeleteIt() throws SQLException, AllowedDateAlreadyInUseException, ParseException, NoSuchAllowedDateException {
        insertDateAndReturnIt();
        dynamoDBDatabase.deleteAllowedDate(PrettyPrintingDate.fromParsableDate("2016-12-19"));
        List<PrettyPrintingDate> allowedDates = dynamoDBDatabase.getAllowedDates();
        assertEquals(
                Arrays.asList(),
                allowedDates);
    }

    /** Used a few places to create a time. */
    private String insertTimeAndReturnIt() throws SQLException, AllowedTimeAlreadyInUseException, NoSuchAllowedTimeException {
        String result = "2:00";
        AddAllowedTimeFormData addAllowedTimeFormData = new AddAllowedTimeFormData();
        addAllowedTimeFormData.setAllowedTime(result);
        addAllowedTimeFormData.setTimeToInsertBefore("");
        dynamoDBDatabase.insertNewAllowedTime(addAllowedTimeFormData);
        return result;
    }

    @Test
    public void testWriteOneTimeAndReadIt() throws SQLException, AllowedTimeAlreadyInUseException, NoSuchAllowedTimeException {
        String timeStr = insertTimeAndReturnIt();
        List<String> allowedTimes = dynamoDBDatabase.getAllowedTimes();
        assertEquals(Arrays.asList(timeStr), allowedTimes);
    }

    @Test
    public void testWriteThreeTimesOutOfOrderAndReadThem() throws SQLException, AllowedTimeAlreadyInUseException, NoSuchAllowedTimeException {
        AddAllowedTimeFormData addAllowedTimeFormData1 = new AddAllowedTimeFormData();
        addAllowedTimeFormData1.setAllowedTime("6:00");
        addAllowedTimeFormData1.setTimeToInsertBefore("");
        dynamoDBDatabase.insertNewAllowedTime(addAllowedTimeFormData1);
        AddAllowedTimeFormData addAllowedTimeFormData2 = new AddAllowedTimeFormData();
        addAllowedTimeFormData2.setAllowedTime("2:00");
        addAllowedTimeFormData2.setTimeToInsertBefore("6:00");
        dynamoDBDatabase.insertNewAllowedTime(addAllowedTimeFormData2);
        AddAllowedTimeFormData addAllowedTimeFormData3 = new AddAllowedTimeFormData();
        addAllowedTimeFormData3.setAllowedTime("4:00");
        addAllowedTimeFormData3.setTimeToInsertBefore("6:00");
        dynamoDBDatabase.insertNewAllowedTime(addAllowedTimeFormData3);
        List<String> allowedTimes = dynamoDBDatabase.getAllowedTimes();
        assertEquals(
                Arrays.asList("2:00", "4:00", "6:00"),
                allowedTimes);
    }


    @Test
    public void testInsertTimeDeleteIt() throws SQLException, AllowedTimeAlreadyInUseException, ParseException, NoSuchAllowedTimeException {
        String time = insertTimeAndReturnIt();
        dynamoDBDatabase.deleteAllowedTime(time);
        List<String> allowedTimes = dynamoDBDatabase.getAllowedTimes();
        assertEquals(
                Arrays.asList(),
                allowedTimes);
    }

    @Test
    public void testRetrieveNonexistentSchool() throws SQLException {
        School school = dynamoDBDatabase.getSchoolById("1234");
        assertNull(school);
    }

    @Test
    public void testInsertSchool() throws SQLException {
        CreateSchoolFormData createSchoolFormData = new CreateSchoolFormData();
        createSchoolFormData.setSchoolName("Mirkwood Elementary");
        createSchoolFormData.setSchoolAddress1("123 Main St.");
        createSchoolFormData.setCity("Richmond");
        createSchoolFormData.setZip("23235");
        createSchoolFormData.setCounty("Delaware");
        createSchoolFormData.setDistrict("Roth");
        createSchoolFormData.setState("VA");
        createSchoolFormData.setPhone("302-234-5678");
        createSchoolFormData.setLmiEligible("15.4");
        createSchoolFormData.setSLC("N120");
        dynamoDBDatabase.insertNewSchool(createSchoolFormData);
    }

    @Test
    public void testGetAllSchoolsWhenThereAreNone() throws SQLException {
        List<School> schools = dynamoDBDatabase.getAllSchools();
        assertEquals(0, schools.size());
    }


    private void insertNewSchool(String schoolName) throws SQLException {
        CreateSchoolFormData createSchoolFormData = new CreateSchoolFormData();
        createSchoolFormData.setSchoolName(schoolName);
        createSchoolFormData.setSchoolAddress1("123 Main St.");
        createSchoolFormData.setCity("Richmond");
        createSchoolFormData.setZip("23235");
        createSchoolFormData.setCounty("Delaware");
        createSchoolFormData.setDistrict("Roth");
        createSchoolFormData.setState("VA");
        createSchoolFormData.setPhone("302-234-5678");
        createSchoolFormData.setLmiEligible("15.4");
        createSchoolFormData.setSLC("N120");
        dynamoDBDatabase.insertNewSchool(createSchoolFormData);
    }

    /** Will be used in a few other tests. */
    private String insertNewSchoolAndReturnTheId() throws SQLException {
        insertNewSchool("Mirkwood Elementary");
        List<School> schools = dynamoDBDatabase.getAllSchools();
        assertEquals(1, schools.size());
        return schools.get(0).getSchoolId();
    }

    @Test
    public void testCreateOneSchool() throws SQLException {
        insertNewSchoolAndReturnTheId();
        List<School> schools = dynamoDBDatabase.getAllSchools();
        assertEquals(1, schools.size());
    }

    @Test
    public void testCreateACoupleOfSchoolsThenGetThemAll() throws SQLException {
        CreateSchoolFormData createSchoolFormData = new CreateSchoolFormData();
        createSchoolFormData.setSchoolName("Mirkwood Elementary");
        createSchoolFormData.setSchoolAddress1("123 Main St.");
        createSchoolFormData.setCity("Richmond");
        createSchoolFormData.setZip("23235");
        createSchoolFormData.setCounty("Delaware");
        createSchoolFormData.setDistrict("Roth");
        createSchoolFormData.setState("VA");
        createSchoolFormData.setPhone("302-234-5678");
        createSchoolFormData.setLmiEligible("15.4");
        createSchoolFormData.setSLC("N120");
        dynamoDBDatabase.insertNewSchool(createSchoolFormData);
        createSchoolFormData.setSchoolName("Norton Academy");
        dynamoDBDatabase.insertNewSchool(createSchoolFormData);
        createSchoolFormData.setSchoolName("Landover Elementary");
        dynamoDBDatabase.insertNewSchool(createSchoolFormData);
        List<School> schools = dynamoDBDatabase.getAllSchools();
        assertEquals(3, schools.size());
        assertEquals("Landover Elementary", schools.get(0).getName());
        assertEquals("Mirkwood Elementary", schools.get(1).getName());
        assertEquals("Norton Academy", schools.get(2).getName());
    }

    @Test
    public void testCreateTwoSchoolsDeleteOneThenGetThem() throws SQLException, NoSuchSchoolException {
        CreateSchoolFormData createSchoolFormData = new CreateSchoolFormData();
        createSchoolFormData.setSchoolName("Mirkwood Elementary");
        createSchoolFormData.setSchoolAddress1("123 Main St.");
        createSchoolFormData.setCity("Richmond");
        createSchoolFormData.setZip("23235");
        createSchoolFormData.setCounty("Delaware");
        createSchoolFormData.setDistrict("Roth");
        createSchoolFormData.setState("VA");
        createSchoolFormData.setPhone("302-234-5678");
        createSchoolFormData.setLmiEligible("15.4");
        createSchoolFormData.setSLC("N120");
        dynamoDBDatabase.insertNewSchool(createSchoolFormData);
        createSchoolFormData.setSchoolName("Norton Academy");
        dynamoDBDatabase.insertNewSchool(createSchoolFormData);
        List<School> firstListOfSchools = dynamoDBDatabase.getAllSchools();
        assertEquals(2, firstListOfSchools.size());
        dynamoDBDatabase.deleteSchool(firstListOfSchools.get(0).getSchoolId());
        List<School> secondListOfSchools = dynamoDBDatabase.getAllSchools();
        assertEquals(1, secondListOfSchools.size());
    }


    @Test
    public void testCreateSchoolThenModifyIt() throws SQLException, NoSuchSchoolException {
        CreateSchoolFormData createSchoolFormData = new CreateSchoolFormData();
        createSchoolFormData.setSchoolName("Mirkwood Elementary");
        createSchoolFormData.setSchoolAddress1("123 Main St.");
        createSchoolFormData.setCity("Richmond");
        createSchoolFormData.setZip("23235");
        createSchoolFormData.setCounty("Delaware");
        createSchoolFormData.setDistrict("Roth");
        createSchoolFormData.setState("VA");
        createSchoolFormData.setPhone("302-234-5678");
        createSchoolFormData.setLmiEligible("15.4");
        createSchoolFormData.setSLC("N120");
        dynamoDBDatabase.insertNewSchool(createSchoolFormData);
        List<School> firstListOfSchools = dynamoDBDatabase.getAllSchools();
        assertEquals(1, firstListOfSchools.size());
        assertEquals("Mirkwood Elementary", firstListOfSchools.get(0).getName());

        EditSchoolFormData editSchoolFormData = new EditSchoolFormData();
        editSchoolFormData.setSchoolId(firstListOfSchools.get(0).getSchoolId());
        editSchoolFormData.setSchoolName("Norton Academy");
        editSchoolFormData.setSchoolAddress1("123 Main St.");
        editSchoolFormData.setCity("Richmond");
        editSchoolFormData.setZip("23235");
        editSchoolFormData.setCounty("Delaware");
        editSchoolFormData.setDistrict("Roth");
        editSchoolFormData.setState("VA");
        editSchoolFormData.setPhone("302-234-5678");
        editSchoolFormData.setLmiEligible("15.4");
        editSchoolFormData.setSLC("N120");
        dynamoDBDatabase.modifySchool(editSchoolFormData);
        List<School> secondListOfSchools = dynamoDBDatabase.getAllSchools();
        assertEquals(1, secondListOfSchools.size());
        assertEquals("Norton Academy", secondListOfSchools.get(0).getName());
    }

    @Test
    public void testGetAllBanksWhenThereAreNone() throws SQLException {
        List<Bank> banks = dynamoDBDatabase.getAllBanks();
        assertEquals(0, banks.size());
    }

    /** Creates a bank and admin. */
    private void insertNewBank(String bankAdminEmail) throws SQLException, EmailAlreadyInUseException {
        CreateBankFormData createBankFormData = new CreateBankFormData();
        createBankFormData.setBankName("Last Trust Bank");
        createBankFormData.setEmail("weibosum@example.org");
        createBankFormData.setFirstName("Wei");
        createBankFormData.setLastName("Bo Sum");
        createBankFormData.setPhoneNumber("302-255-1234");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);
    }

    /** Will be used in a few other tests. */
    private String insertNewBankAndReturnTheId() throws SQLException, EmailAlreadyInUseException {
        insertNewBank("weibosum@example.org");
        List<Bank> banks = dynamoDBDatabase.getAllBanks();
        assertEquals(1, banks.size());
        return banks.get(0).getBankId();
    }

    @Test
    public void testCreateACoupleOfBanksThenGetThemAll() throws SQLException, EmailAlreadyInUseException {
        // Note: This doesn't set minLMIForCRA or bankSpecificDataLabel because those aren't available on this form
        CreateBankFormData createBankFormData = new CreateBankFormData();
        createBankFormData.setBankName("Last Trust Bank");
        createBankFormData.setEmail("weibosum@example.org");
        createBankFormData.setFirstName("Wei");
        createBankFormData.setLastName("Bo Sum");
        createBankFormData.setPhoneNumber("302-255-1234");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);
        createBankFormData.setBankName("First National Bank");
        createBankFormData.setEmail("weibosum2@example.org");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);
        createBankFormData.setBankName("Halfway Federal Trust");
        createBankFormData.setEmail("weibosum3@example.org");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);
        List<Bank> banks = dynamoDBDatabase.getAllBanks();
        assertEquals(3, banks.size());
        assertEquals("First National Bank", banks.get(0).getBankName());
        assertEquals("Halfway Federal Trust", banks.get(1).getBankName());
        assertEquals("Last Trust Bank", banks.get(2).getBankName());
    }

    @Test
    public void testCreateTwoBanksDeleteOneThenGetThem() throws SQLException, NoSuchBankException, EmailAlreadyInUseException {
        CreateBankFormData createBankFormData = new CreateBankFormData();
        createBankFormData.setBankName("Last Trust Bank");
        createBankFormData.setEmail("weibosum@example.org");
        createBankFormData.setFirstName("Wei");
        createBankFormData.setLastName("Bo Sum");
        createBankFormData.setPhoneNumber("302-255-1234");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);
        createBankFormData.setBankName("First National Bank");
        createBankFormData.setEmail("weibosum2@example.org");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);

        List<Bank> firstListOfBanks = dynamoDBDatabase.getAllBanks();
        assertEquals(2, firstListOfBanks.size());
        dynamoDBDatabase.deleteBank(firstListOfBanks.get(0).getBankId());
        List<Bank> secondListOfBanks = dynamoDBDatabase.getAllBanks();
        assertEquals(1, secondListOfBanks.size());
    }

    @Test
    public void testRetrieveBankById() throws SQLException, EmailAlreadyInUseException {
        String bankId = insertNewBankAndReturnTheId();
        Bank bank = dynamoDBDatabase.getBankById(bankId);
        assertEquals("Last Trust Bank", bank.getBankName());
    }

    @Test
    public void testModifyBankWithNumericLMI() throws SQLException, EmailAlreadyInUseException, NoSuchBankException {
        String bankId = insertNewBankAndReturnTheId();
        EditBankFormData editBankFormData = new EditBankFormData();
        editBankFormData.setBankId(bankId);
        editBankFormData.setBankName("First Savings");
        editBankFormData.setMinLMIForCRA("34");
        dynamoDBDatabase.modifyBankAndBankAdmin(editBankFormData);
        Bank bank = dynamoDBDatabase.getBankById(bankId);
        assertEquals("First Savings", bank.getBankName());
        assertEquals(Integer.valueOf(34), bank.getMinLMIForCRA());
    }

    @Test
    public void testModifyBankWithBlankLMI() throws SQLException, EmailAlreadyInUseException, NoSuchBankException {
        String bankId = insertNewBankAndReturnTheId();
        EditBankFormData editBankFormData = new EditBankFormData();
        editBankFormData.setBankId(bankId);
        editBankFormData.setBankName("First Savings");
        editBankFormData.setMinLMIForCRA("");
        dynamoDBDatabase.modifyBankAndBankAdmin(editBankFormData);
        Bank bank = dynamoDBDatabase.getBankById(bankId);
        assertEquals("First Savings", bank.getBankName());
        assertNull(bank.getMinLMIForCRA());
    }

    @Test
    public void testModifyBankAdminFields() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        EditBankFormData editBankFormData = new EditBankFormData();
        editBankFormData.setBankId(bankId);
        editBankFormData.setFirstName("Bob");
        editBankFormData.setLastName("Edwards");
        editBankFormData.setEmail("bob.edwards@gmail.com");
        editBankFormData.setPhoneNumber("555-1234");
        dynamoDBDatabase.modifyBankAndBankAdmin(editBankFormData);
        BankAdmin bankAdmin = dynamoDBDatabase.getBankAdminByBank(bankId);
        assertEquals("Bob", bankAdmin.getFirstName());
        assertEquals("Edwards", bankAdmin.getLastName());
        assertEquals("bob.edwards@gmail.com", bankAdmin.getEmail());
        assertEquals("555-1234", bankAdmin.getPhoneNumber());
    }

    @Test
    public void testSetBankSpecificFieldLabelToString() throws SQLException, EmailAlreadyInUseException, NoSuchBankException {
        String bankId = insertNewBankAndReturnTheId();
        SetBankSpecificFieldLabelFormData formData = new SetBankSpecificFieldLabelFormData();
        formData.setBankId(bankId);
        formData.setBankSpecificFieldLabel("Sample Value");
        dynamoDBDatabase.setBankSpecificFieldLabel(formData);
        Bank bank = dynamoDBDatabase.getBankById(bankId);
        assertEquals("Sample Value", bank.getBankSpecificDataLabel());
    }


    @Test
    public void testSetBankSpecificFieldLabelToEmpty() throws SQLException, EmailAlreadyInUseException, NoSuchBankException {
        String bankId = insertNewBankAndReturnTheId();
        SetBankSpecificFieldLabelFormData formData = new SetBankSpecificFieldLabelFormData();
        formData.setBankId(bankId);
        formData.setBankSpecificFieldLabel("");
        dynamoDBDatabase.setBankSpecificFieldLabel(formData);
        Bank bank = dynamoDBDatabase.getBankById(bankId);
        assertEquals("", bank.getBankSpecificDataLabel());
    }

    private Teacher insertTeacher(String schoolId, String email) throws SQLException, NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException {
        TeacherRegistrationFormData teacherRegistrationFormData = new TeacherRegistrationFormData();
        teacherRegistrationFormData.setEmail(email);
        teacherRegistrationFormData.setFirstName("Jane");
        teacherRegistrationFormData.setLastName("Doe");
        teacherRegistrationFormData.setPhoneNumber("302-255-1234");
        teacherRegistrationFormData.setSchoolId(schoolId);
        String hashedPassword = "pIdlMcr8gPuKCTNlKBR7dayaDVk==";
        String salt = "JEgSZ2VfBC4=";
        return dynamoDBDatabase.insertNewTeacher(teacherRegistrationFormData, hashedPassword, salt);
    }

    /** Inserts a particular set of data to create a teacher and returns the new Teacher object. */
    private Teacher insertTeacherJane(String schoolId) throws SQLException, NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException {
        return insertTeacher(schoolId, "jane@sample.com");
    }

    @Test
    public void testInsertTeacherVerifyFields() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        assertNotNull(teacher.getUserId());
        assertEquals(UserType.TEACHER, teacher.getUserType());
        assertEquals("jane@sample.com", teacher.getEmail());
        assertEquals("Jane", teacher.getFirstName());
        assertEquals("Doe", teacher.getLastName());
        assertEquals("302-255-1234", teacher.getPhoneNumber());
        assertEquals(schoolId, teacher.getSchoolId());
        assertEquals("pIdlMcr8gPuKCTNlKBR7dayaDVk==", teacher.getHashedPassword());
        assertEquals("JEgSZ2VfBC4=", teacher.getSalt());
    }

    @Test
    public void testInsertTeacherAndListBySchool() throws SQLException, NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException {
        // -- Create a school --
        String schoolId = insertNewSchoolAndReturnTheId();
        // -- Create a teacher --
        Teacher teacher = insertTeacherJane(schoolId);
        // -- Check that it is there --
        List<Teacher> teachers = dynamoDBDatabase.getTeachersBySchool(schoolId);
        assertEquals(1, teachers.size());
        assertEquals("Jane", teachers.get(0).getFirstName());
    }

    @Test
    public void testInsertTeacherAndListById() throws SQLException, NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException {
        // -- Create a school --
        String schoolId = insertNewSchoolAndReturnTheId();
        // -- Create a teacher --
        Teacher teacherReturned = insertTeacherJane(schoolId);
        // --- Retrieve and verify --
        User userFetched = dynamoDBDatabase.getUserById(teacherReturned.getUserId());
        assertEquals("Jane", userFetched.getFirstName());
    }

    @Test
    public void testGetUserByIdWhichDoesNotExist() throws Exception {
        User user = dynamoDBDatabase.getUserById("0");
        assertNull(user);
    }

    @Test
    public void testInsertTeacherThenDeleteHer() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        dynamoDBDatabase.deleteTeacher(teacher.getUserId());
        User userFetched = dynamoDBDatabase.getUserById(teacher.getUserId());
        assertNull(userFetched);
    }

    @Test
    @Ignore // Skip this test because those using index give an error when run under maven
    public void testInsertTeacherThenSearchByEmail() throws Exception {
        DynamoDBSetup.initializeUserByEmailIndex(dynamoDB);
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        User userFetched = dynamoDBDatabase.getUserByEmail(teacher.getEmail());
        assertEquals(teacher.getUserId(), userFetched.getUserId());
    }

    @Test
    @Ignore // Skip this test because those using index give an error when run under maven
    public void testSearchByEmailButNotFound() throws Exception {
        DynamoDBSetup.initializeUserByEmailIndex(dynamoDB);
        User user = dynamoDBDatabase.getUserByEmail("fake@place.com");
        assertNull(user);
    }

    @Test
    public void testUpdateUserCredential() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        String hashedPassword = "81+w3TKFvvrIMjU97abZunWYarw=";
        String salt = "i1Ncya9/3xI=";
        dynamoDBDatabase.updateUserCredential(teacher.getUserId(), hashedPassword, salt);
        User userFetched = dynamoDBDatabase.getUserById(teacher.getUserId());
        assertEquals(hashedPassword, userFetched.getHashedPassword());
        assertEquals(salt, userFetched.getSalt());
    }


    @Test
    public void testUpdateResetPasswordToken() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        String resetPasswordToken = "RbehTBuEY0i5GV1P7WcoioOvT3M=";
        dynamoDBDatabase.updateResetPasswordToken(teacher.getUserId(), resetPasswordToken);
        User userFetched = dynamoDBDatabase.getUserById(teacher.getUserId());
        assertEquals(resetPasswordToken, userFetched.getResetPasswordToken());
    }


    @Test
    public void testCreateTeacherThenModifyUserPersonalFields() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);

        EditPersonalDataFormData editPersonalDataFormData = new EditPersonalDataFormData();
        editPersonalDataFormData.setUserId(teacher.getUserId());
        editPersonalDataFormData.setEmail("other@example.com");
        editPersonalDataFormData.setFirstName("Arnold");
        editPersonalDataFormData.setLastName("Smith");
        editPersonalDataFormData.setPhoneNumber("610-842-1102");
        dynamoDBDatabase.modifyUserPersonalFields(editPersonalDataFormData);

        User userFetched = dynamoDBDatabase.getUserById(teacher.getUserId());
        assertEquals("other@example.com", userFetched.getEmail());
        assertEquals("Arnold", userFetched.getFirstName());
        assertEquals("Smith", userFetched.getLastName());
        assertEquals("610-842-1102", userFetched.getPhoneNumber());
    }

    @Test
    public void testCreateTeacherThenModifyTeacherSchool() throws Exception {
        String schoolId1 = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId1);
        insertNewSchool("Midtown Prep");
        List<School> schools = dynamoDBDatabase.getAllSchools();
        String midtownPrepId = null;
        for (School school : schools) {
            if (school.getName().equals("Midtown Prep")) {
                midtownPrepId = school.getSchoolId();
            }
        }
        assertNotNull(midtownPrepId);

        dynamoDBDatabase.modifyTeacherSchool(teacher.getUserId(), midtownPrepId);

        Teacher teacherFetched = (Teacher) dynamoDBDatabase.getUserById(teacher.getUserId());
        assertEquals(teacherFetched.getSchoolId(), midtownPrepId);
    }

    /** Inserts a volunteer with the specified email. */
    private Volunteer insertVolunteer(String bankId, String email) throws SQLException, NoSuchBankException, EmailAlreadyInUseException {
        VolunteerRegistrationFormData volunteerRegistrationFormData = new VolunteerRegistrationFormData();
        volunteerRegistrationFormData.setEmail(email);
        volunteerRegistrationFormData.setFirstName("Anika");
        volunteerRegistrationFormData.setLastName("Doe");
        volunteerRegistrationFormData.setPhoneNumber("302-255-1234");
        volunteerRegistrationFormData.setBankId(bankId);
        volunteerRegistrationFormData.setBankSpecificData("Mail Stop");
        String hashedPassword = "pIdlMcr8gPuKCTNlKBR7dayaDVk==";
        String salt = "JEgSZ2VfBC4=";
        return dynamoDBDatabase.insertNewVolunteer(volunteerRegistrationFormData, hashedPassword, salt);
    }

    /** Inserts a particular set of data to create a volunteer and returns the new Volunteer object. */
    private Volunteer insertVolunteerAnika(String bankId) throws SQLException, NoSuchBankException, EmailAlreadyInUseException {
        return insertVolunteer(bankId, "anika@sample.com");
    }


    @Test
    public void testCreateVolunteer() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer = insertVolunteerAnika(bankId);

        Volunteer volunteerFetched = (Volunteer) dynamoDBDatabase.getUserById(volunteer.getUserId());
        assertEquals(volunteer.getUserId(), volunteerFetched.getUserId());
        assertEquals("anika@sample.com", volunteerFetched.getEmail());
        assertEquals("Anika", volunteerFetched.getFirstName());
        assertEquals("Doe", volunteerFetched.getLastName());
        assertEquals("302-255-1234", volunteerFetched.getPhoneNumber());
        assertEquals(bankId, volunteerFetched.getBankId());
        assertEquals("Mail Stop", volunteerFetched.getBankSpecificData());
        assertEquals("pIdlMcr8gPuKCTNlKBR7dayaDVk==", volunteerFetched.getHashedPassword());
        assertEquals("JEgSZ2VfBC4=", volunteerFetched.getSalt());
    }

    @Test
    public void testCreateVolunteerThenDeleteHer() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer = insertVolunteerAnika(bankId);
        dynamoDBDatabase.deleteVolunteer(volunteer.getUserId());
        User userFetched = dynamoDBDatabase.getUserById(volunteer.getUserId());
        assertNull(userFetched);
    }

    @Test
    public void testCreateVolunteerThenModifyVolunteerPersonalFields() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer = insertVolunteerAnika(bankId);

        EditVolunteerPersonalDataFormData editVolunteerPersonalDataFormData = new EditVolunteerPersonalDataFormData();
        editVolunteerPersonalDataFormData.setUserId(volunteer.getUserId());
        editVolunteerPersonalDataFormData.setEmail("other@example.com");
        editVolunteerPersonalDataFormData.setFirstName("Arnold");
        editVolunteerPersonalDataFormData.setLastName("Smith");
        editVolunteerPersonalDataFormData.setPhoneNumber("610-842-1102");
        editVolunteerPersonalDataFormData.setBankSpecificData("");
        dynamoDBDatabase.modifyVolunteerPersonalFields(editVolunteerPersonalDataFormData);

        Volunteer volunteerFetched = (Volunteer) dynamoDBDatabase.getUserById(volunteer.getUserId());
        assertEquals("other@example.com", volunteerFetched.getEmail());
        assertEquals("Arnold", volunteerFetched.getFirstName());
        assertEquals("Smith", volunteerFetched.getLastName());
        assertEquals("610-842-1102", volunteerFetched.getPhoneNumber());
        assertEquals("", volunteerFetched.getBankSpecificData());
    }

    @Test
    public void testUpdateApprovalStatusToSuspended() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer = insertVolunteerAnika(bankId);
        dynamoDBDatabase.updateApprovalStatusById(volunteer.getUserId(), ApprovalStatus.SUSPENDED);
        Volunteer volunteerFetched = (Volunteer) dynamoDBDatabase.getUserById(volunteer.getUserId());
        assertEquals(ApprovalStatus.SUSPENDED, volunteerFetched.getApprovalStatus());
    }

    @Test
    public void testUpdateApprovalStatusToChecked() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer = insertVolunteerAnika(bankId);
        dynamoDBDatabase.updateApprovalStatusById(volunteer.getUserId(), ApprovalStatus.CHECKED);
        Volunteer volunteerFetched = (Volunteer) dynamoDBDatabase.getUserById(volunteer.getUserId());
        assertEquals(ApprovalStatus.CHECKED, volunteerFetched.getApprovalStatus());
    }

    @Test
    public void testCreateBankAdminAndVerifyListOfAllBankAdmins() throws Exception {
        insertNewBankAndReturnTheId();
        List<BankAdmin> bankAdmins = dynamoDBDatabase.getBankAdmins();
        assertEquals(1, bankAdmins.size());
        assertEquals("Wei", bankAdmins.get(0).getFirstName());
    }


    private void insertEvent(String teacherId, PrettyPrintingDate date, String time) throws Exception {
        CreateEventFormData createEventFormData = new CreateEventFormData();
        createEventFormData.setEventDate(date);
        createEventFormData.setEventTime(time);
        createEventFormData.setGrade("3");
        createEventFormData.setNumberStudents("25");
        createEventFormData.setNotes("The class is quite unruly.");
        dynamoDBDatabase.insertEvent(teacherId, createEventFormData);
    }

    private Event insertEventAndReturnIt(String teacherId) throws Exception {
        PrettyPrintingDate date = insertDateAndReturnIt();
        String time = insertTimeAndReturnIt();
        insertEvent(teacherId, date, time);
        List<Event> events = dynamoDBDatabase.getAllEvents();
        assertEquals(1, events.size());
        return events.get(0);
    }

    @Test
    public void testInsertEventAndListIt() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        PrettyPrintingDate date = insertDateAndReturnIt();
        String time = insertTimeAndReturnIt();
        insertEvent(teacher.getUserId(), date, time);
        List<Event> events = dynamoDBDatabase.getAllEvents();
        assertEquals(1, events.size());
        Event eventFetched = events.get(0);
        assertEquals("2016-12-19", eventFetched.getEventDate().getParseable());
        assertEquals("2:00", eventFetched.getEventTime());
        assertEquals("3", eventFetched.getGrade());
        assertEquals(25, eventFetched.getNumberStudents());
        assertEquals("The class is quite unruly.", eventFetched.getNotes());
        assertNotNull(eventFetched.getTeacherId());
        assertNull(eventFetched.getVolunteerId());
    }

    @Test
    public void testInsertEventThenDeleteIt() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        Event event = insertEventAndReturnIt(teacher.getUserId());
        dynamoDBDatabase.deleteEvent(event.getEventId());
        List<Event> events = dynamoDBDatabase.getAllEvents();
        assertEquals(0, events.size());
    }

    @Test
    public void testInsertEventThenFetchById() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        Event event = insertEventAndReturnIt(teacher.getUserId());
        Event eventFetched = dynamoDBDatabase.getEventById(event.getEventId());
        assertEquals("2016-12-19", eventFetched.getEventDate().getParseable());
        assertEquals("2:00", eventFetched.getEventTime());
        assertEquals("3", eventFetched.getGrade());
        assertEquals(25, eventFetched.getNumberStudents());
        assertEquals("The class is quite unruly.", eventFetched.getNotes());
        assertNotNull(eventFetched.getTeacherId());
        assertNull(eventFetched.getVolunteerId());
    }

    @Test
    public void testInsertEventThenModifyIt() throws Exception {
        // -- Create first event --
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        Event event = insertEventAndReturnIt(teacher.getUserId());

        // -- Allow second date --
        PrettyPrintingDate secondDate;
        String parsableDateStr = "2017-03-12";
        AddAllowedDateFormData addAllowedDateFormData = new AddAllowedDateFormData();
        addAllowedDateFormData.setParsableDateStr(parsableDateStr);
        dynamoDBDatabase.insertNewAllowedDate(addAllowedDateFormData);
        try {
            secondDate = PrettyPrintingDate.fromParsableDate(parsableDateStr);
        } catch(ParseException err) {
            throw new RuntimeException(err);
        }

        // -- Allow second time --
        String secondTime = "3:00 - 4:00";
        AddAllowedTimeFormData addAllowedTimeFormData = new AddAllowedTimeFormData();
        addAllowedTimeFormData.setAllowedTime(secondTime);
        addAllowedTimeFormData.setTimeToInsertBefore("");
        dynamoDBDatabase.insertNewAllowedTime(addAllowedTimeFormData);

        // -- Update first event --
        EventRegistrationFormData eventRegistrationFormData = new EventRegistrationFormData();
        eventRegistrationFormData.setEventId(event.getEventId());
        eventRegistrationFormData.setEventDate(secondDate);
        eventRegistrationFormData.setEventTime(secondTime);
        eventRegistrationFormData.setGrade("4");
        eventRegistrationFormData.setNumberStudents("16");
        eventRegistrationFormData.setNotes("");
        dynamoDBDatabase.modifyEvent(eventRegistrationFormData);
        List<Event> events = dynamoDBDatabase.getAllEvents();
        assertEquals(1, events.size());
        Event eventFetched = events.get(0);

        // -- Validate --
        assertEquals("2017-03-12", eventFetched.getEventDate().getParseable());
        assertEquals(secondTime, eventFetched.getEventTime());
        assertEquals("4", eventFetched.getGrade());
        assertEquals(16, eventFetched.getNumberStudents());
        assertEquals("", eventFetched.getNotes());
        assertNotNull(eventFetched.getTeacherId());
        assertNull(eventFetched.getVolunteerId());
    }


    @Test
    public void testVolunteerForAnEvent() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        Event event = insertEventAndReturnIt(teacher.getUserId());
        assertEquals(null, event.getVolunteerId());
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer = insertVolunteerAnika(bankId);

        dynamoDBDatabase.volunteerForEvent(event.getEventId(), volunteer.getUserId());
        Event eventFetched = dynamoDBDatabase.getEventById(event.getEventId());
        assertEquals(volunteer.getUserId(), eventFetched.getVolunteerId());
    }

    @Test
    public void testGetEventsByTeacher() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher1 = insertTeacherJane(schoolId);
        Teacher teacher2 = insertTeacher(schoolId, "paul@gmail.com");
        PrettyPrintingDate date = insertDateAndReturnIt();
        String time = insertTimeAndReturnIt();

        List<Event> events0 = dynamoDBDatabase.getEventsByTeacher(teacher1.getUserId());
        assertEquals(0, events0.size());
        insertEvent(teacher1.getUserId(), date, time);
        List<Event> events1 = dynamoDBDatabase.getEventsByTeacher(teacher1.getUserId());
        assertEquals(1, events1.size());
        insertEvent(teacher1.getUserId(), date, time);
        List<Event> events2 = dynamoDBDatabase.getEventsByTeacher(teacher1.getUserId());
        assertEquals(2, events2.size());
        insertEvent(teacher2.getUserId(), date, time);
        assertEquals(2, events2.size());
    }

    @Test
    public void testGetAllAvailableEvents() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer = insertVolunteerAnika(bankId);
        PrettyPrintingDate date = insertDateAndReturnIt();
        String time = insertTimeAndReturnIt();

        List<Event> events0 = dynamoDBDatabase.getAllAvailableEvents();
        assertEquals(0, events0.size());
        insertEvent(teacher.getUserId(), date, time);
        List<Event> events1 = dynamoDBDatabase.getAllAvailableEvents();
        assertEquals(1, events1.size());
        insertEvent(teacher.getUserId(), date, time);
        List<Event> events2 = dynamoDBDatabase.getAllAvailableEvents();
        assertEquals(2, events2.size());
        dynamoDBDatabase.volunteerForEvent(events2.get(0).getEventId(), volunteer.getUserId());
        List<Event> events3 = dynamoDBDatabase.getAllAvailableEvents();
        assertEquals(1, events3.size());
    }


    @Test
    public void testGetEventsByVolunteer() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer1 = insertVolunteerAnika(bankId);
        Volunteer volunteer2 = insertVolunteer(bankId, "chip@bankofamerica.com");
        PrettyPrintingDate date = insertDateAndReturnIt();
        String time = insertTimeAndReturnIt();

        List<Event> events0 = dynamoDBDatabase.getEventsByVolunteer(volunteer1.getUserId());
        assertEquals(0, events0.size());
        insertEvent(teacher.getUserId(), date, time);
        insertEvent(teacher.getUserId(), date, time);
        insertEvent(teacher.getUserId(), date, time);
        List<Event> events1 = dynamoDBDatabase.getEventsByVolunteer(volunteer1.getUserId());
        assertEquals(0, events1.size());
        List<Event> allEvents1 = dynamoDBDatabase.getAllEvents();
        assertEquals(3, allEvents1.size());
        String event0Id = allEvents1.get(0).getEventId();
        String event1Id = allEvents1.get(1).getEventId();
        String event2Id = allEvents1.get(2).getEventId();
        dynamoDBDatabase.volunteerForEvent(event0Id, volunteer1.getUserId());
        dynamoDBDatabase.volunteerForEvent(event1Id, volunteer1.getUserId());
        dynamoDBDatabase.volunteerForEvent(event2Id, volunteer2.getUserId());
        List<Event> events2 = dynamoDBDatabase.getEventsByVolunteer(volunteer1.getUserId());
        assertEquals(2, events2.size());
        List<Event> events3 = dynamoDBDatabase.getEventsByVolunteer(volunteer2.getUserId());
        assertEquals(1, events3.size());
        assertEquals(event2Id, events3.get(0).getEventId());
    }

    @Test
    public void testGetVolunteersByBank() throws Exception {
        insertNewBank("rick@neptunetrust.com");
        insertNewBank("glen.smith@bofa.com");
        insertNewBank("cy.arnold@capitalone.com");
        List<Bank> banks = dynamoDBDatabase.getAllBanks();
        assertEquals(3, banks.size());
        String bank0Id = banks.get(0).getBankId();
        String bank1Id = banks.get(1).getBankId();
        String bank2Id = banks.get(2).getBankId();
        Volunteer volunteer0 = insertVolunteer(bank1Id, "stefan@gmail.com");
        Volunteer volunteer1 = insertVolunteer(bank2Id, "anika@bankofamerica.com");
        Volunteer volunteer2 = insertVolunteer(bank2Id, "chip@bankofamerica.com");

        List<Volunteer> volunteers0 = dynamoDBDatabase.getVolunteersByBank(bank0Id);
        assertEquals(0, volunteers0.size());
        List<Volunteer> volunteers1 = dynamoDBDatabase.getVolunteersByBank(bank1Id);
        assertEquals(1, volunteers1.size());
        assertEquals(volunteer0.getUserId(), volunteers1.get(0).getUserId());
        List<Volunteer> volunteers2 = dynamoDBDatabase.getVolunteersByBank(bank2Id);
        assertEquals(2, volunteers2.size());
    }
}