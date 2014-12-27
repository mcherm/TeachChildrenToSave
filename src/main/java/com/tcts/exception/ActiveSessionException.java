package com.tcts.exception;

/**
 * An exception thrown if we EXPECT to NOT have a session, but there IS one.
 */
public class ActiveSessionException extends RuntimeException {
}
