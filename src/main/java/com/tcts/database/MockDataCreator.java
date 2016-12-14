package com.tcts.database;

import com.tcts.common.Configuration;
import com.tcts.common.PrettyPrintingDate;
import com.tcts.database.dynamodb.DynamoDBHelper;
import com.tcts.datamodel.Bank;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.School;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.Volunteer;
import com.tcts.formdata.CreateEventFormData;
import com.tcts.formdata.TeacherRegistrationFormData;
import com.tcts.formdata.VolunteerRegistrationFormData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * This will populate the database with a bunch of mock teachers, volunteers, and events.
 * It can be run from a main() method.
 * <p>
 * NOTE: Real numbers from 2016:
 *    291 Volunteers
 *     21 Bank Admins
 *    256 Teachers
 *    239 Events
 */
public class MockDataCreator {

    // This salt and hash give "pass" as the password
    private static String SALT = "AjVW337bQJs=";
    private static String HASHED_PASSWORD = "jtZ3UlKhhAuyKpo98aGUfTiPy74=";


    /** Inner class that returns random data. */
    public class RandomDataSource {
        private final Random random;
        private final List<String> firstNames;
        private final List<String> lastNames;
        private final List<String> emailDomains;
        private final List<String> bankIds;
        private final List<String> schoolIds;
        private final List<String> eventTimes;
        private final List<String> eventGrades;
        private final List<PrettyPrintingDate> eventDates;

        /** Constructor. */
        public RandomDataSource(Random random) throws IOException, SQLException {
            this.random = random;
            this.firstNames = readListOfStringsFromFile("testdata/firstnames.txt");
            this.lastNames = readListOfStringsFromFile("testdata/lastnames.txt");
            this.emailDomains = readListOfStringsFromFile("testdata/emaildomains.txt");
            this.bankIds = getBankIds();
            this.schoolIds = getSchoolIds();
            this.eventDates = getEventDates();
            this.eventTimes = getEventTimes();
            this.eventGrades = Arrays.asList("3", "4");
        }

        /** Utility to read a list of strings from a newline-separated file. */
        private List<String> readListOfStringsFromFile(String filename) throws IOException {
            List<String> result = new ArrayList<String>();
            InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            while (line != null) {
                if (line.trim().length() > 0) { // only non-blank lines
                    result.add(line);
                }
                line = br.readLine();
            }
            return result;
        }

        /** Gets a list of all the bank IDs. */
        private List<String> getBankIds() throws SQLException {
            List<String> result = new ArrayList<String>();
            for (Bank bank : database.getAllBanks()) {
                result.add(bank.getBankId());
            }
            return result;
        }


        /** Gets a list of all the school IDs. */
        private List<String> getSchoolIds() throws SQLException {
            List<String> result = new ArrayList<String>();
            for (School school : database.getAllSchools()) {
                result.add(school.getSchoolId());
            }
            return result;
        }

        /** Gets a list of the parsable string version of the allowed dates. */
        private List<PrettyPrintingDate> getEventDates() throws SQLException {
            List<PrettyPrintingDate> result = new ArrayList<PrettyPrintingDate>();
            for (PrettyPrintingDate prettyPrintingDate : database.getAllowedDates()) {
                result.add(prettyPrintingDate);
            }
            return result;
        }

        /** Gets a list of the allowed times. */
        private List<String> getEventTimes() throws SQLException {
            List<String> result = new ArrayList<String>();
            for (String time : database.getAllowedTimes()) {
                result.add(time);
            }
            return result;
        }

        public String getRandomEmail(String firstName, String lastName) {
            return firstName.toLowerCase() + "." + lastName.toLowerCase() + "@" + randomFrom(emailDomains);
        }

        public String getRandomFirstName() {
            return randomFrom(firstNames);
        }

        public String getRandomLastName() {
            return randomFrom(lastNames);
        }

        public String getRandomPhoneNumber() {
            return Integer.toString(random.nextInt(1000)) + "-"
                    + Integer.toString(random.nextInt(1000)) + "-"
                    + Integer.toString(random.nextInt(10000));
        }

        public String getRandomEventNotes() {
            return String.format("%s %s is a student in this class.", getRandomFirstName(), getRandomLastName());
        }

        public String getRandomNumberOfStudents() {
            return Integer.toString(5 + random.nextInt(22));
        }

        public String getRandomBankId() {
            return randomFrom(bankIds);
        }

        public String getRandomSchoolId() {
            return randomFrom(schoolIds);
        }

        public String getRandomEventTime() {
            return randomFrom(eventTimes);
        }

        public String getRandomEventGrades() {
            return randomFrom(eventGrades);
        }

        public PrettyPrintingDate getRandomEventDate() {
            return eventDates.get(random.nextInt(eventDates.size()));
        }
    }


    private final DatabaseFacade database;
    private final Random random;
    private final RandomDataSource randomDataSource;

