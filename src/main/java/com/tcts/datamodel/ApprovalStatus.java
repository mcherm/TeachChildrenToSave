package com.tcts.datamodel;

import com.tcts.exception.InconsistentDatabaseException;

import java.util.HashMap;
import java.util.Map;

/**
 * Denotes whether a volunteer has been checked, approved or not approved by the bank admin.
 */
public enum ApprovalStatus {
    UNCHECKED(0),
    CHECKED(1),
    SUSPENDED(2);

    private final int dbValue;

    /** The value to start with for a new person in the database. */
    public static ApprovalStatus INITIAL_APPROVAL_STATUS = UNCHECKED;

    /** Map used to create instances from the DB. */
    private static Map<Integer,ApprovalStatus> approvalStatusByDBValue = new HashMap<Integer,ApprovalStatus>() {{
        put(0, UNCHECKED);
        put(1, CHECKED);
        put(2, SUSPENDED);
    }};

    /**
     * Static factory method that returns the user type for the given
     * string or throws an exception if the string is not a valid
     * value.
     */
    public static ApprovalStatus fromDBValue(int dbValue) {
        ApprovalStatus result = approvalStatusByDBValue.get(dbValue);
        if (result == null) {
            throw new InconsistentDatabaseException("DB Value of '" + dbValue + "'.");
        } else {
            return result;
        }
    }

    /** Constructor. */
    private ApprovalStatus(int dbValue) {
        this.dbValue = dbValue;
    }

    /** Gets the value to store in the DB. */
    public int getDbValue() {
        return dbValue;
    }
}
