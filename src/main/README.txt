Deployment Instructions:

After checkout, before building, go to /src/main/resources and copy
"application.properties.template" into the file "application.properties", then fill
in all the passwords and stuff. Obviously, you need to get the passwords from
someplace that is NOT checked into version control.

Doing "mvn install" will build a war file which can be deployed on Tomcat (or other
application servers) to serve up the site. We are deploying it on Amazon Web Services.

In AWS, use Elastic Beanstalk (which will auto-create the necessary servers and such).

For now, I'm just keeping notes on what I did to create it. From the GUI, I selected
"New Environment". Then I said "Create web server". I seem to have a saved
configuration named "prod-config".

I imported the newly build .war file. I kept the scaling to single instance. I am
not sure what the environment name should be. I'm going to try "teachkidstosavedayde"
('kids' because 'children' is too long).

I am not creating an RDS DB instance with it (that's separately created). I am
using an instance type of "t2.micro". I turned OFF "enable rolling updates" and
"Cross zone load balancing" figuring that those don't apply when you just have one
server.

No tags. Default permissions. Launched it.

I had a lot of trouble after that -- mostly with the Route 53 setup. In the end, I
need a CNAME record for the www domain and an A record for the bare domain, with
the A record pointing to an S3 bucket which is configured to forward requests to
the www domain. Oh, and after setting it up, wait at least 5 minutes for the
changes to propogate (perhaps as much as 24 hours).

[NOTES:]

Before deleting the teachchildrentosavedayde.org hosted zone in Route 53, I
wanted to note that it had an odd record created:
  NAME: _amazonses.teachchildrentosavedayde.org
  TYPE: TXT
  VALUE: "IQjgGB8EIaTYb/RcyrUDjnuUpn755v90H7GufUVFhVA="
     (the quotes were part of it)
  TTL: 1800 seconds
------------
2022-01-07: Today I earned something about setting up a CloudFront
  distribution. Here are my notes:


    Creating a distribution:

    * Delete the old distributions that are using the names
      www.teachchildrentosaveday.org and teachchildrentosaveday.org.
    * Within the CloudFront console, "Create a Distribution".
    * For "Origin Domain", go to the EC2 console and look at load balancers.
      Find the one whose tag shows that it's associated with the elastic
      beanstalk enviroment you want. The Elastic Beanstalk environment MUST
      have a load balancer; it won't work if it's a single instance. Copy
      the load balancer name from here and put it in "Origin Domain".
    * For Protocol (in the section on Origin) it should say "HTTP only".
    * In the section on Default cache behavior, set the Viewer protocol
      policy to "Redirect HTTP to HTTPS".
    * Set "Allowed HTTP Methods" to the one that includes PUT, POST, PATCH,
      and DELETE.
    * In "Cache key and origin requests" select "Cache policy and origin
      request policy".
    * Under that, set the Cache policy to "Cache-Using-JSESSIONID" which is
      a custom policy I have created that includes the cookie JSESSIONID as
      part of the cache key.
    * Leave "Origin request policy" blank.
    * In the section for Settings, set the Price class to "Use only North
      America and Europe".
    * Under Alternate domain name (CNAME) click "Add item" twice.
    * Populate those fields with "teachchildrentosaveday.org" and
      "www.teachchildrentosaveday.org".
    * Under Custom SSL certificate, select the certificate for
      www.teachchildrentosaveday.org.
    * At the bottom, click "Create Distribution".
    * WAIT for it to finish deploying.
    * Then go to the control panel for Route53.
    * Edit the A records for "teachchildrentosaveday.org" and
      "www.teachchildrentosaveday.org" to refer to the new CloudFront
      distribution.
    * WAIT for it to propogate. (At LEAST 60 seconds... I'd suggest waiting
      > 4 minutes.)
    * Then test it.
