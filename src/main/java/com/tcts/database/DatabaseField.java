package com.tcts.database;

/**
 * An enum giving us a name to refer to fields in the database.
 */
public enum DatabaseField {
    site_setting_name, site_setting_value,
    event_time, event_grade, event_notes,
    bank_name,
    user_email, user_first_name, user_last_name, user_phone_number, user_bank_specific_data,
    school_name, school_addr1, school_city, school_zip, school_county,
    school_district, school_state, school_phone, school_slc;
}
