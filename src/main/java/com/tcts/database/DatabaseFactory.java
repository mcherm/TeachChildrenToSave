package com.tcts.database;

import com.tcts.common.Configuration;
import com.tcts.database.dynamodb.DynamoDBHelper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


/**
 * We want a property file setting to be able to drive the setup of the database
 * implementation we use; this class implements that logic.
 */
@Component
public class DatabaseFactory implements ApplicationContextAware {

    private final Configuration configuration;
    private AutowireCapableBeanFactory beanFactory;

    /**
     * Constructor.
     */
    public DatabaseFactory() {
        this.configuration = new Configuration();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    /**
     * This retrieves the database implementation to use.
     */
    public DatabaseFacade getDatabaseImplementation() {
        String databaseToUse = configuration.getProperty("databaseToUse");
        if ("CachingMySQLDB".equals(databaseToUse)) {
            MySQLDatabase mySQLDatabase = new MySQLDatabase();
            beanFactory.autowireBean(mySQLDatabase);
            return new CachingDatabase(mySQLDatabase);
        } else if ("DynamoDB".equals(databaseToUse)) {
            return new DynamoDBDatabase(configuration, new DynamoDBHelper());
        } else if ("PrefetchedDynamoDB".equals(databaseToUse)) {
            return new PrefetchingDatabase(new DynamoDBDatabase(configuration, new DynamoDBHelper()));
        } else if ("SingleTableDynamoDB".equals(databaseToUse)) {
            return new SingleTableDynamoDbDatabase(configuration);
        } else {
            throw new RuntimeException("Value '" + databaseToUse + "' for property 'databaseToUse' is not recognized.");
        }
    }
}
