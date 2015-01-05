package com.tcts.database;

import com.tcts.common.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


@Component
public class ConnectionFactory {

    static {
        // Do this at the TOP of the class so it will execute before anything else.
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Autowired
    Configuration configuration;


    /**
     * Returns a connection that can be used for accessing the database.
     * <p>
     * FIXME: For performance purposes, it would probably be better if we had
     * some level of connection pooling.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                configuration.getProperty("db.url"),
                configuration.getProperty("db.user"),
                configuration.getProperty("db.pass"));
    }

}
