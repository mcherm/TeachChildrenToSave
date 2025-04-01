/*
This has DML for creating tables. WARNING - if you execute this,
it wipes out the entire database!
*/

drop table SiteSettings;
create table SiteSettings
    (
        setting_name VARCHAR(30) NOT NULL,
        setting_value VARCHAR(100) NOT NULL,
        PRIMARY KEY (setting_name)
    );

drop table AllowedDates;
create table AllowedDates
    (
        event_date DATE NOT NULL,
        PRIMARY KEY (event_date)
    );


drop table AllowedTimes;
create table AllowedTimes
    (
        event_time VARCHAR(30) NOT NULL,
        sort_order INT NOT NULL,
        PRIMARY KEY (event_time),
        UNIQUE KEY ix_sort_order (sort_order)
    );


drop table Event;
create table Event
    (
        event_id INT NOT NULL AUTO_INCREMENT,
        teacher_id INT NOT NULL,
        event_date DATE NOT NULL,
        event_time VARCHAR(30) NOT NULL,
        grade VARCHAR(15) NOT NULL,
        number_students INT NOT NULL,
        notes VARCHAR(1000),
        volunteer_id INT,
        PRIMARY KEY (event_id),
        INDEX ix_teacher (teacher_id),
        INDEX ix_volunteer (volunteer_id)
    );


drop table Bank;
create table Bank
    (
        bank_id INT NOT NULL AUTO_INCREMENT,
        bank_name VARCHAR(45) NOT NULL,
        bank_addr1 VARCHAR(45), /* not used */
        bank_addr2 VARCHAR(45), /* not used */
        bank_city VARCHAR(45), /* not used */
        bank_state VARCHAR(2), /* not used */
        bank_zip VARCHAR(10), /* not used */
        bank_county VARCHAR(45), /* not used */
        min_lmi_for_cra INT,
        bank_specific_data_label VARCHAR(70),
        PRIMARY KEY (bank_id),
        UNIQUE KEY ix_name (bank_name)
    );

drop table User;
create table User
    (
        user_id INT NOT NULL AUTO_INCREMENT,
        password_salt VARCHAR(100),
        password_hash VARCHAR(100),
        email VARCHAR(50),
        first_name VARCHAR(50),
        last_name VARCHAR(50),
        access_type VARCHAR(2) NOT NULL,
        organization_id INT,
        phone_number VARCHAR(45),
        user_status INT NOT NULL,
        reset_password_token VARCHAR(500),
        bank_specific_data VARCHAR(500),
        PRIMARY KEY (user_id),
        UNIQUE KEY ix_email (email),
        INDEX ix_organization (organization_id),
        INDEX ix_type (access_type)
    );

drop table School;
create table School
    (
        school_id INT NOT NULL AUTO_INCREMENT,
        school_name VARCHAR(80) NOT NULL,
        school_addr1 VARCHAR(60) NOT NULL,
        school_city VARCHAR(45) NOT NULL,
        school_zip VARCHAR(10) NOT NULL,
        school_county VARCHAR(45) NOT NULL,
        school_district VARCHAR(45),
        school_state VARCHAR(2) NOT NULL,
        school_phone VARCHAR(45),
        school_lmi_eligible INT,
        school_SLC VARCHAR(10) NOT NULL,
        PRIMARY KEY (school_id),
        UNIQUE KEY ix_name (school_name)
    );

