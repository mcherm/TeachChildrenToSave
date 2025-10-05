package com.tcts.email;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import com.tcts.database.DatabaseFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
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
        } catch (RuntimeException err) {
            throw new AppConfigurationException("Cannot read site settings from database.");
        }
        final String result = siteSettings.get("SiteEmail");
        if (result == null) {
            throw new AppConfigurationException("SiteSettings does not contain SiteEmail.");
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
	public void sendEmail(String text, Map<String,Object> model) throws IOException, AppConfigurationException {

        // New AWS SDK v2 code:
        
        // --- Construct the destination address(s) ---
    	final Destination destination;
    	if (model.get("to") != null) {
    		destination = Destination.builder()
    				.toAddresses(model.get("to").toString())
    				.build();
    	} else {
            destination = Destination.builder()
    				.bccAddresses((Collection<String>) model.get("bcc"))
    				.build();
    	}
        
        // --- Create the subject ---
        final Content subject = Content.builder()
        		.data(model.get("subject").toString())
        		.build();

        // --- Create the body of the message ---
        final Content textBody = Content.builder()
        		.data(text)
        		.build();
        final Body body = Body.builder()
        		.html(textBody)
        		.build();
        
        // --- Create a message ---
        final Message message = Message.builder()
        		.subject(subject)
        		.body(body)
        		.build();
        
        // --- Assemble the email ---
        final String from = getSiteEmail(database);
       	final SendEmailRequest request = SendEmailRequest.builder()
       			.source(from)
       			.destination(destination)
       			.message(message)
       			.build();
       			
        // --- Create SES client with credentials ---
        final AwsBasicCredentials credentials = AwsBasicCredentials.create(
                configuration.getProperty("aws.access_key"),
                configuration.getProperty("aws.secret_access_key"));
        final StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        // The try() statement guarantees that the sesClient is closed properly.
        try (
            final SesClient sesClient = SesClient.builder()
        		.region(Region.US_EAST_1)
        		.credentialsProvider(credentialsProvider)
        		.build()
        ) {
            try {
                // --- Send the email ---
                sesClient.sendEmail(request);
            } catch (SdkException err) {
                throw new RuntimeException("Unable to send email.", err);
            }

        }
    }

}
