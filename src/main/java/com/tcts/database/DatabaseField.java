package com.tcts.database;

/**
 * An enum giving us a name to refer to fields in the database.
 */
public enum DatabaseField {
    // SiteSettings
    site_setting_name, site_setting_value,
    // Documents
    document_name, document_show_to_teacher, document_show_to_volunteer, document_show_to_bank_admin,
    // AllowedDates
    event_date_allowed,
    // AllowedTimes
    event_time_allowed, event_time_sort_key,
    // Event
    event_id, event_teacher_id, event_date, event_time, event_grade, event_delivery_method, event_number_students, event_notes, event_volunteer_id,
    // Bank
    bank_id, bank_name, min_lmi_for_cra, bank_specific_data_label,
    // User
    user_id, user_type, user_approval_status, user_email, user_original_email, user_first_name, user_last_name, user_phone_number, user_bank_specific_data,
    user_organization_id, user_hashed_password, user_password_salt, user_reset_password_token,
    //User address (currently only volunteers have addresses)
    user_street_address, user_suite_or_floor_number, user_city, user_state, user_zip,
    // School
    school_id, school_name, school_addr1, school_city, school_zip, school_county,
    school_district, school_state, school_phone, school_lmi_eligible, school_slc;
}
