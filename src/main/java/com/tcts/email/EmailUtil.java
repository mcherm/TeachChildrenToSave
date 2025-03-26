package com.tcts.email;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import com.tcts.database.DatabaseFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.tcts.common.Configuration;
import com.tcts.exception.AppConfigurationException;


@Component
public final class EmailUtil {

    @Autowired
    Configuration configuration;

    @Autowired
    private DatabaseFacade database;

    public EmailUtil() {
        // FIXME: Two of these are being created. Find out why, and make only one be created.
    }

    /**
     * A method for accessing the email for the site. This email is used as the "from" for
     * emails it sends, but also as the contact address, and a copy of all site-wide email
     * announcements are sent to this address. If configured properly, this should always
     * exist, so this throws an AppConfigurationException if not, which the user is not particularly
     * expected to detect and fix.
     */
    public static String getSiteEmail(DatabaseFacade database) {
        final Map<String,String> siteSettings;
        try {
             siteSettings = database.getSiteSettings();
        } catch (SQLException err) {
            throw new AppConfigurationException("Cannot read site settings from database.");
        }
        final String result = siteSettings.get("SiteEmail");
        if (result == null) {
            throw new AppConfigurationException("SiteSettings does not contain SiteEmail.");
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public void sendEmail(String text,Map<String,Object> model) throws IOException, AppConfigurationException {

        // Construct an object to contain the recipient address.
    	Destination destination;
    	if (model.get("to") != null)
    		destination = new Destination().withToAddresses(new String[]{model.get("to").toString()});
    	else {//fixme this is horrible
    		Collection<String> toBccAddresses = (Collection<String>) model.get("bcc");
    		destination = new Destination().withBccAddresses(toBccAddresses);
    	}
        
        // Create the subject and body of the message.
        Content subject = new Content().withData(model.get("subject").toString());
        
        Content textBody = new Content().withData(text);
        
        //Body body = new Body().withText(textBody);
        Body body = new Body().withHtml(textBody);
        
        // Create a message with the specified subject and body.
        Message message = new Message().withSubject(subject).withBody(body);
        
        // Assemble the email.
        final String from = getSiteEmail(database);
       	SendEmailRequest request = new SendEmailRequest().withSource(from).withDestination(destination).withMessage(message);
        AWSCredentials credentials = new BasicAWSCredentials(
                configuration.getProperty("aws.access_key"),
                configuration.getProperty("aws.secret_access_key"));
        AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(credentials);
        //AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient();

        // Choose the AWS region of the Amazon SES endpoint you want to connect to. Note that your production
        // access status, sending limits, and Amazon SES identity-related settings are specific to a given
        // AWS region, so be sure to select an AWS region in which you set up Amazon SES. Here, we are using
        // the US East (N. Virginia) region.

        Region REGION = Region.getRegion(Regions.US_EAST_1);
        client.setRegion(REGION);

        // Send the email.
        client.sendEmail(request);
    }

}
