package com.tcts.model;

/**
 * An enum containing the various types of users of the system.
 * <p>
 * When there are values that need to be associated with the
 * different user types we can embed them in this class.
 */
public enum UserType {
    VOLUNTEER("volunteerHome"),
    TEACHER("teacherHome"),
    BANK_ADMIN("bankAdminHome"),
    SITE_ADMIN("siteAdminHome");


    private String homepage;

    private UserType(String homepage) {
        this.homepage = homepage;
    }

    /**
     * Returns the name of the page to show after this user logs in or when
     * they click on "home" from the navigation.
     */
    public String getHomepage() {
        return homepage;
    }

}
