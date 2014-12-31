/*
This has DML for creating tables. WARNING - if you execute this,
it wipes out the entire database!
*/

drop table AllowedDates;
create table AllowedDates
    (
        event_date DATE NOT NULL,
        PRIMARY KEY (event_date)
    );


drop table AllowedTimes;
create table AllowedTimes
    (
        event_time VARCHAR(8) NOT NULL,
        sort_order INT NOT NULL,
        PRIMARY KEY (event_time)
    );


drop table Event;
create table Event
    (
        event_id INT NOT NULL AUTO_INCREMENT,
        teacher_id INT NOT NULL,
        event_date DATE NOT NULL,
        event_time VARCHAR(8) NOT NULL,
        grade VARCHAR(1) NOT NULL,
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
        bank_admin INT,
        PRIMARY KEY (bank_id)
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
        user_status INT,
        PRIMARY KEY (user_id),
        UNIQUE KEY ix_email (email),
        INDEX ix_organization (organization_id),
        INDEX ix_type (access_type)
    );

/* FIXME: This is missing the code for the "school" table.
*/
