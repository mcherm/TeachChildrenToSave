package com.tcts.dao2;

import com.tcts.database.ConnectionFactory;
import com.tcts.model2.User;
import com.tcts.model2.UserType;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The (one and only) implementation of DatabaseFacade.
 */
@Component
public class MySQLDatabase implements DatabaseFacade {
    final static String selectUserSQL =
            "select user_id, email_1, password, first_name, last_name, access_type, organization_id, phone_number_1 " +
                    "from teachkidsdb.Users where user_id = ?";

    @Override
    public User getUserById(String userId) throws SQLException, InconsistentDatabaseException{
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = ConnectionFactory.getConnection();
            preparedStatement = connection.prepareStatement(selectUserSQL);
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
}
