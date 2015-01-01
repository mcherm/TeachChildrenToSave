package com.tcts.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.School;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.User;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.AppConfigurationException;
import com.tcts.exception.InconsistentDatabaseException;
import com.tcts.exception.NoSuchEventException;
import com.tcts.formdata.EventRegistrationFormData;
import com.tcts.util.EmailUtil;
import com.tcts.util.TemplateUtil;


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


	@RequestMapping(value = "/eventRegistration", method = RequestMethod.GET)
    public String showEventRegistrationPage(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getVolunteer() == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in teacher.");
        }
        model.addAttribute("formData", new EventRegistrationFormData());
        return showFormWithErrorMessage(model, "");
    }

    /**
     * A subroutine used to set up and then show the register teacher form. It
     * returns the string, so you can invoke it as "return showFormWithErrorMessage(...)".
     */
    private String showFormWithErrorMessage(Model model, String errorMessage) throws SQLException {
        List<Event> events = database.getAllAvailableEvents();
        for (Event event : events) {
            Teacher teacher = (Teacher) database.getUserById(event.getTeacherId());
            if (teacher == null) {
                throw new InconsistentDatabaseException("Event " + event.getEventId() + " has no valid teacher.");
            }
            event.setLinkedTeacher(teacher);
            String schoolId = teacher.getSchoolId();
            if (schoolId == null) {
                throw new InconsistentDatabaseException("Teacher " + event.getTeacherId() + " has no valid school.");
            }
            School school = database.getSchoolById(schoolId);
            teacher.setLinkedSchool(school);
        }
        model.addAttribute("events", events);
        model.addAttribute("errorMessage", errorMessage);
        return "eventRegistration";
    }

    @RequestMapping(value="/eventRegistration", method=RequestMethod.POST)
    public String createEvent(HttpSession session, Model model,
                              @ModelAttribute("formData") EventRegistrationFormData formData)
            throws SQLException, NoSuchEventException
    {
        SessionData sessionData = SessionData.fromSession(session);
        Volunteer volunteer = sessionData.getVolunteer();
        if (volunteer == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        // --- Validation Rules ---
        // FIXME: There may not BE any validation needed!
        // --- Create Event ---
        database.volunteerForEvent(formData.eventId, volunteer.getUserId());
        // --- Navigate onward ---
        if (null != formData.getEventId()) {
        	
	        Event event = database.getEventById(formData.getEventId());
	        //Send email to volunteer
	        try {
	        	
	        	Map<String,Object> emailModel = new <String, Object>HashMap();
	        	emailModel.put("to", volunteer.getEmail());
	        	emailModel.put("subject", "Your class has a volunteer!");
	        	emailModel.put("class", event.getEventDate() + " - " + event.getEventTime() + " - " + event.getNotes());
	        	emailModel.put("volunteer", volunteer.getFirstName() + " - " + volunteer.getLastName() + " - " + volunteer.getEmail() + " - " + volunteer.getPhoneNumber());
	        	String emailContent = templateUtil.generateTemplate("volunteerSignUpToVolunteer", emailModel);
	            emailUtil.sendEmail(emailContent, emailModel);
	        } catch(AppConfigurationException err) {
	            // FIXME: Need to log or report this someplace more reliable.
	            System.err.println("Could not send email for new volunteer '" + volunteer.getEmail() + "'.");
	        } catch(IOException err) {
	            // FIXME: Need to log or report this someplace more reliable.
	            System.err.println("Could not send email for new volunteer '" + volunteer.getEmail() + "'.");
	        }
	        
	        //Send email to teacher
	        if (null != event.getTeacherId()) {
		        User teacher = database.getUserById(event.getTeacherId());
		        try {
		        	
		        	Map<String,Object> emailModel = new <String, Object>HashMap();
		        	emailModel.put("to", volunteer.getEmail());
		        	emailModel.put("subject", "You have successfully signed up for a class!");
		        	emailModel.put("class", event.getEventDate() + " - " + event.getEventTime() + " - " + event.getNotes());
		        	emailModel.put("teacher", teacher.getFirstName() );//+ " - " + teacher.getLastName() + " - " + teacher.getEmail() + " - " + teacher.getPhoneNumber());
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