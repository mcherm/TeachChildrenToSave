package com.tcts.database;

import com.tcts.common.Configuration;
import com.tcts.database.dynamodb.DynamoDBHelper;
import org.springframework.stereotype.Component;


/**
 * We want a property file setting to be able to drive the setup of the database
 * implementation we use; this class implements that logic.
 */
@Component
public class DatabaseFactory {

    private final Configuration configuration;

    /**
     * Constructor.
     */
    public DatabaseFactory() {
        this.configuration = new Configuration();
    }

    /**
     * This retrieves the database implementation to use.
     */
    public DatabaseFacade getDatabaseImplementation() {
        String databaseToUse = configuration.getProperty("databaseToUse");
        if ("CachingMySQLDB".equals(databaseToUse)) {
            return new CachingDatabase(new MySQLDatabase());
        } else if ("DynamoDB".equals(databaseToUse)) {
            return new DynamoDBDatabase(configuration, new DynamoDBHelper());
        } else {
            throw new RuntimeException("Value '" + databaseToUse + "' for property 'databaseToUse' is not recognized.");
        }
    }
}
