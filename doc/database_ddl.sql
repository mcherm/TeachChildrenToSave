// This has DML for the tables I have created and their sample data


drop table Event2;
create table Event2
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
insert into Event2
   (teacher_id,
    event_date, event_time,
    grade, number_students, notes,
    volunteer_id
   )
    values
       (3,
        DATE('2015-03-06'), '1:00 PM',
        4, 20, 'These are some notes.',
        5
       );
insert into Event2
   (teacher_id,
    event_date, event_time,
    grade, number_students, notes,
    volunteer_id
   )
    values
       (3,
        DATE('2015-03-06'), '9:00 AM',
        2, 15, '',
        null
       );


drop table Bank2;
create table Bank2
    (
        bank_id INT NOT NULL AUTO_INCREMENT,
        bank_name VARCHAR(45) NOT NULL,
        bank_admin INT NOT NULL,
        PRIMARY KEY (bank_id)
    );
insert into Bank2 (bank_name, bank_admin)
    values ('Capital One', 2);
insert into Bank2 (bank_name, bank_admin)
    values ('Bank of America', 2);
insert into Bank2 (bank_name, bank_admin)
    values ('Dauphin Trust', 2);
insert into Bank2 (bank_name, bank_admin)
    values ('WSFS', 2);


drop table User2;
create table User2
    (
        user_id INT NOT NULL AUTO_INCREMENT,
        user_login VARCHAR(50) NOT NULL,
        password_salt VARCHAR(100) NOT NULL,
        password_hash VARCHAR(100) NOT NULL,
        email VARCHAR(50),
        first_name VARCHAR(50),
        last_name VARCHAR(50),
        access_type VARCHAR(2) NOT NULL,
        organization_id INT,
        phone_number VARCHAR(45),
        user_status INT,
        PRIMARY KEY (user_id),
        UNIQUE KEY ix_login (user_login),
        INDEX ix_organization (organization_id),
        INDEX ix_type (access_type)
    );
insert into User2 (user_login, password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('larry','dummy_salt','pass','larry@foobar.com','Larry','Smith','V','1','1-800-234-1234','1');
insert into User2 (user_login, password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('jane','dummy_salt','pass','janedoe@foobar.com','Jane','Jones','BA','1','1000001','0');
insert into User2 (user_login, password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('lucy','dummy_salt','pass','lucy@foobar.com','Lucy','Adams','T','1','1-800-234-1234','0');
insert into User2 (user_login, password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('harry','dummy_salt','pass','Harry@gmail.com','Harry','Wilson','SA',null,'1-800-234-1234','0');
insert into User2 (user_login, password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('moe','dummy_salt','pass','moe@gmail.com','Moe','Carbine','V','3','1-800-234-1234','0');
insert into User2 (user_login, password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('curley','dummy_salt','pass','curley@gmail.com','Curley','Urbane','V','4','1-800-234-1234','0');
insert into User2 (user_login, password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('shemp','dummy_salt','pass','shemp@hulu.com','Shemp','Blacksmith ','V','1','1-800-234-1234','0');
insert into User2 (user_login, password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('joshua','dummy_salt','pass','josuah@wintergreen.com','Joshua','Wordsmith','T','2','1-800-234-1234','0');
insert into User2 (user_login, password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('allen','dummy_salt','pass','Allen@novel.com','Allen','James','T','3','1-800-234-1234','0');
