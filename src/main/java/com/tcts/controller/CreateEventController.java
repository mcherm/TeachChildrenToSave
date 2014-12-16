package com.tcts.controller;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpSession;

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
import com.tcts.dao.DatabaseFacade;
import com.tcts.datamodel.Teacher;
import com.tcts.model.CreateEventFormData;
import com.tcts.util.EmailUtil;

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

    @RequestMapping(value="/createEvent", method= RequestMethod.GET)
    public String showCreateEventPage(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getTeacher() == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in teacher.");
        }
        model.addAttribute("formData", new CreateEventFormData());
        return showFormWithErrorMessage(model, "");
    }

    /**
     * A subroutine used to set up and then show the register teacher form. It
     * returns the string, so you can invoke it as "return showFormWithErrorMessage(...)".
     */
    private String showFormWithErrorMessage(Model model, String errorMessage) throws SQLException {
        model.addAttribute("allowedDates", database.getAllowedDates()); // FIXME: Shouldn't we cache this?
        model.addAttribute("allowedTimes", database.getAllowedTimes()); // FIXME: Shouldn't we cache this?
        model.addAttribute("errorMessage", errorMessage);
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
        if (teacher == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in teacher.");
        }
        // --- Validation Rules ---
        // FIXME: Need validation, INCLUDING making certain that the date and time are allowed.
        // --- Create Event ---
        database.insertEvent(teacher.getUserId(), formData);
        // --- Navigate onward ---
        return "redirect:" + sessionData.getUser().getUserType().getHomepage();
    }
}
