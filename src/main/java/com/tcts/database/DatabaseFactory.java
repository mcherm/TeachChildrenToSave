package com.tcts.database;

import com.tcts.common.Configuration;
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
        if ("SingleTableDynamoDB".equals(databaseToUse)) {
            // FIXME: Should be new PrefetchingDatabase(new CachingDatabase(new SingleTableDynamoDbDatabase(configuration)))
            return new SingleTableDynamoDbDatabase(configuration);
        } else {
            throw new RuntimeException("Value '" + databaseToUse + "' for property 'databaseToUse' is not recognized.");
        }
    }
}
