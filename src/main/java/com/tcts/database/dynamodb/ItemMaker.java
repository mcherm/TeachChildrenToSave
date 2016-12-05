package com.tcts.database.dynamodb;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.tcts.database.DatabaseField;


/**
 * This provides a "fluent interface" for specifying the primary key and other fields
 * of an object to be created. It is very much like the fluent interface of Amazon's
 * Item class except that it also supports the DatabaseField class we use and has
 * cleaner handling of null and empty-string values (neither will be inserted, which
 * would be an error if you tried it).
 * <p>
 * Sample use:
 * <code><pre>
 *     new ItemMaker(school_id, DynamoDBDatabase.createUniqueId())
 *         .withString(school_name, schoolName)
 *         .withString(school_addr1, schoolAddr1)
 *         .withString(school_city, schoolCity)
 *         .withString(school_state, schoolState)
 *         .withString(school_zip, schoolZip)
 *         .withInt(school_number, schoolNumber)
 * </pre></code>
 */
public class ItemMaker {
    private static abstract class NonEmptyFields {
        final DatabaseField field;
        final NonEmptyFields restOfFields;

        NonEmptyFields(DatabaseField field, NonEmptyFields restOfFields) {
            this.field = field;
            this.restOfFields = restOfFields;
        }

        abstract Item addFieldToItem(Item item);
    }

    private static class NonEmptyStringFields extends NonEmptyFields {
        final String value;

        NonEmptyStringFields(DatabaseField field, String value, NonEmptyFields restOfFields) {
            super(field, restOfFields);
            this.value = value;
        }

        @Override
        Item addFieldToItem(Item item) {
            return item.withString(field.name(), value);
        }
    }

    private static class NonEmptyIntFields extends NonEmptyFields {
        final int value;

        NonEmptyIntFields(DatabaseField field, int value, NonEmptyFields restOfFields) {
            super(field, restOfFields);
            this.value = value;
        }

        @Override
        Item addFieldToItem(Item item) {
            return item.withInt(field.name(), value);
        }
    }

    private final DatabaseField primaryKeyField;
    private final String primaryKeyValue;
    private final NonEmptyFields nonEmptyFields;

    public ItemMaker(DatabaseField primaryKeyField, String primaryKeyValue) {
        this.primaryKeyField = primaryKeyField;
        this.primaryKeyValue = primaryKeyValue;
        this.nonEmptyFields = null;
    }

    private ItemMaker(DatabaseField primaryKeyField, String primaryKeyValue, NonEmptyFields nonEmptyFields) {
        this.primaryKeyField = primaryKeyField;
        this.primaryKeyValue = primaryKeyValue;
        this.nonEmptyFields = nonEmptyFields;
    }

    public ItemMaker withString(DatabaseField field, String value) {
        return new ItemMaker(this.primaryKeyField, this.primaryKeyValue,
                (value == null || value.isEmpty())
                        ? this.nonEmptyFields
                        : new NonEmptyStringFields(field, value, this.nonEmptyFields));
    }

    public ItemMaker withInt(DatabaseField field, int value) {
        return new ItemMaker(this.primaryKeyField, this.primaryKeyValue,
                new NonEmptyIntFields(field, value, this.nonEmptyFields));
    }

    public Item getItem() {
        Item item = new Item().withPrimaryKey(primaryKeyField.name(), primaryKeyValue);
        NonEmptyFields fieldsLeft = nonEmptyFields;
        while (fieldsLeft != null) {
            item = fieldsLeft.addFieldToItem(item);
            fieldsLeft = fieldsLeft.restOfFields;
        }
        return item;
    }
}
