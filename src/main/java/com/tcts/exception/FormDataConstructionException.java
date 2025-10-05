package com.tcts.exception;

import com.tcts.formdata.Errors;

/**
 * This will be thrown by the "strings" version of a FormData object if the actual FormData
 * object cannot be constructed from it. It is a checked exception that has a method getErrors()
 * that returns an Errors object containing error messages for the user, which will always have
 * at least one error message in it.
 */
public class FormDataConstructionException extends Exception {
    private Errors errors;

    /**
     * Constructor.
     */
    public FormDataConstructionException(Errors errors) {
        assert errors != null && errors.hasErrors();
        this.errors = errors;
    }

    /**
     * Retrieve the Errors from constructing this FormData.
     */
    public Errors getErrors() {
        return errors;
    }
}
