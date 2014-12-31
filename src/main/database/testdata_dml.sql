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
insert into Bank (bank_name, bank_admin) values ('HSBC Trust Company', 2);
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
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('xEuPCDFREH0=', 'OWs/YFcrRM2H/JKXSmMi56pdL8E=', 'whitley.mccargo@yahoo.com.com', 'Whitley', 'Mccargo', 'T', '3', '465-276-1272', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('qGy5m8d0wu8=', 'fA6f57SnRXgZ9DARMMs5ZoFt3bs=', 'chrissy.tremblay@cr.k12.de.us.com', 'Chrissy', 'Tremblay', 'T', '3', '153-694-5257', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('qGgBx7BQbIM=', 'jpY7nxqN3YnwNWSa+PtvXKqsVb8=', 'karin.banh@bsd.k12.de.us.com', 'Karin', 'Banh', 'T', '3', '867-189-9003', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('1A4mTllwR2w=', '6TH2XwYfOy7cTBGvGu9tAJZfaQ0=', 'rheba.bryne@gmail.com.com', 'Rheba', 'Bryne', 'T', '1', '766-040-7981', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('NUIkuuLA6EY=', 'GlXRrAWSjGH7y4ambYlZYD6daSE=', 'carina.koga@gmail.com.com', 'Carina', 'Koga', 'T', '4', '848-034-5129', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('M/2Wbt8oo7Y=', 'UDxejBSoH9shONIUPK4eSw8bKBc=', 'daysi.latour@hotmail.com.com', 'Daysi', 'Latour', 'T', '1', '451-028-2757', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('mwLgUByo+EY=', 'oqSHPMfeQNvRnwa0X7eKnJhVaGc=', 'tristan.covey@bsd.k12.de.us.com', 'Tristan', 'Covey', 'T', '4', '106-733-5004', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('IsyFQ9HGBCo=', 'I9pTrl1hQZfqzIcSnNn6WKRA2LM=', 'loyd.heatwole@cr.k12.de.us.com', 'Loyd', 'Heatwole', 'T', '1', '204-061-8310', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('8ydKhHu2qig=', '62W+oNXEoPb0hrRESCyH2aGS6sk=', 'amal.moler@cr.k12.de.us.com', 'Amal', 'Moler', 'T', '4', '962-500-4212', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('kr8YPqnzhtA=', 'BtG+QkjQ0omiOrH0ReQepj2jO4E=', 'myrtice.beltz@bsd.k12.de.us.com', 'Myrtice', 'Beltz', 'T', '5', '127-763-0383', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('WU9HEC4TSSs=', 'P16K6jyUeAOgOytFpkSV0MQmzzA=', 'venita.sciortino@hotmail.com.com', 'Venita', 'Sciortino', 'T', '4', '178-149-7566', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('D+MeZCYEP0I=', 'NM0dL2lMjQAQegb97AI4p1iAh6A=', 'anabel.lanphere@gmail.com.com', 'Anabel', 'Lanphere', 'T', '3', '791-843-4556', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('W7zu+XYDnDE=', 'AO2EdbBiOVEhjy4IVeWyyoxLnkM=', 'brad.barringer@gmail.com.com', 'Brad', 'Barringer', 'T', '2', '822-078-0852', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('lfVeIn3Mu64=', 'XLA5YIlAasCklwA0iGkScs8+hNY=', 'cristal.tschanz@yahoo.com.com', 'Cristal', 'Tschanz', 'T', '1', '066-160-3755', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('oKzJbrysfxk=', 'tgz29MMg/eCNY3ZsxWp1A2dXSRk=', 'vernia.goldsmith@gmail.com.com', 'Vernia', 'Goldsmith', 'T', '2', '508-933-8731', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('EjMXFh/KetE=', '3Wr+S10OYd4Ei9hvnnCqoiaMalM=', 'melodi.keely@bsd.k12.de.us.com', 'Melodi', 'Keely', 'T', '2', '095-300-7264', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('CMh/T8UWVvM=', '861m+JtuiDEdn/WOBhcVMi2QdEI=', 'tabetha.jorgenson@gmail.com.com', 'Tabetha', 'Jorgenson', 'T', '5', '832-715-5386', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('sZLEJIDJwtA=', 'dlyrmADLAh926+G8iMMtN2AC19Q=', 'hester.breck@bsd.k12.de.us.com', 'Hester', 'Breck', 'T', '4', '015-218-0501', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('NHthebbKcBY=', 'r0B1xFT7x6RjTr23b3/ULodTt4k=', 'lucina.dimmick@gmail.com.com', 'Lucina', 'Dimmick', 'T', '2', '283-526-6750', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('nE0Ivw/u7gA=', 'Q4Y2lMDb8nLWgcnYoabWorKTNfw=', 'altagracia.saddler@cr.k12.de.us.com', 'Altagracia', 'Saddler', 'T', '4', '106-621-6872', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('O8+XB48eH2M=', '49GOqnYd4xMNs27JeKnAvPitdzY=', 'doretta.moncayo@hotmail.com.com', 'Doretta', 'Moncayo', 'T', '3', '266-947-3531', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('1/0Iz3i5j9U=', 'sY3MzvICSV2vwNoNsHaIomNRyVA=', 'mindy.labadie@bsd.k12.de.us.com', 'Mindy', 'Labadie', 'T', '2', '990-798-8473', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('C4jFzgabvkM=', 'Ev0WXsKdEXMC/cLoMMqzFhxoIl8=', 'coreen.luth@bsd.k12.de.us.com', 'Coreen', 'Luth', 'T', '2', '138-930-0633', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('i+hhZQ3Kdls=', 'IXNoGP+8HAlOluIjzbzlwA6t6og=', 'lorretta.liptak@hotmail.com.com', 'Lorretta', 'Liptak', 'T', '2', '923-859-7473', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('zoRqS35IWdE=', 'N0PKVh5wpmtjmHEC5lr8BTDjelI=', 'melony.kubala@hotmail.com.com', 'Melony', 'Kubala', 'T', '4', '310-328-7691', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('r20pCtdpD9E=', 'IV/CHJBiWz/WPa/HRRsLQC52Ihc=', 'greg.ellison@gmail.com.com', 'Greg', 'Ellison', 'T', '4', '694-389-2441', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('IjD21R9+Qbg=', 'hdyk3ztJlJFLkgKQYMzOJNbdZE8=', 'porsche.vanloan@bsd.k12.de.us.com', 'Porsche', 'Vanloan', 'T', '3', '644-163-2563', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('/22hu+3zMLE=', 'otqTVp5Z43uevoOcr3xPb8onh70=', 'chante.raya@hotmail.com.com', 'Chante', 'Raya', 'T', '2', '650-581-1787', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('AJMo5JYVwls=', 'J0a6plMKKpdttra5u29bXtpw6KI=', 'carter.tolman@gmail.com.com', 'Carter', 'Tolman', 'T', '3', '462-029-9146', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('gzK9aHvlLCM=', 'UudIuKW2HHQKfcMSbR6TSDEjnCw=', 'dell.hoops@bsd.k12.de.us.com', 'Dell', 'Hoops', 'T', '4', '136-972-3865', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('dorX6NPMWLs=', 'moj6KmmA/0gk24LXYJ3sNRoBSLw=', 'deane.sisto@yahoo.com.com', 'Deane', 'Sisto', 'T', '1', '309-295-4825', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('VWBJtxVuy60=', 'CGDxVwUaON1cXadZLs5AD8xEY4Y=', 'raymond.giblin@cr.k12.de.us.com', 'Raymond', 'Giblin', 'V', '25', '230-716-9763', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('YRfvuacmZL4=', '5bEe8fed+QXCePyYIrqh7h2OKcM=', 'frederick.bucci@gmail.com.com', 'Frederick', 'Bucci', 'V', '3', '235-747-8379', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('eaCrwpOGp+w=', 'x/GPdXhrgLGBbmBh+tLI5o0saME=', 'pandora.kagawa@yahoo.com.com', 'Pandora', 'Kagawa', 'V', '3', '899-498-3840', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('G8qEl5ZNYcA=', 'F1lmHf7Sijg7g1Pg0FI4IiOASKY=', 'anisa.sheetz@gmail.com.com', 'Anisa', 'Sheetz', 'V', '6', '496-248-8328', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('HGijMgS/fjQ=', 'qgOzqJ9JaarhgWS5Lw54z3xb2oM=', 'jesica.winker@cr.k12.de.us.com', 'Jesica', 'Winker', 'V', '25', '060-251-2463', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('z0qPBuqyGiQ=', 'AfV1O7wkW8aUqh5Z8oZZVyfpQII=', 'herman.skates@gmail.com.com', 'Herman', 'Skates', 'V', '2', '904-582-4289', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('kZqxtICcqY0=', 'INvehQhPDOstnwayUI9qkIIFRzo=', 'abigail.orem@gmail.com.com', 'Abigail', 'Orem', 'V', '5', '888-161-3086', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('UoY35m4ruHw=', 'NQ/BOux+Q6RmsdPkCbS98+FcNjc=', 'emory.conklin@cr.k12.de.us.com', 'Emory', 'Conklin', 'V', '33', '609-166-8742', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('SJaZU+RueSI=', 'ChdDaQPbITV7zoF4kfqnPWWMvwc=', 'lindsey.reidhead@bsd.k12.de.us.com', 'Lindsey', 'Reidhead', 'V', '27', '210-500-6929', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('Y63J2WqO7u0=', 'TVlvkTtId/TlxLFxTK53TpRp+jM=', 'alexis.grayson@gmail.com.com', 'Alexis', 'Grayson', 'V', '13', '282-973-2500', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('Y436WIOzr4k=', 'xHcTzcC777L+UUHJdJI5TEbBTRA=', 'morgan.saulnier@yahoo.com.com', 'Morgan', 'Saulnier', 'V', '12', '267-201-8647', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('lJlm7W205tY=', '9RhT0b47X8tXkBK+8FydL7qY2Us=', 'emma.waag@cr.k12.de.us.com', 'Emma', 'Waag', 'V', '13', '588-956-4704', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('VBMdMe+okRs=', '/zbwFpwfcolztn8TWFKBBfLzCXM=', 'tanika.raymond@gmail.com.com', 'Tanika', 'Raymond', 'V', '1', '973-180-0477', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('B1NGfyO/ymY=', 'ksRWiOq1+gxTKGjyHlBWBCiNzoI=', 'darrel.abasta@gmail.com.com', 'Darrel', 'Abasta', 'V', '2', '650-318-8150', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('BHbpnutzbVo=', '9+VYydkLxAJb4jkZdV0eI4b78Pk=', 'terrence.aiello@hotmail.com.com', 'Terrence', 'Aiello', 'V', '11', '089-349-8837', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('3hYf+wtdssI=', 'UjaA4OBHiqk8U0UrwMmnZQ1asjM=', 'sarina.mowbray@hotmail.com.com', 'Sarina', 'Mowbray', 'V', '19', '497-798-5950', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('RSmZc64rVeQ=', 'cuH6b6hyd5tgcKc1Yxw30D3UJm0=', 'rickey.slavens@gmail.com.com', 'Rickey', 'Slavens', 'V', '31', '705-180-9796', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('f0uTm9wh9N8=', 'ALnCTEcc/yl6SNbrDAG+s/8fiks=', 'valerie.daughdrill@bsd.k12.de.us.com', 'Valerie', 'Daughdrill', 'BA', '8', '273-050-4932', '0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status) values ('ExBSfqjA/2c=', 'BPi+lreoH11Zbd2lvOXQHfIn2cI=', 'raelene.mcmenamin@hotmail.com.com', 'Raelene', 'Mcmenamin', 'BA', '9', '155-896-6510', '0');

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
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (10, DATE('2015-04-10'), '9:00 AM', 2, 23, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (10, DATE('2015-04-08'), '2:00 PM', 5, 20, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (11, DATE('2015-04-10'), '11:00 AM', 4, 15, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (11, DATE('2015-04-08'), '9:00 AM', 5, 14, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (12, DATE('2015-04-10'), '11:00 AM', 2, 22, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (12, DATE('2015-04-09'), '12:00 PM', 5, 23, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (13, DATE('2015-04-08'), '9:00 AM', 5, 7, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (13, DATE('2015-04-10'), '9:00 AM', 4, 12, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (14, DATE('2015-04-08'), '11:00 AM', 3, 7, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (14, DATE('2015-04-09'), '11:00 AM', 3, 14, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (15, DATE('2015-04-08'), '9:00 AM', 5, 13, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (15, DATE('2015-04-08'), '10:00 AM', 4, 23, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (16, DATE('2015-04-08'), '11:00 AM', 3, 15, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (16, DATE('2015-04-08'), '9:00 AM', 3, 22, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (17, DATE('2015-04-09'), '12:00 PM', 5, 20, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (17, DATE('2015-04-09'), '2:00 PM', 4, 24, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (18, DATE('2015-04-08'), '1:00 PM', 4, 15, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (18, DATE('2015-04-10'), '2:00 PM', 4, 20, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (19, DATE('2015-04-10'), '11:00 AM', 3, 10, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (19, DATE('2015-04-09'), '2:00 PM', 3, 17, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (20, DATE('2015-04-08'), '10:00 AM', 3, 17, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (20, DATE('2015-04-09'), '11:00 AM', 5, 13, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (21, DATE('2015-04-10'), '1:00 PM', 4, 17, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (21, DATE('2015-04-08'), '11:00 AM', 3, 7, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (22, DATE('2015-04-09'), '12:00 PM', 4, 20, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (22, DATE('2015-04-08'), '11:00 AM', 4, 18, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (23, DATE('2015-04-10'), '9:00 AM', 4, 10, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (23, DATE('2015-04-09'), '2:00 PM', 5, 14, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (24, DATE('2015-04-10'), '11:00 AM', 3, 24, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (24, DATE('2015-04-08'), '1:00 PM', 4, 18, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (25, DATE('2015-04-09'), '9:00 AM', 2, 16, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (25, DATE('2015-04-09'), '2:00 PM', 3, 14, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (26, DATE('2015-04-08'), '12:00 PM', 3, 17, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (26, DATE('2015-04-08'), '12:00 PM', 4, 20, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (27, DATE('2015-04-10'), '11:00 AM', 5, 20, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (27, DATE('2015-04-09'), '2:00 PM', 4, 7, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (28, DATE('2015-04-09'), '9:00 AM', 2, 18, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (28, DATE('2015-04-08'), '12:00 PM', 2, 8, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (29, DATE('2015-04-09'), '9:00 AM', 4, 6, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (29, DATE('2015-04-09'), '12:00 PM', 5, 12, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (30, DATE('2015-04-09'), '11:00 AM', 4, 16, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (30, DATE('2015-04-08'), '2:00 PM', 5, 15, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (31, DATE('2015-04-09'), '9:00 AM', 3, 6, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (31, DATE('2015-04-10'), '10:00 AM', 5, 6, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (32, DATE('2015-04-10'), '9:00 AM', 3, 17, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (32, DATE('2015-04-10'), '12:00 PM', 4, 16, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (33, DATE('2015-04-10'), '1:00 PM', 5, 19, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (33, DATE('2015-04-10'), '12:00 PM', 4, 15, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (34, DATE('2015-04-08'), '11:00 AM', 4, 8, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (34, DATE('2015-04-09'), '9:00 AM', 4, 7, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (35, DATE('2015-04-09'), '2:00 PM', 5, 18, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (35, DATE('2015-04-08'), '9:00 AM', 3, 10, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (36, DATE('2015-04-08'), '9:00 AM', 3, 7, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (36, DATE('2015-04-08'), '9:00 AM', 3, 15, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (37, DATE('2015-04-08'), '1:00 PM', 3, 12, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (37, DATE('2015-04-08'), '11:00 AM', 5, 25, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (38, DATE('2015-04-08'), '10:00 AM', 4, 9, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (38, DATE('2015-04-08'), '2:00 PM', 2, 9, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (39, DATE('2015-04-10'), '2:00 PM', 4, 19, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (39, DATE('2015-04-08'), '11:00 AM', 4, 21, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (40, DATE('2015-04-10'), '2:00 PM', 3, 9, '', null);
insert into Event (teacher_id, event_date, event_time, grade, number_students, notes, volunteer_id) values (40, DATE('2015-04-09'), '1:00 PM', 3, 9, '', null);
