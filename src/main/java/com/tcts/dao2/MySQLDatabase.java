package com.tcts.dao2;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.tcts.database.ConnectionFactory;
import com.tcts.model.TeacherRegistrationFormData;
import com.tcts.model2.*;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * The (one and only) implementation of DatabaseFacade.
 */
@Component
public class MySQLDatabase implements DatabaseFacade {
    private final static String userFields =
            "user_id, user_login, password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status";
    private final static String eventFields =
            "event_id, teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id";
    private final static String bankFields =
            "bank_id, bank_name, bank_admin";
    private final static String schoolFields =
            "school_id, school_name, school_addr1, school_addr2, school_city, school_zip, school_county, school_district, school_state, school_phone, school_lmi_eligible";

    private final static String getUserByIdSQL =
            "select " + userFields + " from User2 where user_id = ?";
    private final static String getUserByLoginSQL =
            "select " + userFields + " from User2 where user_login = ?";
    private final static String getVolunteersByBankSQL =
            "select " + userFields + " from User2 where access_type = 'V' and organization_id = ?";
    private final static String getEventsByTeacherSQL =
            "select " + eventFields + " from Event2 where teacher_id = ?";
    private final static String getEventsByVolunteerSQL =
            "select " + eventFields + " from Event2 where volunteer_id = ?";
    private final static String getBankByIdSQL =
            "select " + bankFields + " from Bank2 where bank_id = ?";
    private final static String getSchoolByIdSQL =
            "select " + schoolFields + " from School where school_id = ?";
    private final static String getAllSchoolsSQL =
            "select " + schoolFields + " from School";
    private final static String getLastInsertIdSQL =
            "select last_insert_id() as last_id";
    private final static String insertUserSQL =
            "insert into User2 (user_login, password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


    @Override
    public User getUserById(String userId) throws SQLException, InconsistentDatabaseException {
        return getUserByIdOrLogin(userId, getUserByIdSQL);
    }


    @Override
    public User getUserByLogin(String login) throws SQLException, InconsistentDatabaseException {
        return getUserByIdOrLogin(login, getUserByLoginSQL);
    }

    private User getUserByIdOrLogin(String key, String sql) throws SQLException, InconsistentDatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
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
                throw new InconsistentDatabaseException("Multiple rows for ID or login of '" + key + "'.");
            } else {
                return user;
            }
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
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
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, userId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Event event = new Event();
                event.setEventId(resultSet.getString("event_id"));
                event.setTeacherId(resultSet.getString("teacher_id"));
                event.setEventDate(resultSet.getDate("event_date"));
                event.setEventTime(resultSet.getString("event_time"));
                event.setGrade(resultSet.getString("grade"));
                event.setNumberStudents(resultSet.getInt("number_students"));
                event.setNotes(resultSet.getString("notes"));
                event.setVolunteerId(resultSet.getString("volunteer_id"));
                events.add(event);
            }
            return events;
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
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getVolunteersByBankSQL);
            preparedStatement.setString(1, bankId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                UserType userType = UserType.fromDBValue(resultSet.getString("access_type"));
                if (userType != UserType.VOLUNTEER) {
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
    public Bank getBankById(String bankId) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
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
            connection = ConnectionFactory.getConnection();
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
            connection = ConnectionFactory.getConnection();
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
    public Teacher insertNewTeacher(TeacherRegistrationFormData formData) throws SQLException, NoSuchSchoolException, LoginAlreadyInUseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(insertUserSQL);
            preparedStatement.setString(1, formData.getLogin());
            preparedStatement.setString(2, "dummy_salt"); // FIXME: Need actual salt someday
            preparedStatement.setString(3, formData.getPassword()); // FIXME: Need actual hash someday
            preparedStatement.setString(4, formData.getEmail());
            preparedStatement.setString(5, formData.getFirstName());
            preparedStatement.setString(6, formData.getLastName());
            preparedStatement.setString(7, UserType.TEACHER.getDBValue());
            preparedStatement.setString(8, formData.getSchoolId());
            preparedStatement.setString(9, formData.getPhoneNumber());
            preparedStatement.setInt(10, 0); // FIXME: This represents "not approved"
            try {
                boolean success = preparedStatement.execute();
                // FIXME: What if it fails?
                // FIXME: How do we detect school-does-not-exist problems?
            } catch (MySQLIntegrityConstraintViolationException err) {
                // FIXME: Check if it matches "Duplicate entry '.*' for key 'ix_login'"
                throw new LoginAlreadyInUseException();
            }
            preparedStatement.close();
            preparedStatement = connection.prepareStatement(getLastInsertIdSQL);
            resultSet = preparedStatement.executeQuery();
            String teacherId = null;
            int numberOfRows = 0;
            while (resultSet.next()) {
                numberOfRows++;
                teacherId = resultSet.getString(1);
            }
            if (numberOfRows < 1) {
                throw new RuntimeException("This should never happen.");
            } else if (numberOfRows > 1) {
                throw new RuntimeException("This should never happen.");
            } else {
                Teacher teacher = (Teacher) getUserById(teacherId);
                return teacher;
            }
        } finally {
            closeSafely(connection, preparedStatement, resultSet);
        }
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
}
