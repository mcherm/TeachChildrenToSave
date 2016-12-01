package com.tcts.database;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.tcts.common.PrettyPrintingDate;
import com.tcts.datamodel.Bank;
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
import com.tcts.formdata.CreateSchoolFormData;
import com.tcts.formdata.EditBankFormData;
import com.tcts.formdata.EditPersonalDataFormData;
import com.tcts.formdata.EditSchoolFormData;
import com.tcts.formdata.EditVolunteerPersonalDataFormData;
import com.tcts.formdata.SetBankSpecificFieldLabelFormData;
import com.tcts.formdata.TeacherRegistrationFormData;
import com.tcts.formdata.VolunteerRegistrationFormData;
import org.junit.Before;
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
public class DynamoDBDatabaseTest {
    private final DynamoDB dynamoDB;
    private DynamoDBDatabase dynamoDBDatabase;

    /** Constructor, just connect to the database once. */
    public DynamoDBDatabaseTest() {
        dynamoDB = DynamoDBDatabase.connectToDB();
    }

    @Before
    public void initialize() throws InterruptedException {
        DynamoDBSetup.reinitializeAllDatabaseTables(dynamoDB);
        dynamoDBDatabase = new DynamoDBDatabase(null);
    }

    @Test
    public void writeOneSettingAndReadIt() throws SQLException {
        dynamoDBDatabase.modifySiteSetting("TestSetting", "TestValue");
        Map<String,String> siteSettings = dynamoDBDatabase.getSiteSettings();
        assertEquals("TestValue", siteSettings.get("TestSetting"));
    }

    @Test
    public void writeOneDateAndReadIt() throws SQLException, AllowedDateAlreadyInUseException, ParseException {
        AddAllowedDateFormData addAllowedDateFormData = new AddAllowedDateFormData();
        addAllowedDateFormData.setParsableDateStr("2016-12-19");
        dynamoDBDatabase.insertNewAllowedDate(addAllowedDateFormData);
        List<PrettyPrintingDate> allowedDates = dynamoDBDatabase.getAllowedDates();
        assertEquals(Arrays.asList(PrettyPrintingDate.fromParsableDate("2016-12-19")), allowedDates);
    }

