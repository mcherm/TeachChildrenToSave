package com.tcts.datamodel;

import java.util.HashMap;
import java.util.Map;

import com.tcts.exception.InconsistentDatabaseException;

/**
 * An enum containing the various types of users of the system.
 * <p>
 * When there are values that need to be associated with the
 * different user types we can embed them in this class.
 */
public enum UserType {
    VOLUNTEER("V", "Volunteer", "volunteerHome.htm"),
    TEACHER("T","Teacher","teacherHome.htm"),
    BANK_ADMIN("BA", "Bank Admin", "bankAdminHome.htm"),
    SITE_ADMIN("SA", "Site Admin", "siteAdminHome.htm");


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
    private String displayName;

    private UserType(String dbValue, String displayName, String homepage) {
        this.dbValue = dbValue;
        this.displayName = displayName;
        this.homepage = homepage;
    }

    /**
     * Returns the value to insert in the database for this user type.
     */
    public String getDBValue() {
        return dbValue;
    }

    /**
     * Returns the path for the page to show after this user logs in or when
     * they click on "home" from the navigation.
     */
    public String getDisplayName() {
        return displayName;
   }
    public String getHomepage() {
        return homepage;
    }

}
