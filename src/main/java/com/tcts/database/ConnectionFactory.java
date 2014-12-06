package com.tcts.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
    static final String dbUrl = "jdbc:mysql://mysqldb.cl0on6rirrkn.us-east-1.rds.amazonaws.com:3306/teachkidsdb";
	static final String dbUsername="teachkidsde";
	static final String dbPassword ="Winter123";

    /** private constructor: use the ConnectionFactory.getConnection() to access the singleton instance. */
    private ConnectionFactory() {
    }
     
    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }
     
    public static Connection getConnection() throws SQLException {
        return instance.createConnection();
    }
}
