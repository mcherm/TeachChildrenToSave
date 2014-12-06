package com.tcts.dao2;

import com.tcts.database.ConnectionFactory;
import com.tcts.model2.Event;
import com.tcts.model2.User;
import com.tcts.model2.UserType;
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
    final static String getUserByIdSQL =
            "select user_id, email_1, password, first_name, last_name, access_type, organization_id, phone_number_1 " +
                    "from Users where user_id = ?";

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
            User user = new User();
            while (resultSet.next()) {
                numberOfRows++;
                user.setUserId(resultSet.getString("user_id"));
                user.setEmail(resultSet.getString("email_1"));
                user.setPassword(resultSet.getString("password"));
                user.setFirstName(resultSet.getString("first_name"));
                user.setLastName(resultSet.getString("last_name"));
                user.setUserType(UserType.fromDBValue(resultSet.getString("access_type")));
                user.setOrganizationId(resultSet.getString("organization_id"));
                user.setPhoneNumber(resultSet.getString("phone_number_1"));
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

    final static String getEventsByTeacherSQL =
            "select event_id, teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id " +
                    "from Event2 where teacher_id = ?";

    @Override
    public List<Event> getEventsByTeacher(String teacherId) throws SQLException {
        List<Event> events = new ArrayList<Event>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(getEventsByTeacherSQL);
            preparedStatement.setString(1, teacherId);
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
