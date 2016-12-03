package com.tcts.controller;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.tcts.datamodel.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Teacher;
import com.tcts.formdata.CreateEventFormData;
import com.tcts.formdata.Errors;

/**
 * A controller for the screens used to create a new event (a class for volunteers to help with).
 */
@Controller
public class CreateEventController {

    @Autowired
    private DatabaseFacade database;


    @InitBinder
    private void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        CustomDateEditor dateEditor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, dateEditor);
    }
    @RequestMapping(value = "/createEventBySiteAdmin", method = RequestMethod.GET)
    public String showPageCreateEventBySiteAdmin(
            HttpSession session,
            Model model
    ) throws SQLException {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in as site admin.");
        }
        model.addAttribute("formData", new CreateEventFormData());
        return showFormWithErrors(model, sessionData, null);
    }

    @RequestMapping(value="/createEvent", method= RequestMethod.GET)
    public String showPageCreateEventByTeacher(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getTeacher() == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in teacher.");
        }
        ensureEventCreationIsOpen();
        model.addAttribute("formData", new CreateEventFormData());
        return showFormWithErrors(model,sessionData, null);
    }

    /**
     * A subroutine used to set up and then show the register teacher form. It
     * returns the string, so you can invoke it as "return showFormWithErrorMessage(...)".
     */
    private String showFormWithErrors(Model model, SessionData sessionData, Errors errors) throws SQLException {
        model.addAttribute("allowedDates", database.getAllowedDates());
        model.addAttribute("allowedTimes", database.getAllowedTimes());
        if (sessionData.getTeacher() != null)  {
            model.addAttribute("calledBy", "teacher");
        } else  if (sessionData.getSiteAdmin() != null) {
            model.addAttribute("calledBy", "siteAdmin");
            List<Teacher> teachers = database.getTeacherWithSchoolData();
            model.addAttribute("teachers", teachers);
        } else {
            throw new RuntimeException("Cannot navigate to this page unless you are logged in.");
        }

        model.addAttribute("errors", errors);
        return "createEvent";
    }


    @RequestMapping(value="/createEvent", method=RequestMethod.POST)
    public String createEvent(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") CreateEventFormData formData)
        throws SQLException
    {
        SessionData sessionData = SessionData.fromSession(session);
        Teacher teacher = sessionData.getTeacher();
        String redirectTo;

        if (teacher != null) {
            ensureEventCreationIsOpen();
            formData.setTeacherId(teacher.getUserId());
            redirectTo = sessionData.getUser().getUserType().getHomepage();
        } else  if (sessionData.getSiteAdmin() != null) {
            redirectTo = "viewEditEvents.htm";
        } else {
            throw new RuntimeException("Cannot navigate to this page unless you are logged in.");
        }
            // --- Validation Rules ---
        Errors errors = formData.validate();
        if (errors.hasErrors()) {
            return showFormWithErrors(model, sessionData, errors);
        }

        // --- Create Event ---
        database.insertEvent(formData.getTeacherId(), formData);

        // --- Navigate onward ---
        return "redirect:" + redirectTo;
    }
    /**
     * This method ensures that the volunteer registration is open, throwing an exception
     * it is not.
     */
    public void ensureEventCreationIsOpen() throws SQLException {
        if (!isEventCreationOpen(database)) {
            throw new RuntimeException("Cannot register for classes if teacher registration is not open.");
        }
    }

    /**
     * This method uses the database connection to verify whether the volunteer registration
     * is open. It return true if it is open, false if not.
     */
    public static boolean isEventCreationOpen(DatabaseFacade database) throws SQLException {
        String setting = database.getSiteSettings().get("CourseCreationOpen");
        return setting != null && setting.trim().toLowerCase().equals("yes");
    }

}
