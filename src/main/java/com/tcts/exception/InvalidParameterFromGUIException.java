package com.tcts.exception;

/**
 * An exception thrown when invalid data is passed in FOR DATA THAT THE
 * GUI ENSURES IS CORRECT. Therefore, this can never be a data entry
 * error, only a bug in the UI or a hack attempt.
 */
public class InvalidParameterFromGUIException extends RuntimeException {
}
