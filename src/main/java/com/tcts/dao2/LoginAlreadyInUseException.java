package com.tcts.dao2;

/**
 * An exception thrown when creating any sort of a user but the proposed
 * login is not unique and available.
 */
public class LoginAlreadyInUseException extends Exception {
}
