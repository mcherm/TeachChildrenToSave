package com.tcts.database;

import com.tcts.common.Configuration;
import com.tcts.database.dynamodb.DynamoDBHelper;
import com.tcts.database.dynamodb.ItemBuilder;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Arrays;

/**
 * A class that I am using to load new school data into the database from a spreadsheet.
 */
public class DBLoadSchoolData {
    //     County,School District,School Name,SLC,Address,City,State,Zip,Phone #,LMI%
    final static String CSV_DATA = """
New Castle,Appoquinimink,Brick Mill Elementary School,N120,378 Brick Mill Road,Middletown,DE,19709,302-378-5288,21.3
New Castle,Appoquinimink,Bunker Hill Elementary School,N120,1070 Bunker Hill Road,Middletown,DE,19709,302-378-5135,15.4
New Castle,Appoquinimink,Crystal Run Elementary School,N120,1470 Aviator Wy,Middletown,DE,19709,302-376-4128,
New Castle,Appoquinimink,Cedar Lane Elementary School,N120,1221 Cedar Lane Road,Middletown,DE,19709,302-378-5045,13
New Castle,Appoquinimink,Lorewood Grove Elementary School,N120,820 Mapleton Ave.,Middletown,DE,19709, 302-842-2575,
New Castle,Appoquinimink,Loss (Olive B.) Elementary School,N120,200 Brennan Boulevard,Bear,DE,19701,302-832-1343,7.7
New Castle,Appoquinimink,Old State Elementary School,N120,580 Tony Marchio Drive,Townsend,DE,19734,302-378-6720,31.5
New Castle,Appoquinimink,Silver Lake Elementary School,N120,200 E. Cochran Street,Middletown,DE,19709,302-378-5023,31.6
New Castle,Appoquinimink,Townsend Elementary School,N120,126 Main Street,Townsend,DE,19734,302-378-5020,31.5
New Castle,Brandywine,Carrcroft Elementary School,N280,503 Crest Road,Wilmington,DE,19803,302-762-7165,36.1
New Castle,Brandywine,Claymont Elementary School,N280,3401 Green Street,Claymont,DE,19703,302-792-3880,42.2
New Castle,Brandywine,Forwood Elementary School,N280,1900 Westminster Drive,Wilmington,DE,19810,302-475-3956,24.9
New Castle,Brandywine,Hanby Elementary School,N280,2523 Berwyn Road,Wilmington,DE,19810,302-479-2220,23.9
New Castle,Brandywine,Harlan (David W.) Elementary School,N280,3601 Jefferson Street,Wilmington,DE,19802,302-762-7156,61
New Castle,Brandywine,Lancashire Elementary School,N280,200 Naamans Road,Wilmington,DE,19810,302-475-3990,25.2
New Castle,Brandywine,Lombardy Elementary School,N280,412 Foulk Road,Wilmington,DE,19803,302-762-7190,26.9
New Castle,Brandywine,Maple Lane Elementary School,N280,100 Maple Lane,Claymont,DE,19703,302-792-3906,46.9
New Castle,Brandywine,Mount Pleasant Elementary School,N280,500 Duncan Road,Wilmington,DE,19809,302-762-7120,35.3
Kent,Caesar Rodney,Brown (W. Reily) Elementary School,D280,360 Webbs Lane,Dover,DE,19904,302-697-2101,58
Kent,Caesar Rodney,David E. Robinson Elementary School,D280,1038 Briarbush Rd,Magnolia,DE,19962,302-698-4230,
Kent,Caesar Rodney,Frear (Allen) Elementary School,D280,238 Sorghum Mill Road,Camden,DE,19934,302-697-3279,35.3
Kent,Caesar Rodney,Kent Elementary Intensive Learning Center,D280,5 Old North Road,Camden,DE,19934,302-697-3504,75.6
Kent,Caesar Rodney,Simpson (W.B.) Elementary School,D280,5 Old North Road,Camden,DE,19934,302-697-3207,38.8
Kent,Caesar Rodney,Star Hill Elementary School,D280,594 Voshells Mill/Star Hill Road,Dover,DE,19901,302-697-6117,25.8
Kent,Caesar Rodney,Stokes (Nellie Hughes) Elementary School,D280,3874 Upper King Road,Dover,DE,19904,302-697-3205,49.6
Kent,Caesar Rodney,Welch (Major George S.) Elementary School,D280,3100 Hawthorne Drive,Dover,DE,19901,302-674-9080,5.6
Sussex,Cape Henlopen,H. O. Brittingham Elementary School,S150,400 Mulberry Street,Milton,DE,19968,302-684-8522,65.5
Sussex,Cape Henlopen,Milton Elementary School,S150,512 Federal Street,Milton,DE,19968,302-684-2516,30.6
Sussex,Cape Henlopen,Love Creek Elementary School,S150,19488 John J. Williams Hwy,Lewes,DE,19958,302-703-3456,
Sussex,Cape Henlopen,Lewes Elementary School,S150,820 Savannah Road,Lewes,DE,19958,302-645-7748,
Sussex,Cape Henlopen,Rehoboth Elementary School,S150,500 Stockely Street,Rehoboth,DE,19971,302-227-2571,37
Sussex,Cape Henlopen,Shields (Richard A.) Elementary School,S150,910 Shields Avenue,Lewes,DE,19958,302-645-7748,32.1
Sussex,Cape Henlopen,Sussex Consortium,S150,520 duPont Avenue,Lewes,DE,19958,302-645-7210,41.2
Kent,Capital,Booker T. Washington Elementary School,D103,901 Forest Street,Dover,DE,19904,302-672-1900,57.4
Kent,Capital,East Dover Elementary School,D103,852 South Little Creek Road,Dover,DE,19901,302-672-1655,67.7
Kent,Capital,Fairview Elementary School,D103,700 Walker Road,Dover,DE,19904,302-672-1645,62.5
Kent,Capital,Hartly Elementary School,D103,2617 Arthursville Road,Hartly,DE,19953,302-492-1870,40.6
Kent,Capital,Kent County Community School,D103,65-1 Carver Road,Dover,DE,19904,302-672-1960,41.8
Kent,Capital,North Dover Elementary School,D103,855 College Road,Dover,DE,19904,302-672-1980,53.7
Kent,Capital,South Dover Elementary School,D103,955 South State Street,Dover,DE,19901,302-672-1690,63.6
Kent,Capital,Towne Point Elementary School,D103,629 Buckson Drive,Dover,DE,19901,302-672-1590,66.3
New Castle,Christina,Bancroft Elementary School,N410,700 North Lombard Street,Wilmington,DE,19801,302-429-4102,79
New Castle,Christina,Brader (Henry M.) Elementary School,N410,350 Four Seasons Parkway,Newark,DE,19702,302-454-5959,37.3
New Castle,Christina,The Brennen School,N410,144 Brennen Drive,Newark,DE,19713,302-722-2758,
New Castle,Christina,The Bayard School,N410,200 South duPont Street,Wilmington,DE,19805,302-429-4134,
New Castle,Christina,Brookside Elementary School,N410,800 Marrows Road,Newark,DE,19713,302-454-5454,63
New Castle,Christina,Delaware School for the Deaf Elementary,N410,630 East Chestnut Hill Road,Newark,DE,19713,302-454-2301,34.8
New Castle,Christina,Downes (John R.) Elementary School,N410,220 Casho Mill Road,Newark,DE,19711,302-454-2133,30.2
New Castle,Christina,Elbert-Palmer Elementary School,N410,1210 Lobdell Street,Wilmington,DE,19801,302-429-4188,89.8
New Castle,Christina,Gallaher (Robert S.) Elementary School,N410,800 North Brownleaf Road,Newark,DE,19713,302-454-2464,39.2
New Castle,Christina,Jones (Albert H.) Elementary School,N410,35 West Main Street,Newark,DE,19702,302-454-2131,48.7
New Castle,Christina,Keene (William B.) Elementary School,N410,200 LaGrange Avenue,Newark,DE,19702,302-454-2018,35.1
New Castle,Christina,Leasure (May B.) Elementary School,N410,1015 Church Road,Newark,DE,19702,302-454-2103,44.4
New Castle,Christina,Maclary (R. Elisabeth) Elementary School,N410,300 St. Regis Drive,Newark,DE,19711,302-454-2142,31.3
New Castle,Christina,Marshall (Thurgood) Elementary School,N410,101 Barrett Run Road,Newark,DE,19702,302-454-4700,26.1
New Castle,Christina,McVey (Joseph M.) Elementary School,N410,908 Janice Drive,Newark,DE,19713,302-454-2145,50.8
New Castle,Christina,Montessori Academy Wilmington,N410,700 North Lombard Street,Wilmington,DE,19801,302-429-4102,79
New Castle,Christina,Oberle (William) Elementary School,N410,500 South Caledonia Way,Bear,DE,19701,302-834-5910,57.9
New Castle,Christina,Pulaski (Casimir) Elementary School,N410,1300 Cedar Street,Wilmington,DE,19805,302-429-4136,81.6
New Castle,Christina,Smith (Jennie E.) Elementary School,N410,142 Brennen Drive,Newark,DE,19713,302-454-2174,45.2
New Castle,Christina,Stubbs (Frederick Douglass) Elementary School,N410,1100 North Pine Street,Wilmington,DE,19801,302-429-4175,89.2
New Castle,Christina,West Park Place Elementary School,N410,193 West Park Place,Newark,DE,19711,302-454-2290,31.2
New Castle,Christina,Wilson (Etta J.) Elementary School,N410,14 Forge Road,Newark,DE,19711,302-454-2180,39.2
New Castle,Colonial,Castle Hills Elementary School,N160,502 Moores Lane,New Castle,DE,19720,302-323-2915,60.8
New Castle,Colonial,Downie (Carrie) Elementary School,N160,1201 Delaware Street,New Castle,DE,19720,302-323-2926,56.7
New Castle,Colonial,Eisenberg (Harry O.) Elementary School,N160,27 Landers Lane,New Castle,DE,19720,302-429-4074,67.8
New Castle,Colonial,New Castle Elementary School,N160,903 Delaware Street,New Castle,DE,19720,302-323-2880,59
New Castle,Colonial,Pleasantville Elementary School,N160,16 Pleasant Street,New Castle,DE,19720,302-323-2935,46.2
New Castle,Colonial ,Leach (John G.) School,N160,10 Landers Lane,New Castle,DE,19720,302-429-4055,
New Castle,Colonial,Southern Elementary School,N160,795 Cox Neck Road,New Castle,DE,19720,302-832-6300,36.5
New Castle,Colonial,Wilbur (Kathleen H.) Elementary,N160,4050 Wrangle Hill Road,Bear,DE,19701,302-832-6330,33.7
New Castle,Colonial,Wilmington Manor Elementary School,N160,200 East Roosevelt Avenue,New Castle,DE,1920,302-323-2901,48.7
Sussex,Indian River,John M. Clayton Elementary School (Frankford),S790,252 Clayton Avenue,Frankford,DE,19945,302-732-3808,62
Sussex,Indian River,East Millsboro Elementary School,S790,29346 Iron Branch Road,Millsboro,DE,19966,302-934-3222,46.2
Sussex,Indian River,Ennis (Howard T.) School,S790,20436 Ennis Road,Georgetown,DE,19947,302-856-1930,43.5
Sussex,Indian River,Long Neck Elementary School,S790,26064 School Lane,Millsboro,DE,19966,302-945-6200,56
Sussex,Indian River,Lord Baltimore Elementary School,S790,120 Atlantic Avenue,Ocean View,DE,19970,302-537-2700,32
Sussex,Indian River,North Georgetown Elementary School,S790,664 North Bedford Street,Georgetown,DE,19947,302-855-2430,60.2
Sussex,Indian River,Showell (Phillip C.) Elementary School,S790,41 Bethany Road,Selbyville,DE,19975,302-436-1040,58.3
Sussex,Indian River,Southern Delaware School of the Arts,S790,27 Hosier Street,Selbyville,DE,19975,302-436-1066,21
Kent,Lake Forest,Lake Forest Central Elementary School,S690,5424 Killens Pond Road,Felton,DE,19943,302-284-5810,48.2
Kent,Lake Forest,Lake Forest East Elementary School,S690,124 West Front Street,Frederica,DE,19946,302-335-5261,53.1
Kent,Lake Forest,Lake Forest North Elementary School,S690,319 East Main Street,Felton,DE,19943,302-284-9611,46.5
Kent,Lake Forest,Lake Forest South Elementary School,S690,301 Dorman Street,Harrington,DE,19952,302-398-8011,57.4
Sussex,Laurel,Laurel Elementary School,S770,1160 S Central Ave,Laurel,DE,19956,302-875-6195,59.3
Sussex,Milford,Banneker (Benjamin) Elementary School,S180,449 North Street,Milford,DE,19963,302-422-1630,56.2
Sussex,Milford,Mispillion Elementary,S180,311 Lovers Lane,Milford,DE,19963,302-424-5800,56
Sussex,Milford,Ross (Lulu M.) Elementary School,S180,310 Lovers Lane,Milford,DE,19963,302-422-1640,50.8
New Castle,Red Clay,Baltz (Austin D.) Elementary School,N270,1500 Spruce Avenue,Wilmington,DE,19805,302-992-5560,61.8
New Castle,Red Clay,Brandywine Springs Elementary School,N270,2916 Duncan Road,Wilmington,DE,19808,302-636-5681,9.2
New Castle,Red Clay,Forest Oak Elementary School,N270,55 South Meadowood Drive,Newark,DE,19711,302-454-3420,37
New Castle,Red Clay,Heritage Elementary School,N270,2815 Highlands Lane,Wilmington,DE,19808,302-454-3424,23.5
New Castle,Red Clay,Johnson (Joseph E. Jr) Elementary School,N270,2100 Gilpin Avenue,Wilmington,DE,19806,302-651-2715,68.1
New Castle,Red Clay,Lewis (William C.) Dual Language Elementary School,N270,920 North Van Buren Street,Wilmington,DE,19806,302-651-2695,74.9
New Castle,Red Clay,Linden Hill Elementary School,N270,3415 Skyline Drive,Wilmington,DE,19808,302-454-3406,10.1
New Castle,Red Clay,Marbrook Elementary School,N270,2101 Centerville Road,Wilmington,DE,19808,302-992-5555,56.3
New Castle,Red Clay,Mote (Anna P.) Elementary School,N270,2110 Edwards Avenue,Wilmington,DE,19808,302-992-5565,56.5
New Castle,Red Clay,North Star Elementary School,N270,1340 Little Baltimore Road,Hockessin,DE,19707,302-234-7200,3.9
New Castle,Red Clay,Richardson Park Elementary School,N270,16 Idella Avenue,Wilmington,DE,19804,302-992-5570,69.9
New Castle,Red Clay,Richey Elementary School,N270,105 East Highland Avenue,Wilmington,DE,19804,302-992-5535,44.1
New Castle,Red Clay,Shortlidge (Evan G.) Academy,N270,100 West 18th Street,Wilmington,DE,19802,302-651-2710,85.4
New Castle,Red Clay,William Cooke Jr. Elementary School,N270,2025 Graves Road,Hockessin,DE,19707,302-235-6600,
New Castle,Red Clay,Warner Elementary School,N270,801 West 18th Street,Wilmington,DE,19802,302-651-2740,85
Sussex,Seaford,Blades Elementary School,S730,900 South Arch Street,Blades,DE,19973,302-628-4416,64.2
Sussex,Seaford,Frederick Douglass Elementary School,S730,1 Swain Road,Seaford,DE,19973,302-628-4413,64
Sussex,Seaford,Seaford Central Elementary School,S730,1 Delaware Place,Seaford,DE,19973,302-629-4587,47
Sussex,Seaford,West Seaford Elementary School,S730,511 Sussex Avenue,Seaford,DE,19973,302-628-4414,65.6
Kent,Smyrna,Clayton Elementary School (Smyrna),N460,510 West Main Street,Clayton,DE,19938,302-653-8587,30
Kent,Smyrna,Moore (John Bassett) Intermediate School,N460,20 West Frazier Street,Smyrna,DE,19977,302-659-6297,
Kent,Smyrna,North Smyrna Elementary School,N460,365 North Main Street,Smyrna,DE,19977,302-653-8589,47.8
Kent,Smyrna,Smyrna Elementary School,N460,121 South School Lane,Smyrna,DE,19977,302-653-8588,32.8
Kent,Smyrna,Sunnyside Elementary School,N460,123 Rabbit Chase Road,Smyrna,DE,19977,302-653-8580,25.3
Sussex,Woodbridge,Phillis Wheatley Elementary School,S710,48 Church Street,Bridgeville,DE,19933,302-337-3469,
Kent,,Academy Of Dover Charter School,N/A,104 Saulsbury Road,Dover,DE,19904,302-674-0684,68.8
Kent,,Campus Community School,N/A,350 Pear Street,Dover,DE,19904,302-736-0403,39.2
New Castle,,East Side Charter School,N/A,3000 North Claymont Street,Wilmington,DE,19802,302-762-5834,84.1
New Castle,,Edison (Thomas A.) Charter School,N/A,2200 North Locust Street,Wilmington,DE,19802,302-778-1101,80.9
New Castle,,Family Foundations Academy,N/A,1101 Delaware Street,New Castle,DE,19720,302-324-8901,49.9
New Castle,,First State Montessori Academy,N/A,1000 North French Street,Wilmington,DE,19801,302-576-1500,
New Castle,,Gateway Lab School,N/A,2501 Centerville Road,Wilmington,DE,19808,302-633-4091,27.9
New Castle,,Kuumba Academy Charter School,N/A,519 North Market Street,Wilmington,DE,19801,302-472-6450,63.1
New Castle,,Las Americas ASPIRA Academy,N/A,326 Ruthar Drive,Newark,DE,19711,302-292-1463,27.8
New Castle,,MOT Charter School,N/A,1156 Levels Road,Middletown,DE,19709,302-376-5125,4.9
New Castle,,Newark Charter School,N/A,2001 Patriot Way,Newark,DE,19711,302-369-2001,8.4
New Castle,,Odyssey Charter School,N/A,201 Bayard Avenue,Wilmington,DE,19805,302-655-5760,17.8
New Castle,,Providence Creek Academy Charter School,N/A,273 West Duck Creek Road,Clayton,DE,19938,302-653-6276,24.5
New Castle,,Reach Academy for Girls,N/A,170 Lukens Drive,New Castle,DE,19720,302-654-3720,59
New Castle,,Wilmington Montessori School,N/A,1400 Harvey Road,Wilmington,DE,19810,302-475-0555,
New Castle,,Delaware College Preparatory Academy,N/A,510 W. 28th Street,Wilmington,DE,19802,302-762-7424,82.1
New Castle,,Corpus Christie  Elementary School,N/A,907 New Road Elsmere,Wilmington,DE,19805,302-995-2231,
New Castle,,Holy Angels School,N/A,82 Possum Park Road,Newark,DE,19711,302-731-2210,
Kent,,Holy Cross Elementary School,N/A,631 South State Street,Dover,DE,19901,302-674-5787,
New Castle,,Holy Rosary School,N/A,3210 Philadelphia Pike,Claymont,DE,19703,302-798-5584,
New Castle,,Holy Spirit School,N/A,Church Drive,New Castle,DE,19720,302-658-5345,
New Castle,,Immaculate Heart of Mary School,N/A,1000 Shipley Road,Wilmington,DE,19803,302-764-0977,
New Castle,,Our Lady of Fatima School,N/A,801 N . Dupont Highway,New Castle,DE,19720,302-328-2803,
New Castle,,St. Ann's School,N/A,2006 Shallcross Avenue,Wilmington,DE,19806,302-652-6567,
New Castle,,St. Anthony of Padua School,N/A,1715 West 9th Street,Wilmington,DE,19805,302-421-3743,
New Castle,,St. Catherine of Siena School,N/A,2503 Centerville Road,Wilmington,DE,19808,302-633-4900,
New Castle,,St. Edmond's Academy,N/A,2120 Veale Road,Wilmington,DE,19810,302-475-5370,
New Castle,,St. Hedwig School,N/A,408 South Harrison Street,Wilmington,DE,19805,302-594-1402,
New Castle,,St. Helena School,N/A,210 Bellefonte Avenue,Wilmington,DE,19809,302-762-5595,
New Castle,,St. John the Beloved School,N/A,907 Milltown Road,Wilmington,DE,19808,302-998-5525,
New Castle,,St. Mary Magdalen School,N/A,9 Sharpley Road,Wilmington,DE,19803,302-656-2745,
New Castle,,St. Matthews School,N/A,1 Fallon Avenue,Wilmington,DE,19804,302-633-5860,
New Castle,,St. Paul Elementary School,N/A,312 N Van Buren Street,Wilmington,DE,19805,302-656-1372,
New Castle,,St. Peter Cathedral School,N/A,310 West 6th Street,Wilmington,DE,19801,302-656-5234,
New Castle,,St. Peter the Apostle,N/A,515 Harmony Street,New Castle,DE,19720,302-328-1191,
New Castle,,St. Thomas the Apostle School,N/A,201 Bayard Avenue,Wilmington,DE,19805,302-658-5131,
New Castle,,Ursuline Academy,N/A,1106 Pennsylvania Avenue,Wilmington,DE,19808,302-658-7158,
New Castle,,Christ the Teacher Catholic School,N/A,2451 Frazer Road,Newark,DE,19702,302-838-8850,
New Castle,,Wilmington Christian School,N/A,825 Loveville Road,Hockessin,DE,19707,302-239-2121,
New Castle,,The Hockessin Montessori School,N/A,1000 Old Lancaster Pike,Hockessin,DE,19707,302-234-1240,
New Castle,,St. Anne's Episcopal School,N/A,211 Silver Lake Road,Middletown,DE,19709,302-378-3179,
New Castle,,Red Lion Christian Academy,N/A,1390 Red Lion Road,Bear,DE,19701,302-834-2526,
New Castle,,Caravel Academy,N/A,2801 Del Laws Road,Bear,DE,19701,302-834-8938,
Sussex,,Eagles Nest Christian School,N/A,26633 Zion Church Road,Milton,DE,19968,302-684-3149,
New Castle,,Greenwood Mennonite School,N/A,12525 Shawnee Road,Greenwood,DE,19950,302-349-5296,
New Castle,,Sanford School,N/A,6900 Lancaster Pike,Hockessin,DE,19707,302-235-6500,
New Castle,,The College School,N/A,459 Wyoming Road,Newark,DE,19716,302-831-0222,
New Castle,,Wilmington Friends School,N/A,101 School Road,Wilmington,DE,19803,302-576-2900,
New Castle,,The Independence School,N/A,1300 Papermill Road,Newark,DE,19711,302-239-0330,
New Castle,,Centerville Layton School,N/A,6201 Kennett Pike,Centerville,DE,19807,302-571-0230,
New Castle,,Saint Edmond's Academy,N/A,2120 Veale Road,Wilmington,DE,19810,302-475-5370,
Sussex,,The Jefferson School,N/A,22051 Wilson Road,Georgetown,DE,19947,302-856-3300,
""";