    @Test
    public void writeThreeDatesOutOfOrderAndReadThem() throws SQLException, AllowedDateAlreadyInUseException, ParseException {
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
    public void writeSameDateTwiceAndReadIt() throws SQLException, AllowedDateAlreadyInUseException, ParseException {
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
    public void insertDateDeleteIt() throws SQLException, AllowedDateAlreadyInUseException, ParseException, NoSuchAllowedDateException {
        AddAllowedDateFormData addAllowedDateFormData = new AddAllowedDateFormData();
        addAllowedDateFormData.setParsableDateStr("2016-12-19");
        dynamoDBDatabase.insertNewAllowedDate(addAllowedDateFormData);
        dynamoDBDatabase.deleteAllowedDate(PrettyPrintingDate.fromParsableDate("2016-12-19"));
        List<PrettyPrintingDate> allowedDates = dynamoDBDatabase.getAllowedDates();
        assertEquals(
                Arrays.asList(),
                allowedDates);
    }

    @Test
    public void writeOneTimeAndReadIt() throws SQLException, AllowedTimeAlreadyInUseException, NoSuchAllowedTimeException {
        AddAllowedTimeFormData addAllowedTimeFormData = new AddAllowedTimeFormData();
        addAllowedTimeFormData.setAllowedTime("2:00");
        addAllowedTimeFormData.setTimeToInsertBefore("");
        dynamoDBDatabase.insertNewAllowedTime(addAllowedTimeFormData);
        List<String> allowedTimes = dynamoDBDatabase.getAllowedTimes();
        assertEquals(Arrays.asList("2:00"), allowedTimes);
    }

    @Test
    public void writeThreeTimesOutOfOrderAndReadThem() throws SQLException, AllowedTimeAlreadyInUseException, NoSuchAllowedTimeException {
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
    public void insertTimeDeleteIt() throws SQLException, AllowedTimeAlreadyInUseException, ParseException, NoSuchAllowedTimeException {
        AddAllowedTimeFormData addAllowedTimeFormData = new AddAllowedTimeFormData();
        addAllowedTimeFormData.setAllowedTime("2:00");
        addAllowedTimeFormData.setTimeToInsertBefore("");
        dynamoDBDatabase.insertNewAllowedTime(addAllowedTimeFormData);
        dynamoDBDatabase.deleteAllowedTime("2:00");
        List<String> allowedTimes = dynamoDBDatabase.getAllowedTimes();
        assertEquals(
                Arrays.asList(),
                allowedTimes);
    }

    @Test
    public void retrieveNonexistentSchool() throws SQLException {
        School school = dynamoDBDatabase.getSchoolById("1234");
        assertNull(school);
    }

    @Test
    public void insertSchool() throws SQLException {
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
    public void getAllSchoolsWhenThereAreNone() throws SQLException {
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
    public void createOneSchool() throws SQLException {
        insertNewSchoolAndReturnTheId();
        List<School> schools = dynamoDBDatabase.getAllSchools();
        assertEquals(1, schools.size());
    }

    @Test
    public void createACoupleOfSchoolsThenGetThemAll() throws SQLException {
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
    public void createTwoSchoolsDeleteOneThenGetThem() throws SQLException, NoSuchSchoolException {
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
    public void createSchoolThenModifyIt() throws SQLException, NoSuchSchoolException {
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
    public void getAllBanksWhenThereAreNone() throws SQLException {
        List<Bank> banks = dynamoDBDatabase.getAllBanks();
        assertEquals(0, banks.size());
    }


    // FIXME: This ignores the fields that are part of the BankAdmin because that's not built yet.
    /** Will be used in a few other tests. */
    private String insertNewBankAndReturnTheId() throws SQLException, EmailAlreadyInUseException {
        CreateBankFormData createBankFormData = new CreateBankFormData();
        createBankFormData.setBankName("Last Trust Bank");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);

        List<Bank> banks = dynamoDBDatabase.getAllBanks();
        assertEquals(1, banks.size());
        return banks.get(0).getBankId();
    }

    // FIXME: This ignores the fields that are part of the BankAdmin because that's not built yet.
    @Test
    public void createACoupleOfBanksThenGetThemAll() throws SQLException, EmailAlreadyInUseException {
        // Note: This doesn't set minLMIForCRA or bankSpecificDataLabel because those aren't available on this form
        CreateBankFormData createBankFormData = new CreateBankFormData();
        createBankFormData.setBankName("Last Trust Bank");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);
        createBankFormData.setBankName("First National Bank");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);
        createBankFormData.setBankName("Halfway Federal Trust");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);
        List<Bank> banks = dynamoDBDatabase.getAllBanks();
        assertEquals(3, banks.size());
        assertEquals("First National Bank", banks.get(0).getBankName());
        assertEquals("Halfway Federal Trust", banks.get(1).getBankName());
        assertEquals("Last Trust Bank", banks.get(2).getBankName());
    }

    @Test
    public void createTwoBanksDeleteOneThenGetThem() throws SQLException, NoSuchBankException, EmailAlreadyInUseException {
        CreateBankFormData createBankFormData = new CreateBankFormData();
        createBankFormData.setBankName("Last Trust Bank");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);
        createBankFormData.setBankName("First National Bank");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);

        List<Bank> firstListOfBanks = dynamoDBDatabase.getAllBanks();
        assertEquals(2, firstListOfBanks.size());
        dynamoDBDatabase.deleteBank(firstListOfBanks.get(0).getBankId());
        List<Bank> secondListOfBanks = dynamoDBDatabase.getAllBanks();
        assertEquals(1, secondListOfBanks.size());
    }

    @Test
    public void retrieveBankById() throws SQLException, EmailAlreadyInUseException {
        CreateBankFormData createBankFormData = new CreateBankFormData();
        createBankFormData.setBankName("Last Trust Bank");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);
        List<Bank> listOfBanks = dynamoDBDatabase.getAllBanks();
        assertEquals(1, listOfBanks.size());
        Bank bank = dynamoDBDatabase.getBankById(listOfBanks.get(0).getBankId());
        assertEquals("Last Trust Bank", bank.getBankName());
    }

    // FIXME: This ignores the fields that are part of the BankAdmin because that's not built yet.
    @Test
    public void modifyBankWithNumericLMI() throws SQLException, EmailAlreadyInUseException, NoSuchBankException {
        CreateBankFormData createBankFormData = new CreateBankFormData();
        createBankFormData.setBankName("Last Trust Bank");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);
        List<Bank> listOfBanks = dynamoDBDatabase.getAllBanks();
        assertEquals(1, listOfBanks.size());

        EditBankFormData editBankFormData = new EditBankFormData();
        editBankFormData.setBankId(listOfBanks.get(0).getBankId());
        editBankFormData.setBankName("First Savings");
        editBankFormData.setMinLMIForCRA("34");
        dynamoDBDatabase.modifyBankAndBankAdmin(editBankFormData);
        Bank bank = dynamoDBDatabase.getBankById(listOfBanks.get(0).getBankId());
        assertEquals("First Savings", bank.getBankName());
        assertEquals(Integer.valueOf(34), bank.getMinLMIForCRA());
    }