    /**
     * Constructor.
     */
    public MockDataCreator(DatabaseFacade database) throws IOException, SQLException {
        this.database = database;
        this.random = new Random();
        this.randomDataSource = new RandomDataSource(random);
    }


    /** Select one item from a list randomly. */
    private String randomFrom(List<String> strings) {
        return strings.get(random.nextInt(strings.size()));
    }

    Teacher insertTeacher() throws Exception {
        TeacherRegistrationFormData teacherRegistrationFormData = new TeacherRegistrationFormData();
        String firstName = randomDataSource.getRandomFirstName();
        String lastName = randomDataSource.getRandomLastName();
        teacherRegistrationFormData.setFirstName(firstName);
        teacherRegistrationFormData.setLastName(lastName);
        teacherRegistrationFormData.setEmail(randomDataSource.getRandomEmail(firstName, lastName));
        teacherRegistrationFormData.setPhoneNumber(randomDataSource.getRandomPhoneNumber());
        teacherRegistrationFormData.setSchoolId(randomDataSource.getRandomSchoolId());
        return database.insertNewTeacher(teacherRegistrationFormData, HASHED_PASSWORD, SALT);
    }

    Volunteer insertVolunteer() throws Exception {
        VolunteerRegistrationFormData volunteerRegistrationFormData = new VolunteerRegistrationFormData();
        String firstName = randomDataSource.getRandomFirstName();
        String lastName = randomDataSource.getRandomLastName();
        volunteerRegistrationFormData.setFirstName(firstName);
        volunteerRegistrationFormData.setLastName(lastName);
        volunteerRegistrationFormData.setEmail(randomDataSource.getRandomEmail(firstName, lastName));
        volunteerRegistrationFormData.setPhoneNumber(randomDataSource.getRandomPhoneNumber());
        volunteerRegistrationFormData.setBankId(randomDataSource.getRandomBankId());
        volunteerRegistrationFormData.setBankSpecificData("Mail Stop");
        return database.insertNewVolunteer(volunteerRegistrationFormData, HASHED_PASSWORD, SALT);
    }

    void insertEvent(String teacherId) throws Exception {
        CreateEventFormData createEventFormData = new CreateEventFormData();
        createEventFormData.setEventDate(randomDataSource.getRandomEventDate());
        createEventFormData.setEventTime(randomDataSource.getRandomEventTime());
        createEventFormData.setGrade(randomDataSource.getRandomEventGrades());
        createEventFormData.setNumberStudents(randomDataSource.getRandomNumberOfStudents());
        createEventFormData.setNotes(randomDataSource.getRandomEventNotes());
        database.insertEvent(teacherId, createEventFormData);
    }

    /**
     * Add teachers and events.
     */
    void populateWithTeachersAndEvents(int numTeachers) throws Exception {
        for (int teacherNum=0; teacherNum<numTeachers; teacherNum++) {
            Teacher teacher = insertTeacher();
            // NOTE: Choosing to go with 90% chance 1 event per teacher, 10% chance of 2.
            int numEventsForThisTeacher = random.nextInt(10) == 0 ? 2 : 1;
            for (int eventNum=0; eventNum<numEventsForThisTeacher; eventNum++) {
                insertEvent(teacher.getUserId());
            }
        }
    }

    /**
     * Add volunteers to the database.
     */
    void populateWithVolunteers(int numVolunteers) throws Exception {
        List<Event> allAvailableEvents = database.getAllAvailableEvents();
        // We will not allow more than half the available events to be volunteered for
        // The reason is that we need to test performance when there are lots of available events
        int maxEventWeWillAllowVolunteersFor = allAvailableEvents.size() / 2;
        int nextEventToGiveOut = 0;
        for (int volunteerNum=0; volunteerNum<numVolunteers; volunteerNum++) {
            Volunteer volunteer = insertVolunteer();
            // With 90% they want 1 class, with 10% they want 2 classes
            int numEventsDesiredByThisVolunteer = random.nextInt(10) == 0 ? 2 : 1;
            for (int eventNum = 0; eventNum < numEventsDesiredByThisVolunteer; eventNum++) {
                if (nextEventToGiveOut < maxEventWeWillAllowVolunteersFor) {
                    Event event = allAvailableEvents.get(nextEventToGiveOut);
                    nextEventToGiveOut += 1;
                    database.volunteerForEvent(event.getEventId(), volunteer.getUserId());
                }
            }
        }
    }

    /**
     * Call this method to actually generate and insert the mock data.
     */
    public void populateWithData(int numTeachers, int numVolunteers) throws Exception {
        populateWithTeachersAndEvents(numTeachers);
        populateWithVolunteers(numVolunteers);
    }

    public static void main(String[] args) {
        System.out.println("Starting");
        try {
            DatabaseFacade database = new DynamoDBDatabase(new Configuration(), new DynamoDBHelper());
            MockDataCreator mockDataCreator = new MockDataCreator(database);
            mockDataCreator.populateWithData(600, 500);
        } catch(Exception err) {
            err.printStackTrace();
        }
        System.out.println("Done.");
    }
}
