package com.tcts.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.tcts.database.DatabaseFacade;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.common.Configuration;
import com.tcts.exception.AppConfigurationException;
import com.tcts.exception.InconsistentDatabaseException;
import com.tcts.formdata.ContactUsFormData;
import com.tcts.email.EmailUtil;
import com.tcts.util.TemplateUtil;


/**
 * This controller displays the "contact" page.
 */
@Controller
public class ContactController {

    @Autowired
    private DatabaseFacade database;

    @Autowired
    private EmailUtil emailUtil;
    
    @Autowired
    TemplateUtil templateUtil;

    @RequestMapping(value = "/contact.htm", method = RequestMethod.GET)
    public String aboutPage(Model model) {
        model.addAttribute("email", EmailUtil.getSiteEmail(database));
        model.addAttribute("formData", new ContactUsFormData());
        return "contact";
    }
    
    @RequestMapping(value = "/contactConfirm.htm", method = RequestMethod.GET)
    public String doContactUsConfirm(Model model) {
        model.addAttribute("email", EmailUtil.getSiteEmail(database));
        model.addAttribute("confirmationMessage", "Your message has been sent to site administrator successfully.");
        model.addAttribute("formData", new ContactUsFormData());
        return "contact";
    }
    
    @RequestMapping(value = "/contact.htm", method = RequestMethod.POST)
    public String doContactUs(
            HttpServletRequest request,
            @ModelAttribute("formData") ContactUsFormData formData
        ) throws SQLException, InconsistentDatabaseException, UnsupportedEncodingException
    {
    	try {
    		Map<String,Object> emailModel = new HashMap<String, Object>();
    		String logoImage =  request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/img/logo-tcts.png";;
    		
    		emailModel.put("logoImage", logoImage);
            emailModel.put("to", EmailUtil.getSiteEmail(database));
        	emailModel.put("subject", "Contact Us message");
        	emailModel.put("emailAddress", formData.getEmailAddress());
        	emailModel.put("message", formData.getMessage());
        	String emailContent = templateUtil.generateTemplate("contactUs", emailModel);
            emailUtil.sendEmail(emailContent, emailModel);
        } catch(AppConfigurationException err) {
            // FIXME: Need to log or report this someplace more reliable.
            System.err.println("Could not send email for contact us form '" + formData.getEmailAddress() + "'.");
        } catch(IOException err) {
            // FIXME: Need to log or report this someplace more reliable.
        	 System.err.println("Could not send email for contact us form '" + formData.getEmailAddress() + "'.");
        }
        
        return "redirect:" + "/contactConfirm.htm";

    }

}
