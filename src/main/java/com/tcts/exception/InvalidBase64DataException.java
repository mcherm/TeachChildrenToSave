package com.tcts.exception;

/**
 * An exception type thrown when we attempt to base64decode a string which
 * turns out NOT to have valid base64 data.
 */
public class InvalidBase64DataException extends RuntimeException {
}
