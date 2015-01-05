package com.tcts.util;

import java.io.IOException;
import java.util.Map;

import com.tcts.common.Configuration;
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
import com.tcts.exception.AppConfigurationException;


@Component
public final class EmailUtil {

    Configuration configuration = new Configuration();

    public EmailUtil() {
        // FIXME: Two of these are being created. Find out why, and make only one be created.
    }
    
    public void sendEmail(String text,Map<String,Object> model) throws IOException, AppConfigurationException {

        // Construct an object to contain the recipient address.
    	 Destination destination = new Destination().withToAddresses(new String[]{model.get("to").toString()});
        
        // Create the subject and body of the message.
        Content subject = new Content().withData(model.get("subject").toString());
        
        Content textBody = new Content().withData(text);
        
        //Body body = new Body().withText(textBody);
        Body body = new Body().withHtml(textBody);
        
        // Create a message with the specified subject and body.
        Message message = new Message().withSubject(subject).withBody(body);
        
        // Assemble the email.
        String from = configuration.getProperty("email.from");
        SendEmailRequest request = new SendEmailRequest().withSource(from).withDestination(destination).withMessage(message);
        
        try
        {        
           
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
        catch (Exception ex)  // FIXME: Can't safely use a global catch-and-ignore like this
        {
           ex.printStackTrace();
        }
    }

}
