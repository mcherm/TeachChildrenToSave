package com.tcts.database;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.tcts.datamodel.ApprovalStatus;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.tcts.common.PrettyPrintingDate;
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


/**
 * The (one and only) implementation of DatabaseFacade.
 */
public class MySQLDatabase implements DatabaseFacade {
    private final static String userFields =
            "user_id, password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status, reset_password_token, bank_specific_data";
    private final static String eventFields =
            "event_id, teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id";
    private final static String bankFields =
            "bank_id, bank_name, min_lmi_for_cra, bank_specific_data_label";
    private final static String schoolFields =
            "school_id, school_name, school_addr1, school_city, school_zip, school_county, school_district, school_state, school_phone, school_lmi_eligible, school_SLC";

    private final static String getUserByIdSQL =
            "select " + userFields + " from User where user_id = ?";
    
    private final static String getUserByTypeSQL =
            "select " + userFields + " from User where access_type = ? order by last_name asc, first_name asc";
    
    private final static String getUserByEmailSQL =
            "select " + userFields + " from User where email = ?";
    private final static String getVolunteersByBankSQL =
            "select " + userFields + " from User where (access_type = 'V' or access_type = 'BA') and organization_id = ?" +
            " order by last_name asc, first_name asc";
    private final static String getBankAdminByBankSQL =
            "select " + userFields + " from User where access_type = 'BA' and organization_id = ?";

    private final static String getEventByIdSQL =
            "select " + eventFields + " from Event where event_id = ? order by event_id asc";
    private final static String getAllEventsWithTeacherAndSchoolAndVolunteerAndBankSQL =
            "select " + eventFields + ", " + prefixFieldsWith("teacher.", userFields) + ", " + schoolFields +
                    ", " + prefixFieldsWith("volunteer.",userFields) + ", " + bankFields +
                    " from Event" +
                    " join User teacher on teacher_id = teacher.user_id" +
                    " join School on teacher.organization_id = school_id" +
                    " left join User volunteer on volunteer_id = volunteer.user_id" +
                    " left join Bank on volunteer.organization_id = bank_id" +
                    " order by school_name asc, teacher.last_name asc, event_date asc";
    private final static String getEventsByTeacherSQL =
            "select " + eventFields + " from Event where teacher_id = ? order by event_date asc, event_id asc";
    private final static String getEventsByVolunteerSQL =
            "select " + eventFields + " from Event where volunteer_id = ? order by event_date asc, event_id asc";
    private final static String getEventsByVolunteerWithTeacherAndSchoolSQL =
            "select " + eventFields + ", " + userFields + ", " + schoolFields +
                    " from Event join User on teacher_id = user_id join School on organization_id = school_id" +
                    " where volunteer_id = ?" +
                    " order by event_date asc, event_id asc";
    private final static String getAllAvailableEventsWithTeacherAndSchoolSQL =
    		"select " + eventFields + ", " + userFields + ", " + schoolFields +
            " from Event join User on teacher_id = user_id join School on organization_id = school_id" +
            " where volunteer_id is null" +
            " order by event_id asc"; // Put the oldest unfulfilled requests at the top unless you sort them

    private final static String getBankByIdSQL =
            "select " + bankFields + " from Bank where bank_id = ?";
    private final static String getAllBanksSQL =
            "select " + bankFields + " from Bank order by bank_name";
    private final static String getSchoolByIdSQL =
            "select " + schoolFields + " from School where school_id = ?";
    private final static String getAllSchoolsSQL =
            "select " + schoolFields + " from School order by school_name";
    private final static String getAllowedDatesSQL =
            "select event_date from AllowedDates order by event_date";
    private final static String getAllowedTimesSQL =
            "select event_time from AllowedTimes order by sort_order";
    private final static String getLastInsertIdSQL =
            "select last_insert_id() as last_id";
    private final static String modifyUserPersonalFieldsSQL =
            "update User set email=?, first_name=?, last_name=?, phone_number=? where user_id=?";
    private final static String modifyVolunteerPersonalFieldsSQL =
            "update User set email=?, first_name=?, last_name=?, phone_number=?, bank_specific_data=? where user_id=?";
    private final static String modifyTeacherSchoolSQL =
            "update User set organization_id=? where user_id=?";
    private final static String insertUserSQL =
            "insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final static String insertEventSQL =
            "insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (?, ?, ?, ?, ?, ?, ?)";
    private final static String volunteerForEventSQL =
            "update Event set volunteer_id = ? where event_id = ?";
    
    private final static String insertSchoolSQL  =
    		"insert into School (school_name,school_addr1,school_city,school_zip,school_county,school_district,school_state,school_phone,school_lmi_eligible,school_SLC) VALUES (?,?,?,?,?,?,?,?,?,?)";
    private final static String insertBankSQL =
    		"insert into Bank (bank_name) VALUES (?)";
    private final static String insertAllowedDateSQL =
            "insert into AllowedDates (event_date) VALUES (?)";

    private final static String modifyBankByIdSQL =
            "update Bank set bank_name = ?, min_lmi_for_cra = ? where bank_id = ?";

    private final static String deleteUsersByBankId =
            "delete from User where (access_type = 'BA' or access_type = 'V') and organization_id = ?";
    private final static String deleteBankSQL =
    		"delete from Bank where bank_id = ?";
    
    private final static String deleteSchooldSQL =
    		"delete from School where school_id = ?";
      
    private final static String deleteUserSQL =
    		"delete from User where user_id = ? ";
        
    private final static String deleteEventSQL =
    		"delete from Event where event_id=? ";

    private final static String updateEventByIdSQL =
    		"UPDATE Event SET event_date = ?,event_time = ?,grade = ?,number_students = ?,notes = ? WHERE event_id = ?";
    
    private final static String updateSchoolByIdSQL =
    		"UPDATE School SET " +
    		"school_name = ?,school_addr1 = ?,school_city = ?,school_zip = ?,school_county = ?," +
    		"school_district = ?,school_state = ?,school_phone = ?,school_lmi_eligible = ?,school_SLC = ? WHERE school_id = ?";

    private final static String updateUserCredentialsByIdSQL =
    		"update User set password_salt = ?, password_hash = ? where user_id = ?";
    		
    private final static String updateResetPasswordTokenByIdSQL =
    		"update User set reset_password_token = ? where user_id = ?";

    private final static String updateUserStatusByIdSQL =
            "update User set user_status = ? where user_id = ?";
    
    private final static String insertSpaceForAllowedTimeSQL =
            "update AllowedTimes set sort_order = sort_order + 1 where sort_order >= ? order by sort_order desc;";
    private final static String getLargestSortOrderForAllowedTimeSQL =
            "select max(sort_order) from AllowedTimes";
    private final static String getSortOrderForAllowedTimeSQL =
            "select sort_order from AllowedTimes where event_time = ?";
    private final static String insertAllowedTimeSQL =
            "insert into AllowedTimes (event_time, sort_order) values (?, ?)";