    // FIXME: This ignores the fields that are part of the BankAdmin because that's not built yet.
    @Test
    public void modifyBankWithBlankLMI() throws SQLException, EmailAlreadyInUseException, NoSuchBankException {
        CreateBankFormData createBankFormData = new CreateBankFormData();
        createBankFormData.setBankName("Last Trust Bank");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);
        List<Bank> listOfBanks = dynamoDBDatabase.getAllBanks();
        assertEquals(1, listOfBanks.size());
        String bankId = listOfBanks.get(0).getBankId();

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
    public void setBankSpecificFieldLabelToString() throws SQLException, EmailAlreadyInUseException, NoSuchBankException {
        CreateBankFormData createBankFormData = new CreateBankFormData();
        createBankFormData.setBankName("Last Trust Bank");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);
        List<Bank> listOfBanks = dynamoDBDatabase.getAllBanks();
        assertEquals(1, listOfBanks.size());
        String bankId = listOfBanks.get(0).getBankId();

        SetBankSpecificFieldLabelFormData formData = new SetBankSpecificFieldLabelFormData();
        formData.setBankId(bankId);
        formData.setBankSpecificFieldLabel("Sample Value");
        dynamoDBDatabase.setBankSpecificFieldLabel(formData);
        Bank bank = dynamoDBDatabase.getBankById(bankId);
        assertEquals("Sample Value", bank.getBankSpecificDataLabel());
    }


    @Test
    public void setBankSpecificFieldLabelToEmpty() throws SQLException, EmailAlreadyInUseException, NoSuchBankException {
        CreateBankFormData createBankFormData = new CreateBankFormData();
        createBankFormData.setBankName("Last Trust Bank");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);
        List<Bank> listOfBanks = dynamoDBDatabase.getAllBanks();
        assertEquals(1, listOfBanks.size());
        String bankId = listOfBanks.get(0).getBankId();

        SetBankSpecificFieldLabelFormData formData = new SetBankSpecificFieldLabelFormData();
        formData.setBankId(bankId);
        formData.setBankSpecificFieldLabel("");
        dynamoDBDatabase.setBankSpecificFieldLabel(formData);
        Bank bank = dynamoDBDatabase.getBankById(bankId);
        assertEquals("", bank.getBankSpecificDataLabel());
    }

    /** Inserts a particular set of data to create a teacher and returns the new Teacher object. */
    private Teacher createTeacherJane(String schoolId) throws SQLException, NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException {
        TeacherRegistrationFormData teacherRegistrationFormData = new TeacherRegistrationFormData();
        teacherRegistrationFormData.setEmail("jane@sample.com");
        teacherRegistrationFormData.setFirstName("Jane");
        teacherRegistrationFormData.setLastName("Doe");
        teacherRegistrationFormData.setPhoneNumber("302-255-1234");
        teacherRegistrationFormData.setSchoolId(schoolId);
        String hashedPassword = "pIdlMcr8gPuKCTNlKBR7dayaDVk==";
        String salt = "JEgSZ2VfBC4=";
        Teacher teacher = dynamoDBDatabase.insertNewTeacher(teacherRegistrationFormData, hashedPassword, salt);
        return teacher;
    }

    @Test
    public void insertTeacherVerifyFields() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = createTeacherJane(schoolId);
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
    public void insertTeacherAndListBySchool() throws SQLException, NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException {
        // -- Create a school --
        String schoolId = insertNewSchoolAndReturnTheId();
        // -- Create a teacher --
        Teacher teacher = createTeacherJane(schoolId);
        // -- Check that it is there --
        List<Teacher> teachers = dynamoDBDatabase.getTeachersBySchool(schoolId);
        assertEquals(1, teachers.size());
        assertEquals("Jane", teachers.get(0).getFirstName());
    }

    @Test
    public void insertTeacherAndListById() throws SQLException, NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException {
        // -- Create a school --
        String schoolId = insertNewSchoolAndReturnTheId();
        // -- Create a teacher --
        Teacher teacherReturned = createTeacherJane(schoolId);
        // --- Retrieve and verify --
        User userFetched = dynamoDBDatabase.getUserById(teacherReturned.getUserId());
        assertEquals("Jane", userFetched.getFirstName());
    }

    @Test
    public void getUserByIdWhichDoesNotExist() throws Exception {
        User user = dynamoDBDatabase.getUserById("0");
        assertNull(user);
    }

    @Test
    public void insertTeacherThenDeleteHer() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = createTeacherJane(schoolId);
        dynamoDBDatabase.deleteTeacher(teacher.getUserId());
        User userFetched = dynamoDBDatabase.getUserById(teacher.getUserId());
        assertNull(userFetched);
    }

    @Test
    public void insertTeacherThenSearchByEmail() throws Exception {
        DynamoDBSetup.initializeUserByEmailIndex(dynamoDB);
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = createTeacherJane(schoolId);
        User userFetched = dynamoDBDatabase.getUserByEmail(teacher.getEmail());
        assertEquals(teacher.getUserId(), userFetched.getUserId());
    }

    @Test
    public void searchByEmailButNotFound() throws Exception {
        DynamoDBSetup.initializeUserByEmailIndex(dynamoDB);
        User user = dynamoDBDatabase.getUserByEmail("fake@place.com");
        assertNull(user);
    }

    @Test
    public void updateUserCredential() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = createTeacherJane(schoolId);
        String hashedPassword = "81+w3TKFvvrIMjU97abZunWYarw=";
        String salt = "i1Ncya9/3xI=";
        dynamoDBDatabase.updateUserCredential(teacher.getUserId(), hashedPassword, salt);
        User userFetched = dynamoDBDatabase.getUserById(teacher.getUserId());
        assertEquals(hashedPassword, userFetched.getHashedPassword());
        assertEquals(salt, userFetched.getSalt());
    }


    @Test
    public void updateResetPasswordToken() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = createTeacherJane(schoolId);
        String resetPasswordToken = "RbehTBuEY0i5GV1P7WcoioOvT3M=";
        dynamoDBDatabase.updateResetPasswordToken(teacher.getUserId(), resetPasswordToken);
        User userFetched = dynamoDBDatabase.getUserById(teacher.getUserId());
        assertEquals(resetPasswordToken, userFetched.getResetPasswordToken());
    }


    @Test
    public void createTeacherThenModifyUserPersonalFields() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = createTeacherJane(schoolId);

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
    public void createTeacherThenModifyTeacherSchool() throws Exception {
        String schoolId1 = insertNewSchoolAndReturnTheId();
        Teacher teacher = createTeacherJane(schoolId1);
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

    /** Inserts a particular set of data to create a teacher and returns the new Teacher object. */
    private Volunteer createVolunteerAnika(String bankId) throws SQLException, NoSuchBankException, EmailAlreadyInUseException {
        VolunteerRegistrationFormData volunteerRegistrationFormData = new VolunteerRegistrationFormData();
        volunteerRegistrationFormData.setEmail("anika@sample.com");
        volunteerRegistrationFormData.setFirstName("Anika");
        volunteerRegistrationFormData.setLastName("Doe");
        volunteerRegistrationFormData.setPhoneNumber("302-255-1234");
        volunteerRegistrationFormData.setBankId(bankId);
        volunteerRegistrationFormData.setBankSpecificData("Mail Stop");
        String hashedPassword = "pIdlMcr8gPuKCTNlKBR7dayaDVk==";
        String salt = "JEgSZ2VfBC4=";
        Volunteer volunteer = dynamoDBDatabase.insertNewVolunteer(volunteerRegistrationFormData, hashedPassword, salt);
        return volunteer;
    }


    @Test
    public void createVolunteer() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer = createVolunteerAnika(bankId);

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
    public void createVolunteerThenDeleteHer() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer = createVolunteerAnika(bankId);
        dynamoDBDatabase.deleteVolunteer(volunteer.getUserId());
        User userFetched = dynamoDBDatabase.getUserById(volunteer.getUserId());
        assertNull(userFetched);
    }

    @Test
    public void createVolunteerThenModifyVolunteerPersonalFields() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer = createVolunteerAnika(bankId);

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





    // FIXME: Add this test once I have a volunteer to test with
    /*
    @Test
    public void updateApprovalStatus() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = createTeacherJane(schoolId);
        dynamoDBDatabase.updateApprovalStatusById(teacher.getUserId(), ApprovalStatus.CHECKED);
        User userFetched = dynamoDBDatabase.getUserById(teacher.getUserId());
    }
    */

}
