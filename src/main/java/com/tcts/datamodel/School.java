package com.tcts.datamodel;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a single school. Contains data from one row of the School table.
 */
public class School {
    // --- Basic data fields ---
    private String schoolId;
    private String name;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zip;
    private String county;
    private String schoolDistrict;
    private String phone;
    private boolean lmiEligible;

    /**
     * This can be called to populate fields from the current row of a resultSet.
     */
    public void populateFieldsFromResultSetRow(ResultSet resultSet) throws SQLException {
        setSchoolId(resultSet.getString("school_id"));
        setName(resultSet.getString("school_name"));
        setAddressLine1(resultSet.getString("school_addr1"));
        setAddressLine2(resultSet.getString("school_addr2"));
        setCity(resultSet.getString("school_city"));
        setState(resultSet.getString("school_state"));
        setZip(resultSet.getString("school_zip"));
        setCounty(resultSet.getString("school_county"));
        setSchoolDistrict(resultSet.getString("school_district"));
        setPhone(resultSet.getString("school_phone"));
        setLmiEligible(1 == resultSet.getInt("school_lmi_eligible"));
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getSchoolDistrict() {
        return schoolDistrict;
    }

    public void setSchoolDistrict(String schoolDistrict) {
        this.schoolDistrict = schoolDistrict;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isLmiEligible() {
        return lmiEligible;
    }

    public void setLmiEligible(boolean lmiEligible) {
        this.lmiEligible = lmiEligible;
    }


    /**
     * Subroutine of getAddressInGoogleMapsForm. Appends s to buffer, EXCEPT
     * that (1) append a '+' first if buffer is non-empty, (2) replace any
     * spaces in s with '+',  (3) does nothing if s is null.
     */
    private void addWithSpace(StringBuffer buffer, String s) {
        if (s != null) {
            if (buffer.length() > 0) {
                buffer.append('+');
            }
            buffer.append(s.replace(' ','+'));
        }
    }

    /**
     * Retrieves the address encoded so that it can be used as a search query
     * for google maps.
     */
    public String getAddressInGoogleMapsForm() {
        StringBuffer result = new StringBuffer();
        addWithSpace(result, addressLine1);
        addWithSpace(result, addressLine2);
        addWithSpace(result, city);
        addWithSpace(result, state);
        return result.toString();
    }
}
