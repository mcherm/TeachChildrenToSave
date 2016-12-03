package com.tcts.database;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateGlobalSecondaryIndexAction;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Script to do the initial setup of a DynamoDB database.
 */
public class DynamoDBSetup {

    public static void createAllDatabaseTables(DynamoDB dynamoDB) throws InterruptedException {
        Table siteSettingsTable = createTable(dynamoDB, "SiteSettings", DatabaseField.site_setting_name, ScalarAttributeType.S);
        Table allowedDatesTable = createTable(dynamoDB, "AllowedDates", DatabaseField.event_date_allowed, ScalarAttributeType.S);
        Table allowedTimesTable = createTable(dynamoDB, "AllowedTimes", DatabaseField.event_time_allowed, ScalarAttributeType.S);
        Table eventTable = createTable(dynamoDB, "Event", DatabaseField.event_id, ScalarAttributeType.S);
        Table bankTable = createTable(dynamoDB, "Bank", DatabaseField.bank_id, ScalarAttributeType.S);
        Table userTable = createTable(dynamoDB, "User", DatabaseField.user_id, ScalarAttributeType.S);
        Table schoolTable = createTable(dynamoDB, "School", DatabaseField.school_id, ScalarAttributeType.S);

        siteSettingsTable.waitForActive();
        allowedDatesTable.waitForActive();
        allowedTimesTable.waitForActive();
        eventTable.waitForActive();
        bankTable.waitForActive();
        userTable.waitForActive();
        schoolTable.waitForActive();
    }

    /**
     * When called, this wipes the entire DynamoDB database and then recreates the
     * tables but none of the indexes.
     */
    public static void deleteAllDatabaseTables(DynamoDB dynamoDB) throws InterruptedException {
        DynamoDBDatabase.Tables tables = DynamoDBDatabase.getTables(dynamoDB);
        tables.siteSettingsTable.delete();
        tables.allowedDatesTable.delete();
        tables.allowedTimesTable.delete();
        tables.eventTable.delete();
        tables.bankTable.delete();
        tables.userTable.delete();
        tables.schoolTable.delete();

        tables.siteSettingsTable.waitForDelete();
        tables.allowedDatesTable.waitForDelete();
        tables.allowedTimesTable.waitForDelete();
        tables.eventTable.waitForDelete();
        tables.bankTable.waitForDelete();
        tables.userTable.waitForDelete();
        tables.schoolTable.waitForDelete();
    }

    /** Initializes the userByEmail index. */
    public static void initializeUserByEmailIndex(DynamoDB dynamoDB) throws InterruptedException {
        DynamoDBDatabase.Tables tables = DynamoDBDatabase.getTables(dynamoDB);
        Index userByEmailIndex = createIndex(tables.userTable, "byEmail", DatabaseField.user_email, ScalarAttributeType.S);
        userByEmailIndex.waitForActive();
    }

    /**
     * When called, this wipes the entire DynamoDB database and then recreates it.
     */
    public static void reinitializeDatabase(DynamoDB dynamoDB) throws InterruptedException {
        deleteAllDatabaseTables(dynamoDB);
        createAllDatabaseTables(dynamoDB);
        initializeUserByEmailIndex(dynamoDB);
    }

    /**
     * Main method that sets up the DynamoDB database.
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting...");
        DynamoDB dynamoDB = DynamoDBDatabase.connectToDB();
        reinitializeDatabase(dynamoDB);

        DynamoDBDatabase.Tables tables = DynamoDBDatabase.getTables(dynamoDB);
        tables.siteSettingsTable.putItem(new Item()
                .withPrimaryKey(DatabaseField.site_setting_name.name(), "TestSetting")
                .with(DatabaseField.site_setting_value.name(), "2016-12-21"));

        System.out.println("Inserted a value");

        Item item = tables.siteSettingsTable.getItem(new PrimaryKey(DatabaseField.site_setting_name.name(), "TestSetting"));
        System.out.println("Got item " + item);
    }



    private static Table createTable(DynamoDB dynamoDB, String tableName, DatabaseField databaseField, ScalarAttributeType keyType)
            throws InterruptedException {
        ArrayList<AttributeDefinition> attributeDefinitions= new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName(databaseField.name()).withAttributeType(keyType));

        CreateTableRequest createTableRequest = new CreateTableRequest()
                .withTableName(tableName)
                .withKeySchema(Arrays.asList(new KeySchemaElement(databaseField.name(), KeyType.HASH)))
                .withAttributeDefinitions(attributeDefinitions)
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits(1L)
                        .withWriteCapacityUnits(1L));


        Table table = dynamoDB.createTable(createTableRequest);
        return table;
    }

    private static Index createIndex(Table table, String indexName, DatabaseField databaseField, ScalarAttributeType keyType) throws InterruptedException {
        table.waitForActive();
        return table.createGSI(
                new CreateGlobalSecondaryIndexAction()
                .withIndexName(indexName)
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits(1L)
                        .withWriteCapacityUnits(1L))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                .withKeySchema(new KeySchemaElement(databaseField.name(), KeyType.HASH)),
                new AttributeDefinition().withAttributeName(databaseField.name()).withAttributeType(keyType)
        );

    }
}
