package com.tcts.exception;

/**
 * This exception is thrown when the server name from the URL that was used to access this
 * application is not one that has a specific known site. The error message will contain
 * the server name which was not found.
 */
public class UnknownSiteException extends RuntimeException {
    public UnknownSiteException(String serverName) {
        super("Unknown site for server name \"" + serverName + "\".");
    }
}
