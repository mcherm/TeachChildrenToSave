package com.tcts.database;

import com.tcts.common.Configuration;
import com.tcts.database.dynamodb.DynamoDBHelper;
import com.tcts.database.dynamodb.ItemBuilder;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tcts.database.SingleTableDbField.*;


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
            final SingleTableDynamoDBSetup instance = new SingleTableDynamoDBSetup(dynamoDbClient, tableName);

            instance.reinitializeDatabase();
            instance.insertData();
        } catch(Exception err) {
            err.printStackTrace();
        }

        System.out.println("Done.");
    }

    // ========== Instance Variables ==========

    final DynamoDbClient dynamoDbClient;
    final String tableName;
    final DynamoDBHelper dynamoDBHelper;

    /**
     * Constructor.
     */
    private SingleTableDynamoDBSetup(DynamoDbClient dynamoDbClient, String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
        this.dynamoDBHelper = new DynamoDBHelper();
    }


    /**
     * When called, this wipes the entire DynamoDB database and then recreates it.
     */
    public void reinitializeDatabase() {
        deleteDatabaseTable();
        createDatabaseTable();
    }

    /** Delete a table. If it doesn't exist, returns without doing anything. */
    private void deleteDatabaseTable() {
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
    private void createDatabaseTable() {
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
                        Stream.of(table_key.name()), // the primary key of the table
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
                        .attributeName(table_key.name())
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
    private void insertData() {
        insertSiteSettings();
        insertAllowedDates();
        insertAllowedTimes();

        insertBank("Applied Bank", null);
        insertBank("Artisan's Bank", null);
        insertBank("Bank of America", "BofA Internal Mail Code");
        insertBank("Barclay's Bank Delaware", null);
        insertBank("BNY Mellon Trust of Delaware", null);
        insertBank("Brandywine Trust Company", null);
        insertBank("Brown Brothers Harriman Trust Company", null);
        final String aBankId = insertBank("Capital One", null);
        insertBank("Charles Schwab Bank", null);
        insertBank("Chase Bank USA", null);
        insertBank("CNB", null);
        insertBank("Comenity Bank", null);
        insertBank("Commonwealth Trust Company", null);
        insertBank("Community Bank Delaware", null);
        insertBank("County Bank", null);
        insertBank("Deutsche Bank Trust Company Delaware", null);
        insertBank("Discover Bank", null);
        insertBank("Fulton Bank", null);
        insertBank("Glenmede", null);
        insertBank("HSBC Trust Company",  null);
        insertBank("JPMorgan Trust Company of Delaware", null);
        insertBank("Key National Trust Company of Delaware", null);
        insertBank("M&T Bank", null);
        insertBank("MidCoast Community Bank", null);
        insertBank("Morgan Stanley Private Bank", null);
        insertBank("PNC Bank", null);
        insertBank("Principal Trust Company", null);
        insertBank("UBS Trust Company", null);
        insertBank("Wells Fargo Bank", null);
        insertBank("WSFS Bank", null);
        insertBank("Delaware Bankers Association", null);
        insertBank("CEEE Volunteers", null);
        insertBank("Middletown High School - Bank at School", null);
        insertBank("The Bryn Mawr Trust Company of DE", null);

        insertUser("AjVW337bQJs=","jtZ3UlKhhAuyKpo98aGUfTiPy74=","mcherm@mcherm.com","Michael","Chermside","SA",null,"610-810-1806",0);
        insertUser("AjVW337bQJs=","jtZ3UlKhhAuyKpo98aGUfTiPy74=","mcherm+BankAdmin@gmail.com","Michael","Chermside","BA",aBankId,"610-810-1806",0);
        insertUser("AjVW337bQJs=","jtZ3UlKhhAuyKpo98aGUfTiPy74=","mcherm+Volunteer@gmail.com","Michael","Chermside","V",aBankId,"610-810-1806",0);
    }

    /**
     * Insert the single record that has the starting values for site settings.
     */
    private void insertSiteSettings() {
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(new ItemBuilder("siteSettings")
                        .withStrings(
                                site_setting_entries,
                                "CourseCreationOpen=yes",
                                "CurrentYear=2025",
                                "EventDatesOnHomepage=April 25 - 29, 2024",
                                "ShowDocuments=yes",
                                "VolunteerSignupsOpen=yes"
                        )
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    /**
     * Insert the single record that has the starting values for allowed dates.
     */
    private void insertAllowedDates() {
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(new ItemBuilder("allowedDates")
                        .withStrings(
                                allowed_date_values,
                                "2017-04-24",
                                "2017-04-25",
                                "2017-04-26",
                                "2017-04-27",
                                "2017-04-28"
                        )
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    /**
     * Insert the single record that has the starting values for allowed times.
     */
    private void insertAllowedTimes() {
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(new ItemBuilder("allowedTimes")
                        .withStrings(
                                allowed_time_values_with_sort,
                                "0|9:00 to 9:45 AM",
                                "1|9:30 to 10:15 AM",
                                "2|10:00 to 10:45 AM",
                                "3|10:30 to 11:15 AM",
                                "4|11:00 to 11:45 AM",
                                "5|11:30 AM to 12:15 PM",
                                "6|12:00 to 12:45 PM",
                                "7|12:30 to 1:15 PM",
                                "8|1:00 to 1:45 PM",
                                "9|1:30 to 2:15 PM",
                                "10|2:00 to 2:45 PM",
                                "11|2:30 to 3:15 PM",
                                "12|3:00 to 3:45 PM",
                                "13|Flexible"
                        )
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    /** Insert a bank into the database. Returns the unique ID it was assigned. */
    public String insertBank(String bankName, String bankSpecificDataLabel)
    {
        final String uniqueId = dynamoDBHelper.createUniqueId();
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(new ItemBuilder("bank", bank_id, uniqueId)
                        .withString(bank_name, bankName)
                        .withString(bank_specific_data_label, bankSpecificDataLabel)
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
        return uniqueId;
    }

    /** Insert a user into the database. Returns the unique ID it was assigned. */
    private String insertUser(String passwordSalt, String passwordHash, String email,
                            String firstName, String lastName, String userType,
                            String organizationId, String phoneNumber, int userStatus)
    {
        final String uniqueId = dynamoDBHelper.createUniqueId();
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(new ItemBuilder("user", user_id, uniqueId)
                        .withString(user_type, userType)
                        .withString(user_password_salt, passwordSalt)
                        .withString(user_hashed_password, passwordHash)
                        .withString(user_email, email.toLowerCase()) // in canonical form
                        .withString(user_original_email, email) // in original form
                        .withString(user_first_name, firstName)
                        .withString(user_last_name, lastName)
                        .withString(user_phone_number, phoneNumber)
                        .withString(user_organization_id, organizationId)
                        .withInt(user_approval_status, userStatus)
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
        return uniqueId;
    }


}
