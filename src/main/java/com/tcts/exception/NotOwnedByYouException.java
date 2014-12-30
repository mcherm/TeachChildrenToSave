package com.tcts.exception;

/**
 * An exception thrown when a user is attempting to view or modify
 * data not owned by them. This will normally be because of a hack
 * attempt or UI error of some sort.
 */
public class NotOwnedByYouException extends RuntimeException {
}
