package com.tcts.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.tcts.datamodel.*;
import com.tcts.exception.EventAlreadyHasAVolunteerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.common.PrettyPrintingDate;
import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.exception.AppConfigurationException;
import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NoSuchEventException;
import com.tcts.formdata.EventRegistrationFormData;
import com.tcts.email.EmailUtil;
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

    @RequestMapping(value = "/eventRegistrationBySiteAdmin.htm", method = RequestMethod.GET)
    public String eventRegistrationBySiteAdmin(
            HttpSession session,
            Model model,
            @RequestParam("userId") String userId
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

    @RequestMapping(value = "/eventRegistration.htm", method = RequestMethod.GET)
    public String showEventRegistrationPage(HttpSession session, Model model) throws SQLException {
        Volunteer volunteer;
        SessionData sessionData = SessionData.fromSession(session);

        if (sessionData.getBankAdmin() != null){
            volunteer = (Volunteer) sessionData.getBankAdmin();
        } else if (sessionData.getVolunteer() != null){
            volunteer = sessionData.getVolunteer();
        } else {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in.");
        }

        model.addAttribute("formData", new EventRegistrationFormData());

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
        model.addAttribute("allowedGrades", database.getAllowedGrades());
        model.addAttribute("allowedDeliveryMethods", database.getAllowedDeliveryMethods());
        boolean volunteerSignupsOpen = EventRegistrationController.isVolunteerSignupsOpen(database);
        model.addAttribute("volunteerSignupsOpen", volunteerSignupsOpen);

        if (sessionData.getBankAdmin() != null)  {
            model.addAttribute("calledBy", "bankAdmin");
            model.addAttribute("calledByURL", "eventRegistration.htm");
        } else if (sessionData.getVolunteer() != null){
            model.addAttribute("calledBy", "volunteer");
            model.addAttribute("calledByURL", "eventRegistration.htm");
        } else  if (sessionData.getSiteAdmin() != null) {
            model.addAttribute("calledBy", "siteAdmin");
            model.addAttribute("calledByURL", "eventRegistrationBySiteAdmin.htm?userId=" + volunteer.getUserId());
            // get a list of events the volunteer is currently signed up for
            List<Event> volunteerEvents = database.getEventsByVolunteerWithTeacherAndSchool(volunteer.getUserId());
            model.addAttribute("volunteerEvents", volunteerEvents);
        } else {
            throw new RuntimeException("Cannot navigate to this page unless you are logged in.");
        }

        // the siteadmin and bankadmin both have currently signed up events listed on eventregistration page. a regular volunteer does not
        if ((sessionData.getSiteAdmin() != null)|| (sessionData.getBankAdmin() != null)) {
            // get a list of events the volunteer is currently signed up for
            List<Event> volunteerEvents = database.getEventsByVolunteerWithTeacherAndSchool(volunteer.getUserId());
            model.addAttribute("volunteerEvents", volunteerEvents); }

        return "eventRegistration";
    }

    @RequestMapping(value="/eventRegistration.htm", method=RequestMethod.POST)
    public String volunteerForEvent(HttpSession session, HttpServletRequest request,
                              @ModelAttribute("formData") EventRegistrationFormData formData)
            throws SQLException, NoSuchEventException, EventAlreadyHasAVolunteerException
    {
        SessionData sessionData = SessionData.fromSession(session);
        String volunteerId;
        Volunteer volunteer;
        boolean open = isVolunteerSignupsOpen(database);
        if (sessionData.getSiteAdmin() != null) {
            volunteerId = formData.getVolunteerId();
        } else if (sessionData.getVolunteer() != null  && open) {
            volunteerId = sessionData.getVolunteer().getUserId();
        } else if (sessionData.getBankAdmin() != null && open) {
            volunteerId = sessionData.getBankAdmin().getUserId();
        } else {
            throw new RuntimeException("Cannot navigate to this page unless you are logged-in.");
        }
        volunteer = (Volunteer) database.getUserById(volunteerId);

        //If you are signed is as a volunteer but your approval status is suspended
        if ((volunteer != null) && (volunteer.getApprovalStatus() == ApprovalStatus.SUSPENDED)) {
           throw new InvalidParameterFromGUIException("GUI should not let a suspended volunteer register for an event.");
        }
        // --- Validation Rules ---
        // FIXME: There may not BE any validation needed!

        // --- Create Event ---

       database.volunteerForEvent(formData.eventId, volunteerId);


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
                
                Map<String,Object> emailModel = new HashMap<String, Object>();
                String logoImage =  request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/img/logo-tcts.png";;
                Teacher teacher = (Teacher) database.getUserById(event.getTeacherId());
                School school = database.getSchoolById(teacher.getSchoolId());
                emailModel.put("logoImage", logoImage);
                emailModel.put("to", volunteer.getEmail());
                emailModel.put("subject", "You have successfully signed up for a class!");
                
                String htmlTableDataHeader = "<table cellpadding='1'><tr> " +
                        "<td style=\"background-color:#66CCFF\">Class Date</td>" +
                       "<td style=\"background-color:#66CCFF\">Class Time</td>" +
                       "<td style=\"background-color:#66CCFF\">Teacher</td>" +
                       "<td style=\"background-color:#66CCFF\">Volunteer</td>" +
                       "<td style=\"background-color:#66CCFF\">Grade</td>" +
                       "<td style=\"background-color:#66CCFF\">Delivery Method</td>" +
                       "<td style=\"background-color:#66CCFF\">Number of students</td>" +
                       "<td style=\"background-color:#66CCFF\">Class Notes<td/></tr><tr>";
                String htmlTableDataValue = "<td>" + new PrettyPrintingDate(event.getEventDate())  + "</td>" +    
                            "<td>" + event.getEventTime()  + "</td>" +
                            "<td>" + teacher.getFirstName() + " " + teacher.getLastName()  + "</td>" +
                            "<td>" + volunteer.getFirstName() + " " + volunteer.getLastName()  + "</td>" +
                            "<td>" + event.getGrade()  + "</td>" +
                            "<td>" + event.getDeliveryMethod()  + "</td>" +
                            "<td>" + event.getNumberStudents()  + "</td>" +
                            "<td>" + event.getNotes()  + "</td></tr></table>";
                emailModel.put("class", htmlTableDataHeader + htmlTableDataValue);
                
                String htmlTableDataHeaderForValunteer = "<table><tr> " +
                        "<td style=\"background-color:#66CCFF\">Teacher Name</td>" +
                       "<td style=\"background-color:#66CCFF\">Teacher Email Id</td>" +
                       "<td style=\"background-color:#66CCFF\">Teacher Phone Number</td></tr><tr>";
                       
                String htmlTableDataValueForVolunteer = "<td>" + teacher.getFirstName() + " " + teacher.getLastName()  + "</td>" +    
                            "<td>" + teacher.getEmail()  + "</td>" +
                            "<td>" + teacher.getPhoneNumber() +"</td></tr></table>";
                            
                emailModel.put("teacher", htmlTableDataHeaderForValunteer + htmlTableDataValueForVolunteer);

                String htmlTableDataHeaderForSchool = "<table><tr> " +
                        "<td style=\"background-color:#66CCFF\">School Name</td>" +
                        "<td style=\"background-color:#66CCFF\">School Address</td></tr>";

                String htmlTableDataValueForSchool = "<tr> <td>" + school.getName() + "</td>" +
                        "<td>" + school.getAddressLine1()  + "  " + school.getCity() +","+ school.getState()  + "</td></tr></table>";

                emailModel.put ("school", htmlTableDataHeaderForSchool + htmlTableDataValueForSchool);

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
                    
                    Map<String,Object> emailModel = new HashMap<String, Object>();
                    String logoImage =  request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/img/logo-tcts.png";;
                    
                    emailModel.put("logoImage", logoImage);
                    emailModel.put("to", teacher.getEmail());
                    emailModel.put("subject", "Your class has a volunteer!");
                    
                    String htmlTableDataHeader = "<table cellpadding='1'><tr> " +
                            "<td style=\"background-color:#66CCFF\">Class Date</td>" +
                           "<td style=\"background-color:#66CCFF\">Class Time</td>" +
                           "<td style=\"background-color:#66CCFF\">Teacher</td>" +
                           "<td style=\"background-color:#66CCFF\">Volunteer</td>" +
                           "<td style=\"background-color:#66CCFF\">Grade</td>" +
                           "<td style=\"background-color:#66CCFF\">Delivery Method</td>" +
                           "<td style=\"background-color:#66CCFF\">Number of students</td>" +
                           "<td style=\"background-color:#66CCFF\">Class Notes<td/></tr><tr>";
                    String htmlTableDataValue = "<td>" + new PrettyPrintingDate(event.getEventDate())  + "</td>" +    
                                "<td>" + event.getEventTime()  + "</td>" +
                                "<td>" + teacher.getFirstName() + " " + teacher.getLastName()  + "</td>" +
                                "<td>" + volunteer.getFirstName() + " " + volunteer.getLastName()  + "</td>" +
                                "<td>" + event.getGrade()  + "</td>" +
                                "<td>" + event.getDeliveryMethod()  + "</td>" +
                                "<td>" + event.getNumberStudents()  + "</td>" +
                                "<td>" + event.getNotes()  + "</td></tr></table>";
                    emailModel.put("class", htmlTableDataHeader + htmlTableDataValue);
                    
                    String htmlTableDataHeaderForTeacher = "<table><tr> " +
                            "<td style=\"background-color:#66CCFF\">Volunteer Name</td>" +
                           "<td style=\"background-color:#66CCFF\">Volunteer Email Id</td>" +
                           "<td style=\"background-color:#66CCFF\">Volunteer Phone Number</td></tr>";
                           
                    String htmlTableDataValueForTeacher = "<tr><td>" + volunteer.getFirstName() + " " + volunteer.getLastName()  + "</td>" +    
                                "<td>" + volunteer.getEmail()  + "</td>" +
                                "<td>" + volunteer.getPhoneNumber() + "</td></tr></table>";
                    
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
        if (sessionData.getSiteAdmin() != null)
        {
            return "redirect:eventRegistrationBySiteAdmin.htm?userId=" + volunteer.getUserId();
        } else if ( sessionData.getBankAdmin() != null) {
            return "redirect:eventRegistration.htm";
        } else {
            return "redirect:" + sessionData.getUser().getUserType().getHomepage();
        }
    }

    /**
     * This method uses the database connection to verify whether the volunteer registration
     * is open. It return true if it is open, false if not.
     */
    public static boolean isVolunteerSignupsOpen(DatabaseFacade database) throws SQLException {
        String setting = database.getSiteSettings().get("VolunteerSignupsOpen");
        return setting != null && setting.trim().toLowerCase().equals("yes");
    }
    /**
     * This method ensures that the volunteer registration is open, throwing an exception
     * it is not.
     */
    public void ensureVolunteerSignupsIsOpen() throws SQLException {
        if (!isVolunteerSignupsOpen(database)) {
            throw new RuntimeException("Cannot register for classes if teacher registration is not open.");
        }
    }


}