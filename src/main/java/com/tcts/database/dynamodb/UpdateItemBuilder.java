package com.tcts.database.dynamodb;

import com.tcts.database.SingleTableDbField;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tcts.database.SingleTableDbField.table_key;

/**
 * This is used to construct an UpdateItemRequest for performing an update to an
 * existing record. The syntax for doing so in AWS's second Java API is so much
 * of a mess, it even involves its own language (which is badly designed and poorly
 * documented)! Therefore, this looks a lot like ItemBuilder, but it returns the
 * entire UpdateItemRequest.
 */
public class UpdateItemBuilder {
    private final String tableName;
    private final String itemKey;
    private final List<FieldToSet> fieldsToSet;

    /**
     * Constructor.
     *
     * @param tableName the name of the table
     * @param itemKey the primary key for the item being modified (this cannot be changed)
     */
    public UpdateItemBuilder(String tableName, String itemKey) {
        this.tableName = tableName;
        this.itemKey = itemKey;
        this.fieldsToSet = new ArrayList<>();
    }

    /**
     * Adds another field of type String.
     */
    public UpdateItemBuilder withString(SingleTableDbField field, String value) {
        fieldsToSet.add(new StringFieldToSet(fieldsToSet.size(), field, value == null ? "" : value));
        return this;
    }

    /**
     * Adds another field of type int.
     */
    public UpdateItemBuilder withInt(SingleTableDbField field, int value) {
        fieldsToSet.add(new IntFieldToSet(fieldsToSet.size(), field, value));
        return this;
    }

    /**
     * Adds another field of type set of strings.
     */
    public UpdateItemBuilder withStrings(SingleTableDbField field, String[] value) {
        fieldsToSet.add(new SetOfStringsFieldToSet(fieldsToSet.size(), field, value));
        return this;
    }

    /** Call this after setting all the fields to get the request object. */
    public UpdateItemRequest build() {
        return UpdateItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(table_key.name(), AttributeValue.builder().s(itemKey).build()))
                .updateExpression(
                        "SET " + fieldsToSet.stream()
                        .map(FieldToSet::getSetSubexpression)
                        .collect(Collectors.joining(", ")))
                .expressionAttributeValues(this.fieldsToSet.stream()
                        .collect(Collectors.toMap(FieldToSet::getExpressionName, FieldToSet::getAttributeValue)))
                .build();
    }

    // ========== Inner Classes ==========

    /** Internal data - parent of any field. */
    private static abstract class FieldToSet {
        protected final int index;
        protected final SingleTableDbField field;

        /** Constructor. */
        public FieldToSet(int index, SingleTableDbField field) {
            this.index = index;
            this.field = field;
        }

        /** How we'll name the field in the expression. */
        public String getExpressionName() {
            return ":field" + index;
        }

        /** Return the text we put into the updateExpression for this field. */
        public String getSetSubexpression() {
            return this.field.name() + " = " + getExpressionName();
        }

        public abstract AttributeValue getAttributeValue();
    }

    /** For fields that are strings. */
    private static class StringFieldToSet extends FieldToSet {
        private final String value;

        /** Constructor. */
        public StringFieldToSet(int index, SingleTableDbField field, String value) {
            super(index, field);
            this.value = value;
        }

        @Override
        public AttributeValue getAttributeValue() {
            return AttributeValue.builder().s(value).build();
        }
    }

    /** For fields that are ints. */
    private static class IntFieldToSet extends FieldToSet {
        private final int value;

        /** Constructor. */
        public IntFieldToSet(int index, SingleTableDbField field, int value) {
            super(index, field);
            this.value = value;
        }

        @Override
        public AttributeValue getAttributeValue() {
            return AttributeValue.builder().n(String.valueOf(value)).build();
        }
    }

    /** For fields that are a set of strings. */
    private static class SetOfStringsFieldToSet extends FieldToSet {
        private final String[] value;

        /** Constructor. */
        public SetOfStringsFieldToSet(int index, SingleTableDbField field, String[] value) {
            super(index, field);
            this.value = value;
        }

        @Override
        public AttributeValue getAttributeValue() {
            return AttributeValue.builder().ss(value).build();
        }
    }
}