    public static void main(String[] args) {

        final Configuration configuration = new Configuration();
        final DynamoDbClient dynamoDbClient = SingleTableDynamoDbDatabase.connectToDB(configuration);
        final String site = "DE"; // NOTE: The source site is hard-coded here
        final String environment = configuration.getProperty("dynamoDB.environment", "prod");
        final String singleTableName = "TCTS." + site + "." + environment;
        final DynamoDBHelper dynamoDBHelper = new DynamoDBHelper();

        final String[] lines = CSV_DATA.split("\n");
        Arrays.stream(lines)
            .forEach(line -> {
                final String schoolId = dynamoDBHelper.createUniqueId();
                final String[] parts = line.split(",", 10);
                assert parts.length == 10;

                //     County,School District,School Name,SLC,Address,City,State,Zip,Phone #,LMI%
                final String county = parts[0];
                final String district = parts[1];
                final String name = parts[2];
                final String slc = parts[3];
                final String address = parts[4];
                final String city = parts[5];
                final String state = parts[6];
                final String zip = parts[7];
                final String phone = parts[8];
                final String lmiStr = parts[9];

                final PutItemRequest putItemRequest = PutItemRequest.builder()
                    .tableName(singleTableName)
                    .item(new ItemBuilder("school", DatabaseField.school_id, schoolId)
                        .withString(DatabaseField.school_name, name)
                        .withString(DatabaseField.school_addr1, address)
                        .withString(DatabaseField.school_city, city)
                        .withString(DatabaseField.school_state, state)
                        .withString(DatabaseField.school_zip, zip)
                        .withString(DatabaseField.school_county, county)
                        .withString(DatabaseField.school_district, district)
                        .withString(DatabaseField.school_phone, phone)
                        .withString(DatabaseField.school_lmi_eligible, lmiStr)
                        .withString(DatabaseField.school_slc, slc)
                        .build())
                    .conditionExpression("attribute_not_exists(" + DatabaseField.table_key.name() + ")") // verify it is unique
                    .build();
                dynamoDbClient.putItem(putItemRequest);
            });
    }

}
