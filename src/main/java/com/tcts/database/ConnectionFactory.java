package com.tcts.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
	//static reference to itself
    private static ConnectionFactory instance = new ConnectionFactory();
    static String dbUrl = "jdbc:mysql://mysqldb.cl0on6rirrkn.us-east-1.rds.amazonaws.com:3306/teachkidsdb";
	static String driverClass = "com.mysql.jdbc.Driver";
	static String dbName ="teachkidsdb";
	static String dbUsername="teachkidsde";
	static String dbPassword ="Winter123";
     
    //private constructor
    private ConnectionFactory() {
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
     
    private Connection createConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        } catch (SQLException e) {
            System.out.println("ERROR: Unable to Connect to Database.");
        }
        return connection;
    }   
     
    public static Connection getConnection() {
        return instance.createConnection();
    }
}
