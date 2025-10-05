package com.tcts.datamodel;


/**
 * Represents a single teacher. Contains data from one row of the Users table.
 */
public class Teacher extends User {

    // --- Basic data fields ---
    private String schoolId;

    // --- Linked data - loaded only when needed ---
    private School linkedSchool;


    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public School getLinkedSchool() {
        return linkedSchool;
    }

    public void setLinkedSchool(School linkedSchool) {
        this.linkedSchool = linkedSchool;
    }

}