    private final static String deleteAllowedTimeSQL =
    		"delete from AllowedTimes where event_time = ? ";
    
    private final static String deleteAllowedDateSQL =
    		"delete from AllowedDates where event_date = ? ";
    
    private final static String getVolunteersWithBankSQL =
            "select " + userFields + ", " + bankFields + 
		    " from User join Bank on bank_id = organization_id  and (access_type = 'V' or access_type ='BA')" +
            " order by last_name asc, first_name asc";
    
    private final static String getTeacherWithSchoolSQL =
    		"select " + userFields + ", " + schoolFields + 
		    " from User join School on school_id = organization_id  and access_type = 'T'" +
            " order by last_name asc, first_name asc";

    private final static String getTeachersBySchoolSQL =
            "select " + userFields + " from User where access_type = 'T'  and organization_id = ?" +
                    " order by last_name asc, first_name asc";

    private final static String getMatchedTeachersSQL =
    		"select " + userFields + " " + 
		    " from Event join User on user_id = teacher_id  and access_type = 'T' and volunteer_id is not null" +
            " order by last_name asc, first_name asc";
    
    private final static String getUnMatchedTeachersSQL =
    		"select " + userFields + " " + 
    		" from Event join User on user_id = teacher_id  and access_type = 'T' and volunteer_id is null" +
            " order by last_name asc, first_name asc";
    
    private final static String getMatchedVolunteersSQL =
    		"select " + userFields + " " + 
    		" from Event join User on user_id = volunteer_id " +
            " order by last_name asc, first_name asc";
    
    private final static String getUnMatchedVolunteersSQL =
    		"select " + userFields + " " + 
		    " from User where (access_type = 'V' or access_type = 'BA') and not exists ( select * from Event where volunteer_id = user_id)" +
            " order by last_name asc, first_name asc";

    private final static String setBankSpecificFieldLabelSQL =
            "update Bank set bank_specific_data_label = ? where bank_id = ?";

    private final static String setBankSpecificFieldSQL =
            "update User set bank_specific_data = ? where user_id = ?";

    private final static String getNumEventsSQL = "select count(*) from Event";
    private final static String getNumMatchedEventsSQL = "select count(*) from Event where volunteer_id is not null";
    private final static String getNumUnmatchedEventsSQL = "select count(*) from Event where volunteer_id is null";
    private final static String getNum3rdGradeEventsSQL = "select count(*) from Event where grade = 3";
    private final static String getNum4thGradeEventsSQL = "select count(*) from Event where grade = 4";
    private final static String getNumVolunteersSQL = "select count(*) from User where access_type = 'V'";
    private final static String getNumParticipatingTeachersSQL = "select count(distinct teacher_id) from Event";
    private final static String getNumParticipatingSchoolsSQL = "select count(distinct organization_id) from Event join User on teacher_id = user_id";
    private final static String getSiteSettingsSQL = "select * from SiteSettings";
    private final static String insertOrModifySiteSettingSQL = "insert into SiteSettings (setting_name, setting_value) VALUES(?, ?) on duplicate key update setting_value = ?";


    /**
     * This is a subroutine used to build the SQL queries in a few complex cases.
     * It takes a prefix and a ", " separated list of strings. It returns the same
     * list of strings, but with the prefix before each one. Thus,
     * <code>prefixFieldsWith("t.", "owner, user, size")</code> results in
     * <code>"t.owner, t.user, t.size"</code>
     */
    private static String prefixFieldsWith(String prefix, String fieldString) {
        String[] fields = fieldString.split("\\,\\s?");
        StringBuilder result = new StringBuilder();
        boolean firstTimeThroughTheLoop = true;
        for (String field : fields) {
            if (firstTimeThroughTheLoop) {
                firstTimeThroughTheLoop = false;
            } else {
                result.append(", ");
            }
            result.append(prefix);
            result.append(field);
        }
        return result.toString();
    }
    
    @Autowired
    private ConnectionFactory connectionFactory;

