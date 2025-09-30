package com.tcts.database;

import com.tcts.common.Configuration;
import com.tcts.database.dynamodb.ItemBuilder;
import com.tcts.datamodel.Document;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.function.Function;


/**
 * A "script" to migrate from the older, multi-table DynamoDB structure to the Single Table structure.
 */
public class DynamoDBMigrate {


    public static void main(String[] args) {
        System.out.println("Starting...");
        try {
            final DynamoDBMigrate instance = new DynamoDBMigrate();
            instance.migrate();
        } catch(Exception err) {
            err.printStackTrace();
        }

        System.out.println("Done.");
    }

    private final DynamoDbClient dynamoDbClient;
    private final String multiTablePrefix;
    private final String singleTableName;

    public DynamoDBMigrate() {
        final Configuration configuration = new Configuration();
        dynamoDbClient = SingleTableDynamoDbDatabase.connectToDB(configuration);
        multiTablePrefix = "TCTS.prod."; // NOTE: The source is hard-coded here, while SOME destination come from config
        final String site = "DE"; // NOTE: The destination site is hard-coded here
        final String environment = configuration.getProperty("dynamoDB.environment", "dev");
        singleTableName = "TCTS." + site + "." + environment;
    }

    public void migrate() {
        System.out.println("Reinitializing database " + singleTableName);
        SingleTableDynamoDBSetup.reinitializeDatabase(dynamoDbClient, singleTableName);
        migrateAllowedDates();
        migrateAllowedTimes();
        migrateSiteSettings();
        migrateDocuments();
        migrateSchool();
        migrateBank();
        migrateUser();
        migrateEvent();
    }

