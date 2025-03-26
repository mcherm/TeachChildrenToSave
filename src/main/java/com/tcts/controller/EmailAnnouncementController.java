package com.tcts.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.tcts.common.Configuration;
import com.tcts.datamodel.BankAdmin;
import com.tcts.exception.InconsistentDatabaseException;
import com.tcts.formdata.Errors;
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
import com.tcts.email.EmailUtil;
import com.tcts.util.TemplateUtil;



@Controller
public class EmailAnnouncementController {

    @Autowired
    private DatabaseFacade database;
    
    @Autowired
    TemplateUtil templateUtil;
    
    @Autowired
    EmailUtil emailUtil;

    @Autowired
    Configuration configuration;



    @RequestMapping(value = "/emailAnnouncement.htm", method = RequestMethod.GET)
    public String emailAnnouncement(HttpSession session, Model model) throws SQLException {
		SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        return showFormWithErrors(model, null);
    }

    /**
     * A subroutine used to set up and then show the email announcement form. It
     * returns the string, so you can invoke it as "return showForm(...)".
     */
    private String showFormWithErrors(Model model, Errors errors) throws SQLException {
    	model.addAttribute("formData", new EmailAnnouncementFormData());
        model.addAttribute("errors", errors);
    	return "emailAnnouncement";
    }

    @RequestMapping(value="/emailAnnouncement.htm", method=RequestMethod.POST)
    public String doEmailAnnouncement(HttpSession session, Model model, HttpServletRequest request,
                              @ModelAttribute("formData") EmailAnnouncementFormData formData)
            throws SQLException, InconsistentDatabaseException
    {
    	SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        
        SortedSet<String> emails = new TreeSet<String>();
        String groupsSentTo = null;
        
        if ("Yes".equalsIgnoreCase(formData.getMatchedTeachers())){
            groupsSentTo = "Matched Teachers";
            for (Teacher teacher : database.getMatchedTeachers()) {
                emails.add(teacher.getEmail());
            }
        }
        if ("Yes".equalsIgnoreCase(formData.getUnmatchedTeachers())){
            if (groupsSentTo == null) {
                groupsSentTo = "Unmatched Teachers";
            } else{
                groupsSentTo = groupsSentTo +" + Unmatched Teachers";
            }
            for (Teacher teacher : database.getUnMatchedTeachers()) {
                emails.add(teacher.getEmail());
            }
        }
        if ("Yes".equalsIgnoreCase(formData.getMatchedVolunteer())){
            if (groupsSentTo == null) {
                groupsSentTo = "Matched Volunteers";
            } else{
                groupsSentTo = groupsSentTo +" + Matched Volunteers";
            }

            for (Volunteer volunteer : database.getMatchedVolunteers()) {
                emails.add(volunteer.getEmail());
            }
        }
        if ("Yes".equalsIgnoreCase(formData.getUnmatchedVolunteers())){
            if (groupsSentTo == null) {
                groupsSentTo = "Unmatched Volunteers";
            } else{
                groupsSentTo = groupsSentTo +" + Unmatched Volunteers";
            }

            for (Volunteer volunteer : database.getUnMatchedVolunteers()) {
                emails.add(volunteer.getEmail());
            }
        }
        if ("Yes".equalsIgnoreCase(formData.getBankAdmins())) {
            if (groupsSentTo == null) {
                groupsSentTo = "Bank Admins";
            } else{
                groupsSentTo = groupsSentTo +" + BankAdmins";
            }

            for (BankAdmin bankAdmin : database.getBankAdmins()) {
                emails.add(bankAdmin.getEmail());
            }
        }

        if (emails.isEmpty() || groupsSentTo == null) {
            Errors errors = new Errors("There are no users that meet that criteria.");
            return showFormWithErrors(model, errors);
        } else {
            String logoImage = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/img/logo-tcts.png";
            try {
                // Send Email Announcement
                List<String> emailList = new ArrayList(emails);
                Map<String, Object> emailModel = new HashMap<String, Object>();
                emailModel.put("logoImage", logoImage);
                emailModel.put("message", formData.getMessage());
                emailModel.put("subject", "Message from teach children to save program!");
                //we send the emails with a max of 40 addresses in the bcc line because of a limit imposed by amazon simple email (of 50 addresses per email)
                for (int i=0; i<=(((emailList.size()-1)/40)); i++){
                    emailModel.put("bcc", emailList.subList(i*40, Math.min(emailList.size(), ((i+1)*40))));
                    String emailContent = templateUtil.generateTemplate("emailAnnouncement", emailModel);
                    emailUtil.sendEmail(emailContent, emailModel);
                }
                // Send Email Announcement Receipt to Teach Children to Save Email
                Map<String, Object> receiptModel = new HashMap<String, Object>();
                receiptModel.put("logoImage", logoImage);
                receiptModel.put("message", formData.getMessage());
                receiptModel.put("emails",emailList);
                receiptModel.put("subject","Teach Children To Save Email Announcement Receipt");
                receiptModel.put("groupsSentTo",groupsSentTo );
                String tctsEmail = EmailUtil.getSiteEmail(database);
                receiptModel.put("to", tctsEmail );
                String receiptContent = templateUtil.generateTemplate("announcementReceipt", receiptModel);
                emailUtil.sendEmail(receiptContent, receiptModel);


            } catch(AppConfigurationException err) {

                // FIXME: Need to log or report this someplace more reliable.
                System.err.println("Could not send email for email announcement " + err.getStackTrace());
                throw new RuntimeException("Could not send email for email announcement:"  + err.getMessage(), err);
            } catch(IOException err) {
                // FIXME: Need to log or report this someplace more reliable.
                System.err.println("Could not send email for email announcement: " + err.getStackTrace());
                throw new RuntimeException("Could not send email for email announcement: " + err.getMessage(), err);
            }

            return "redirect:" + sessionData.getUser().getUserType().getHomepage();
        }
    }
}