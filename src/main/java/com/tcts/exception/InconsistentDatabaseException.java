package com.tcts.exception;

/**
 * Thrown when the database contains a value that it is not permitted to have.
 */
public class InconsistentDatabaseException extends RuntimeException {
    /** Constructor. */
    public InconsistentDatabaseException(String msg) {
        super(msg);
    }
}