    private void migrateAllowedDates() {
        // --- read ---
        final ScanRequest scanRequest = ScanRequest.builder()
                .tableName(multiTablePrefix + "AllowedDates")
                .build();
        final ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        // --- convert ---
        final String[] allowedDates = scanResponse.items().stream()
                .map(x -> x.get(DatabaseField.event_date_allowed.name()).s())
                .toArray(String[]::new);
        // --- write ---
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(singleTableName)
                .item(new ItemBuilder("allowedDates")
                        .withStrings(SingleTableDbField.allowed_date_values, allowedDates)
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    private void migrateAllowedTimes() {
        // --- read ---
        final ScanRequest scanRequest = ScanRequest.builder()
                .tableName(multiTablePrefix + "AllowedTimes")
                .build();
        final ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        // --- convert ---
        final String[] newAllowedTimesWithSort = scanResponse.items().stream()
                .map(x -> {
                    final String time = x.get(DatabaseField.event_time_allowed.name()).s();
                    final String index = x.get(DatabaseField.event_time_sort_key.name()).n();
                    return index + "|" + time;
                })
                .toArray(String[]::new);
        // --- write ---
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(singleTableName)
                .item(new ItemBuilder("allowedTimes")
                        .withStrings(SingleTableDbField.allowed_time_values_with_sort, newAllowedTimesWithSort)
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    private void migrateSiteSettings() {
        // --- read ---
        final ScanRequest scanRequest = ScanRequest.builder()
                .tableName(multiTablePrefix + "SiteSettings")
                .build();
        final ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        // --- convert ---
        final String[] siteSettingKeyValues = scanResponse.items().stream()
                .map(x -> {
                    final String name = x.get(DatabaseField.site_setting_name.name()).s();
                    final String value = x.get(DatabaseField.site_setting_value.name()).s();
                    return name + "=" + value;
                })
                .toArray(String[]::new);
        // --- write ---
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(singleTableName)
                .item(new ItemBuilder("siteSettings")
                        .withStrings(SingleTableDbField.site_setting_entries, siteSettingKeyValues)
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    private void migrateDocuments() {
        // --- read ---
        final ScanRequest scanRequest = ScanRequest.builder()
                .tableName(multiTablePrefix + "Documents")
                .build();
        final ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        // --- convert ---
        final String[] documentsValues = scanResponse.items().stream()
                .map(x -> {
                    String name = x.get(DatabaseField.document_name.name()).s();
                    boolean showToTeacher = x.get(DatabaseField.document_show_to_teacher.name()).bool();
                    boolean showToVolunteer = x.get(DatabaseField.document_show_to_volunteer.name()).bool();
                    boolean showToBankAdmin = x.get(DatabaseField.document_show_to_bank_admin.name()).bool();
                    return new Document(name, showToTeacher, showToVolunteer, showToBankAdmin);
                })
                .map(doc ->
                        (doc.getShowToTeacher() ? "T" : "F") + "|" +
                        (doc.getShowToVolunteer() ? "T" : "F") + "|" +
                        (doc.getShowToBankAdmin() ? "T" : "F") + "|" +
                        doc.getName()
                )
                .toArray(String[]::new);
        // --- write ---
        final PutItemRequest putItemRequest = PutItemRequest.builder()
                .tableName(singleTableName)
                .item(new ItemBuilder("documents")
                        .withStrings(SingleTableDbField.documents_values, documentsValues)
                        .build())
                .build();
        dynamoDbClient.putItem(putItemRequest);
    }

    private void migrateSchool() {
        // --- read ---
        final ScanRequest scanRequest = ScanRequest.builder()
                .tableName(multiTablePrefix + "School")
                .build();
        final ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        // --- convert ---
        scanResponse.items().stream()
                .map(SingleTableDynamoDbDatabase::createSchoolFromDynamoDbItem)
                .forEach(school -> {
                    // --- write ---
                    final PutItemRequest putItemRequest = PutItemRequest.builder()
                            .tableName(singleTableName)
                            .item(new ItemBuilder("school", SingleTableDbField.school_id, school.getSchoolId())
                                    .withString(SingleTableDbField.school_name, school.getName())
                                    .withString(SingleTableDbField.school_addr1, school.getAddressLine1())
                                    .withString(SingleTableDbField.school_city, school.getCity())
                                    .withString(SingleTableDbField.school_state, school.getState())
                                    .withString(SingleTableDbField.school_zip, school.getZip())
                                    .withString(SingleTableDbField.school_county, school.getCounty())
                                    .withString(SingleTableDbField.school_district, school.getSchoolDistrict())
                                    .withString(SingleTableDbField.school_phone, school.getPhone())
                                    .withString(
                                            SingleTableDbField.school_lmi_eligible,
                                            school.getLmiEligible() == null
                                                  ? ""
                                                  : school.getLmiEligible().toPlainString())
                                    .withString(SingleTableDbField.school_slc, school.getSLC())
                                    .build())
                            .conditionExpression("attribute_not_exists(" + SingleTableDbField.table_key.name() + ")") // verify it is unique
                            .build();
                    dynamoDbClient.putItem(putItemRequest);
                });
    }

    private void migrateBank() {
        // --- read ---
        final ScanRequest scanRequest = ScanRequest.builder()
                .tableName(multiTablePrefix + "Bank")
                .build();
        final ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        // --- convert ---
        scanResponse.items().stream()
                .map(SingleTableDynamoDbDatabase::createBankFromDynamoDbItem)
                .forEach(bank -> {
                    // --- write ---
                    final PutItemRequest putItemRequest = PutItemRequest.builder()
                            .tableName(singleTableName)
                            .item(new ItemBuilder("bank", SingleTableDbField.bank_id, bank.getBankId())
                                    .withString(SingleTableDbField.bank_name, bank.getBankName())
                                    .withString(
                                            SingleTableDbField.min_lmi_for_cra,
                                            bank.getMinLMIForCRA() == null
                                                ? ""
                                                : bank.getMinLMIForCRA().toPlainString()
                                    )
                                    .withString(SingleTableDbField.bank_specific_data_label, bank.getBankSpecificDataLabel())
                                    .build())
                            .conditionExpression("attribute_not_exists(" + SingleTableDbField.table_key.name() + ")") // verify it is unique
                            .build();
                    dynamoDbClient.putItem(putItemRequest);
                });
    }

    private void migrateUser() {
        // --- read ---
        final ScanRequest scanRequest = ScanRequest.builder()
                .tableName(multiTablePrefix + "User")
                .build();
        final ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        // --- convert ---
        scanResponse.items().stream()
                .forEach(item -> {
                    final Function<SingleTableDbField,String> getStringField = (SingleTableDbField field) -> item.get(field.name()) == null
                            ? null
                            : item.get(field.name()).s();
                    // --- write ---
                    ItemBuilder itemBuilder = new ItemBuilder("user", SingleTableDbField.user_id, getStringField.apply(SingleTableDbField.user_id))
                            .withString(
                                    SingleTableDbField.user_type,
                                    getStringField.apply(SingleTableDbField.user_type))
                            .withString(
                                    SingleTableDbField.user_email,
                                    getStringField.apply(SingleTableDbField.user_email))
                            .withString(
                                    SingleTableDbField.user_original_email,
                                    getStringField.apply(SingleTableDbField.user_original_email))
                            .withString(
                                    SingleTableDbField.user_first_name,
                                    getStringField.apply(SingleTableDbField.user_first_name))
                            .withString(
                                    SingleTableDbField.user_last_name,
                                    getStringField.apply(SingleTableDbField.user_last_name))
                            .withString(
                                    SingleTableDbField.user_phone_number,
                                    getStringField.apply(SingleTableDbField.user_phone_number))
                            .withString(
                                    SingleTableDbField.user_bank_specific_data,
                                    getStringField.apply(SingleTableDbField.user_bank_specific_data))
                            .withString(
                                    SingleTableDbField.user_organization_id,
                                    getStringField.apply(SingleTableDbField.user_organization_id))
                            .withString(
                                    SingleTableDbField.user_hashed_password,
                                    getStringField.apply(SingleTableDbField.user_hashed_password))
                            .withString(
                                    SingleTableDbField.user_password_salt,
                                    getStringField.apply(SingleTableDbField.user_password_salt))
                            .withString(
                                    SingleTableDbField.user_reset_password_token,
                                    getStringField.apply(SingleTableDbField.user_reset_password_token))
                            .withString(
                                    SingleTableDbField.user_street_address,
                                    getStringField.apply(SingleTableDbField.user_street_address))
                            .withString(
                                    SingleTableDbField.user_suite_or_floor_number,
                                    getStringField.apply(SingleTableDbField.user_suite_or_floor_number))
                            .withString(
                                    SingleTableDbField.user_city,
                                    getStringField.apply(SingleTableDbField.user_city))
                            .withString(
                                    SingleTableDbField.user_state,
                                    getStringField.apply(SingleTableDbField.user_state))
                            .withString(
                                    SingleTableDbField.user_zip,
                                    getStringField.apply(SingleTableDbField.user_zip));
                    // withString() supports null, but withInt() doesn't, so we check for that field before using it
                    if (item.containsKey(SingleTableDbField.user_approval_status.name())) {
                        itemBuilder = itemBuilder.withInt(
                                SingleTableDbField.user_approval_status,
                                Integer.parseInt(item.get(SingleTableDbField.user_approval_status.name()).n()));
                    }

                    final PutItemRequest putItemRequest = PutItemRequest.builder()
                            .tableName(singleTableName)
                            .item(itemBuilder.build())
                            .conditionExpression("attribute_not_exists(" + SingleTableDbField.table_key.name() + ")") // verify it is unique
                            .build();
                    dynamoDbClient.putItem(putItemRequest);
                });
    }

    private void migrateEvent() {
        // --- read ---
        final ScanRequest scanRequest = ScanRequest.builder()
                .tableName(multiTablePrefix + "Event")
                .build();
        final ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        // --- convert ---
        scanResponse.items().stream()
                .map(SingleTableDynamoDbDatabase::createEventFromDynamoDbItem)
                .forEach(event -> {
                    // --- write ---
                    final PutItemRequest putItemRequest = PutItemRequest.builder()
                            .tableName(singleTableName)
                            .item(new ItemBuilder("event", SingleTableDbField.event_id, event.getEventId())
                                    .withString(SingleTableDbField.event_teacher_id, event.getTeacherId())
                                    .withString(SingleTableDbField.event_date, event.getEventDate().getParseable())
                                    .withString(SingleTableDbField.event_time, event.getEventTime())
                                    .withString(SingleTableDbField.event_grade, patchGrade(event.getGrade()))
                                    .withString(SingleTableDbField.event_delivery_method, patchDeliveryMethod(event.getDeliveryMethod()))
                                    .withInt(SingleTableDbField.event_number_students, event.getNumberStudents())
                                    .withString(SingleTableDbField.event_notes, event.getNotes())
                                    .withString(SingleTableDbField.event_volunteer_id, event.getVolunteerId())
                                    .build())
                            .conditionExpression("attribute_not_exists(" + SingleTableDbField.table_key.name() + ")") // verify it is unique
                            .build();
                    dynamoDbClient.putItem(putItemRequest);
                });
    }

    /**
     * We changed the encoding for Grade between old and new DB so this converts it if we encounter
     * the old format.
     */
    private static String patchGrade(String grade) {
        if (grade == null) {
            return null;
}
        return switch (grade) {
            case "3" -> "3rd Grade";
            case "4" -> "4th Grade";
            default -> grade;
        };
    }

    /**
     * We changed the encoding for DeliveryMethod between old and new DB so this converts it if we encounter
     * the old format.
     */
    private static String patchDeliveryMethod(String deliveryMethod) {
        if (deliveryMethod == null) {
            return null;
        }
        return switch (deliveryMethod) {
            case "P" -> "In-Person";
            case "V" -> "Virtual";
            default -> deliveryMethod;
        };
    }

}
