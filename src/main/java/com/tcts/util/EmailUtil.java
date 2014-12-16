package com.tcts.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

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


@Component
public final class EmailUtil {
	private static String awsAccessKeyId = "";
	private static String awsSecretKey="";
	
	static String from = "";  // Replace with your "From" address. This address must be verified.
                                                      // production access, this address must be verified.
	@Autowired
	private VelocityEngine velocityEngine;
	
    static final String BODY = "This email was sent through Amazon SES ";
    static final String SUBJECT = "Amazon SES Service at work";
    
    
    public EmailUtil() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch(IOException err) {
            throw new RuntimeException("Cannot read properties file to connect to database.", err);
        }
        awsAccessKeyId = properties.getProperty("aws.access_key");
        awsSecretKey = properties.getProperty("aws.secret_access_key");
        from = properties.getProperty("email.from");
    }
    
    public void sendEmail(String to) throws IOException {    	
        
        // Construct an object to contain the recipient address.
        Destination destination = new Destination().withToAddresses(new String[]{to});
        
        // Create the subject and body of the message.
        Content subject = new Content().withData(SUBJECT);
        Map model = new HashMap();
        model.put("to", to);
        
        String text = 	VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "template/volunteerRegistration.vm", model);
        Content textBody = new Content().withData(text);
        
        //Body body = new Body().withText(textBody);
        Body body = new Body().withHtml(textBody);
        
        // Create a message with the specified subject and body.
        Message message = new Message().withSubject(subject).withBody(body);
        
        // Assemble the email.
        SendEmailRequest request = new SendEmailRequest().withSource(from).withDestination(destination).withMessage(message);
        
        try
        {        
           
            AWSCredentials credentials = new BasicAWSCredentials(awsAccessKeyId,awsSecretKey);
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
        catch (Exception ex) 
        {
           ex.printStackTrace();
        }
    }

}
