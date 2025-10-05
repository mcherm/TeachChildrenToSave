package com.tcts.formdata;

import com.tcts.database.DatabaseField;
import com.tcts.exception.InvalidParameterFromGUIException;

import java.util.List;

/**
 * Fields needed to create an allowed Time/Date/DeliveryMethod.
 */
public class AddAllowedValueFormData extends ValidatedFormData<RuntimeException> {
    /** One of "Time", "Date", "Delivery Method". */
    public String valueType;

    public String allowedValue;

    /**
     * Gives a particular value this should be inserted before. An empty
     * string ("") means add it to the end of the list of values.
     */
    public String valueToInsertBefore;

    @Override
    protected void validationRules(Errors errors) {
        final DatabaseField dbField = switch (valueType) {
            case "Time" -> DatabaseField.event_time;
            case "Grade" -> DatabaseField.event_grade;
            case "Delivery Method" -> DatabaseField.event_delivery_method;
            default -> throw new InvalidParameterFromGUIException("Unsupported field type, '" + valueType + "'.");
        };
        List<String> allowedValues = switch(valueType) {
            case "Time" -> database.getAllowedTimes();
            case "Grade" -> database.getAllowedGrades();
            case "Delivery Method" -> database.getAllowedDeliveryMethods();
            default -> throw new InvalidParameterFromGUIException("Unsupported field type, '" + valueType + "'.");
        };
        if (allowedValue == null || allowedValue.isEmpty()) {
            errors.addError("You must enter a value.");
        }
        validateLength(allowedValue, dbField, errors);
        if (!"".equals(valueToInsertBefore) && !allowedValues.contains(valueToInsertBefore)) {
            errors.addError("You must specify where in the list to insert the new " + valueType + ".");
        }
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getAllowedValue() {
        return allowedValue;
    }

    public void setAllowedValue(String allowedValue) {
        this.allowedValue = allowedValue;
    }

    public String getValueToInsertBefore() {
        return valueToInsertBefore;
    }

    public void setValueToInsertBefore(String valueToInsertBefore) {
        this.valueToInsertBefore = valueToInsertBefore;
    }
}
