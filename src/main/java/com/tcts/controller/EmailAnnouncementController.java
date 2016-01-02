package com.tcts.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.tcts.datamodel.BankAdmin;
import com.tcts.exception.InconsistentDatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.AppConfigurationException;
import com.tcts.exception.NotLoggedInException;
import com.tcts.formdata.EmailAnnouncementFormData;
import com.tcts.util.EmailUtil;
import com.tcts.util.TemplateUtil;



@Controller
public class EmailAnnouncementController {

    @Autowired
    private DatabaseFacade database;
    
    @Autowired
    TemplateUtil templateUtil;
    
    @Autowired
    EmailUtil emailUtil;


	@RequestMapping(value = "/emailAnnouncement", method = RequestMethod.GET)
    public String emailAnnouncement(HttpSession session, Model model) throws SQLException {
		SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        return showForm(model);
    }

    /**
     * A subroutine used to set up and then show the email announcement form. It
     * returns the string, so you can invoke it as "return showForm(...)".
     */
    private String showForm(Model model) throws SQLException {
    	model.addAttribute("formData", new EmailAnnouncementFormData());
    	return "emailAnnouncement";
    }

    @RequestMapping(value="/emailAnnouncement", method=RequestMethod.POST)
    public String doEmailAnnouncement(HttpSession session, Model model, HttpServletRequest request,
                              @ModelAttribute("formData") EmailAnnouncementFormData formData)
            throws SQLException, InconsistentDatabaseException
    {
    	SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        
        ArrayList<String> emailList = new ArrayList<String>();
        
        if ("Yes".equalsIgnoreCase(formData.getMatchedTeachers())){
            for (Teacher teacher : database.getMatchedTeachers()) {
                emailList.add(teacher.getEmail() );
            }
        }
        if ("Yes".equalsIgnoreCase(formData.getUnmachedTeachers())){
            for (Teacher teacher : database.getUnMatchedTeachers()) {
                emailList.add(teacher.getEmail() );
            }
        }
        if ("Yes".equalsIgnoreCase(formData.getMatchedVolunteer())){
            for (Volunteer volunteer : database.getMatchedVolunteers()) {
                emailList.add(volunteer.getEmail() );
            }
        }
        if ("Yes".equalsIgnoreCase(formData.getUnmatchedvolunteers())){
            for (Volunteer volunteer : database.getUnMatchedVolunteers()) {
                emailList.add(volunteer.getEmail());
            }
        }
        if ("Yes".equalsIgnoreCase(formData.getBankAdmins())) {
            for (BankAdmin bankAdmin : database.getBankAdmins()) {
                emailList.add(bankAdmin.getEmail());
            }
        }
        
       //Send email to volunteer
        try {
        	
        	Map<String,Object> emailModel = new HashMap<String, Object>();
        	String logoImage =  request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/img/logo-tcts.png";;
    		
    		emailModel.put("logoImage", logoImage);
    		emailModel.put("message", formData.getMessage());
        	emailModel.put("bcc", emailList);
        	emailModel.put("subject", "Message from teach children to save program!");
        	String emailContent = templateUtil.generateTemplate("emailAnnouncement", emailModel);
            emailUtil.sendEmail(emailContent, emailModel);
            
        } catch(AppConfigurationException err) {
            // FIXME: Need to log or report this someplace more reliable.
            System.err.println("Could not send email for email announcement " + err.getStackTrace());
        } catch(IOException err) {
            // FIXME: Need to log or report this someplace more reliable.
        	System.err.println("Could not send email for email announcement " + err.getStackTrace());
        }
        
        return "redirect:" + sessionData.getUser().getUserType().getHomepage();
    }
}