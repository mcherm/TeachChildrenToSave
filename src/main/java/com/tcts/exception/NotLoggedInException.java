package com.tcts.exception;

/**
 * This exception is thrown when you attempt to perform an action
 * that can only be done when logged in, and the user is not
 * logged in.
 */
public class NotLoggedInException extends RuntimeException {

    /** Empty constructor. */
    public NotLoggedInException() {
        super();
    }

    /** Construct with error message. */
    public NotLoggedInException(String msg) {
        super(msg);
    }
}
