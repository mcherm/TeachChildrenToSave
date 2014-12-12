package com.tcts.database;

/**
 * Thrown when the database contains a value that is not permitted to have.
 */
public class InconsistentDatabaseException extends RuntimeException {
    /** Constructor. */
    public InconsistentDatabaseException(String msg) {
        super(msg);
    }
}
