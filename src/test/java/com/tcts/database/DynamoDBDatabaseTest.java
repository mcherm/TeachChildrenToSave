package com.tcts.database;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.tcts.common.PrettyPrintingDate;
import com.tcts.datamodel.School;
import com.tcts.exception.AllowedDateAlreadyInUseException;
import com.tcts.exception.AllowedTimeAlreadyInUseException;
import com.tcts.exception.NoSuchAllowedDateException;
import com.tcts.exception.NoSuchAllowedTimeException;
import com.tcts.exception.NoSuchSchoolException;
import com.tcts.formdata.AddAllowedDateFormData;
import com.tcts.formdata.AddAllowedTimeFormData;
import com.tcts.formdata.CreateSchoolFormData;
import com.tcts.formdata.EditSchoolFormData;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
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
    private DynamoDBDatabase dynamoDBDatabase;

    @Before
    public void initialize() throws InterruptedException {
        DynamoDB dynamoDB = DynamoDBDatabase.connectToDB();
        DynamoDBSetup.reinitializeDatabase(dynamoDB);
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

    @Test(expected = NoSuchSchoolException.class)
    public void deleteSchoolThatDoesNotExist() throws SQLException, NoSuchSchoolException {
        dynamoDBDatabase.deleteSchool("99324");
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
}
