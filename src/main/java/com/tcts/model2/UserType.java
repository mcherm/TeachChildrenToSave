package com.tcts.model2;

import com.sun.media.sound.InvalidDataException;
import com.tcts.dao2.InconsistentDatabaseException;

import java.util.HashMap;
import java.util.Map;

/**
 * An enum containing the various types of users of the system.
 * <p>
 * When there are values that need to be associated with the
 * different user types we can embed them in this class.
 */
public enum UserType {
    VOLUNTEER("V", "volunteerHome"),
    TEACHER("T", "teacherHome"),
    BANK_ADMIN("BA", "bankAdminHome"),
    SITE_ADMIN("SA", "siteAdminHome");


    private static Map<String,UserType> userTypeByDBValue = new HashMap<String,UserType>() {{
        put("V", VOLUNTEER);
        put("T", TEACHER);
        put("BA", BANK_ADMIN);
        put("SA", SITE_ADMIN);
    }};

    /**
     * Static factory method that returns the user type for the given
     * string or throws an exception if the string is not a valid
     * value.
     */
    public static UserType fromDBValue(String dbValue) {
        UserType result = userTypeByDBValue.get(dbValue);
        if (result == null) {
            throw new InconsistentDatabaseException("DB Value of '" + dbValue + "'.");
        } else {
            return result;
        }
    }

    private String dbValue;
    private String homepage;

    private UserType(String dbValue, String homepage) {
        this.dbValue = dbValue;
        this.homepage = homepage;
    }

    /**
     * Returns the value to insert in the database for this user type.
     */
    public String getDBValue() {
        return dbValue;
    }

    /**
     * Returns the name of the page to show after this user logs in or when
     * they click on "home" from the navigation.
     */
    public String getHomepage() {
        return homepage;
    }

}
