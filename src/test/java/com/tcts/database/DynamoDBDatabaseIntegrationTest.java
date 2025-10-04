package com.tcts.database;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.tcts.common.Configuration;
import com.tcts.common.PrettyPrintingDate;
import com.tcts.database.dynamodb.DynamoDBHelper;
import com.tcts.datamodel.ApprovalStatus;
import com.tcts.datamodel.Bank;
import com.tcts.datamodel.BankAdmin;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.School;
import com.tcts.datamodel.SiteStatistics;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.User;
import com.tcts.datamodel.UserType;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.AllowedValueAlreadyInUseException;
import com.tcts.exception.EmailAlreadyInUseException;
import com.tcts.exception.EventAlreadyHasAVolunteerException;
import com.tcts.exception.NoSuchAllowedValueException;
import com.tcts.exception.NoSuchBankException;
import com.tcts.exception.NoSuchSchoolException;
import com.tcts.exception.TeacherHasEventsException;
import com.tcts.exception.VolunteerHasEventsException;
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
import com.tcts.formdata.SetBankSpecificFieldLabelFormData;
import com.tcts.formdata.TeacherRegistrationFormData;
import com.tcts.formdata.VolunteerRegistrationFormData;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.math.BigDecimal;

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
    private DynamoDBDatabase dynamoDBDatabase;
    private Configuration configuration;

    /**
     * Returns a configuration based on application.properties EXCEPT
     * that dynamoDB.environment has been set to "test".
     * @return
     */
    public static Configuration getTestConfiguration() {
        final Configuration rawConfiguration = new Configuration();
        return new Configuration() {
            @Override
            public String getProperty(String key) {
                if ("dynamoDB.environment".equals(key)) {
                    return "test";
                } else {
                    return rawConfiguration.getProperty(key);
                }
            }
        };
    }

    @BeforeClass
    public static void initializeClass() throws InterruptedException {
        // --- Create a configuration() that overrides dynamoDB.environment to be "test" ---
        Configuration configuration = getTestConfiguration();
        // ---
        DynamoDB dynamoDB = DynamoDBDatabase.connectToDB(configuration);
        try {
            DynamoDBSetup.deleteAllDatabaseTables(dynamoDB, configuration);
        } catch(ResourceNotFoundException err) {
            // It's fine if the deletions failed.
        }
        DynamoDBSetup.createAllDatabaseTables(dynamoDB, configuration);
    }

    @Before
    public void initializeTest() throws InterruptedException {
        Configuration configuration = getTestConfiguration();
        DynamoDB dynamoDB = DynamoDBDatabase.connectToDB(configuration);
        DynamoDBSetup.wipeAllDatabaseTables(dynamoDB, configuration);
        dynamoDBDatabase = new DynamoDBDatabase(configuration, new DynamoDBHelper());
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

    private PrettyPrintingDate insertDateAndReturnIt() throws SQLException, AllowedValueAlreadyInUseException {
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
    public void testWriteOneDateAndReturnIt() throws SQLException, AllowedValueAlreadyInUseException, ParseException {
        insertDateAndReturnIt();
        List<PrettyPrintingDate> allowedDates = dynamoDBDatabase.getAllowedDates();
        assertEquals(Collections.singletonList(PrettyPrintingDate.fromParsableDate("2016-12-19")), allowedDates);
    }

    @Test
    public void testWriteThreeDatesOutOfOrderAndReadThem() throws SQLException, AllowedValueAlreadyInUseException, ParseException {
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

    @Test(expected = AllowedValueAlreadyInUseException.class)
    public void testWriteSameDateTwiceAndReadIt() throws SQLException, AllowedValueAlreadyInUseException, ParseException {
        AddAllowedDateFormData addAllowedDateFormData1 = new AddAllowedDateFormData();
        addAllowedDateFormData1.setParsableDateStr("2016-12-19");
        dynamoDBDatabase.insertNewAllowedDate(addAllowedDateFormData1);
        AddAllowedDateFormData addAllowedDateFormData2 = new AddAllowedDateFormData();
        addAllowedDateFormData2.setParsableDateStr("2016-12-19");
        dynamoDBDatabase.insertNewAllowedDate(addAllowedDateFormData2);
    }

    @Test
    public void testInsertDateDeleteIt() throws SQLException, AllowedValueAlreadyInUseException, ParseException, NoSuchAllowedValueException {
        insertDateAndReturnIt();
        dynamoDBDatabase.deleteAllowedDate(PrettyPrintingDate.fromParsableDate("2016-12-19"));
        List<PrettyPrintingDate> allowedDates = dynamoDBDatabase.getAllowedDates();
        assertEquals(
                Collections.emptyList(),
                allowedDates);
    }

    /** Used a few places to create a time. */
    private String insertTimeAndReturnIt() throws SQLException, AllowedValueAlreadyInUseException, NoSuchAllowedValueException {
        String result = "2:00";
        dynamoDBDatabase.insertNewAllowedTime(result, "");
        return result;
    }

    @Test
    public void testWriteOneTimeAndReadIt() throws SQLException, AllowedValueAlreadyInUseException, NoSuchAllowedValueException {
        String timeStr = insertTimeAndReturnIt();
        List<String> allowedTimes = dynamoDBDatabase.getAllowedTimes();
        assertEquals(Collections.singletonList(timeStr), allowedTimes);
    }

    @Test
    public void testWriteThreeTimesOutOfOrderAndReadThem() throws SQLException, AllowedValueAlreadyInUseException, NoSuchAllowedValueException {
        dynamoDBDatabase.insertNewAllowedTime("6:00", "");
        dynamoDBDatabase.insertNewAllowedTime("2:00", "6:00");
        dynamoDBDatabase.insertNewAllowedTime("4:00", "6:00");
        List<String> allowedTimes = dynamoDBDatabase.getAllowedTimes();
        assertEquals(
                Arrays.asList("2:00", "4:00", "6:00"),
                allowedTimes);
    }


    @Test
    public void testInsertTimeDeleteIt() throws SQLException, AllowedValueAlreadyInUseException, ParseException, NoSuchAllowedValueException {
        String time = insertTimeAndReturnIt();
        dynamoDBDatabase.deleteAllowedTime(time);
        List<String> allowedTimes = dynamoDBDatabase.getAllowedTimes();
        assertEquals(
                Collections.emptyList(),
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
        createBankFormData.setEmail(bankAdminEmail);
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

    private void insertBankWithoutAdmin() throws SQLException, EmailAlreadyInUseException {
        CreateBankFormData createBankFormData = new CreateBankFormData();
        createBankFormData.setBankName("Last Trust Bank");
        dynamoDBDatabase.insertNewBankAndAdmin(createBankFormData);
    }

    @Test
    public void testCreateBankWithoutAdmin() throws Exception {
        insertBankWithoutAdmin();
        List<Bank> banks = dynamoDBDatabase.getAllBanks();
        assertEquals(1, banks.size());
    }

    @Test
    public void testCreateTwoBanksDeleteOneThenGetThem() throws Exception {
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
        dynamoDBDatabase.deleteBankAndBankVolunteers(firstListOfBanks.get(0).getBankId());
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
    public void testModifyBankWithIntegerLMI() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        EditBankFormData editBankFormData = new EditBankFormData();
        editBankFormData.setBankId(bankId);
        editBankFormData.setBankName("First Savings");
        editBankFormData.setMinLMIForCRA("34");
        dynamoDBDatabase.modifyBank(editBankFormData);
        Bank bank = dynamoDBDatabase.getBankById(bankId);
        assertEquals("First Savings", bank.getBankName());
        assertEquals(new BigDecimal(34), bank.getMinLMIForCRA());
    }

    @Test
    public void testModifyBankWithDecimalLMI() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        EditBankFormData editBankFormData = new EditBankFormData();
        editBankFormData.setBankId(bankId);
        editBankFormData.setBankName("First Savings");
        editBankFormData.setMinLMIForCRA("12.6");
        dynamoDBDatabase.modifyBank(editBankFormData);
        Bank bank = dynamoDBDatabase.getBankById(bankId);
        assertEquals("First Savings", bank.getBankName());
        assertEquals(new BigDecimal("12.6"), bank.getMinLMIForCRA());
    }

    @Test
    public void testModifyBankWithBlankLMI() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        EditBankFormData editBankFormData = new EditBankFormData();
        editBankFormData.setBankId(bankId);
        editBankFormData.setBankName("First Savings");
        editBankFormData.setMinLMIForCRA("");
        dynamoDBDatabase.modifyBank(editBankFormData);
        Bank bank = dynamoDBDatabase.getBankById(bankId);
        assertEquals("First Savings", bank.getBankName());
        assertNull(bank.getMinLMIForCRA());
    }

    @Test
    public void testModifyBankAdminDeletingBankAdmin() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        List<Volunteer> volunteersAndBankAdmins1 = dynamoDBDatabase.getVolunteersByBank(bankId);
        assertEquals(1, volunteersAndBankAdmins1.size());
        EditBankFormData editBankFormData = new EditBankFormData();
        editBankFormData.setBankId(bankId);
        editBankFormData.setBankName("Neighbors Bank");
        dynamoDBDatabase.modifyBank(editBankFormData);
        List<Bank> banks = dynamoDBDatabase.getAllBanks();
        assertEquals(1, banks.size());
        List<Volunteer> volunteersAndBankAdmins2 = dynamoDBDatabase.getVolunteersByBank(banks.get(0).getBankId());
        assertEquals(1, volunteersAndBankAdmins2.size());
        assertEquals(volunteersAndBankAdmins1.get(0).getUserId(), volunteersAndBankAdmins2.get(0).getUserId());
        assertEquals(UserType.BANK_ADMIN, volunteersAndBankAdmins2.get(0).getUserType());
    }

    @Test
    public void testModifyBankWhenThereIsNoBankAdmin() throws Exception {
        insertBankWithoutAdmin();
        List<Bank> banks1 = dynamoDBDatabase.getAllBanks();
        assertEquals(1, banks1.size());
        String bankId = banks1.get(0).getBankId();
        EditBankFormData editBankFormData = new EditBankFormData();
        editBankFormData.setBankId(bankId);
        editBankFormData.setBankName("Neighbors Bank");
        dynamoDBDatabase.modifyBank(editBankFormData);
        List<Bank> banks2 = dynamoDBDatabase.getAllBanks();
        assertEquals(1, banks2.size());
        Bank bank2 = banks2.get(0);
        assertEquals("Neighbors Bank", bank2.getBankName());
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
        assertEquals(teacher.getFirstName(), teachers.get(0).getFirstName());
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


    @Test(expected = TeacherHasEventsException.class)
    public void testDeleteTeacherThatHasEvents() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        insertEventAndReturnIt(teacher.getUserId());
        dynamoDBDatabase.deleteTeacher(teacher.getUserId());
    }


    @Test
    public void testInsertTeacherThenSearchByEmail() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        User userFetched = dynamoDBDatabase.getUserByEmail(teacher.getEmail());
        assertEquals(teacher.getUserId(), userFetched.getUserId());
    }

    @Test(expected = EmailAlreadyInUseException.class)
    public void testInsertTeacherWithExistingEmail() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        insertTeacher(schoolId, "simple.email@sample.com");
        insertTeacher(schoolId, "simple.email@sample.com");
    }

    @Test
    public void testSearchByEmailButNotFound() throws Exception {
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
    public void testUpdateUserDataWithoutChangingEmail() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);

        EditPersonalDataFormData editPersonalDataFormData = new EditPersonalDataFormData();
        editPersonalDataFormData.setUserId(teacher.getUserId());
        editPersonalDataFormData.setEmail("jane@sample.com");
        editPersonalDataFormData.setFirstName("Arnold");
        editPersonalDataFormData.setLastName("Smith");
        editPersonalDataFormData.setPhoneNumber("610-842-1102");
        dynamoDBDatabase.modifyUserPersonalFields(editPersonalDataFormData);
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

    @Test(expected = VolunteerHasEventsException.class)
    public void testDeleteVolunteerThatHasEvents() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer = insertVolunteerAnika(bankId);
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        Event event = insertEventAndReturnIt(teacher.getUserId());
        dynamoDBDatabase.volunteerForEvent(event.getEventId(), volunteer.getUserId());
        dynamoDBDatabase.deleteVolunteer(volunteer.getUserId());
    }

    @Test(expected = EmailAlreadyInUseException.class)
    public void testCreateVolunteerWithExistingEmail() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        insertVolunteer(bankId, "simple.email@sample.com");
        insertVolunteer(bankId, "simple.email@sample.com");
    }

    @Test(expected = EmailAlreadyInUseException.class)
    public void testCreateVolunteerWithConflictingTeacherEmail() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        String bankId = insertNewBankAndReturnTheId();
        insertTeacher(schoolId, "simple.email@sample.com");
        insertVolunteer(bankId, "simple.email@sample.com");
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

    @Test(expected = EmailAlreadyInUseException.class)
    public void createBankAdminWithConflictingEmailAddress() throws Exception {
        insertNewBank("sample_email@example.org");
        insertNewBank("sample_email@example.org");
    }


    private void insertEvent(String teacherId, PrettyPrintingDate date, String time) throws Exception {
        CreateEventFormData createEventFormData = new CreateEventFormData();
        createEventFormData.setEventDate(date);
        createEventFormData.setEventTime(time);
        createEventFormData.setGrade("3rd Grade");
        createEventFormData.setDeliveryMethod("In-Person");
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
        assertEquals("3rd Grade", eventFetched.getGrade());
        assertEquals("In-Person", eventFetched.getDeliveryMethod());
        assertEquals(25, eventFetched.getNumberStudents());
        assertEquals("The class is quite unruly.", eventFetched.getNotes());
        assertNotNull(eventFetched.getTeacherId());
        assertNotNull(eventFetched.getLinkedTeacher());
        assertNotNull(eventFetched.getLinkedTeacher().getLinkedSchool());
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
        assertEquals("3rd Grade", eventFetched.getGrade());
        assertEquals("In-Person", eventFetched.getDeliveryMethod());
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
        dynamoDBDatabase.insertNewAllowedTime(secondTime, "");

        // -- Update first event --
        EditEventFormData editEventFormData = new EditEventFormData();
        editEventFormData.setEventId(event.getEventId());
        editEventFormData.setEventDate(secondDate);
        editEventFormData.setEventTime(secondTime);
        editEventFormData.setGrade("4th Grade");
        editEventFormData.setDeliveryMethod("In-Person");
        editEventFormData.setNumberStudents("16");
        editEventFormData.setNotes("");
        dynamoDBDatabase.modifyEvent(editEventFormData);
        List<Event> events = dynamoDBDatabase.getAllEvents();
        assertEquals(1, events.size());
        Event eventFetched = events.get(0);

        // -- Validate --
        assertEquals("2017-03-12", eventFetched.getEventDate().getParseable());
        assertEquals(secondTime, eventFetched.getEventTime());
        assertEquals("4th Grade", eventFetched.getGrade());
        assertEquals("In-Person", eventFetched.getDeliveryMethod());
        assertEquals(16, eventFetched.getNumberStudents());
        assertEquals("", eventFetched.getNotes());
        assertNotNull(eventFetched.getTeacherId());
        assertNotNull(eventFetched.getLinkedTeacher());
        assertNotNull(eventFetched.getLinkedTeacher().getLinkedSchool());
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

    @Test(expected = EventAlreadyHasAVolunteerException.class)
    public void testVolunteerForAnEventThatSomeoneElseIsVolunteeringForAlso() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        Event event = insertEventAndReturnIt(teacher.getUserId());
        assertEquals(null, event.getVolunteerId());
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer1 = insertVolunteer(bankId, "firstVolunteer@bigcobank.com");
        Volunteer volunteer2 = insertVolunteer(bankId, "secondVolunteer@bigcobank.com");

        dynamoDBDatabase.volunteerForEvent(event.getEventId(), volunteer1.getUserId());
        dynamoDBDatabase.volunteerForEvent(event.getEventId(), volunteer2.getUserId());
    }

    @Test
    public void testWithdrawVolunteerFromAnEvent() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        Event event = insertEventAndReturnIt(teacher.getUserId());
        assertEquals(null, event.getVolunteerId());
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer = insertVolunteerAnika(bankId);

        dynamoDBDatabase.volunteerForEvent(event.getEventId(), volunteer.getUserId());
        List<Event> events1 = dynamoDBDatabase.getAllEvents();
        assertEquals(1, events1.size());
        assertEquals(volunteer.getUserId(), events1.get(0).getVolunteerId());

        dynamoDBDatabase.volunteerForEvent(event.getEventId(), null);
        List<Event> events2 = dynamoDBDatabase.getAllEvents();
        assertEquals(1, events2.size());
        assertEquals(null, events2.get(0).getVolunteerId());
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
        Event event1 = events1.get(0);
        assertEquals(teacher.getUserId(), event1.getLinkedTeacher().getUserId());
        assertEquals(schoolId, event1.getLinkedTeacher().getLinkedSchool().getSchoolId());
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
        for (Bank bank : banks) {
            final List<Volunteer> volunteersAtBank = dynamoDBDatabase.getVolunteersByBank(bank.getBankId());
            assertEquals(1, volunteersAtBank.size());
            assertEquals(bank.getBankId(), volunteersAtBank.get(0).getBankId());
            assertEquals(UserType.BANK_ADMIN, volunteersAtBank.get(0).getUserType());
            assertEquals(ApprovalStatus.CHECKED, volunteersAtBank.get(0).getApprovalStatus());
        }

        String bank0Id = banks.get(0).getBankId();
        String bank1Id = banks.get(1).getBankId();
        String bank2Id = banks.get(2).getBankId();
        Volunteer volunteer0 = insertVolunteer(bank1Id, "stefan@gmail.com");
        Volunteer volunteer1 = insertVolunteer(bank2Id, "anika@bankofamerica.com");
        Volunteer volunteer2 = insertVolunteer(bank2Id, "chip@bankofamerica.com");

        List<Volunteer> volunteers1 = dynamoDBDatabase.getVolunteersByBank(bank0Id);
        assertEquals(1, volunteers1.size());
        List<Volunteer> volunteers2 = dynamoDBDatabase.getVolunteersByBank(bank1Id);
        assertEquals(2, volunteers2.size());
        List<Volunteer> volunteers3 = dynamoDBDatabase.getVolunteersByBank(bank2Id);
        assertEquals(3, volunteers3.size());
    }


    @Test
    public void testGetVolunteersWithBankDataWhenThereAreNone() throws Exception {
        List<Volunteer> volunteers = dynamoDBDatabase.getVolunteersWithBankData();
        assertEquals(0, volunteers.size());
    }

    @Test
    public void testGetVolunteersWithBankData() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        List<Volunteer> volunteers0 = dynamoDBDatabase.getVolunteersWithBankData();
        assertEquals(1, volunteers0.size());
        String bankAdminId = volunteers0.get(0).getUserId();
        Volunteer volunteer = insertVolunteerAnika(bankId);

        List<Volunteer> volunteers1 = dynamoDBDatabase.getVolunteersWithBankData();
        assertEquals(2, volunteers1.size());
        Volunteer volunteerFetched = null;
        for (Volunteer v : volunteers1) {
            if (!v.getUserId().equals(bankAdminId)) {
                assertNull(volunteerFetched); // effectively asserts that it's only in the list once
                volunteerFetched = v;
            }
        }
        assertEquals(volunteer.getUserId(), volunteerFetched.getUserId());
        assertEquals(bankId, volunteerFetched.getBankId());
        assertEquals(bankId, volunteerFetched.getLinkedBank().getBankId());
    }


    @Test
    public void testGetEventsByVolunteerWithTeacherAndSchool() throws Exception {
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer1 = insertVolunteerAnika(bankId);
        Volunteer volunteer2 = insertVolunteer(bankId, "chip@bankofamerica.com");
        PrettyPrintingDate date = insertDateAndReturnIt();
        String time = insertTimeAndReturnIt();

        List<Event> events0 = dynamoDBDatabase.getEventsByVolunteerWithTeacherAndSchool(volunteer1.getUserId());
        assertEquals(0, events0.size());
        insertEvent(teacher.getUserId(), date, time);
        insertEvent(teacher.getUserId(), date, time);
        insertEvent(teacher.getUserId(), date, time);
        List<Event> events1 = dynamoDBDatabase.getEventsByVolunteerWithTeacherAndSchool(volunteer1.getUserId());
        assertEquals(0, events1.size());
        List<Event> allEvents1 = dynamoDBDatabase.getAllEvents();
        assertEquals(3, allEvents1.size());
        String event0Id = allEvents1.get(0).getEventId();
        String event1Id = allEvents1.get(1).getEventId();
        String event2Id = allEvents1.get(2).getEventId();
        dynamoDBDatabase.volunteerForEvent(event0Id, volunteer1.getUserId());
        dynamoDBDatabase.volunteerForEvent(event1Id, volunteer1.getUserId());
        dynamoDBDatabase.volunteerForEvent(event2Id, volunteer2.getUserId());
        List<Event> events2 = dynamoDBDatabase.getEventsByVolunteerWithTeacherAndSchool(volunteer1.getUserId());
        assertEquals(2, events2.size());
        List<Event> events3 = dynamoDBDatabase.getEventsByVolunteerWithTeacherAndSchool(volunteer2.getUserId());
        assertEquals(1, events3.size());
        Event eventFetched = events3.get(0);
        assertEquals(event2Id, eventFetched.getEventId());
        assertEquals(teacher.getUserId(), eventFetched.getTeacherId());
        assertEquals(teacher.getUserId(), eventFetched.getLinkedTeacher().getUserId());
        assertEquals(teacher.getFirstName(), eventFetched.getLinkedTeacher().getFirstName());
        assertEquals(schoolId, eventFetched.getLinkedTeacher().getSchoolId());
        assertEquals(schoolId, eventFetched.getLinkedTeacher().getLinkedSchool().getSchoolId());
    }

    @Test
    public void testGetTeachersWithSchoolData() throws Exception {
        List<Teacher> teachers0 = dynamoDBDatabase.getTeachersWithSchoolData();
        assertEquals(0, teachers0.size());

        String schoolId1 = insertNewSchoolAndReturnTheId();
        insertTeacherJane(schoolId1);

        List<Teacher> teachers1 = dynamoDBDatabase.getTeachersWithSchoolData();
        assertEquals(1, teachers1.size());
        assertEquals(schoolId1, teachers1.get(0).getSchoolId());
        assertEquals(schoolId1, teachers1.get(0).getLinkedSchool().getSchoolId());

        insertTeacher(schoolId1, "sea328@matelem.edu.de.us");
        List<Teacher> teachers2 = dynamoDBDatabase.getTeachersWithSchoolData();
        assertEquals(2, teachers2.size());
        assertEquals(schoolId1, teachers2.get(0).getSchoolId());
        assertEquals(schoolId1, teachers2.get(0).getLinkedSchool().getSchoolId());
        assertEquals(schoolId1, teachers2.get(1).getSchoolId());
        assertEquals(schoolId1, teachers2.get(1).getLinkedSchool().getSchoolId());
    }

    /** Convenient way to do a bunch of asserts on one line. */
    private void assertSiteStatisticsValues(int numEvents,
                                            int numMatchedEvents,
                                            int numUnmatchedEvents,
                                            int num3rdGradeEvents,
                                            int num4thGradeEvents,
                                            int numInPersonEvents,
                                            int numVirtualEvents,
                                            int numVolunteers,
                                            int numParticipatingTeachers,
                                            int numParticipatingSchools) throws SQLException {
        SiteStatistics siteStatistics = dynamoDBDatabase.getSiteStatistics();
        assertEquals(numEvents, siteStatistics.getNumEvents());
        assertEquals(numMatchedEvents, siteStatistics.getNumMatchedEvents());
        assertEquals(numUnmatchedEvents, siteStatistics.getNumUnmatchedEvents());
        assertEquals(num3rdGradeEvents, siteStatistics.getNum3rdGradeEvents());
        assertEquals(num4thGradeEvents, siteStatistics.getNum4thGradeEvents());
        assertEquals(numInPersonEvents, siteStatistics.getNumInPersonEvents());
        assertEquals(numVirtualEvents, siteStatistics.getNumVirtualEvents());
        assertEquals(numVolunteers, siteStatistics.getNumVolunteers());
        assertEquals(numParticipatingTeachers, siteStatistics.getNumParticipatingTeachers());
        assertEquals(numParticipatingSchools, siteStatistics.getNumParticipatingSchools());
    }


    @Test
    public void testSiteStatistics() throws Exception {
        assertSiteStatisticsValues(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer = insertVolunteerAnika(bankId);
        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        assertSiteStatisticsValues(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        Event event = insertEventAndReturnIt(teacher.getUserId());
        assertSiteStatisticsValues(1, 0, 1, 1, 0, 1, 0, 0, 0, 0);
        dynamoDBDatabase.volunteerForEvent(event.getEventId(), volunteer.getUserId());
        assertSiteStatisticsValues(1, 1, 0, 1, 0, 1, 0, 1, 1, 1);
    }


    @Test
    public void testGetUnMatchedVolunteers() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer = insertVolunteerAnika(bankId);

        List<Volunteer> unMatchedVolunteers = dynamoDBDatabase.getUnMatchedVolunteers();
        assertEquals(2,unMatchedVolunteers.size());
        //Verify contents
        Set<String> actualEmails = new HashSet<String>();
        Set<String> expectedEmails = new HashSet<String>();

        expectedEmails.add("weibosum@example.org");
        expectedEmails.add (volunteer.getEmail());

        actualEmails.add(unMatchedVolunteers.get(0).getEmail());
        actualEmails.add(unMatchedVolunteers.get(1).getEmail());

        assertEquals(expectedEmails,actualEmails);
    }


    @Test
    public void testGetMatchedVolunteers() throws Exception {
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer = insertVolunteerAnika(bankId);

        List<Volunteer> matchedVolunteers = dynamoDBDatabase.getMatchedVolunteers();
        assertEquals(0,matchedVolunteers.size());
        //Verify contents

        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        Event event = insertEventAndReturnIt(teacher.getUserId());
        dynamoDBDatabase.volunteerForEvent(event.getEventId(), volunteer.getUserId());

        matchedVolunteers = dynamoDBDatabase.getMatchedVolunteers();
        assertEquals(1,matchedVolunteers.size());

        Set<String> actualEmails = new HashSet<String>();
        Set<String> expectedEmails = new HashSet<String>();

        expectedEmails.add (volunteer.getEmail());

        actualEmails.add(matchedVolunteers.get(0).getEmail());

        assertEquals(expectedEmails,actualEmails);
    }


    /* Tests to see if getUnMatchedTeachers returns the list of teachers that have at least one event that has not
    been volunteered for*/
    @Test
    public void testGetUnMatchedTeachers() throws Exception {

        List<Teacher> unMatchedTeachers = dynamoDBDatabase.getUnMatchedTeachers();
        assertEquals(0,unMatchedTeachers.size());
        //Verify contents

        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        Event event = insertEventAndReturnIt(teacher.getUserId());

        unMatchedTeachers = dynamoDBDatabase.getUnMatchedTeachers();
        assertEquals(1,unMatchedTeachers.size());
        assertEquals(teacher.getEmail(), unMatchedTeachers.get(0).getEmail());
        //Volunteer a volunteer for the event and make sure the teacher does not get returned
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer = insertVolunteerAnika(bankId);
        dynamoDBDatabase.volunteerForEvent(event.getEventId(), volunteer.getUserId());
        unMatchedTeachers = dynamoDBDatabase.getUnMatchedTeachers();
        assertEquals(0,unMatchedTeachers.size());
    }


    @Test
    public void testGetMatchedTeachers() throws Exception{
        List<Teacher> matchedTeachers = dynamoDBDatabase.getMatchedTeachers();
        assertEquals(0,matchedTeachers.size());
        //Verify contents

        String schoolId = insertNewSchoolAndReturnTheId();
        Teacher teacher = insertTeacherJane(schoolId);
        Event event = insertEventAndReturnIt(teacher.getUserId());

        matchedTeachers = dynamoDBDatabase.getMatchedTeachers();
        assertEquals(0,matchedTeachers.size());
        //Volunteer a volunteer for the event and make sure the teacher does not get returned
        String bankId = insertNewBankAndReturnTheId();
        Volunteer volunteer = insertVolunteerAnika(bankId);
        dynamoDBDatabase.volunteerForEvent(event.getEventId(), volunteer.getUserId());
        matchedTeachers = dynamoDBDatabase.getMatchedTeachers();
        assertEquals(1,matchedTeachers.size());
        assertEquals(teacher.getEmail(), matchedTeachers.get(0).getEmail());
    }


}
