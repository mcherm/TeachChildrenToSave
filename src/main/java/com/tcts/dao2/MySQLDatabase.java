package com.tcts.dao2;

import com.tcts.database.ConnectionFactory;
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
            "user_id, email_1, password, first_name, last_name, access_type, organization_id, phone_number_1, user_status";
    private final static String eventFields =
            "event_id, teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id";

    private final static String getUserByIdSQL =
            "select " + userFields + " from Users where user_id = ?";
    private final static String getVolunteersByBankSQL =
            "select " + userFields + " from Users where access_type = 'V' and organization_id = ?";
    private final static String getEventsByTeacherSQL =
            "select " + eventFields + " from Event2 where teacher_id = ?";
    private final static String getEventsByVolunteerSQL =
            "select " + eventFields + " from Event2 where volunteer_id = ?";


    @Override
    public User getUserById(String userId) throws SQLException, InconsistentDatabaseException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getUserByIdSQL);
            preparedStatement.setString(1, userId);
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
                throw new InconsistentDatabaseException("Multiple rows for user '" + userId + "'.");
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