    /* Constants used for the field lengths. */
    private final Map<DatabaseField,Integer> FIELD_LENGTHS = new HashMap() {{
        put(DatabaseField.site_setting_name, 30);
        put(DatabaseField.site_setting_value, 100);
        put(DatabaseField.event_time, 30);
        put(DatabaseField.event_grade, 8);
        put(DatabaseField.event_notes, 1000);
        put(DatabaseField.bank_name, 45);
        put(DatabaseField.user_email, 50);
        put(DatabaseField.user_first_name, 50);
        put(DatabaseField.user_last_name, 50);
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

    @Override
    public int getFieldLength(DatabaseField field) {
        return FIELD_LENGTHS.get(field);
    }

    @Override
    public User getUserById(String userId) throws SQLException, InconsistentDatabaseException {
        return getUserByIdOrEmail(userId, getUserByIdSQL);
    }


    @Override
    public User getUserByEmail(String email) throws SQLException, InconsistentDatabaseException {
        return getUserByIdOrEmail(email, getUserByEmailSQL);
    }

    private User getUserByIdOrEmail(String key, String sql) throws SQLException, InconsistentDatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, key);
            resultSet = preparedStatement.executeQuery();
            int numberOfRows = 0;
            User user = null;
            while (resultSet.next()) {
                numberOfRows++;
                UserType userType = UserType.fromDBValue(resultSet.getString("access_type"));
                switch(userType) {
                    case VOLUNTEER: {
                        Volunteer volunteer = new Volunteer();
                        volunteer.populateFieldsFromResultSetRow(resultSet);
                        user = volunteer;
                    } break;
                    case TEACHER: {
                        Teacher teacher = new Teacher();
                        teacher.populateFieldsFromResultSetRow(resultSet);
                        user = teacher;
                    } break;
                    case BANK_ADMIN: {
                        BankAdmin bankAdmin = new BankAdmin();
                        bankAdmin.populateFieldsFromResultSetRow(resultSet);
                        user = bankAdmin;
                    } break;
                    case SITE_ADMIN: {
                        SiteAdmin siteAdmin = new SiteAdmin();
                        siteAdmin.populateFieldsFromResultSetRow(resultSet);
                        user = siteAdmin;
                    } break;
                    default: {
                        throw new RuntimeException("This should never occur.");
                    }
                }
            }
            if (numberOfRows < 1) {
                return null; // No user found
            } else if (numberOfRows > 1) {
                throw new InconsistentDatabaseException("Multiple rows for ID or email of '" + key + "'.");
            } else {
                return user;
            }
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


    @Override
    public void modifyUserPersonalFields(EditPersonalDataFormData formData)
            throws SQLException, EmailAlreadyInUseException, InconsistentDatabaseException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(modifyUserPersonalFieldsSQL);
            preparedStatement.setString(1, formData.getEmail());
            preparedStatement.setString(2, formData.getFirstName());
            preparedStatement.setString(3, formData.getLastName());
            preparedStatement.setString(4, formData.getPhoneNumber());
            preparedStatement.setString(5, formData.getUserId());
            try {
                preparedStatement.executeUpdate();
            } catch(MySQLIntegrityConstraintViolationException err) {
                if (err.getMessage().contains("Duplicate entry") && err.getMessage().contains("for key 'ix_email'")) {
                    throw new EmailAlreadyInUseException();
                } else {
                    throw err;
                }
            }
        } finally {
            closeSafely(connection, preparedStatement, null);
        }
    }

    @Override
    public void modifyVolunteerPersonalFields(EditVolunteerPersonalDataFormData formData)
            throws SQLException, EmailAlreadyInUseException, InconsistentDatabaseException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(modifyVolunteerPersonalFieldsSQL);
            preparedStatement.setString(1, formData.getEmail());
            preparedStatement.setString(2, formData.getFirstName());
            preparedStatement.setString(3, formData.getLastName());
            preparedStatement.setString(4, formData.getPhoneNumber());
            preparedStatement.setString(5, formData.getBankSpecificData());
            preparedStatement.setString(6, formData.getUserId());
            try {
                preparedStatement.executeUpdate();
            } catch(MySQLIntegrityConstraintViolationException err) {
                if (err.getMessage().contains("Duplicate entry") && err.getMessage().contains("for key 'ix_email'")) {
                    throw new EmailAlreadyInUseException();
                } else {
                    throw err;
                }
            }
        } finally {
            closeSafely(connection, preparedStatement, null);
        }
    }


    @Override
    public void modifyTeacherSchool(String userId, String schoolId)
            throws SQLException, NoSuchSchoolException, NoSuchUserException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionFactory.getConnection();
            if (getSchoolById(schoolId)== null) {
                throw new  NoSuchSchoolException();
            }
            preparedStatement = connection.prepareStatement(modifyTeacherSchoolSQL);
            preparedStatement.setString(1, schoolId);
            preparedStatement.setString(2, userId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0){
                throw new NoSuchUserException();
            }
        } finally {
            closeSafely(connection, preparedStatement, null);
        }
   }

    @Override
    public List<Event> getEventsByTeacher(String teacherId) throws SQLException {
        return getEventsByTeacherOrVolunteer(teacherId, getEventsByTeacherSQL);
    }


    @Override
    public List<Event> getEventsByVolunteer(String volunteerId) throws SQLException {
        return getEventsByTeacherOrVolunteer(volunteerId, getEventsByVolunteerSQL);
    }

    @Override
    public List<Event> getEventsByVolunteerWithTeacherAndSchool(String volunteerId) throws SQLException{
        List<Event> events;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getEventsByVolunteerWithTeacherAndSchoolSQL);
            preparedStatement.setString(1, volunteerId);
            resultSet = preparedStatement.executeQuery();
            events = getEventListWithTeacherAndSchool(resultSet);
            return events;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }

    }


    /**
     * Common code to get events by either teacher or volunteer.
     *
     * @param userId the teacherId or volunteerId
     * @param sql either getEventsByTeacherSQL or getEventsByVolunteerSQL
     * @return the list of events
     * @throws SQLException
     */
    public List<Event> getEventsByTeacherOrVolunteer(String userId, String sql) throws SQLException {
        List<Event> events = new ArrayList<Event>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Event event = new Event();
                event.populateFieldsFromResultSetRow(resultSet);
                events.add(event);
            }
            return events;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }
    
    @Override
    public List<Event> getAllAvailableEvents() throws SQLException {
        List<Event> events;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getAllAvailableEventsWithTeacherAndSchoolSQL);
            resultSet = preparedStatement.executeQuery();
            events = getEventListWithTeacherAndSchool(resultSet);
            return events;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


    public List<Event> getEventListWithTeacherAndSchool (ResultSet resultSet) throws SQLException {
        List<Event> events = new ArrayList<Event>();

        while (resultSet.next()) {
            Event event = new Event();
            event.populateFieldsFromResultSetRowWithTeacherAndSchool(resultSet);
            events.add(event);
        }
        return events;

    }

    @Override
    public void insertEvent(String teacherId, CreateEventFormData formData) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(insertEventSQL);
            preparedStatement.setString(1, teacherId);
            preparedStatement.setDate(2, new java.sql.Date(formData.getEventDate().getTime()));
            preparedStatement.setString(3, formData.getEventTime());
            preparedStatement.setString(4, formData.getGrade());
            preparedStatement.setInt(5, Integer.parseInt(formData.getNumberStudents()));
            preparedStatement.setString(6, stringToHTMLString(formData.getNotes()));
            preparedStatement.setString(7, null); // volunteerId
            preparedStatement.executeUpdate();
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }
    
    @Override
    public void volunteerForEvent(String eventId, String volunteerId) throws SQLException, NoSuchEventException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(volunteerForEventSQL);
            preparedStatement.setString(1, volunteerId);
            preparedStatement.setString(2, eventId);
            preparedStatement.executeUpdate();
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


    @Override
    public List<Volunteer> getVolunteersByBank(String bankId) throws SQLException {
        List<Volunteer> volunteers = new ArrayList<Volunteer>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getVolunteersByBankSQL);
            preparedStatement.setString(1, bankId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                UserType userType = UserType.fromDBValue(resultSet.getString("access_type"));
                if ((userType != UserType.VOLUNTEER) && (userType !=UserType.BANK_ADMIN)){
                    throw new RuntimeException("This should not occur.");
                }
                Volunteer volunteer = new Volunteer();
                volunteer.populateFieldsFromResultSetRow(resultSet);
                volunteers.add(volunteer);
            }
            return volunteers;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


    @Override
    public BankAdmin getBankAdminByBank(String bankId) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getBankAdminByBankSQL);
            preparedStatement.setString(1, bankId);
            resultSet = preparedStatement.executeQuery();
            BankAdmin bankAdmin = null;
            int resultCount = 0;
            while (resultSet.next()) {
                resultCount += 1;
                if (resultCount > 1) {
                    throw new InconsistentDatabaseException("Multiple bank admins for bank " + bankId);
                }
                UserType userType = UserType.fromDBValue(resultSet.getString("access_type"));
                if (userType != UserType.BANK_ADMIN) {
                    throw new RuntimeException("This should not occur.");
                }
                bankAdmin = new BankAdmin();
                bankAdmin.populateFieldsFromResultSetRow(resultSet);
            }
            return bankAdmin;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


    @Override
    public Bank getBankById(String bankId) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getBankByIdSQL);
            preparedStatement.setString(1, bankId);
            resultSet = preparedStatement.executeQuery();
            int numberOfRows = 0;
            Bank bank = null;
            while (resultSet.next()) {
                numberOfRows++;
                bank = new Bank();
                bank.populateFieldsFromResultSetRow(resultSet);
            }
            if (numberOfRows < 1) {
                return null; // No bank found
            } else if (numberOfRows > 1) {
                throw new InconsistentDatabaseException("Multiple rows for bank '" + bankId + "'.");
            } else {
                return bank;
            }
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


    @Override
    public School getSchoolById(String schoolId) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getSchoolByIdSQL);
            preparedStatement.setString(1, schoolId);
            resultSet = preparedStatement.executeQuery();
            int numberOfRows = 0;
            School school = null;
            while (resultSet.next()) {
                numberOfRows++;
                school = new School();
                school.populateFieldsFromResultSetRow(resultSet);
            }
            if (numberOfRows < 1) {
                return null; // No school found
            } else if (numberOfRows > 1) {
                throw new InconsistentDatabaseException("Multiple rows for school '" + schoolId + "'.");
            } else {
                return school;
            }
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


    @Override
    public List<School> getAllSchools() throws SQLException {
        List<School> schools = new ArrayList<School>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getAllSchoolsSQL);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                School school = new School();
                school.populateFieldsFromResultSetRow(resultSet);
                schools.add(school);
            }
            return schools;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }
    
    @Override
    public List<Bank> getAllBanks() throws SQLException {
        List<Bank> banks = new ArrayList<Bank>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getAllBanksSQL);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Bank bank = new Bank();
                bank.populateFieldsFromResultSetRow(resultSet);
                banks.add(bank);
            }
            return banks;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }



    @Override
    public List<PrettyPrintingDate> getAllowedDates() throws SQLException {
        List<PrettyPrintingDate> dates = new ArrayList<PrettyPrintingDate>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getAllowedDatesSQL);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Date date = resultSet.getDate("event_date");
                dates.add(new PrettyPrintingDate(date));
            }
            return dates;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public List<String> getAllowedTimes() throws SQLException {
        List<String> times = new ArrayList<String>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getAllowedTimesSQL);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String time = resultSet.getString("event_time");
                times.add(time);
            }
            return times;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }
    		
	private User insertNewUser(String hashedPassword, String salt, String email,
			String firstName, String lastName, UserType userType,
			String organizationId, String phoneNumber)
        throws SQLException, EmailAlreadyInUseException, InconsistentDatabaseException
    {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			connection = connectionFactory.getConnection();
			preparedStatement = connection.prepareStatement(insertUserSQL);

			preparedStatement.setString(1, salt);
            preparedStatement.setString(2, hashedPassword);
			preparedStatement.setString(3, email);
			preparedStatement.setString(4, firstName);
			preparedStatement.setString(5, lastName);
			preparedStatement.setString(6, userType.getDBValue());
			preparedStatement.setString(7, organizationId);
			preparedStatement.setString(8, phoneNumber);
			preparedStatement.setInt(9, ApprovalStatus.INITIAL_APPROVAL_STATUS.getDbValue());
			try {
				preparedStatement.executeUpdate();
			} catch (MySQLIntegrityConstraintViolationException err) {
				// FIXME: Check if it matches
				// "Duplicate entry '.*' for key 'ix_email'"
				throw new EmailAlreadyInUseException();
			}
			preparedStatement.close();
			preparedStatement = connection.prepareStatement(getLastInsertIdSQL);
			resultSet = preparedStatement.executeQuery();
			String userId = null;
			int numberOfRows = 0;
			while (resultSet.next()) {
				numberOfRows++;
				userId = resultSet.getString(1);
			}
			if (numberOfRows < 1) {
				throw new RuntimeException("This should never happen.");
			} else if (numberOfRows > 1) {
				throw new RuntimeException("This should never happen.");
			} else {
				return getUserById(userId);
			}
		} finally {
			closeSafely(connection, preparedStatement, resultSet);
		}
	}
    
    @Override
    public Teacher insertNewTeacher(TeacherRegistrationFormData formData, String hashedPassword, String salt) throws SQLException, NoSuchSchoolException, EmailAlreadyInUseException, NoSuchAlgorithmException, UnsupportedEncodingException {
        return (Teacher) insertNewUser(hashedPassword, salt, formData.getEmail(),
                formData.getFirstName(), formData.getLastName(), UserType.TEACHER, formData.getSchoolId(),
                formData.getPhoneNumber());
    }

    @Override
    public Volunteer insertNewVolunteer(VolunteerRegistrationFormData formData, String hashedPassword, String salt)
            throws SQLException, NoSuchBankException, EmailAlreadyInUseException
    {
        User newUser = insertNewUser(hashedPassword, salt, formData.getEmail(),
                formData.getFirstName(), formData.getLastName(), UserType.VOLUNTEER, formData.getBankId(),
                formData.getPhoneNumber());
        String newUserId = newUser.getUserId();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(setBankSpecificFieldSQL);
            preparedStatement.setString(1, formData.getBankSpecificData());
            preparedStatement.setString(2, newUserId);
            preparedStatement.executeUpdate();
        }
        finally {
            closeSafely(connection, preparedStatement, null);
        }

        return (Volunteer) getUserById(newUserId);
    }

    
    /**
     * Safely close several things that might be open or not or might not even exist.
     */
    private void closeSafely(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            resultSet.close();
        } catch (Exception err) {
            // ignore problems closing
        }
        try {
            preparedStatement.close();
        } catch (Exception err) {
            // ignore problems closing
        }
        try {
            connection.close();
        } catch (Exception err) {
            // ignore problems closing
        }
    }

    @Override
    public List<BankAdmin> getBankAdmins() throws SQLException {
        List<BankAdmin> result = new ArrayList<BankAdmin>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getUserByTypeSQL);
            preparedStatement.setString(1, UserType.BANK_ADMIN.getDBValue());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                BankAdmin bankAdmin = new BankAdmin();
                bankAdmin.populateFieldsFromResultSetRow(resultSet);
                if (bankAdmin.getUserType() != UserType.BANK_ADMIN) {
                    throw new RuntimeException("DB returned a different type of user than was requested.");
                }
                result.add(bankAdmin);
            }
            return result;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


    @Override
	public void deleteSchool(String schoolId) throws SQLException, NoSuchSchoolException
    {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(deleteSchooldSQL);
            preparedStatement.setString(1, schoolId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new NoSuchSchoolException();
            } else if (rowsAffected > 1) {
                throw new InconsistentDatabaseException("More than one school had id of '" + schoolId + "'.");
            }
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
	}


	@Override
	public void modifySchool(EditSchoolFormData formData) throws SQLException,
			InconsistentDatabaseException {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(updateSchoolByIdSQL);
            preparedStatement.setString(1, formData.getSchoolName());
            preparedStatement.setString(2, formData.getSchoolAddress1());
            preparedStatement.setString(3, formData.getCity());
            preparedStatement.setString(4, formData.getZip());
            preparedStatement.setString(5, formData.getCounty());
            preparedStatement.setString(6, formData.getDistrict());
            preparedStatement.setString(7, formData.getState());
            preparedStatement.setString(8, formData.getPhone());
            preparedStatement.setString(9, formData.getLmiEligible());
            preparedStatement.setString(10, formData.getSLC());
            preparedStatement.setString(11, formData.getSchoolId());
            preparedStatement.executeUpdate();
        } 
        finally {
            closeSafely(connection, preparedStatement, null);
            
        }
 	}


	@Override
	public void deleteBank(String bankId) throws SQLException, NoSuchBankException {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionFactory.getConnection();

            // --- Delete all users ---
            preparedStatement = connection.prepareStatement(deleteUsersByBankId);
            preparedStatement.setString(1, bankId);
            preparedStatement.executeUpdate();
            // No need to check how many were deleted since it could be ANY number
            // and it would still be valid.
            preparedStatement.close();

            // --- Delete the bank ---
            preparedStatement = connection.prepareStatement(deleteBankSQL);
            preparedStatement.setString(1, bankId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new NoSuchBankException();
            }
        } finally {
            closeSafely(connection, preparedStatement, null);
        }
	}


	@Override
	public void deleteVolunteer(String volunteerId) throws SQLException, NoSuchUserException, VolunteerHasEventsException {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();

            // --- Check if the volunteer has any events ---
            preparedStatement = connection.prepareStatement(getEventsByVolunteerSQL);
            preparedStatement.setString(1, volunteerId);
            resultSet = preparedStatement.executeQuery();
            int numberOfEvents = 0;
            while (resultSet.next()) {
                numberOfEvents++;
            }
            if (numberOfEvents > 0) {
                throw new VolunteerHasEventsException();
            }
            resultSet.close();
            preparedStatement.close();

            // --- Actually delete the volunteer ---
            preparedStatement = connection.prepareStatement(deleteUserSQL);
            preparedStatement.setString(1, volunteerId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new NoSuchUserException();
            } else if (rowsAffected > 1) {
                throw new InconsistentDatabaseException("More than one user had id of '" + volunteerId + "'.");
            }
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
	}


    public void deleteTeacher(String teacherId) throws SQLException, NoSuchUserException, TeacherHasEventsException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();

            // --- Check if the teacher has any events ---
            preparedStatement = connection.prepareStatement(getEventsByTeacherSQL);
            preparedStatement.setString(1, teacherId);
            resultSet = preparedStatement.executeQuery();
            int numberOfEvents = 0;
            while (resultSet.next()) {
                numberOfEvents++;
            }
            if (numberOfEvents > 0) {
                throw new TeacherHasEventsException();
            }
            resultSet.close();
            preparedStatement.close();

            // --- Actually delete the teacher ---
            preparedStatement = connection.prepareStatement(deleteUserSQL);
            preparedStatement.setString(1, teacherId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new NoSuchUserException();
            } else if (rowsAffected > 1) {
                throw new InconsistentDatabaseException("More than one user had id of '" + teacherId + "'.");
            }
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


	@Override
	public void modifyEvent(EventRegistrationFormData formData) throws SQLException,
			NoSuchEventException {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
        	
        	connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(updateEventByIdSQL);
            preparedStatement.setDate(1,  new java.sql.Date(formData.getEventDate().getTime()));
            preparedStatement.setString(2, formData.getEventTime());
            preparedStatement.setString(3, formData.getGrade());
            preparedStatement.setInt(4, Integer.parseInt(formData.getNumberStudents().equalsIgnoreCase("")?"0":formData.getNumberStudents()));
            preparedStatement.setString(5, stringToHTMLString(formData.getNotes()));
            preparedStatement.setString(6, formData.getEventId());
            preparedStatement.executeUpdate();
        } 
        finally {
            closeSafely(connection, preparedStatement, null);
            
        }

	}


	@Override
	public void deleteEvent(String eventId) throws SQLException, NoSuchEventException {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(deleteEventSQL);
            preparedStatement.setString(1, eventId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new NoSuchEventException();
            } else if (rowsAffected > 1) {
                throw new InconsistentDatabaseException("Cannot happen: eventId is the primary key!");
            } else {
                return;
            }
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
	}


	@Override
	public List<Event> getAllEvents() throws SQLException,
			InconsistentDatabaseException {
		List<Event> events = new ArrayList<Event>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getAllEventsWithTeacherAndSchoolAndVolunteerAndBankSQL);
            
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Event event = new Event();
                event.populateFieldsFromResultSetRow(resultSet);
                Teacher teacher = new Teacher();
                teacher.populateFieldsFromResultSetRowWithPrefix(resultSet, "teacher.");
                event.setLinkedTeacher(teacher);
                School school = new School();
                school.populateFieldsFromResultSetRow(resultSet);
                teacher.setLinkedSchool(school);
                if (event.getVolunteerId() != null) {
                    Volunteer volunteer = new Volunteer();
                    volunteer.populateFieldsFromResultSetRowWithPrefix(resultSet, "volunteer.");
                    event.setLinkedVolunteer(volunteer);
                    Bank bank = new Bank();
                    bank.populateFieldsFromResultSetRow(resultSet);
                    volunteer.setLinkedBank(bank);
                }
                events.add(event);
            }
            return events;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
	}


	@Override
	public Event getEventById(String eventId) throws SQLException {
		Event event = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getEventByIdSQL);
            preparedStatement.setString(1, eventId);
            resultSet = preparedStatement.executeQuery();
            int numberOfRows = 0;
            while (resultSet.next()) {
                numberOfRows += 1;
                event = new Event();
                event.setEventId(resultSet.getString("event_id"));
                event.setTeacherId(resultSet.getString("teacher_id"));
                event.setEventDate(new PrettyPrintingDate(resultSet.getDate("event_date")));
                event.setEventTime(resultSet.getString("event_time"));
                event.setGrade(resultSet.getString("grade"));
                event.setNumberStudents(resultSet.getInt("number_students"));
                event.setNotes(resultSet.getString("notes"));
                event.setVolunteerId(resultSet.getString("volunteer_id"));
            }
            if (numberOfRows == 0) {
                // did not find such a user
                return null;
            } else if (numberOfRows == 1) {
                return event;
            } else {
                throw new InconsistentDatabaseException("More than one event with the id '" + eventId + "'.");
            }
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
	}


	@Override
	public void insertNewBankAndAdmin(CreateBankFormData formData) throws SQLException, EmailAlreadyInUseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();

            // --- Insert the bank ---
            preparedStatement = connection.prepareStatement(insertBankSQL);
            preparedStatement.setString(1, formData.getBankName());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows != 1) {
                throw new RuntimeException("Should never happen: we inserted one row!");
            }
            preparedStatement.close();

            // --- Find out its bankId ---
            preparedStatement = connection.prepareStatement(getLastInsertIdSQL);
            resultSet = preparedStatement.executeQuery();
            String bankId = null;
            int numberOfRows = 0;
            while (resultSet.next()) {
                numberOfRows++;
                bankId = resultSet.getString(1);
            }
            if (numberOfRows < 1) {
                throw new RuntimeException("This should never happen.");
            } else if (numberOfRows > 1) {
                throw new RuntimeException("This should never happen.");
            }
            preparedStatement.close();

            if (!( formData.getEmail() == null || formData.getEmail().isEmpty())) {
                // Only add a bank admin if there is a bank admin to add
                // --- Insert the bank admin ---
                String salt = null;
                String hashedPassword = null;
                preparedStatement = connection.prepareStatement(insertUserSQL);
                preparedStatement.setString(1, salt);
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, formData.getEmail());
                preparedStatement.setString(4, formData.getFirstName());
                preparedStatement.setString(5, formData.getLastName());
                preparedStatement.setString(6, UserType.BANK_ADMIN.getDBValue());
                preparedStatement.setString(7, bankId);
                preparedStatement.setString(8, formData.getPhoneNumber());
                preparedStatement.setInt(9, ApprovalStatus.INITIAL_APPROVAL_STATUS.getDbValue());
                affectedRows = preparedStatement.executeUpdate();
                if (affectedRows != 1) {
                    throw new RuntimeException("Should never happen: we inserted one row!");
                }
            }
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
	}


    @Override
    public void modifyBankAndBankAdmin(
            EditBankFormData formData
        ) throws SQLException, EmailAlreadyInUseException, NoSuchBankException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            // -- Modify or insert Bank Admin
            // --- Check if a Bank Admin was passed in -- If no bank admin is passed in none needs to be inserted or modified --//
            if (!(formData.getEmail() == null || formData.getEmail().isEmpty())){
                // --- Check if Bank Admin Exists ---
                preparedStatement = connection.prepareStatement(getBankAdminByBankSQL);
                preparedStatement.setString(1, formData.getBankId());
                resultSet = preparedStatement.executeQuery();
                BankAdmin bankAdmin = null;
                int resultCount = 0;
                while (resultSet.next()) {
                    resultCount += 1;
                    if (resultCount > 1) {
                        throw new InconsistentDatabaseException("Multiple bank admins for bank " + formData.getBankId());
                    }
                    UserType userType = UserType.fromDBValue(resultSet.getString("access_type"));
                    if (userType != UserType.BANK_ADMIN) {
                        throw new InconsistentDatabaseException("This should not occur.");
                    }
                    bankAdmin = new BankAdmin();
                    bankAdmin.populateFieldsFromResultSetRow(resultSet);
                }
                String bankAdminId = bankAdmin == null ? null : bankAdmin.getUserId();

                if (bankAdminId == null) {
                    // --- Insert new Bank Admin ---
                    String salt = null;
                    String hashedPassword = null;
                    preparedStatement = connection.prepareStatement(insertUserSQL);
                    preparedStatement.setString(1, salt);
                    preparedStatement.setString(2, hashedPassword);
                    preparedStatement.setString(3, formData.getEmail());
                    preparedStatement.setString(4, formData.getFirstName());
                    preparedStatement.setString(5, formData.getLastName());
                    preparedStatement.setString(6, UserType.BANK_ADMIN.getDBValue());
                    preparedStatement.setString(7, formData.getBankId());
                    preparedStatement.setString(8, formData.getPhoneNumber());
                    preparedStatement.setInt(9, ApprovalStatus.INITIAL_APPROVAL_STATUS.getDbValue());
                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows != 1) {
                        throw new RuntimeException("Should never happen: we inserted one row!");
                    }
                    preparedStatement.close();

                    // --- Find out its userId ---
                    preparedStatement = connection.prepareStatement(getLastInsertIdSQL);
                    resultSet = preparedStatement.executeQuery();
                    int numberOfRows = 0;
                    while (resultSet.next()) {
                        numberOfRows++;
                        bankAdminId = resultSet.getString(1);
                    }
                    if (numberOfRows < 1) {
                        throw new RuntimeException("This should never happen.");
                    } else if (numberOfRows > 1) {
                        throw new RuntimeException("This should never happen.");
                    }
                    preparedStatement.close();

                } else {
                    // --- Modify existing Bank Admin ---
                    preparedStatement = connection.prepareStatement(modifyUserPersonalFieldsSQL);
                    preparedStatement.setString(1, formData.getEmail());
                    preparedStatement.setString(2, formData.getFirstName());
                    preparedStatement.setString(3, formData.getLastName());
                    preparedStatement.setString(4, formData.getPhoneNumber());
                    preparedStatement.setString(5, bankAdminId);
                    try {
                        preparedStatement.executeUpdate();
                    } catch (MySQLIntegrityConstraintViolationException err) {
                        if (err.getMessage().contains("Duplicate entry") && err.getMessage().contains("for key 'ix_email'")) {
                            throw new EmailAlreadyInUseException();
                        } else {
                            throw err;
                        }
                    }
                    preparedStatement.close();
                }
            }
            // --- Modify Bank ---
            String minLMIforCRA = formData.getMinLMIForCRA();
            if (minLMIforCRA == null || minLMIforCRA.isEmpty()) {
                minLMIforCRA = null;
            }
            preparedStatement = connection.prepareStatement(modifyBankByIdSQL);
            preparedStatement.setString(1, formData.getBankName());
            preparedStatement.setString(2, minLMIforCRA);
            preparedStatement.setString(3, formData.getBankId());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows != 1) {
                throw new RuntimeException("How could the bank be deleted before we finish adding it?");
            }

        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


    @Override
    public void setBankSpecificFieldLabel(SetBankSpecificFieldLabelFormData formData)
            throws SQLException, NoSuchBankException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(setBankSpecificFieldLabelSQL);
            preparedStatement.setString(1, emptyMeansNull(formData.getBankSpecificFieldLabel()));
            preparedStatement.setString(2, formData.getBankId());

            preparedStatement.executeUpdate();
        } finally {
            closeSafely(connection, preparedStatement, null);
        }
    }

    /** Just a simple string helper. Returns the string, but empty or blank is returned as null. */
    private String emptyMeansNull(String s) {
        return s == null || s.trim().length() == 0 ? null : s;
    }

    @Override
	public void insertNewSchool(CreateSchoolFormData formData) throws SQLException,
			InconsistentDatabaseException {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(insertSchoolSQL);
            preparedStatement.setString(1, formData.getSchoolName());
            preparedStatement.setString(2, formData.getSchoolAddress1());
            preparedStatement.setString(3, formData.getCity());
            preparedStatement.setString(4, formData.getZip());
            preparedStatement.setString(5, formData.getCounty());
            preparedStatement.setString(6, formData.getDistrict());
            preparedStatement.setString(7, formData.getState());
            preparedStatement.setString(8, formData.getPhone());
            if(formData.getLmiEligible() != null && !StringUtils.isEmpty(formData.getLmiEligible())) {
            	preparedStatement.setInt(9, Integer.parseInt(formData.getLmiEligible()));
            } 
            else {
            	preparedStatement.setInt(9, 0);
            }
            preparedStatement.setString(10, formData.getSLC());

            preparedStatement.executeUpdate();

        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
	}


	@Override
	public void updateUserCredential(String userId, String hashedPassword, String salt)
            throws SQLException
    {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(updateUserCredentialsByIdSQL);
            preparedStatement.setString(1, salt);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, userId);
            preparedStatement.executeUpdate();
        } finally {
            closeSafely(connection, preparedStatement, null);
        }
	}
	
	@Override
	public void updateResetPasswordToken(String userId, String resetPasswordToken)
            throws SQLException
    {
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(updateResetPasswordTokenByIdSQL);
            preparedStatement.setString(1, resetPasswordToken);
            preparedStatement.setString(2, userId);
            preparedStatement.executeUpdate();
        } 
        finally {
            closeSafely(connection, preparedStatement, null);
        }
	}

    @Override
    public void updateApprovalStatusById(String volunteerId, ApprovalStatus approvalStatus) throws SQLException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(updateUserStatusByIdSQL);
            preparedStatement.setInt(1, approvalStatus.getDbValue());
            preparedStatement.setString(2, volunteerId);
            preparedStatement.executeUpdate();
        }
        finally {
            closeSafely(connection, preparedStatement, null);
        }
    }
	
	@Override
	public void deleteAllowedTime(String time) throws SQLException, NoSuchAllowedTimeException
	{
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(deleteAllowedTimeSQL);
            preparedStatement.setString(1, time);
            preparedStatement.executeUpdate();
        } 
        finally {
            closeSafely(connection, preparedStatement, null);
            
        }
	}


    @Override
    public void insertNewAllowedDate(AddAllowedDateFormData formData) throws SQLException, AllowedDateAlreadyInUseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(insertAllowedDateSQL);
            preparedStatement.setDate(1, formData.getDate());
            try {
                preparedStatement.executeUpdate();
            } catch(MySQLIntegrityConstraintViolationException err) {
                throw new AllowedDateAlreadyInUseException();
            }
        }
        finally {
            closeSafely(connection, preparedStatement, null);
        }
    }


    @Override
    public void insertNewAllowedTime(AddAllowedTimeFormData formData)
            throws SQLException, AllowedTimeAlreadyInUseException, NoSuchAllowedTimeException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();

            // --- Figure whether it goes before something or gets put at the end
            boolean insertAtEnd = "".equals(formData.getTimeToInsertBefore());
            int newItemSortOrder;

            if (insertAtEnd) {

                // --- Find the maximum value ---
                preparedStatement = connection.prepareStatement(getLargestSortOrderForAllowedTimeSQL);
                resultSet = preparedStatement.executeQuery();
                int maxSortOrder = 0;
                while(resultSet.next()) {
                    maxSortOrder = resultSet.getInt(1);
                }
                newItemSortOrder = maxSortOrder + 1;
                resultSet.close();
                preparedStatement.close();

            } else {

                // --- Figure out which ones need to be updated ---
                preparedStatement = connection.prepareStatement(getSortOrderForAllowedTimeSQL);
                preparedStatement.setString(1, formData.getTimeToInsertBefore());
                resultSet = preparedStatement.executeQuery();
                newItemSortOrder = Integer.MIN_VALUE;
                while(resultSet.next()) {
                    newItemSortOrder = resultSet.getInt("sort_order");
                }
                if (newItemSortOrder == Integer.MIN_VALUE) {
                    throw new NoSuchAllowedTimeException();
                }
                resultSet.close();
                preparedStatement.close();

                // --- Move things down so there's space for it ---
                preparedStatement = connection.prepareStatement(insertSpaceForAllowedTimeSQL);
                preparedStatement.setInt(1, newItemSortOrder);
                preparedStatement.executeUpdate();
                preparedStatement.close();

            }

            // --- Insert the new allowed time ---
            preparedStatement = connection.prepareStatement(insertAllowedTimeSQL);
            preparedStatement.setString(1, formData.getAllowedTime());
            preparedStatement.setInt(2, newItemSortOrder);
            try {
                preparedStatement.executeUpdate();
            } catch(MySQLIntegrityConstraintViolationException err) {
                throw new AllowedTimeAlreadyInUseException();
            }

        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }


	@Override
	public void deleteAllowedDate(PrettyPrintingDate date) throws SQLException, NoSuchAllowedDateException
	{
		Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(deleteAllowedDateSQL);
            preparedStatement.setDate(1, date);
            preparedStatement.executeUpdate();
        } 
        finally {
            closeSafely(connection, preparedStatement, null);
        }
	}


    @Override
    public SiteStatistics getSiteStatistics() throws SQLException {
        Connection connection = null;
        try {
            connection = connectionFactory.getConnection();

            SiteStatistics siteStatistics = new SiteStatistics();
            siteStatistics.setNumEvents(runSQLReturningInt(connection, getNumEventsSQL));
            siteStatistics.setNumMatchedEvents(runSQLReturningInt(connection, getNumMatchedEventsSQL));
            siteStatistics.setNumUnmatchedEvents(runSQLReturningInt(connection, getNumUnmatchedEventsSQL));
            siteStatistics.setNum3rdGradeEvents(runSQLReturningInt(connection, getNum3rdGradeEventsSQL));
            siteStatistics.setNum4thGradeEvents(runSQLReturningInt(connection, getNum4thGradeEventsSQL));
            siteStatistics.setNumVolunteers(runSQLReturningInt(connection, getNumVolunteersSQL));
            siteStatistics.setNumParticipatingTeachers(runSQLReturningInt(connection, getNumParticipatingTeachersSQL));
            siteStatistics.setNumParticipatingSchools(runSQLReturningInt(connection, getNumParticipatingSchoolsSQL));
            return siteStatistics;
        }
        finally {
            closeSafely(connection, null, null);
        }
    }


    /** A private subroutine of getSiteStatistics(). Must be passed an SQL select returning exactly 1 integer. */
    private int runSQLReturningInt(Connection connection, String sql) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            int outputInt = Integer.MIN_VALUE; // will not get used since we check that there was 1 row
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            int numberOfRows = 0;
            while (resultSet.next()) {
                numberOfRows++;
                outputInt = resultSet.getInt(1);
            }
            if (numberOfRows < 1) {
                throw new RuntimeException("This should never happen.");
            } else if (numberOfRows > 1) {
                throw new RuntimeException("This should never happen.");
            }
            return outputInt;
        }
        finally {
            closeSafely(null, preparedStatement, resultSet);
        }

    }
    
    @Override
    public List<Volunteer> getVolunteersWithBankData() throws SQLException {
        Connection connection = null;
        List<Volunteer> usersList = new ArrayList<Volunteer>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getVolunteersWithBankSQL);
            resultSet = preparedStatement.executeQuery();
            int numberOfRows = 0;
            while (resultSet.next()) {
            			numberOfRows++;
                        Volunteer volunteer = new Volunteer();
                        volunteer.populateFieldsFromResultSetRow(resultSet);
                        Bank bank = new Bank();
                        bank.populateFieldsFromResultSetRow(resultSet);
                        volunteer.setLinkedBank(bank);
                        usersList.add(volunteer);
                 }
             
            if (numberOfRows < 1) {
                return null; // No user found
            } else {
                return usersList;
            } 
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }
    
    @Override
    public List<Teacher> getTeachersWithSchoolData() throws SQLException {
        Connection connection = null;
        List<Teacher> usersList = new ArrayList<Teacher>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getTeacherWithSchoolSQL);
            resultSet = preparedStatement.executeQuery();
            int numberOfRows = 0;
            while (resultSet.next()) {
            			numberOfRows++;
                        Teacher teacher = new Teacher();
                        teacher.populateFieldsFromResultSetRow(resultSet);
                        School school = new School();
                        school.populateFieldsFromResultSetRow(resultSet);
                        teacher.setLinkedSchool(school);
                        usersList.add(teacher);
                 }

            if (numberOfRows < 1) {
                return null; // No user found
            } else {
                return usersList;
            }
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public List<Teacher> getTeachersBySchool(String schoolId) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            List<Teacher> usersList = new ArrayList<Teacher>();
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getTeachersBySchoolSQL);
            preparedStatement.setString(1, schoolId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.populateFieldsFromResultSetRow(resultSet);
                usersList.add(teacher);
            }
            return usersList;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public List<Teacher> getMatchedTeachers() throws SQLException {
        Connection connection = null;
        List<Teacher> usersList = new ArrayList<Teacher>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getMatchedTeachersSQL);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                        Teacher teacher = new Teacher();
                        teacher.populateFieldsFromResultSetRow(resultSet);
                        usersList.add(teacher);
                 }
            return usersList;

        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }
    
    @Override
    public List<Teacher> getUnMatchedTeachers() throws SQLException {
        Connection connection = null;
        List<Teacher> usersList = new ArrayList<Teacher>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getUnMatchedTeachersSQL);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.populateFieldsFromResultSetRow(resultSet);
                        usersList.add(teacher);
            }
            return usersList;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }
    
    @Override
    public List<Volunteer> getMatchedVolunteers() throws SQLException {
        Connection connection = null;
        List<Volunteer> usersList = new ArrayList<Volunteer>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getMatchedVolunteersSQL);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
            			Volunteer volunteer = new Volunteer();
            			volunteer.populateFieldsFromResultSetRow(resultSet);
                        usersList.add(volunteer);
                 }
            return usersList;
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }
    
    @Override
    public List<Volunteer> getUnMatchedVolunteers() throws SQLException {
        Connection connection = null;
        List<Volunteer> usersList = new ArrayList<Volunteer>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getUnMatchedVolunteersSQL);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
            			Volunteer volunteer = new Volunteer();
            			volunteer.populateFieldsFromResultSetRow(resultSet);
                        usersList.add(volunteer);
                 }
             
            return usersList;

        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }
    
    public static String stringToHTMLString(String string) {
        StringBuffer sb = new StringBuffer(string.length());
        // true if last char was blank
        boolean lastWasBlankChar = false;
        int len = string.length();
        char c;

        for (int i = 0; i < len; i++)
            {
            c = string.charAt(i);
            if (c == ' ') {
                // blank gets extra work,
                // this solves the problem you get if you replace all
                // blanks with &nbsp;, if you do that you loss 
                // word breaking
                if (lastWasBlankChar) {
                    lastWasBlankChar = false;
                    sb.append("&nbsp;");
                    }
                else {
                    lastWasBlankChar = true;
                    sb.append(' ');
                    }
                }
            else {
                lastWasBlankChar = false;
                //
                // HTML Special Chars
                if (c == '"')
                    sb.append("&quot;");
                else if (c == '&')
                    sb.append("&amp;");
                else if (c == '<')
                    sb.append("&lt;");
                else if (c == '>')
                    sb.append("&gt;");
                else if (c == '\n')
                    // Handle Newline
                    sb.append("");
                else if (c == '\r')
                    // Handle Newline
                    sb.append("");
                else {
                    int ci = 0xffff & c;
                    if (ci < 160 )
                        // nothing special only 7 Bit
                        sb.append(c);
                    else {
                        // Not 7 Bit use the unicode system
                        sb.append("&#");
                        sb.append(new Integer(ci).toString());
                        sb.append(';');
                        }
                    }
                }
            }
        return sb.toString();
    }

    @Override
    public SortedMap<String, String> getSiteSettings() throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getSiteSettingsSQL);
            resultSet = preparedStatement.executeQuery();
            SortedMap<String,String> result = new TreeMap<String, String>();
            while (resultSet.next()) {
                String setting_name = resultSet.getString("setting_name");
                String setting_value = resultSet.getString("setting_value");
                result.put(setting_name, setting_value);
            }
            return Collections.unmodifiableSortedMap(result);
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
    }

    @Override
    public void modifySiteSetting(String settingName, String settingValue) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = connectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(insertOrModifySiteSettingSQL);
            preparedStatement.setString(1, settingName);
            preparedStatement.setString(2, settingValue);
            preparedStatement.setString(3, settingValue);
            preparedStatement.executeUpdate();
        }
        finally {
            closeSafely(connection, preparedStatement, null);
        }
    }
}
