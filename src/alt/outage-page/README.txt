Instructions on how to deploy this:

(These instructions are rough... I just wrote them and tried it once. There could be errors.)

 * In S3, there are two buckets named "teachchildrentosaveday.org" and "www.teachchildrentosaveday.org".
   The files from this directory should be deployed into BOTH of those buckets.
 * In Route 53, there is a hosted zone for the domain teachchildrentosaveday.org. This has two
   type A records for the names "teachchildrentosaveday.org" and "www.teachchildrentosaveday.org".
   Both of the records say "Alias: Yes" and have an alias target of "s3-website-us-east-1.amazonaws.com".
   There's an alias hosted zone id of Z3AQBSTGFYJSTF. The routing policy is "simple".

   There's some other stuff too, but I'm guessing it's not important to this. I'm not sure.
  * When putting it back, you probably want something like this:
    - teachchildrentosaveday.org
      - A : ALIAS prod-tcts-env.us-east-1.elasticbeanstalk.com
      - NS : ns-1205.awsdns-22.org
             ns-978.awsdns-58.net
             ns-281.awsdns-35.com
             ns-1645.awsdns-13.co.uk
      - SOA : ns-1205.awsdns-22.org. awsdns-hostmaster.amazon.com. 1 7200 900 1209600 86400
    - _amazonses .teachchildrentosaveday.org
      - TXT : "IQjgGB8EIaTYb/RcyrUDjnuUpn755v90H7GufUVFhVA="
    -  www.teachchildrentosaveday.org
      - A : prod-tcts-env.us-east-1.elasticbeanstalk.com.
