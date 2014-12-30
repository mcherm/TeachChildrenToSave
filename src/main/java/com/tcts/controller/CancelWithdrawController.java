package com.tcts.controller;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NoSuchEventException;
import com.tcts.exception.NotLoggedInException;
import com.tcts.exception.NotOwnedByYouException;
import com.tcts.formdata.WithdrawFormData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;


/**
 * Used for a teacher to cancel a class or a volunteer to cancel their registration
 * for a course.
 */
@Controller
public class CancelWithdrawController {

    @Autowired
    private DatabaseFacade database;

    /**
     * A volunteer withdraws themselves from a class they had previously
     * volunteered for.
     */
    @RequestMapping(value = "volunteerWithdraw", method = RequestMethod.GET)
    public String showVolunteerWithdraw(
            HttpSession session,
            Model model,
            @RequestParam(value = "eventId") String eventId
        ) throws SQLException
    {
        // --- Ensure logged in as a Volunteer ---
        SessionData sessionData = SessionData.fromSession(session);
        Volunteer loggedInVolunteer = sessionData.getVolunteer();
        if (loggedInVolunteer == null) {
            throw new NotLoggedInException();
        }

        // --- Ensure the event is valid and is listed for this volunteer ---
        if (eventId == null || eventId.length() == 0) {
            throw new InvalidParameterFromGUIException();
        }
        Event event = database.getEventById(eventId);
        if (!loggedInVolunteer.getUserId().equals(event.getVolunteerId())) {
            throw new NotOwnedByYouException();
        }

        // --- Display the page ---
        WithdrawFormData formData = new WithdrawFormData();
        formData.setEventId(eventId);
        model.addAttribute("formData", formData);
        model.addAttribute("errorMessage", "");
        return "volunteerWithdraw";
    }


    /**
     * A volunteer withdraws themselves from a class they had previously
     * volunteered for.
     */
    @RequestMapping(value = "volunteerWithdraw", method = RequestMethod.POST)
    public String doVolunteerWithdraw(
            HttpSession session,
            @ModelAttribute WithdrawFormData formData
        ) throws SQLException
    {
        // --- Ensure logged in as a Volunteer ---
        SessionData sessionData = SessionData.fromSession(session);
        Volunteer loggedInVolunteer = sessionData.getVolunteer();
        if (loggedInVolunteer == null) {
            throw new NotLoggedInException();
        }

        // --- Ensure the event is valid and is listed for this volunteer ---
        String eventId = formData.getEventId();
        if (eventId == null || eventId.length() == 0) {
            throw new InvalidParameterFromGUIException();
        }
        Event event = database.getEventById(eventId);
        if (!loggedInVolunteer.getUserId().equals(event.getVolunteerId())) {
            throw new NotOwnedByYouException();
        }

        // --- Update the database ---
        try {
            database.volunteerForEvent(eventId, null);
        } catch(NoSuchEventException err) {
            throw new RuntimeException("Shouldn't happen; we just checked if it was there.");
        }

        // --- Send Emails ---
        // FIXME: Here it should send an email to the teacher and perhaps to the site admin,
        // and the email should include formData.getWithdrawNotes()

        // --- Done ---
        return "redirect:" + loggedInVolunteer.getUserType().getHomepage();
    }
}
