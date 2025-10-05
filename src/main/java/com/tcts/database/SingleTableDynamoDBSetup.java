package com.tcts.database;

import com.tcts.common.Configuration;
import com.tcts.database.dynamodb.DynamoDBHelper;
import com.tcts.database.dynamodb.ItemBuilder;
import com.tcts.exception.AppConfigurationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
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
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.tcts.database.DatabaseField.*;


/**
 * A class with code for setting up a database. Can be executed from main(), but might
 * need some careful code fiddling first.
 */
public class SingleTableDynamoDBSetup {
    public static void main(String[] args) {
        System.out.println("Starting...");
        String site = "FL";
        try {
            Configuration configuration = new Configuration();
            final DynamoDbClient dynamoDbClient = SingleTableDynamoDbDatabase.connectToDB(configuration);
            final String environment = configuration.getProperty("dynamoDB.environment", "dev");

            final String tableName = "TCTS." + site + "." + environment;

            System.out.println("DO YOU MEAN TO RE-INITIALIZE " + tableName + "?");
            final String response = new Scanner(System.in).nextLine();
            if (!response.equalsIgnoreCase("y")) {
                System.out.println("Exiting without doing anything!");
                return;
            }

            reinitializeDatabase(dynamoDbClient, tableName);

            final SingleTableDynamoDBSetup instance = new SingleTableDynamoDBSetup(dynamoDbClient, tableName);
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
        insertAllowedGrades();
        insertAllowedDeliveryMethods();
        insertDocuments();

        final String aSchoolId = insertSchool("New Castle", "Appoquinimink", "Bunker Hill Elementary School", "N120", "1070 Bunker Hill Road", "Middletown", "DE", "19709", "302.378.5135", "15.4");
        insertSchool("New Castle", "Appoquinimink", "Cedar Lane Elementary School", "N120", "1221 Cedar Lane Road", "Middletown", "DE", "19709", "302.378.5045", "13");
        insertSchool("New Castle", "Appoquinimink", "Loss (Olive B.) Elementary School", "N120", "200 Brennan Boulevard", "Bear", "DE", "19701", "302.832.1343", "7.7");
        insertSchool("New Castle", "Appoquinimink", "Old State Elementary School", "N120", "580 Tony Marchio Drive", "Townsend", "DE", "19734", "302.378.6720", "31.5");
        insertSchool("New Castle", "Appoquinimink", "Silver Lake Elementary School", "N120", "200 E. Cochran Street", "Middletown", "DE", "19709", "302.378.5023", "31.6");
        insertSchool("New Castle", "Appoquinimink", "Townsend Elementary School", "N120", "126 Main Street", "Townsend", "DE", "19734", "302.378.5020", "31.5");
        insertSchool("New Castle", "Brandywine", "Carrcroft Elementary School", "N280", "503 Crest Road", "Wilmington", "DE", "19803", "302.762.7165", "36.1");
        insertSchool("New Castle", "Brandywine", "Claymont Elementary School", "N280", "3401 Green Street", "Claymont", "DE", "19703", "302.792.3880", "42.2");
        insertSchool("New Castle", "Brandywine", "Forwood Elementary School", "N280", "1900 Westminster Drive", "Wilmington", "DE", "19810", "302.475.3956", "24.9");
        insertSchool("New Castle", "Brandywine", "Hanby Elementary School", "N280", "2523 Berwyn Road", "Wilmington", "DE", "19810", "302.479.2220", "23.9");
        insertSchool("New Castle", "Brandywine", "Harlan (David W.) Elementary School", "N280", "3601 Jefferson Street", "Wilmington", "DE", "19802", "302.762.7156", "61");
        insertSchool("New Castle", "Brandywine", "Lancashire Elementary School", "N280", "200 Naamans Road", "Wilmington", "DE", "19810", "302.475.3990", "25.2");
        insertSchool("New Castle", "Brandywine", "Lombardy Elementary School", "N280", "412 Foulk Road", "Wilmington", "DE", "19803", "302.762.7190", "26.9");
        insertSchool("New Castle", "Brandywine", "Maple Lane Elementary School", "N280", "100 Maple Lane", "Claymont", "DE", "19703", "302.792.3906", "46.9");
        insertSchool("New Castle", "Brandywine", "Mount Pleasant Elementary School", "N280", "500 Duncan Road", "Wilmington", "DE", "19809", "302.762.7120", "35.3");
        insertSchool("Kent", "Caesar Rodney", "Brown (W. Reily) Elementary School", "D280", "360 Webbs Lane", "Dover", "DE", "19904", "302.697.2101", "58");
        insertSchool("Kent", "Caesar Rodney", "Charlton (John S.) School", "D280", "278 Sorghum Mill Road", "Camden-Wyoming", "DE", "19901", "302.697.3103", "27");
        insertSchool("Kent", "Caesar Rodney", "Frear (Allen) Elementary School", "D280", "238 Sorghum Mill Road", "Camden", "DE", "19934", "302.697.3279", "35.3");
        insertSchool("Kent", "Caesar Rodney", "Kent Elementary Intensive Learning Center", "D280", "5 Old North Road", "Camden", "DE", "19934", "302.697.3504", "75.6");
        insertSchool("Kent", "Caesar Rodney", "Simpson (W.B.) Elementary School", "D280", "5 Old North Road", "Camden", "DE", "19934", "302.697.3207", "38.8");
        insertSchool("Kent", "Caesar Rodney", "Star Hill Elementary School", "D280", "594 Voshells Mill/Star Hill Road", "Dover", "DE", "19901", "302.697.6117", "25.8");
        insertSchool("Kent", "Caesar Rodney", "Stokes (Nellie Hughes) Elementary School", "D280", "3874 Upper King Road", "Dover", "DE", "19904", "302.697.3205", "49.6");
        insertSchool("Kent", "Caesar Rodney", "Welch (Major George S.) Elementary School", "D280", "3100 Hawthorne Drive", "Dover", "DE", "19901", "302.674.9080", "5.6");
        insertSchool("Sussex", "Cape Henlopen", "H. O. Brittingham Elementary School", "S150", "400 Mulberry Street", "Milton", "DE", "19968", "302.684.8522", "65.5");
        insertSchool("Sussex", "Cape Henlopen", "Milton Elementary School", "S150", "512 Federal Street", "Milton", "DE", "19968", "302.684.2516", "30.6");
        insertSchool("Sussex", "Cape Henlopen", "Rehoboth Elementary School", "S150", "500 Stockely Street", "Rehoboth", "DE", "19971", "302.227.2571", "37");
        insertSchool("Sussex", "Cape Henlopen", "Shields (Richard A.) Elementary School", "S150", "910 Shields Avenue", "Lewes", "DE", "19958", "302.645.7748", "32.1");
        insertSchool("Sussex", "Cape Henlopen", "Sussex Consortium", "S150", "520 duPont Avenue", "Lewes", "DE", "19958", "302.645.7210", "41.2");
        insertSchool("Kent", "Capital", "Booker T. Washington Elementary School", "D103", "901 Forest Street", "Dover", "DE", "19904", "302.672.1900", "57.4");
        insertSchool("Kent", "Capital", "East Dover Elementary School", "D103", "852 South Little Creek Road", "Dover", "DE", "19901", "302.672.1655", "67.7");
        insertSchool("Kent", "Capital", "Fairview Elementary School", "D103", "700 Walker Road", "Dover", "DE", "19904", "302.672.1645", "62.5");
        insertSchool("Kent", "Capital", "Hartly Elementary School", "D103", "2617 Arthursville Road", "Hartly", "DE", "19953", "302.492.1870", "40.6");
        insertSchool("Kent", "Capital", "Kent County Community School", "D103", "65-1 Carver Road", "Dover", "DE", "19904", "302.672.1960", "41.8");
        insertSchool("Kent", "Capital", "North Dover Elementary School", "D103", "855 College Road", "Dover", "DE", "19904", "302.672.1980", "53.7");
        insertSchool("Kent", "Capital", "South Dover Elementary School", "D103", "955 South State Street", "Dover", "DE", "19901", "302.672.1690", "63.6");
        insertSchool("Kent", "Capital", "Towne Point Elementary School", "D103", "629 Buckson Drive", "Dover", "DE", "19901", "302.672.1590", "66.3");
        insertSchool("New Castle", "Christina", "Bancroft Elementary School", "N410", "700 North Lombard Street", "Wilmington", "DE", "19801", "302.429.4102", "79");
        insertSchool("New Castle", "Christina", "Brader (Henry M.) Elementary School", "N410", "350 Four Seasons Parkway", "Newark", "DE", "19702", "302.454.5959", "37.3");
        insertSchool("New Castle", "Christina", "Brookside Elementary School", "N410", "800 Marrows Road", "Newark", "DE", "19713", "302.454.5454", "63");
        insertSchool("New Castle", "Christina", "Delaware School for the Deaf Elementary", "N410", "630 East Chestnut Hill Road", "Newark", "DE", "19713", "302.454.2301", "34.8");
        insertSchool("New Castle", "Christina", "Douglass School", "N410", "1800 Prospect Road", "Wilmington", "DE", "19805", "302.429.4146", "71");
        insertSchool("New Castle", "Christina", "Downes (John R.) Elementary School", "N410", "220 Casho Mill Road", "Newark", "DE", "19711", "302.454.2133", "30.2");
        insertSchool("New Castle", "Christina", "Elbert-Palmer Elementary School", "N410", "1210 Lobdell Street", "Wilmington", "DE", "19801", "302.429.4188", "89.8");
        insertSchool("New Castle", "Christina", "Gallaher (Robert S.) Elementary School", "N410", "800 North Brownleaf Road", "Newark", "DE", "19713", "302.454.2464", "39.2");
        insertSchool("New Castle", "Christina", "Jones (Albert H.) Elementary School", "N410", "35 West Main Street", "Newark", "DE", "19702", "302.454.2131", "48.7");
        insertSchool("New Castle", "Christina", "Keene (William B.) Elementary School", "N410", "200 LaGrange Avenue", "Newark", "DE", "19702", "302.454.2018", "35.1");
        insertSchool("New Castle", "Christina", "Leasure (May B.) Elementary School", "N410", "1015 Church Road", "Newark", "DE", "19702", "302.454.2103", "44.4");
        insertSchool("New Castle", "Christina", "Maclary (R. Elisabeth) Elementary School", "N410", "300 St. Regis Drive", "Newark", "DE", "19711", "302.454.2142", "31.3");
        insertSchool("New Castle", "Christina", "Marshall (Thurgood) Elementary School", "N410", "101 Barrett Run Road", "Newark", "DE", "19702", "302.454.4700", "26.1");
        insertSchool("New Castle", "Christina", "McVey (Joseph M.) Elementary School", "N410", "908 Janice Drive", "Newark", "DE", "19713", "302.454.2145", "50.8");
        insertSchool("New Castle", "Christina", "Montessori Academy Wilmington", "N410", "700 North Lombard Street", "Wilmington", "DE", "19801", "302.429.4102", "79");
        insertSchool("New Castle", "Christina", "Oberle (William) Elementary School", "N410", "500 South Caledonia Way", "Bear", "DE", "19701", "302.834.5910", "57.9");
        insertSchool("New Castle", "Christina", "Pulaski (Casimir) Elementary School", "N410", "1300 Cedar Street", "Wilmington", "DE", "19805", "302.429.4136", "81.6");
        insertSchool("New Castle", "Christina", "Smith (Jennie E.) Elementary School", "N410", "142 Brennen Drive", "Newark", "DE", "19713", "302.454.2174", "45.2");
        insertSchool("New Castle", "Christina", "Stubbs (Frederick Douglass) Elementary School", "N410", "1100 North Pine Street", "Wilmington", "DE", "19801", "302.429.4175", "89.2");
        insertSchool("New Castle", "Christina", "West Park Place Elementary School", "N410", "193 West Park Place", "Newark", "DE", "19711", "302.454.2290", "31.2");
        insertSchool("New Castle", "Christina", "Wilson (Etta J.) Elementary School", "N410", "14 Forge Road", "Newark", "DE", "19711", "302.454.2180", "39.2");
        insertSchool("New Castle", "Colonial", "Castle Hills Elementary School", "N160", "502 Moores Lane", "New Castle", "DE", "19720", "302.323.2915", "60.8");
        insertSchool("New Castle", "Colonial", "Downie (Carrie) Elementary School", "N160", "1201 Delaware Street", "New Castle", "DE", "19720", "302.323.2926", "56.7");
        insertSchool("New Castle", "Colonial", "Eisenberg (Harry O.) Elementary School", "N160", "27 Landers Lane", "New Castle", "DE", "19720", "302.429.4074", "67.8");
        insertSchool("New Castle", "Colonial", "New Castle Elementary School", "N160", "903 Delaware Street", "New Castle", "DE", "19720", "302.323.2880", "59");
        insertSchool("New Castle", "Colonial", "Pleasantville Elementary School", "N160", "16 Pleasant Street", "New Castle", "DE", "19720", "302.323.2935", "46.2");
        insertSchool("New Castle", "Colonial", "Southern Elementary School", "N160", "795 Cox Neck Road", "New Castle", "DE", "19720", "302.832.6300", "36.5");
        insertSchool("New Castle", "Colonial", "Wilbur (Kathleen H.) Elementary", "N160", "4050 Wrangle Hill Road", "Bear", "DE", "19701", "302.832.6330", "33.7");
        insertSchool("New Castle", "Colonial", "Wilmington Manor Elementary School", "N160", "200 East Roosevelt Avenue", "New Castle", "DE", "1920", "302.323.2901", "48.7");
        insertSchool("Sussex", "Indian River", "Clayton (John M.) Elementary School", "S790", "252 Clayton Avenue", "Frankford", "DE", "19945", "302.732.3808", "62");
        insertSchool("Sussex", "Indian River", "East Millsboro Elementary School", "S790", "29346 Iron Branch Road", "Millsboro", "DE", "19966", "302.934.3222", "46.2");
        insertSchool("Sussex", "Indian River", "Ennis (Howard T.) School", "S790", "20436 Ennis Road", "Georgetown", "DE", "19947", "302.856.1930", "43.5");
        insertSchool("Sussex", "Indian River", "Georgetown Elementary School", "S790", "301-A West Market Street", "Georgetown", "DE", "19947", "302.856.1940", "61.4");
        insertSchool("Sussex", "Indian River", "Long Neck Elementary School", "S790", "26064 School Lane", "Millsboro", "DE", "19966", "302.945.6200", "56");
        insertSchool("Sussex", "Indian River", "Lord Baltimore Elementary School", "S790", "120 Atlantic Avenue", "Ocean View", "DE", "19970", "302.537.2700", "32");
        insertSchool("Sussex", "Indian River", "North Georgetown Elementary School", "S790", "664 North Bedford Street", "Georgetown", "DE", "19947", "302.855.2430", "60.2");
        insertSchool("Sussex", "Indian River", "Showell (Phillip C.) Elementary School", "S790", "41 Bethany Road", "Selbyville", "DE", "19975", "302.436.1040", "58.3");
        insertSchool("Sussex", "Indian River", "Southern Delaware School of the Arts", "S790", "27 Hosier Street", "Selbyville", "DE", "19975", "302.436.1066", "21");
        insertSchool("Kent", "Lake Forest", "Lake Forest Central Elementary School", "S690", "5424 Killens Pond Road", "Felton", "DE", "19943", "302.284.5810", "48.2");
        insertSchool("Kent", "Lake Forest", "Lake Forest East Elementary School", "S690", "124 West Front Street", "Frederica", "DE", "19946", "302.335.5261", "53.1");
        insertSchool("Kent", "Lake Forest", "Lake Forest North Elementary School", "S690", "319 East Main Street", "Felton", "DE", "19943", "302.284.9611", "46.5");
        insertSchool("Kent", "Lake Forest", "Lake Forest South Elementary School", "S690", "301 Dorman Street", "Harrington", "DE", "19952", "302.398.8011", "57.4");
        insertSchool("Sussex", "Laurel", "North Laurel Elementary School", "S770", "300 Wilson Street", "Laurel", "DE", "19956", "302.875.6130", "59.3");
        insertSchool("Sussex", "Milford", "Banneker (Benjamin) Elementary School", "S180", "449 North Street", "Milford", "DE", "19963", "302.422.1630", "56.2");
        insertSchool("Sussex", "Milford", "Mispillion Elementary", "S180", "311 Lovers Lane", "Milford", "DE", "19963", "302.424.5800", "56");
        insertSchool("Sussex", "Milford", "Ross (Lulu M.) Elementary School", "S180", "310 Lovers Lane", "Milford", "DE", "19963", "302.422.1640", "50.8");
        insertSchool("New Castle", "Red Clay", "Baltz (Austin D.) Elementary School", "N270", "1500 Spruce Avenue", "Wilmington", "DE", "19805", "302.992.5560", "61.8");
        insertSchool("New Castle", "Red Clay", "Brandywine Springs School", "N270", "2916 Duncan Road", "Wilmington", "DE", "19808", "302.636.5681", "9.2");
        insertSchool("New Castle", "Red Clay", "Forest Oak Elementary School", "N270", "55 South Meadowood Drive", "Newark", "DE", "19711", "302.454.3420", "37");
        insertSchool("New Castle", "Red Clay", "Heritage Elementary School", "N270", "2815 Highlands Lane", "Wilmington", "DE", "19808", "302.454.3424", "23.5");
        insertSchool("New Castle", "Red Clay", "Highlands Elementary School", "N270", "2100 Gilpin Avenue", "Wilmington", "DE", "19806", "302.651.2715", "68.1");
        insertSchool("New Castle", "Red Clay", "Lewis (William C.) Dual Language Elementary School", "N270", "920 North Van Buren Street", "Wilmington", "DE", "19806", "302.651.2695", "74.9");
        insertSchool("New Castle", "Red Clay", "Linden Hill Elementary School", "N270", "3415 Skyline Drive", "Wilmington", "DE", "19808", "302.454.3406", "10.1");
        insertSchool("New Castle", "Red Clay", "Marbrook Elementary School", "N270", "2101 Centerville Road", "Wilmington", "DE", "19808", "302.992.5555", "56.3");
        insertSchool("New Castle", "Red Clay", "Mote (Anna P.) Elementary School", "N270", "2110 Edwards Avenue", "Wilmington", "DE", "19808", "302.992.5565", "56.5");
        insertSchool("New Castle", "Red Clay", "North Star Elementary School", "N270", "1340 Little Baltimore Road", "Hockessin", "DE", "19707", "302.234.7200", "3.9");
        insertSchool("New Castle", "Red Clay", "Richardson Park Learning Center", "N270", "99 Middleboro Road", "Wilmington", "DE", "19804", "302.992.5574", "43.5");
        insertSchool("New Castle", "Red Clay", "Richardson Park Elementary School", "N270", "16 Idella Avenue", "Wilmington", "DE", "19804", "302.992.5570", "69.9");
        insertSchool("New Castle", "Red Clay", "Richey Elementary School", "N270", "105 East Highland Avenue", "Wilmington", "DE", "19804", "302.992.5535", "44.1");
        insertSchool("New Castle", "Red Clay", "Shortlidge (Evan G.) Academy", "N270", "100 West 18th Street", "Wilmington", "DE", "19802", "302.651.2710", "85.4");
        insertSchool("New Castle", "Red Clay", "William Cooke Jr. Elementary School", "N270", "2025 Graves Road", "Hockessin", "DE", "19707", "302.235.6600", null);
        insertSchool("New Castle", "Red Clay", "Warner Elementary School", "N270", "801 West 18th Street", "Wilmington", "DE", "19802", "302.651.2740", "85");
        insertSchool("Sussex", "Seaford", "Blades Elementary School", "S730", "900 South Arch Street", "Blades", "DE", "19973", "302.628.4416", "64.2");
        insertSchool("Sussex", "Seaford", "Frederick Douglass Elementary School", "S730", "1 Swain Road", "Seaford", "DE", "19973", "302.628.4413", "64");
        insertSchool("Sussex", "Seaford", "Seaford Central Elementary School", "S730", "1 Delaware Place", "Seaford", "DE", "19973", "302.629.4587", "47");
        insertSchool("Sussex", "Seaford", "West Seaford Elementary School", "S730", "511 Sussex Avenue", "Seaford", "DE", "19973", "302.628.4414", "65.6");
        insertSchool("Kent", "Smyrna", "Clayton Elementary School", "N460", "510 West Main Street", "Clayton", "DE", "19938", "302.653.8587", "30");
        insertSchool("Kent", "Smyrna", "North Smyrna Elementary School", "N460", "365 North Main Street", "Smyrna", "DE", "19977", "302.653.8589", "47.8");
        insertSchool("Kent", "Smyrna", "Smyrna Elementary School", "N460", "121 South School Lane", "Smyrna", "DE", "19977", "302.653.8588", "32.8");
        insertSchool("Kent", "Smyrna", "Sunnyside Elementary School", "N460", "123 Rabbit Chase Road", "Smyrna", "DE", "19977", "302.653.8580", "25.3");
        insertSchool("Sussex", "Woodbridge", "Phillis Wheatley Elementary School", "S710", "48 Church Street", "Bridgeville", "DE", "19933", "302-337-3469", null);
        insertSchool("Sussex", "Woodbridge", "Woodbridge Elementary School", "S710", "400 Governors Avenue", "Greenwood", "DE", "19950", "302.349.4010", "58");
        insertSchool("Kent", null, "Academy Of Dover Charter School", "N/A", "104 Saulsbury Road", "Dover", "DE", "19904", "302.674.0684", "68.8");
        insertSchool("Kent", null, "Campus Community School", "N/A", "350 Pear Street", "Dover", "DE", "19904", "302.736.0403", "39.2");
        insertSchool("New Castle", null, "East Side Charter School", "N/A", "3000 North Claymont Street", "Wilmington", "DE", "19802", "302.762.5834", "84.1");
        insertSchool("New Castle", null, "Edison (Thomas A.) Charter School", "N/A", "2200 North Locust Street", "Wilmington", "DE", "19802", "302.778.1101", "80.9");
        insertSchool("New Castle", null, "Family Foundations Academy", "N/A", "1101 Delaware Street", "New Castle", "DE", "19720", "302.324.8901", "49.9");
        insertSchool("New Castle", null, "First State Montessori Academy", "N/A", "1000 North French Street", "Wilmington", "DE", "19801", "302.576.1500", null);
        insertSchool("New Castle", null, "Gateway Lab School", "N/A", "2501 Centerville Road", "Wilmington", "DE", "19808", "302.633.4091", "27.9");
        insertSchool("New Castle", null, "Kuumba Academy Charter School", "N/A", "519 North Market Street", "Wilmington", "DE", "19801", "302.472.6450", "63.1");
        insertSchool("New Castle", null, "Las Americas ASPIRA Academy", "N/A", "326 Ruthar Drive", "Newark", "DE", "19711", "302.292.1463", "27.8");
        insertSchool("New Castle", null, "MOT Charter School", "N/A", "1156 Levels Road", "Middletown", "DE", "19709", "302.376.5125", "4.9");
        insertSchool("New Castle", null, "Newark Charter School", "N/A", "2001 Patriot Way", "Newark", "DE", "19711", "302.369.2001", "8.4");
        insertSchool("New Castle", null, "Odyssey Charter School", "N/A", "201 Bayard Avenue", "Wilmington", "DE", "19805", "302.655.5760", "17.8");
        insertSchool("New Castle", null, "Providence Creek Academy Charter School", "N/A", "273 West Duck Creek Road", "Clayton", "DE", "19938", "302.653.6276", "24.5");
        insertSchool("New Castle", null, "Reach Academy for Girls", "N/A", "170 Lukens Drive", "New Castle", "DE", "19720", "302.654.3720", "59");
        insertSchool("New Castle", null, "Wilmington Montessori School", "N/A", "1400 Harvey Road", "Wilmington", "DE", "19810", "302.475.0555", null);
        insertSchool("New Castle", null, "Delaware College Preparatory Academy", "N/A", "510 W. 28th Street", "Wilmington", "DE", "19802", "302.762.7424", "82.1");
        insertSchool("New Castle", null, "Corpus Christie  Elementary School", "N/A", "907 New Road Elsmere", "Wilmington", "DE", "19805", "302.995.2231", null);
        insertSchool("New Castle", null, "Holy Angels School", "N/A", "82 Possum Park Road", "Newark", "DE", "19711", "302.731.2210", null);
        insertSchool("Kent", null, "Holy Cross Elementary School", "N/A", "631 South State Street", "Dover", "DE", "19901", "302.674.5787", null);
        insertSchool("New Castle", null, "Holy Rosary School", "N/A", "3210 Philadelphia Pike", "Claymont", "DE", "19703", "302.798.5584", null);
        insertSchool("New Castle", null, "Holy Spirit School", "N/A", "Church Drive", "New Castle", "DE", "19720", "302.658.5345", null);
        insertSchool("New Castle", null, "Immaculate Heart of Mary School", "N/A", "1000 Shipley Road", "Wilmington", "DE", "19803", "302.764.0977", null);
        insertSchool("New Castle", null, "Our Lady of Fatima School", "N/A", "801 N . Dupont Highway", "New Castle", "DE", "19720", "302.328.2803", null);
        insertSchool("New Castle", null, "St. Ann's School", "N/A", "2006 Shallcross Avenue", "Wilmington", "DE", "19806", "302.652.6567", null);
        insertSchool("New Castle", null, "St. Anthony of Padua School", "N/A", "1715 West 9th Street", "Wilmington", "DE", "19805", "302.421.3743", null);
        insertSchool("New Castle", null, "St. Catherine of Siena School", "N/A", "2503 Centerville Road", "Wilmington", "DE", "19808", "302.633.4900", null);
        insertSchool("New Castle", null, "St. Edmond's Academy", "N/A", "2120 Veale Road", "Wilmington", "DE", "19810", "302.475.5370", null);
        insertSchool("New Castle", null, "St. Hedwig School", "N/A", "408 South Harrison Street", "Wilmington", "DE", "19805", "302.594.1402", null);
        insertSchool("New Castle", null, "St. Helena School", "N/A", "210 Bellefonte Avenue", "Wilmington", "DE", "19809", "302.762.5595", null);
        insertSchool("New Castle", null, "St. John the Beloved School", "N/A", "907 Milltown Road", "Wilmington", "DE", "19808", "302.998.5525", null);
        insertSchool("New Castle", null, "St. Mary Magdalen School", "N/A", "9 Sharpley Road", "Wilmington", "DE", "19803", "302.656.2745", null);
        insertSchool("New Castle", null, "St. Matthews School", "N/A", "1 Fallon Avenue", "Wilmington", "DE", "19804", "302.633.5860", null);
        insertSchool("New Castle", null, "St. Paul Elementary School", "N/A", "312 N Van Buren Street", "Wilmington", "DE", "19805", "302.656.1372", null);
        insertSchool("New Castle", null, "St. Peter Cathedral School", "N/A", "310 West 6th Street", "Wilmington", "DE", "19801", "302.656.5234", null);
        insertSchool("New Castle", null, "St. Peter the Apostle", "N/A", "515 Harmony Street", "New Castle", "DE", "19720", "302.328.1191", null);
        insertSchool("New Castle", null, "St. Thomas the Apostle School", "N/A", "201 Bayard Avenue", "Wilmington", "DE", "19805", "302.658.5131", null);
        insertSchool("New Castle", null, "Ursuline Academy", "N/A", "1106 Pennsylvania Avenue", "Wilmington", "DE", "19808", "302.658.7158", null);
        insertSchool("New Castle", null, "Christ the Teacher Catholic School", "N/A", "2451 Frazer Road", "Newark", "DE", "19702", "302.838.8850", null);
        insertSchool("New Castle", null, "Wilmington Christian School", "N/A", "825 Loveville Road", "Hockessin", "DE", "19707", "302.239.2121", null);
        insertSchool("New Castle", null, "The Hockessin Montessori School", "N/A", "1000 Old Lancaster Pike", "Hockessin", "DE", "19707", "302.234.1240", null);
        insertSchool("New Castle", null, "St. Anne's Episcopal School", "N/A", "211 Silver Lake Road", "Middletown", "DE", "19709", "302.378.3179", null);
        insertSchool("New Castle", null, "Red Lion Christian Academy", "N/A", "1390 Red Lion Road", "Bear", "DE", "19701", "302.834.2526", null);
        insertSchool("New Castle", null, "Caravel Academy", "N/A", "2801 Del Laws Road", "Bear", "DE", "19701", "302.834.8938", null);
        insertSchool("Sussex", null, "Eagles Nest Christian School", "N/A", "26633 Zion Church Road", "Milton", "DE", "19968", "302.684.3149", null);
        insertSchool("New Castle", null, "Greenwood Mennonite School", "N/A", "12525 Shawnee Road", "Greenwood", "DE", "19950", "302.349.5296", null);
        insertSchool("New Castle", null, "Sanford School", "N/A", "6900 Lancaster Pike", "Hockessin", "DE", "19707", "302.235.6500", null);
        insertSchool("New Castle", null, "The College School", "N/A", "459 Wyoming Road", "Newark", "DE", "19716", "302.831.0222", null);
        insertSchool("New Castle", null, "Wilmington Friends School", "N/A", "101 School Road", "Wilmington", "DE", "19803", "302.576.2900", null);
        insertSchool("New Castle", null, "The Independence School", "N/A", "1300 Papermill Road", "Newark", "DE", "19711", "302.239.0330", null);
        insertSchool("New Castle", null, "Centerville Layton School", "N/A", "6201 Kennett Pike", "Centerville", "DE", "19807", "302.571.0230", null);
        insertSchool("New Castle", null, "Saint Edmond's Academy", "N/A", "2120 Veale Road", "Wilmington", "DE", "19810", "302.475.5370", null);
        insertSchool("Sussex", null, "The Jefferson School", "N/A", "22051 Wilson Road", "Georgetown", "DE", "19947", "302.856.3300", null);

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
        insertUser("AjVW337bQJs=","jtZ3UlKhhAuyKpo98aGUfTiPy74=","mcherm+BankAdmin@gmail.com","BankAdmin","Chermside","BA",aBankId,"610-810-1806",0);
        insertUser("AjVW337bQJs=","jtZ3UlKhhAuyKpo98aGUfTiPy74=","mcherm+Volunteer@gmail.com","Volunteer","Chermside","V",aBankId,"610-810-1806",0);
        insertUser("AjVW337bQJs=","jtZ3UlKhhAuyKpo98aGUfTiPy74=","mcherm+Teacher@gmail.com","Teacher","Chermside","T",aSchoolId,"610-810-1806",0);
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
                                "CourseCreationOpen=No",
                                "CurrentYear=2026",
                                "EventDatesOnHomepage=April 27 - May 1, 2026",
                                "ShowDocuments=Yes",
                                "VolunteerSignupsOpen=No",
                                "SiteEmail=teach2save@udel.edu"
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

    /**
     * Insert the single record that has the starting values for allowed times.
     */
    private void insertAllowedGrades() {
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(new ItemBuilder("allowedGrades")
                        .withStrings(
                                allowed_grade_values_with_sort,
                                "0|3rd Grade",
                                "1|4th Grade"
                        )
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    /**
     * Insert the single record that has the starting values for allowed times.
     */
    private void insertAllowedDeliveryMethods() {
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(new ItemBuilder("allowedDeliveryMethods")
                        .withStrings(
                                allowed_delivery_method_values_with_sort,
                                "0|In-Person",
                                "1|Virtual"
                        )
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    /**
     * Insert the single record that has the starting value for documents.
     * <p>
     *     WARNING FOR FUTURE MAINTAINERS OF THIS CODE: DynmoDB cannot support
     *     a StringSet with zero strings in it. So don't populate this with
     *     nothing!!
     * </p>
     */
    private void insertDocuments() {
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(new ItemBuilder("documents")
                        .withStrings(
                                documents_values,
                                "T|F|F|TCTSD FAQs.pdf",
                                "F|T|T|TCTSD Lesson Plan_Handouts 2024_IN-PERSON_REV.pdf",
                                "F|T|T|TCTSD Lesson Plan_Handouts 2024_VIRTUAL_REV.pdf",
                                "F|T|T|TCTSD Logo & Slogan Contest 2025.pdf",
                                "F|T|T|TCTSD Volunteer Certificate 2024.pdf"
                        )
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    /** Insert a bank into the database. Returns the unique ID it was assigned. */
    private String insertBank(String bankName, String bankSpecificDataLabel)
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

    /** Insert a school into the database. Returns the unique ID it was assigned. */
    private String insertSchool(String schoolCounty, String schoolDistrict, String schoolName, String schoolSlc,
                               String schoolAddr1, String schoolCity, String schoolState, String schoolZip,
                               String schoolPhone, String schoolLmiEligible)
    {
        final String uniqueId = dynamoDBHelper.createUniqueId();
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(tableName)
                .item(new ItemBuilder("school", school_id, uniqueId)
                        .withString(school_name, schoolName)
                        .withString(school_addr1, schoolAddr1)
                        .withString(school_city, schoolCity)
                        .withString(school_state, schoolState)
                        .withString(school_zip, schoolZip)
                        .withString(school_county, schoolCounty)
                        .withString(school_district, schoolDistrict)
                        .withString(school_phone, schoolPhone)
                        .withString(school_lmi_eligible, schoolLmiEligible)
                        .withString(school_slc, schoolSlc)
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
