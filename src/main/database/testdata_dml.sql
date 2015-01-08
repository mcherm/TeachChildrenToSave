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
  values ("12:00 PM", 4);
insert into AllowedTimes (event_time, sort_order)
  values ("1:00 PM", 5);
insert into AllowedTimes (event_time, sort_order)
  values ("2:00 PM", 6);


insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Appoquinimink","Brick Mill Elementary School","N120","378 Brick Mill Road","Middletown","DE","19709","302.378.5288","21.3");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Appoquinimink","Bunker Hill Elementary School","N120","1070 Bunker Hill Road","Middletown","DE","19709","302.378.5135","15.4");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Appoquinimink","Cedar Lane Elementary School","N120","1221 Cedar Lane Road","Middletown","DE","19709","302.378.5045","13");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Appoquinimink","Loss (Olive B.) Elementary School","N120","200 Brennan Boulevard","Bear","DE","19701","302.832.1343","7.7");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Appoquinimink","Old State Elementary School","N120","580 Tony Marchio Drive","Townsend","DE","19734","302.378.6720","31.5");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Appoquinimink","Silver Lake Elementary School","N120","200 E. Cochran Street","Middletown","DE","19709","302.378.5023","31.6");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Appoquinimink","Townsend Elementary School","N120","126 Main Street","Townsend","DE","19734","302.378.5020","31.5");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Brandywine","Carrcroft Elementary School","N280","503 Crest Road","Wilmington","DE","19803","302.762.7165","36.1");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Brandywine","Claymont Elementary School","N280","3401 Green Street","Claymont","DE","19703","302.792.3880","42.2");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Brandywine","Forwood Elementary School","N280","1900 Westminster Drive","Wilmington","DE","19810","302.475.3956","24.9");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Brandywine","Hanby Elementary School","N280","2523 Berwyn Road","Wilmington","DE","19810","302.479.2220","23.9");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Brandywine","Harlan (David W.) Elementary School","N280","3601 Jefferson Street","Wilmington","DE","19802","302.762.7156","61");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Brandywine","Lancashire Elementary School","N280","200 Naamans Road","Wilmington","DE","19810","302.475.3990","25.2");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Brandywine","Lombardy Elementary School","N280","412 Foulk Road","Wilmington","DE","19803","302.762.7190","26.9");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Brandywine","Maple Lane Elementary School","N280","100 Maple Lane","Claymont","DE","19703","302.792.3906","46.9");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Brandywine","Mount Pleasant Elementary School","N280","500 Duncan Road","Wilmington","DE","19809","302.762.7120","35.3");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Caesar Rodney","Brown (W. Reily) Elementary School","D280","360 Webbs Lane","Dover","DE","19904","302.697.2101","58");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Caesar Rodney","Charlton (John S.) School","D280","278 Sorghum Mill Road","Camden-Wyoming","DE","19901","302.697.3103","27");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Caesar Rodney","Frear (Allen) Elementary School","D280","238 Sorghum Mill Road","Camden","DE","19934","302.697.3279","35.3");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Caesar Rodney","Kent Elementary Intensive Learning Center","D280","5 Old North Road","Camden","DE","19934","302.697.3504","75.6");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Caesar Rodney","Simpson (W.B.) Elementary School","D280","5 Old North Road","Camden","DE","19934","302.697.3207","38.8");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Caesar Rodney","Star Hill Elementary School","D280","594 Voshells Mill/Star Hill Road","Dover","DE","19901","302.697.6117","25.8");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Caesar Rodney","Stokes (Nellie Hughes) Elementary School","D280","3874 Upper King Road","Dover","DE","19904","302.697.3205","49.6");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Caesar Rodney","Welch (Major George S.) Elementary School","D280","3100 Hawthorne Drive","Dover","DE","19901","302.674.9080","5.6");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Cape Henlopen","H. O. Brittingham Elementary School","S150","400 Mulberry Street","Milton","DE","19968","302.684.8522","65.5");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Cape Henlopen","Milton Elementary School","S150","512 Federal Street","Milton","DE","19968","302.684.2516","30.6");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Cape Henlopen","Rehoboth Elementary School","S150","500 Stockely Street","Rehoboth","DE","19971","302.227.2571","37");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Cape Henlopen","Shields (Richard A.) Elementary School","S150","910 Shields Avenue","Lewes","DE","19958","302.645.7748","32.1");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Cape Henlopen","Sussex Consortium","S150","520 duPont Avenue","Lewes","DE","19958","302.645.7210","41.2");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Capital","Booker T. Washington Elementary School","D103","901 Forest Street","Dover","DE","19904","302.672.1900","57.4");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Capital","East Dover Elementary School","D103","852 South Little Creek Road","Dover","DE","19901","302.672.1655","67.7");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Capital","Fairview Elementary School","D103","700 Walker Road","Dover","DE","19904","302.672.1645","62.5");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Capital","Hartly Elementary School","D103","2617 Arthursville Road","Hartly","DE","19953","302.492.1870","40.6");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Capital","Kent County Community School","D103","65-1 Carver Road","Dover","DE","19904","302.672.1960","41.8");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Capital","North Dover Elementary School","D103","855 College Road","Dover","DE","19904","302.672.1980","53.7");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Capital","South Dover Elementary School","D103","955 South State Street","Dover","DE","19901","302.672.1690","63.6");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Capital","Towne Point Elementary School","D103","629 Buckson Drive","Dover","DE","19901","302.672.1590","66.3");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Bancroft Elementary School","N410","700 North Lombard Street","Wilmington","DE","19801","302.429.4102","79");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Brader (Henry M.) Elementary School","N410","350 Four Seasons Parkway","Newark","DE","19702","302.454.5959","37.3");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Brookside Elementary School","N410","800 Marrows Road","Newark","DE","19713","302.454.5454","63");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Delaware School for the Deaf Elementary","N410","630 East Chestnut Hill Road","Newark","DE","19713","302.454.2301","34.8");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Douglass School","N410","1800 Prospect Road","Wilmington","DE","19805","302.429.4146","71");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Downes (John R.) Elementary School","N410","220 Casho Mill Road","Newark","DE","19711","302.454.2133","30.2");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Elbert-Palmer Elementary School","N410","1210 Lobdell Street","Wilmington","DE","19801","302.429.4188","89.8");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Gallaher (Robert S.) Elementary School","N410","800 North Brownleaf Road","Newark","DE","19713","302.454.2464","39.2");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Jones (Albert H.) Elementary School","N410","35 West Main Street","Newark","DE","19702","302.454.2131","48.7");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Keene (William B.) Elementary School","N410","200 LaGrange Avenue","Newark","DE","19702","302.454.2018","35.1");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Leasure (May B.) Elementary School","N410","1015 Church Road","Newark","DE","19702","302.454.2103","44.4");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Maclary (R. Elisabeth) Elementary School","N410","300 St. Regis Drive","Newark","DE","19711","302.454.2142","31.3");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Marshall (Thurgood) Elementary School","N410","101 Barrett Run Road","Newark","DE","19702","302.454.4700","26.1");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","McVey (Joseph M.) Elementary School","N410","908 Janice Drive","Newark","DE","19713","302.454.2145","50.8");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Montessori Academy Wilmington","N410","700 North Lombard Street","Wilmington","DE","19801","302.429.4102","79");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Oberle (William) Elementary School","N410","500 South Caledonia Way","Bear","DE","19701","302.834.5910","57.9");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Pulaski (Casimir) Elementary School","N410","1300 Cedar Street","Wilmington","DE","19805","302.429.4136","81.6");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Smith (Jennie E.) Elementary School","N410","142 Brennen Drive","Newark","DE","19713","302.454.2174","45.2");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Stubbs (Frederick Douglass) Elementary School","N410","1100 North Pine Street","Wilmington","DE","19801","302.429.4175","89.2");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","West Park Place Elementary School","N410","193 West Park Place","Newark","DE","19711","302.454.2290","31.2");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Christina","Wilson (Etta J.) Elementary School","N410","14 Forge Road","Newark","DE","19711","302.454.2180","39.2");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Colonial","Castle Hills Elementary School","N160","502 Moores Lane","New Castle","DE","19720","302.323.2915","60.8");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Colonial","Downie (Carrie) Elementary School","N160","1201 Delaware Street","New Castle","DE","19720","302.323.2926","56.7");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Colonial","Eisenberg (Harry O.) Elementary School","N160","27 Landers Lane","New Castle","DE","19720","302.429.4074","67.8");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Colonial","New Castle Elementary School","N160","903 Delaware Street","New Castle","DE","19720","302.323.2880","59");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Colonial","Pleasantville Elementary School","N160","16 Pleasant Street","New Castle","DE","19720","302.323.2935","46.2");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Colonial","Southern Elementary School","N160","795 Cox Neck Road","New Castle","DE","19720","302.832.6300","36.5");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Colonial","Wilbur (Kathleen H.) Elementary","N160","4050 Wrangle Hill Road","Bear","DE","19701","302.832.6330","33.7");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Colonial","Wilmington Manor Elementary School","N160","200 East Roosevelt Avenue","New Castle","DE","1920","302.323.2901","48.7");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Indian River","Clayton (John M.) Elementary School","S790","252 Clayton Avenue","Frankford","DE","19945","302.732.3808","62");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Indian River","East Millsboro Elementary School","S790","29346 Iron Branch Road","Millsboro","DE","19966","302.934.3222","46.2");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Indian River","Ennis (Howard T.) School","S790","20436 Ennis Road","Georgetown","DE","19947","302.856.1930","43.5");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Indian River","Georgetown Elementary School","S790","301-A West Market Street","Georgetown","DE","19947","302.856.1940","61.4");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Indian River","Long Neck Elementary School","S790","26064 School Lane","Millsboro","DE","19966","302.945.6200","56");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Indian River","Lord Baltimore Elementary School","S790","120 Atlantic Avenue","Ocean View","DE","19970","302.537.2700","32");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Indian River","North Georgetown Elementary School","S790","664 North Bedford Street","Georgetown","DE","19947","302.855.2430","60.2");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Indian River","Showell (Phillip C.) Elementary School","S790","41 Bethany Road","Selbyville","DE","19975","302.436.1040","58.3");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Indian River","Southern Delaware School of the Arts","S790","27 Hosier Street","Selbyville","DE","19975","302.436.1066","21");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Lake Forest","Lake Forest Central Elementary School","S690","5424 Killens Pond Road","Felton","DE","19943","302.284.5810","48.2");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Lake Forest","Lake Forest East Elementary School","S690","124 West Front Street","Frederica","DE","19946","302.335.5261","53.1");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Lake Forest","Lake Forest North Elementary School","S690","319 East Main Street","Felton","DE","19943","302.284.9611","46.5");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Lake Forest","Lake Forest South Elementary School","S690","301 Dorman Street","Harrington","DE","19952","302.398.8011","57.4");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Laurel","North Laurel Elementary School","S770","300 Wilson Street","Laurel","DE","19956","302.875.6130","59.3");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Milford","Banneker (Benjamin) Elementary School","S180","449 North Street","Milford","DE","19963","302.422.1630","56.2");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Milford","Mispillion Elementary","S180","311 Lovers Lane","Milford","DE","19963","302.424.5800","56");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Milford","Ross (Lulu M.) Elementary School","S180","310 Lovers Lane","Milford","DE","19963","302.422.1640","50.8");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Red Clay","Baltz (Austin D.) Elementary School","N270","1500 Spruce Avenue","Wilmington","DE","19805","302.992.5560","61.8");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Red Clay","Brandywine Springs School","N270","2916 Duncan Road","Wilmington","DE","19808","302.636.5681","9.2");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Red Clay","Delaware College Preparatory Academy","","510 W. 28th Street","Wilmington","DE","19802","302.762.7424","82.1");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Red Clay","Forest Oak Elementary School","N270","55 South Meadowood Drive","Newark","DE","19711","302.454.3420","37");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Red Clay","Heritage Elementary School","N270","2815 Highlands Lane","Wilmington","DE","19808","302.454.3424","23.5");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Red Clay","Highlands Elementary School","N270","2100 Gilpin Avenue","Wilmington","DE","19806","302.651.2715","68.1");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Red Clay","Lewis (William C.) Dual Language Elementary School","N270","920 North Van Buren Street","Wilmington","DE","19806","302.651.2695","74.9");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Red Clay","Linden Hill Elementary School","N270","3415 Skyline Drive","Wilmington","DE","19808","302.454.3406","10.1");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Red Clay","Marbrook Elementary School","N270","2101 Centerville Road","Wilmington","DE","19808","302.992.5555","56.3");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Red Clay","Mote (Anna P.) Elementary School","N270","2110 Edwards Avenue","Wilmington","DE","19808","302.992.5565","56.5");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Red Clay","North Star Elementary School","N270","1340 Little Baltimore Road","Hockessin","DE","19707","302.234.7200","3.9");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Red Clay","Richardson Park Learning Center","N270","99 Middleboro Road","Wilmington","DE","19804","302.992.5574","43.5");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Red Clay","Richardson Park Elementary School","N270","16 Idella Avenue","Wilmington","DE","19804","302.992.5570","69.9");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Red Clay","Richey Elementary School","N270","105 East Highland Avenue","Wilmington","DE","19804","302.992.5535","44.1");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Red Clay","Shortlidge (Evan G.) Academy","N270","100 West 18th Street","Wilmington","DE","19802","302.651.2710","85.4");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle","Red Clay","Warner Elementary School","N270","801 West 18th Street","Wilmington","DE","19802","302.651.2740","85");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Seaford","Blades Elementary School","S730","900 South Arch Street","Blades","DE","19973","302.628.4416","64.2");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Seaford","Frederick Douglass Elementary School","S730","1 Swain Road","Seaford","DE","19973","302.628.4413","64");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Seaford","Seaford Central Elementary School","S730","1 Delaware Place","Seaford","DE","19973","302.629.4587","47");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Seaford","West Seaford Elementary School","S730","511 Sussex Avenue","Seaford","DE","19973","302.628.4414","65.6");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Smyrna","Clayton Elementary School","N460","510 West Main Street","Clayton","DE","19938","302.653.8587","30");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Smyrna","North Smyrna Elementary School","N460","365 North Main Street","Smyrna","DE","19977","302.653.8589","47.8");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Smyrna","Smyrna Elementary School","N460","121 South School Lane","Smyrna","DE","19977","302.653.8588","32.8");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent","Smyrna","Sunnyside Elementary School","N460","123 Rabbit Chase Road","Smyrna","DE","19977","302.653.8580","25.3");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex","Woodbridge","Woodbridge Elementary School","S710"," 400 Governors Avenue","Greenwood","DE","19950","302.349.4010","58");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent",null,"Academy Of Dover Charter School","N/A","104 Saulsbury Road","Dover","DE","19904","302.674.0684","68.8");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent",null,"Campus Community School","N/A","350 Pear Street","Dover","DE","19904","302.736.0403","39.2");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"East Side Charter School","N/A","3000 North Claymont Street","Wilmington","DE","19802","302.762.5834","84.1");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Edison (Thomas A.) Charter School","N/A","2200 North Locust Street","Wilmington","DE","19802","302.778.1101","80.9");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Family Foundations Academy","N/A","1101 Delaware Street","New Castle","DE","19720","302.324.8901","49.9");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"First State Montessori Academy","N/A","1000 North French Street","Wilmington","DE","19801","302.576.1500",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Gateway Lab School","N/A","2501 Centerville Road","Wilmington","DE","19808","302.633.4091","27.9");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Kuumba Academy Charter School","N/A","519 North Market Street","Wilmington","DE","19801","302.472.6450","63.1");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Las Americas ASPIRA Academy","N/A","326 Ruthar Drive","Newark","DE","19711","302.292.1463","27.8");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"MOT Charter School","N/A","1156 Levels Road","Middletown","DE","19709","302.376.5125","4.9");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Newark Charter School","N/A","2001 Patriot Way","Newark","DE","19711","302.369.2001","8.4");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Odyssey Charter School","N/A","201 Bayard Avenue","Wilmington","DE","19805","302.655.5760","17.8");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Providence Creek Academy Charter School","N/A","273 West Duck Creek Road","Clayton","DE","19938","302.653.6276","24.5");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Reach Academy for Girls","N/A","170 Lukens Drive","New Castle","DE","19720","302.654.3720","59");
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Corpus Christie  Elementary School","N/A","907 New Road Elsmere","Wilmington","DE","19805","302.995.2231",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Holy Angels School","N/A","82 Possum Park Road","Newark","DE","19711","302.731.2210",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Kent",null,"Holy Cross Elementary School","N/A","631 South State Street","Dover","DE","19901","302.674.5787",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Holy Rosary School","N/A","3210 Philadelphia Pike","Claymont","DE","19703","302.798.5584",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Holy Spirit School","N/A","Church Drive","New Castle","DE","19720","302.658.5345",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Immaculate Heat of Mary School","N/A","1000 Shipley Road","Wilmington","DE","19803","302.764.0977",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Our Lady of Fatima School","N/A","801 N . Dupont Highway","New Castle","DE","19720","302.328.2803",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"St. Ann's School","N/A","2006 Shallcross Avenue","Wilmington","DE","19806","302.652.6567",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"St. Anthony of Padua School","N/A","1715 West 9th Street","Wilmington","DE","19805","302.421.3743",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"St. Catherine of Siena School","N/A","2503 Centerville Road","Wilmington","DE","19808","302.633.4900",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"St. Edmond's Academy","N/A","2120 Veale Road","Wilmington","DE","19810","302.475.5370",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"St. Hedwig School","N/A","408 South Harrison Street","Wilmington","DE","19805","302.594.1402",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"St. Helena School","N/A","210 Bellefonte Avenue","Wilmington","DE","19809","302.762.5595",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"St. John the Beloved School","N/A","907 Milltown Road","Wilmington","DE","19808","302.998.5525",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"St. Mary Magdalen School","N/A","9 Sharpley Road","Wilmington","DE","19803","302.656.2745",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"St. Matthews School","N/A","1 Fallon Avenue","Wilmington","DE","19804","302.633.5860",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"St. Paul Elementary School","N/A","312 N Van Buren Street","Wilmington","DE","19805","302.656.1372",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"St. Peter Cathedral School","N/A","310 West 6th Street","Wilmington","DE","19801","302.656.5234",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"St. Peter the Apostle","N/A","515 Harmony Street","New Castle","DE","19720","302.328.1191",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"St. Thomas the Apostle School","N/A","201 Bayard Avenue","Wilmington","DE","19805","302.658.5131",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Ursuline Academy","N/A","1106 Pennsylvania Avenue","Wilmington","DE","19808","302.658.7158",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Caravel Academy","N/A","2801 Del Laws Road","Bear","DE","19701","302.834.8938",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Greenwood Mennonite School","N/A","12525 Shawnee Road","Greenwood","DE","19950","302.349.5296",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"Sanford School","N/A","6900 Lancaster Pike","Hockessin","DE","19707","302.235.6500",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("New Castle",null,"The College School","N/A","459 Wyoming Road","Newark","DE","19716","302.831.0222",null);
insert into School ( School_county,School_district,School_name,school_SLC, School_addr1
  , School_city,school_state,school_zip,school_phone,school_lmi_eligible)   VALUES
