/* This has DML for the tables I have created and their sample data */

insert into AllowedDates (event_date)
    values (DATE('2015-04-08'));
insert into AllowedDates (event_date)
    values (DATE('2015-04-09'));
insert into AllowedDates (event_date)
    values (DATE('2015-04-10'));


insert into AllowedTimes (event_time, sort_order)
  values ("9:00 AM", 1);
insert into AllowedTimes (event_time, sort_order)
  values ("10:00 AM", 2);
insert into AllowedTimes (event_time, sort_order)
  values ("11:00 AM", 3);
insert into AllowedTimes (event_time, sort_order)
  values ("12:00 AM", 4);
insert into AllowedTimes (event_time, sort_order)
  values ("1:00 PM", 5);
insert into AllowedTimes (event_time, sort_order)
  values ("2:00 PM", 6);


insert into Event
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
insert into Event
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


insert into Bank (bank_name, bank_admin) values ('AIG Federal Savings Bank', 2);
insert into Bank (bank_name, bank_admin) values ('Applied Bank', 2);
insert into Bank (bank_name, bank_admin) values ('Artisans'' Bank', 2);
insert into Bank (bank_name, bank_admin) values ('BNY Mellon Trust of Delaware', 2);
insert into Bank (bank_name, bank_admin) values ('Barclays Bank Delaware', 2);
insert into Bank (bank_name, bank_admin) values ('Brandywine Trust Company', 2);
insert into Bank (bank_name, bank_admin) values ('Brown Brothers Harriman Trust Company', 2);
insert into Bank (bank_name, bank_admin) values ('Capital One', 2);
insert into Bank (bank_name, bank_admin) values ('Charles Schwab Bank', 2);
insert into Bank (bank_name, bank_admin) values ('Chase Bank USA, N.A.', 2);
insert into Bank (bank_name, bank_admin) values ('CNB', 2);
insert into Bank (bank_name, bank_admin) values ('Comenity Bank', 2);
insert into Bank (bank_name, bank_admin) values ('Commonwealth Trust Company', 2);
insert into Bank (bank_name, bank_admin) values ('Community Bank Delaware', 2);
insert into Bank (bank_name, bank_admin) values ('County Bank', 2);
insert into Bank (bank_name, bank_admin) values ('Deutsche Bank Trust Company Delaware', 2);
insert into Bank (bank_name, bank_admin) values ('Discover Bank', 2);
insert into Bank (bank_name, bank_admin) values ('Bank of America', 2);
insert into Bank (bank_name, bank_admin) values ('The First National Bank of Wyoming', 2);
insert into Bank (bank_name, bank_admin) values ('Fulton Bank, N.A.', 2);
insert into Bank (bank_name, bank_admin) values ('Glenmede', 2);
insert into Bank (bank_name, bank_admin) values ('The Goldman Sachs Trust Company of Delaware', 2);
insert into Bank (bank_name, bank_admin) values ('HSBC Trust Company (Delaware) National Association', 2);
insert into Bank (bank_name, bank_admin) values ('J.P.Morgan Trust Company of Delaware', 2);
insert into Bank (bank_name, bank_admin) values ('Key National Trust Company of Delaware', 2);
insert into Bank (bank_name, bank_admin) values ('M&T Bank', 2);
insert into Bank (bank_name, bank_admin) values ('MidCoast Community Bank', 2);
insert into Bank (bank_name, bank_admin) values ('Morgan Stanley Private Bank', 2);
insert into Bank (bank_name, bank_admin) values ('PNC Bank, Delaware', 2);
insert into Bank (bank_name, bank_admin) values ('Principal Trust Company', 2);
insert into Bank (bank_name, bank_admin) values ('UBS Trust Company, N.A.', 2);
insert into Bank (bank_name, bank_admin) values ('Wells Fargo Bank, N.A.', 2);
insert into Bank (bank_name, bank_admin) values ('Wilmington Savings Fund Society, FSB', 2);
insert into Bank (bank_name, bank_admin) values ('Wilmington Trust Company', 2);


insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('kNF87zO+5bo=','JcDDbNz0dXVdUvDEzfzVmNEhHjk=','larry@foobar.com','Larry','Smith','V','1','1-800-234-1234','1');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('84oH5LTcO30=','inZgchLYXtt2wor3razfPvtub+o=','janedoe@foobar.com','Jane','Jones','BA','1','1000001','0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('9HYa4nSj3Jg=','wrkttouWUjjV/sDVJh632UZvu5g=','lucy@foobar.com','Lucy','Adams','T','1','1-800-234-1234','0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('Urdqicfklbw=','I2uKh9UDwqkhLOppWX4haBMFK+M=','Harry@gmail.com','Harry','Wilson','SA',null,'1-800-234-1234','0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('7U5JMa0bB70=','+vQRkteaU2CEhdmwUaof1Pn/8eE=','moe@gmail.com','Moe','Carbine','V','3','1-800-234-1234','0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('x0JQ3ovD18k=','R3vvnDTL7TGaTOMBlHY4FndCb3U=','curley@gmail.com','Curley','Urbane','V','4','1-800-234-1234','0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('lsi4KL+dKv0=','VHMxx4AHj70PFq38sVios7dPbK0=','shemp@hulu.com','Shemp','Blacksmith ','V','1','1-800-234-1234','0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('RLwBcwWzkw8=','SXxr0aC/ULZRb61gziXoUUM/J7E=','josuah@wintergreen.com','Joshua','Wordsmith','T','2','1-800-234-1234','0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('KsRpejm8v8w=','sxHS4Hjqvfr57nKVCPO49KibSc4=','Allen@novel.com','Allen','James','T','3','1-800-234-1234','0');

