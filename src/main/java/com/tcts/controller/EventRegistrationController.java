package com.tcts.controller;

import java.sql.SQLException;
import java.util.List;

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
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.InconsistentDatabaseException;
import com.tcts.exception.NoSuchEventException;
import com.tcts.formdata.EventRegistrationFormData;


/**
 * A controller for volunteers to register for an event.
 */
@Controller
public class EventRegistrationController {

    @Autowired
    private DatabaseFacade database;


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
        return "redirect:" + sessionData.getUser().getUserType().getHomepage();
    }
}