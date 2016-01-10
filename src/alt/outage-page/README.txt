Instructions on how to deploy this:

(These instructions are rough... I just wrote them and tried it once. There could be errors.)

 * In S3, there are two buckets named "teachchildrentosavedayde.org" and "www.teachchildrentosavedayde.org".
   The files from this directory should be deployed into BOTH of those buckets.
 * In Route 53, there is a hosted zone for the domain teachchildrentosavedayde.org. This has two
   type A records for the names "teachchildrentosavedayde.org" and "www.teachchildrentosavedayde.org".
   Both of the records say "Alias: Yes" and have an alias target of "s3-website-us-east-1.amazonaws.com".
   There's an alias hosted zone id of Z3AQBSTGFYJSTF. The routing policy is "simple".

   There's some other stuff too, but I'm guessing it's not important to this. I'm not sure.
