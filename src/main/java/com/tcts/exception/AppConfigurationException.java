package com.tcts.exception;

/**
 * An exception thrown when it is discovered that the application has been
 * mis-configured and will not be able to function properly.
 */
public class AppConfigurationException extends RuntimeException {
    public AppConfigurationException(String msg) {
        super(msg);
    }
}
