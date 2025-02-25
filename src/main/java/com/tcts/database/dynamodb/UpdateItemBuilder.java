package com.tcts.database.dynamodb;

import com.tcts.database.SingleTableDbField;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ReturnValuesOnConditionCheckFailure;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tcts.database.SingleTableDbField.event_volunteer_id;
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
    private final List<Condition> conditions;

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
        this.conditions = new ArrayList<>();
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

    /** Add a condition where some field is equal to a value. */
    public UpdateItemBuilder withStringFieldEqualsCondition(SingleTableDbField field, String value) {
        conditions.add(new StringFieldEqualsCondition(conditions.size(), field, value));
        return this;
    }

    /** Add a condition where some field is not equal to a value. */
    public UpdateItemBuilder withStringFieldNotEqualsCondition(SingleTableDbField field, String value) {
        conditions.add(new StringFieldNotEqualsCondition(conditions.size(), field, value));
        return this;
    }

    /** Call this after setting all the fields to get the request object. */
    public UpdateItemRequest build() {
        final Map<String,AttributeValue> attributeValues = new HashMap<>();
        fieldsToSet.forEach(x -> attributeValues.put(x.getExpressionName(), x.getAttributeValue()));

        final UpdateItemRequest.Builder builder = UpdateItemRequest.builder()
                .tableName(tableName)
                .key(Map.of(table_key.name(), AttributeValue.builder().s(itemKey).build()))
                .updateExpression(
                        "SET " + fieldsToSet.stream()
                        .map(FieldToSet::getSetSubexpression)
                        .collect(Collectors.joining(", ")));
        if (!conditions.isEmpty()) {
            builder.conditionExpression(conditions.stream()
                    .map(Condition::getConditionExpression)
                    .collect(Collectors.joining(" AND ")));
            conditions.forEach(cond -> attributeValues.put(cond.getExpressionName(), cond.getAttributeValue()));
        }
        builder.expressionAttributeValues(attributeValues);
        return builder.build();
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

    /** Internal data - parent for any condition. */
    private static abstract class Condition {
        protected final int index;

        /** Constructor. */
        public Condition(int index) {
            this.index = index;
        }

        /** How we'll name the field in the expression. */
        public String getExpressionName() {
            return ":cond" + index;
        }

        /** Returns the value in the expression. */
        public abstract AttributeValue getAttributeValue();

        /** Returns the condition expression string. */
        public abstract String getConditionExpression();
    }

    /** For conditions where field = value. */
    private static class StringFieldEqualsCondition extends Condition {
        protected final SingleTableDbField field;
        protected final String value;

        /** Constructor. */
        public StringFieldEqualsCondition(int index, SingleTableDbField field, String value) {
            super(index);
            this.field = field;
            this.value = value;
        }

        @Override
        public AttributeValue getAttributeValue() {
            return AttributeValue.builder().s(value).build();
        }

        @Override
        public String getConditionExpression() {
            return field.name() + " = " + getExpressionName();
        }
    }

    /** For conditions where field != value. */
    private static class StringFieldNotEqualsCondition extends Condition {
        protected final SingleTableDbField field;
        protected final String value;

        /** Constructor. */
        public StringFieldNotEqualsCondition(int index, SingleTableDbField field, String value) {
            super(index);
            this.field = field;
            this.value = value;
        }

        @Override
        public AttributeValue getAttributeValue() {
            return AttributeValue.builder().s(value).build();
        }

        @Override
        public String getConditionExpression() {
            return field.name() + " <> " + getExpressionName();
        }
    }
}
