package com.tcts.exception;

/**
 * An exception thrown when attempting to insert a value into the database but it turns
 * out that the primary key for it already exists. Should not occur under any normal
 * circumstances; ought to indicate an error in the database.
 */
public class PrimaryKeyAlreadyExistsException extends RuntimeException {
}
