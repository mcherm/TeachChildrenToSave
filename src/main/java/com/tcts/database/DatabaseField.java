package com.tcts.database;

/**
 * An enum giving us a name to refer to fields in the database.
 */
public enum DatabaseField {
    site_setting_name, site_setting_value,
    event_date_allowed,
    event_time_allowed, event_time_sort_key,
    event_id, event_time, event_grade, event_notes,
    bank_id, bank_name,
    user_id, user_email, user_first_name, user_last_name, user_phone_number, user_bank_specific_data,
    school_id, school_name, school_addr1, school_city, school_zip, school_county,
    school_district, school_state, school_phone, school_slc;
}
