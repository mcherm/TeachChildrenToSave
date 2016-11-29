package com.tcts.database;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Script to do the initial setup of a DynamoDB database.
 */
public class DynamoDBSetup {

    public static void reinitializeDatabase(DynamoDB dynamoDB) throws InterruptedException {
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
     * Main method that sets up the DynamoDB database.
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting...");
        DynamoDB dynamoDB = DynamoDBDatabase.connectToDB();
        reinitializeDatabase(dynamoDB);

        DynamoDBDatabase.Tables tables = DynamoDBDatabase.getTables(dynamoDB);
        tables.siteSettingsTable.putItem(new Item()
                .withPrimaryKey("name", "TestSetting")
                .with("value", "2016-12-21"));

        System.out.println("Inserted a value");

        Item item = tables.siteSettingsTable.getItem(new PrimaryKey("name", "TestSetting"));
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
}
