package com.tcts.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.common.PrettyPrintingDate;
import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.User;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.AppConfigurationException;
import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NoSuchEventException;
import com.tcts.formdata.EventRegistrationFormData;
import com.tcts.util.EmailUtil;
import com.tcts.util.TemplateUtil;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * A controller for volunteers to register for an event.
 */
@Controller
public class EventRegistrationController {

    @Autowired
    private DatabaseFacade database;
    
    @Autowired
    TemplateUtil templateUtil;
    
    @Autowired
    EmailUtil emailUtil;

    @RequestMapping(value = "/eventRegistrationBySiteAdmin", method = RequestMethod.GET)
    public String eventRegistrationBySiteAdmin(
            HttpSession session,
            Model model,
            @RequestParam String userId
    ) throws SQLException {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in as site admin.");
        }

        // --- Load existing data ---
        Volunteer volunteer = (Volunteer) database.getUserById(userId);

        return showForm (model, volunteer, sessionData );
    }

    @RequestMapping(value = "/eventRegistration", method = RequestMethod.GET)
    public String showEventRegistrationPage(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getVolunteer() == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        model.addAttribute("formData", new EventRegistrationFormData());
        Volunteer volunteer = sessionData.getVolunteer();
        return showForm(model, volunteer, sessionData);
    }

    /**
     * A subroutine used to set up and then show the register teacher form. It
     * returns the string, so you can invoke it as "return showForm(...)".
     */
    private String showForm(Model model, //contains list of attributes that is passed to the form
                            Volunteer volunteer, //the volunteer to be signed up for a class),
                            SessionData sessionData)
            throws SQLException {
        model.addAttribute ("volunteerId", volunteer.getUserId());
        model.addAttribute("volunteerFirstName",volunteer.getFirstName());
        model.addAttribute("volunteerLastName", volunteer.getLastName());
        model.addAttribute("bank", database.getBankById(volunteer.getBankId()));
        model.addAttribute("events", database.getAllAvailableEvents());
        model.addAttribute("allowedDates", database.getAllowedDates());
        model.addAttribute("allowedTimes", database.getAllowedTimes());
        if (sessionData.getVolunteer() != null) {
            model.addAttribute("calledBy", "volunteer");
        } else  if (sessionData.getSiteAdmin() != null) {
            model.addAttribute("calledBy", "siteAdmin");
        } else {
            throw new RuntimeException("Cannot navigate to this page unless you are logged in.");
        }
        return "eventRegistration";
    }

    @RequestMapping(value="/eventRegistration", method=RequestMethod.POST)
    public String createEvent(HttpSession session, HttpServletRequest request,
                              @ModelAttribute("formData") EventRegistrationFormData formData)
            throws SQLException, NoSuchEventException
    {
        SessionData sessionData = SessionData.fromSession(session);
        Volunteer volunteer = (Volunteer) database.getUserById(formData.getVolunteerId());

        //If you are not signed in as a volunteer or a SiteAdmin
        if ((sessionData.getVolunteer() == null) && (sessionData.getSiteAdmin() == null))
        {
            throw new RuntimeException("Cannot navigate to this page unless you are logged-in.");
        }
        if ((volunteer != null) && (volunteer.getApprovalStatus() == ApprovalStatus.Suspended)) {
           throw new InvalidParameterFromGUIException("GUI should not let a suspended volunteer register for an event.");
        }
        // --- Validation Rules ---
        // FIXME: There may not BE any validation needed!
        // --- Create Event ---
        database.volunteerForEvent(formData.eventId, formData.getVolunteerId());
        // --- Navigate onward ---
        if (null != formData.getEventId()) {
            
            Event event = database.getEventById(formData.getEventId());
            
            if (event == null) {
                // No such event by that ID, but somehow we just volunteered for it???!!
                throw new InvalidParameterFromGUIException();
            }
            //Send email to volunteer
            if (null != event.getVolunteerId()) {
            try {
                
                Map<String,Object> emailModel = new <String, Object>HashMap();
                String logoImage =  request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/img/logo-tcts.png";;
                User teacher = database.getUserById(event.getTeacherId());
                emailModel.put("logoImage", logoImage);
                emailModel.put("to", volunteer.getEmail());
                emailModel.put("subject", "You have successfully signed up for a class!");
                
                String htmlTableDataHeader = "<table><tr> " +
                        "<td style=\"background-color:#66CCFF\">Class Date</td>" +
                       "<td style=\"background-color:#66CCFF\">Class Time</td>" +
                       "<td style=\"background-color:#66CCFF\">Teacher</td>" +
                       "<td style=\"background-color:#66CCFF\">Volunteer</td>" +
                       "<td style=\"background-color:#66CCFF\">Grade</td>" +
                       "<td style=\"background-color:#66CCFF\">Number of student</td>" +
                       "<td style=\"background-color:#66CCFF\">Class Notes<td/></tr><tr>";
                String htmlTableDataValue = "<td>" + new PrettyPrintingDate(event.getEventDate())  + "</td>" +    
                            "<td>" + event.getEventTime()  + "</td>" +
                            "<td>" + teacher.getFirstName() + " " + teacher.getLastName()  + "</td>" +
                            "<td>" + volunteer.getFirstName() + " " + volunteer.getLastName()  + "</td>" +
                            "<td>" + event.getGrade()  + "</td>" +
                            "<td>" + event.getNumberStudents()  + "</td>" +
                            "<td>" + event.getNotes()  + "</td></tr>";
                emailModel.put("class", htmlTableDataHeader + htmlTableDataValue);
                
                String htmlTableDataHeaderForValunteer = "<table><tr> " +
                        "<td style=\"background-color:#66CCFF\">Teach Name</td>" +
                       "<td style=\"background-color:#66CCFF\">Teacher Email Id</td>" +
                       "<td style=\"background-color:#66CCFF\">Teacher Phone Number</td></tr><tr>";
                       
                String htmlTableDataValueForVolunteer = "<td>" + teacher.getFirstName() + " " + teacher.getLastName()  + "</td>" +    
                            "<td>" + teacher.getEmail()  + "</td>" +
                            "<td>" + teacher.getPhoneNumber() +"</td></tr>";
                            
                emailModel.put("teacher", htmlTableDataHeaderForValunteer + htmlTableDataValueForVolunteer);
                
                String emailContent = templateUtil.generateTemplate("volunteerSignUpToVolunteer", emailModel);
                emailUtil.sendEmail(emailContent, emailModel);
            } catch(AppConfigurationException err) {
                // FIXME: Need to log or report this someplace more reliable.
                System.err.println("Could not send email for new volunteer '" + volunteer.getEmail() + "'.");
            } catch(IOException err) {
                // FIXME: Need to log or report this someplace more reliable.
                System.err.println("Could not send email for new volunteer '" + volunteer.getEmail() + "'.");
            }
        }
            
            //Send email to teacher
            if (null != event.getTeacherId()) {
                User teacher = database.getUserById(event.getTeacherId());
                try {
                    
                    Map<String,Object> emailModel = new <String, Object>HashMap();
                    String logoImage =  request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/img/logo-tcts.png";;
                    
                    emailModel.put("logoImage", logoImage);
                    emailModel.put("to", teacher.getEmail());
                    emailModel.put("subject", "Your class has a volunteer!");
                    
                    String htmlTableDataHeader = "<table><tr> " +
                            "<td style=\"background-color:#66CCFF\">Class Date</td>" +
                           "<td style=\"background-color:#66CCFF\">Class Time</td>" +
                           "<td style=\"background-color:#66CCFF\">Teacher</td>" +
                           "<td style=\"background-color:#66CCFF\">Volunteer</td>" +
                           "<td style=\"background-color:#66CCFF\">Grade</td>" +
                           "<td style=\"background-color:#66CCFF\">Number of student</td>" +
                           "<td style=\"background-color:#66CCFF\">Class Notes<td/></tr><tr>";
                    String htmlTableDataValue = "<td>" + new PrettyPrintingDate(event.getEventDate())  + "</td>" +    
                                "<td>" + event.getEventTime()  + "</td>" +
                                "<td>" + teacher.getFirstName() + " " + teacher.getLastName()  + "</td>" +
                                "<td>" + volunteer.getFirstName() + " " + volunteer.getLastName()  + "</td>" +
                                "<td>" + event.getGrade()  + "</td>" +
                                "<td>" + event.getNumberStudents()  + "</td>" +
                                "<td>" + event.getNotes()  + "</td></tr>";
                    emailModel.put("class", htmlTableDataHeader + htmlTableDataValue);
                    
                    String htmlTableDataHeaderForTeacher = "<table><tr> " +
                            "<td style=\"background-color:#66CCFF\">Volunteer Name</td>" +
                           "<td style=\"background-color:#66CCFF\">Volunteer Email Id</td>" +
                           "<td style=\"background-color:#66CCFF\">Volunteer Phone Number</td></tr>";
                           
                    String htmlTableDataValueForTeacher = "<tr><td>" + volunteer.getFirstName() + " " + volunteer.getLastName()  + "</td>" +    
                                "<td>" + volunteer.getEmail()  + "</td>" +
                                "<td>" + volunteer.getPhoneNumber() + "</td></tr>";
                    
                    emailModel.put("class", htmlTableDataHeader + htmlTableDataValue);
                    emailModel.put("volunteer", htmlTableDataHeaderForTeacher + htmlTableDataValueForTeacher);
                    String emailContent = templateUtil.generateTemplate("volunteerSignUpToTeacher", emailModel);
                    emailUtil.sendEmail(emailContent, emailModel);
                } catch(AppConfigurationException err) {
                    // FIXME: Need to log or report this someplace more reliable.
                    System.err.println("Could not send email for new volunteer '" + volunteer.getEmail() + "'.");
                } catch(IOException err) {
                    // FIXME: Need to log or report this someplace more reliable.
                    System.err.println("Could not send email for new volunteer '" + volunteer.getEmail() + "'.");
                }
            }
        }

        return "redirect:" + sessionData.getUser().getUserType().getHomepage();
    }
}