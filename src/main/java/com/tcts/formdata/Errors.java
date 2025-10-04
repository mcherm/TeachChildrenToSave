package com.tcts.formdata;

import java.util.ArrayList;
import java.util.List;

/**
 * This represents a collection of error messages to be displayed -- possibly
 * NO errors, in cases where nothing is wrong.
 */
public class Errors {
    private List<String> errorMessages = new ArrayList<String>();

    /**
     * Constructor that creates an empty Errors.
     */
    public Errors() {
    }

    /**
     * Constructor that creates an Errors with a single error message.
     */
    public Errors(String errorMessage) {
        errorMessages.add(errorMessage);
    }

    /**
     * Add a new error.
     * @param errorMessage text to display to the user
     */
    public void addError(String errorMessage) {
        this.errorMessages.add(errorMessage);
    }

    /**
     * Returns true if there are errors to display, false if everything is fine.
     */
    public boolean hasErrors() {
        return errorMessages.size() > 0;
    }

    /**
     * Returns the list of error messages to display.
     */
    public List<String> getErrorMessages() {
        return errorMessages;
    }

    @Override
    public String toString() {
        return "Errors(" + (hasErrors() ? String.join("; ", errorMessages) : "NoErrors") + ")";
    }
}
