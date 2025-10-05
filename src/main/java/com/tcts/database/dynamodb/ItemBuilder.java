package com.tcts.database.dynamodb;

import com.tcts.database.DatabaseField;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This provides a "fluent interface" for specifying the primary key and other fields
 * of an object to be created. It is very much like the fluent interface of Amazon's
 * Item class except that it also supports the DatabaseField class we use and has
 * cleaner handling of null and empty-string values (neither will be inserted, which
 * would be an error if you tried it).
 * <p>
 * Sample use:
 * <code><pre>
 *     new ItemBuilder("school", school_id, DynamoDBDatabase.createUniqueId())
 *         .withString(school_name, schoolName)
 *         .withString(school_addr1, schoolAddr1)
 *         .withString(school_city, schoolCity)
 *         .withString(school_state, schoolState)
 *         .withString(school_zip, schoolZip)
 *         .withInt(school_number, schoolNumber)
 *         .build()
 * </pre></code>
 */
public class ItemBuilder {
    private static abstract class NonEmptyField {
        final DatabaseField field;

        NonEmptyField(DatabaseField field) {
            this.field = field;
        }

        abstract void addFieldToMap(Map<String,AttributeValue> map);
    }

    private static class NonEmptyStringField extends NonEmptyField {
        final String value;

        NonEmptyStringField(DatabaseField field, String value) {
            super(field);
            this.value = value;
        }

        @Override
        void addFieldToMap(Map<String,AttributeValue> map) {
            map.put(field.name(), AttributeValue.builder().s(value).build());
        }
    }

    private static class NonEmptyIntField extends NonEmptyField {
        final int value;

        NonEmptyIntField(DatabaseField field, int value) {
            super(field);
            this.value = value;
        }

        @Override
        void addFieldToMap(Map<String,AttributeValue> map) {
            map.put(field.name(), AttributeValue.builder().n(String.valueOf(value)).build());
        }
    }

    private static class NonEmptySetOfStringsField extends NonEmptyField {
        final String[] value;

        NonEmptySetOfStringsField(DatabaseField field, String[] value) {
            super(field);
            this.value = value;
        }

        @Override
        void addFieldToMap(Map<String, AttributeValue> map) {
            map.put(field.name(), AttributeValue.builder().ss(value).build());
        }
    }

    // ======== instance variables ========
    private final String tableKeyValue;
    private final List<NonEmptyField> nonEmptyFields;

    /**
     * Constructor for single-entry records like allowedDates, allowedTimes, documents, or siteSettings.
     *
     * @param key the key to use (eg: "allowedDates")
     */
    public ItemBuilder(String key) {
        this.tableKeyValue = key;
        this.nonEmptyFields = new ArrayList<>();
    }

    /**
     * Constructor for records that have a prefix key, like bank (which uses "bank:&lt;bank_id>") or
     * event (which uses "event:&lt;event_id").
     *
     * @param keyPrefix the prefix (eg: "bank" or "event")
     * @param primaryKeyField the field which contains the key (eg: bank_id or event_id)
     * @param primaryKeyValue the value of the field containing the key (eg: "9384732")
     */
    public ItemBuilder(String keyPrefix, DatabaseField primaryKeyField, String primaryKeyValue) {
        if (primaryKeyValue == null || primaryKeyValue.isEmpty()) {
            throw new IllegalArgumentException("primaryKeyValue cannot be null or empty");
        }
        this.tableKeyValue = keyPrefix + ":" + primaryKeyValue;
        this.nonEmptyFields = new ArrayList<>();
        withString(primaryKeyField, primaryKeyValue);
    }

    /**
     * Adds another field of type String.
     */
    public ItemBuilder withString(DatabaseField field, String value) {
        if (value != null && !value.isEmpty()) {
            nonEmptyFields.add(new NonEmptyStringField(field, value));
        }
        return this;
    }

    /**
     * Adds another field of type int.
     */
    public ItemBuilder withInt(DatabaseField field, int value) {
        nonEmptyFields.add(new NonEmptyIntField(field, value));
        return this;
    }

    /**
     * Adds another field of type set-of-strings.
     */
    public ItemBuilder withStrings(DatabaseField field, String... values) {
        nonEmptyFields.add(new NonEmptySetOfStringsField(field, values));
        return this;
    }

    public Map<String, AttributeValue> build() {
        final Map<String, AttributeValue> result = new HashMap<>();
        result.put(DatabaseField.table_key.name(), AttributeValue.builder().s(tableKeyValue).build());
        for (NonEmptyField nonEmptyField : this.nonEmptyFields) {
            nonEmptyField.addFieldToMap(result);
        }
        return result;
    }
}