("Sussex",null,"Eagles Nest Christian School","N/A","26633 Zion Church Road","Milton","DE","19968","302.684.3149",null);



insert into Bank (bank_name) VALUES ("AIG Federal Savings Bank");
insert into Bank (bank_name) VALUES ("Applied Bank");
insert into Bank (bank_name) VALUES ("Artisan's Bank");
insert into Bank (bank_name) VALUES ("Bank of America");
insert into Bank (bank_name) VALUES ("Barclay's Bank Delaware");
insert into Bank (bank_name) VALUES ("BNY Mellon Trust of Delaware");
insert into Bank (bank_name) VALUES ("Brandywine Trust Company");
insert into Bank (bank_name) VALUES ("Brown Brothers Harriman Trust Company");
insert into Bank (bank_name) VALUES ("Capital One");
insert into Bank (bank_name) VALUES ("Charles Schwab Bank");
insert into Bank (bank_name) VALUES ("Chase Bank USA");
insert into Bank (bank_name) VALUES ("CNB");
insert into Bank (bank_name) VALUES ("Comenity Bank");
insert into Bank (bank_name) VALUES ("Commonwealth Trust Company");
insert into Bank (bank_name) VALUES ("Community Bank Delaware");
insert into Bank (bank_name) VALUES ("County Bank");
insert into Bank (bank_name) VALUES ("Deutsche Bank Trust Company Delaware");
insert into Bank (bank_name) VALUES ("Discover Bank");
insert into Bank (bank_name) VALUES ("Fulton Bank");
insert into Bank (bank_name) VALUES ("Glenmede");
insert into Bank (bank_name) VALUES ("HSBC Trust Company");
insert into Bank (bank_name) VALUES ("JPMorgan Trust Company of Delaware");
insert into Bank (bank_name) VALUES ("Key National Trust Company of Delaware");
insert into Bank (bank_name) VALUES ("M&T Bank");
insert into Bank (bank_name) VALUES ("MidCoast Community Bank");
insert into Bank (bank_name) VALUES ("Morgan Stanley Private Bank");
insert into Bank (bank_name) VALUES ("PNC Bank");
insert into Bank (bank_name) VALUES ("Principal Trust Company");
insert into Bank (bank_name) VALUES ("The First National Bank of Wyoming");
insert into Bank (bank_name) VALUES ("UBS Trust Company");
insert into Bank (bank_name) VALUES ("Wells Fargo Bank");
insert into Bank (bank_name) VALUES ("Wilmington Trust Company");
insert into Bank (bank_name) VALUES ("WSFS Bank");


insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('Urdqicfklbw=','I2uKh9UDwqkhLOppWX4haBMFK+M=','bonnie@mcherm.com','Bonnie','Charles','SA',null,'1-800-234-1234','0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('kNF87zO+5bo=','JcDDbNz0dXVdUvDEzfzVmNEhHjk=','larry@foobar.com','Larry','Smith','V','1','1-800-234-1234','0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('84oH5LTcO30=','inZgchLYXtt2wor3razfPvtub+o=','janedoe@foobar.com','Jane','Jones','BA','1','1000001','0');
insert into User (password_salt, password_hash, email, first_name, last_name, access_type, organization_id, phone_number, user_status)
  values ('9HYa4nSj3Jg=','wrkttouWUjjV/sDVJh632UZvu5g=','lucy@foobar.com','Lucy','Adams','T','1','1-800-234-1234','0');
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
  (4,
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
  (4,
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


insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip)
  VALUES("AIG Federal Savings Bank","600 North King Street","Suite 2"
    ,"Wilmington","DE","19801");


insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Applied Bank","660 Plaza Drive","","Newark ","DE","19702");

insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Artisan's Bank","2961 Centerville Road","","Wilmington","DE","19808");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Bank of America","1100 North King Street","","Wilmington","DE","19801");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Barclay's Bank Delaware","100 South West Street","","Wilmington","DE","19801");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("BNY Mellon Trust of Delaware","301 Bellevue Parkway","Suite 19A-307","Wilmington","DE","19809");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Brandywine Trust Company","7234 Lancaster Pike","","Hockessin","DE","19707");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Brown Brothers Harriman Trust Company","919 North Market Street","Suite 420"
  ,"Wilmington","DE","19801");

insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Capital One","802 Delaware Avenue","","Wilmington","DE","19801");

insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Charles Schwab Bank","602 Delaware Avenue","","Wilmington","DE","19801");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Chase Bank USA","201 North Walnut Street","","Wilmington","DE","19801");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("CNB","4580 South DuPont Highway","","Camden","DE","19934");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Comenity Bank","1 Righter Parkway","","Wilmington","DE","19803");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Commonwealth Trust Company","29 Bancroft Mills Road","","Wilmington","DE","19806");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Community Bank Delaware","16982 Kings Highway","","Lewes","DE","19958");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("County Bank","19927 Shuttle Road","","Rehoboth Beach","DE","19971");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Deutsche Bank Trust Company Delaware","1011 Centre Road","Suite 200","Wilmington","DE","19805");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Discover Bank","12 Read's Way","","New Castle","DE","19720");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Fulton Bank","1013 Center Road","3rd Floor","Wilmington","DE","19805");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Glenmede","1201 North Market Street","Suite 1501","Wilmington","DE","19801");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("HSBC Trust Company","300 Delaware Avenue","Suite 1401","Wilmington","DE","19801");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("JPMorgan Trust Company of Delaware","500 Stanton Christiana Road","","Newark ","DE","19713");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Key National Trust Company of Delaware","1105 North Market Street","","Wilmington","DE","19801");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("M&T Bank","1100 North Market Street","","Wilmington","DE","19801");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("MidCoast Community Bank","974 Justison Street","","Wilmington","DE","19801");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Morgan Stanley Private Bank","919 North Market Street","","Wilmington","DE","19801");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("PNC Bank","222 Delaware Avenue","","Wilmington","DE","19899");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Principal Trust Company","1013 Center Road","","Wilmington","DE","19805");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("The First National Bank of Wyoming","120 West Camden-Wyoming Avenue","","Camden-Wyoming","DE","19934");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("UBS Trust Company","500 Delaware Avenue","9th Floor","Wilmington","DE","19801");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Wells Fargo Bank","814 Philadelphia Pike","","Wilmington","DE","19809");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("Wilmington Trust Company","100 North Market Street","","Wilmington","DE","19890");
insert into Bank2 (bank_name, bank_addr1, bank_addr2, bank_city
  ,bank_state,bank_zip) VALUES
("WSFS Bank","500 Delaware Avenue","","Wilmington","DE","19801");
