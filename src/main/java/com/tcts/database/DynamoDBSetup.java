package com.tcts.database;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
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

    /**
     * Main method that sets up the DynamoDB database.
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting...");
        DynamoDB dynamoDB = DynamoDBDatabase.connectToDB();

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

        Table siteSettingsTable = createTable(dynamoDB, "SiteSettings", "name", ScalarAttributeType.S);
        Table allowedDatesTable = createTable(dynamoDB, "AllowedDates", "date", ScalarAttributeType.S);
        Table allowedTimesTable = createTable(dynamoDB, "AllowedTimes", "time", ScalarAttributeType.S);
        Table eventTable = createTable(dynamoDB, "Event", "event_id", ScalarAttributeType.N);
        Table bankTable = createTable(dynamoDB, "Bank", "bank_id", ScalarAttributeType.N);
        Table userTable = createTable(dynamoDB, "User", "user_id", ScalarAttributeType.N);
        Table schoolTable = createTable(dynamoDB, "School", "school_id", ScalarAttributeType.N);

        siteSettingsTable.waitForActive();
        allowedDatesTable.waitForActive();
        allowedTimesTable.waitForActive();
        eventTable.waitForActive();
        bankTable.waitForActive();
        userTable.waitForActive();
        schoolTable.waitForActive();

        PutItemOutcome putItemOutcome = siteSettingsTable.putItem(new Item()
                .withPrimaryKey("name", "TestSetting")
                .with("value", "2016-12-21"));

        System.out.println("Inserted a value");

        Item item = siteSettingsTable.getItem(new PrimaryKey("name", "TestSetting"));
        System.out.println("Got item " + item);
    }



    private static Table createTable(DynamoDB dynamoDB, String tableName, String primaryKeyName, ScalarAttributeType keyType)
            throws InterruptedException {
        ArrayList<AttributeDefinition> attributeDefinitions= new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName(primaryKeyName).withAttributeType(keyType));

        CreateTableRequest createTableRequest = new CreateTableRequest()
                .withTableName(tableName)
                .withKeySchema(Arrays.asList(new KeySchemaElement(primaryKeyName, KeyType.HASH)))
                .withAttributeDefinitions(attributeDefinitions)
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits(1L)
                        .withWriteCapacityUnits(1L));


        Table table = dynamoDB.createTable(createTableRequest);
        System.out.println("Created table " + tableName);
        return table;
    }
}
