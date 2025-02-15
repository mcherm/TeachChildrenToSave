package com.tcts.database;

import com.tcts.common.Configuration;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BillingMode;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.Projection;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class with code for setting up a database. Can be executed from main(), but might
 * need some careful code fiddling first.
 */
public class SingleTableDynamoDBSetup {
    public static void main(String[] args) {
        System.out.println("Starting...");
        try {
            Configuration configuration = new Configuration();
            final DynamoDbClient dynamoDbClient = SingleTableDynamoDBDatabase.connectToDB(configuration);
            final String tableName = SingleTableDynamoDBDatabase.getTableName(configuration);

            reinitializeDatabase(dynamoDbClient, tableName);
            insertData(dynamoDbClient, tableName);
        } catch(Exception err) {
            err.printStackTrace();
        }

        System.out.println("Done.");
    }

    /**
     * When called, this wipes the entire DynamoDB database and then recreates it.
     */
    public static void reinitializeDatabase(DynamoDbClient dynamoDbClient, String tableName) {
        deleteDatabaseTable(dynamoDbClient, tableName);
        createDatabaseTable(dynamoDbClient, tableName);
    }

    /** Delete a table. If it doesn't exist, returns without doing anything. */
    private static void deleteDatabaseTable(DynamoDbClient dynamoDbClient, String tableName) {
        final DeleteTableRequest deleteTableRequest = DeleteTableRequest.builder().tableName(tableName).build();
        try {
            dynamoDbClient.deleteTable(deleteTableRequest);
            dynamoDbClient.waiter()
                    .waitUntilTableNotExists(DescribeTableRequest.builder()
                            .tableName(tableName)
                            .build())
                    .matched();
        } catch(ResourceNotFoundException err) {
            // just return without complaining
        }
    }


    private record GSIData(String name, String attribute) {}

    /** Create a table. */
    private static void createDatabaseTable(DynamoDbClient dynamoDbClient, String tableName) {
        final GSIData[] gsiData = {
                new GSIData("ByBankId", "bank_id"),
                new GSIData("ByEventId", "event_id"),
                new GSIData("BySchoolId", "school_id"),
                new GSIData("ByUserEmail", "user_email"),
                new GSIData("ByEventTeacherId", "event_teacher_id"),
                new GSIData("ByEventVolunteerId", "event_volunteer_id"),
                new GSIData("ByUserOrganizationId", "user_organization_id"),
                new GSIData("ByUserType", "user_type"),
        };

        final List<AttributeDefinition> attributeDefinitions = Stream
                .concat(
                        Stream.of("key"), // the primary key of the table
                        Arrays.stream(gsiData).map(x -> x.attribute)
                )
                .map(x -> AttributeDefinition.builder()
                        .attributeName(x)
                        .attributeType(ScalarAttributeType.S)
                        .build())
                .toList();

        final List<GlobalSecondaryIndex> gsis = Arrays.stream(gsiData)
                .map(x -> GlobalSecondaryIndex.builder()
                        .indexName(x.name)
                        .keySchema(KeySchemaElement.builder()
                                .attributeName(x.attribute)
                                .keyType(KeyType.HASH)
                                .build())
                        .projection(Projection.builder()
                                .projectionType(ProjectionType.KEYS_ONLY)
                                .build())
                        .build())
                .collect(Collectors.toList());

        final CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .tableName(tableName)
                .attributeDefinitions(attributeDefinitions)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("key")
                        .keyType(KeyType.HASH)
                        .build())
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .globalSecondaryIndexes(gsis)
                .build();
        dynamoDbClient.createTable(createTableRequest);

        // Wait until the Amazon DynamoDB table is created
        dynamoDbClient.waiter()
                .waitUntilTableExists(DescribeTableRequest.builder()
                        .tableName(tableName)
                        .build())
                .matched();
        // We don't care much if it succeeded or failed, just that we waited for it.
    }

    /**
     * Insert the starting data into the DB.
     */
    private static void insertData(DynamoDbClient dynamoDbClient, String tableName) {
        insertSiteSettings(dynamoDbClient, tableName);

    }

    /**
     * Insert the single record that has the starting values for site settings.
     */
    private static void insertSiteSettings(DynamoDbClient dynamoDbClient, String tableName) {
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(Map.of(
                        "key", AttributeValue.builder().s("siteSettings").build(),
                        "keyvalues", AttributeValue.builder().ss(
                                "CourseCreationOpen=yes",
                                "CurrentYear=2025",
                                "EventDatesOnHomepage=April 25 - 29, 2024",
                                "ShowDocuments=yes",
                                "VolunteerSignupsOpen=yes"
                        ).build()
                ))
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }
}
