package com.tcts.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class ConnectionFactory {
    static {
        // Do this at the TOP of the class so it will execute before anything else.
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

	//static reference to itself
    private static final ConnectionFactory instance = new ConnectionFactory();
    private String dbUrl;
    private String dbUsername;
	private String dbPassword;

    /** private constructor: use the ConnectionFactory.getConnection() to access the singleton instance. */
    private ConnectionFactory() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch(IOException err) {
            throw new RuntimeException("Cannot read properties file to connect to database.", err);
        }
        dbUrl = properties.getProperty("db.url");
        dbUsername = properties.getProperty("db.user");
        dbPassword = properties.getProperty("db.pass");
    }
     
    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }
     
    public static Connection getConnection() throws SQLException {
        return instance.createConnection();
    }
}
