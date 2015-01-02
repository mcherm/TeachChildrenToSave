package com.tcts.controller;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.tcts.common.Cache;
import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Event;
import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NoSuchEventException;
import com.tcts.exception.NotLoggedInException;
import com.tcts.formdata.CreateEventFormData;
import com.tcts.formdata.EventRegistrationFormData;

/**
 * This is a controller for the "home page" for users. It renders substantially
 * different information depending on the type of user who is logged in.
 */
@Controller

public class EventController {

    @Autowired
    private DatabaseFacade database;
    
    @Autowired
    private Cache cache;
    

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        CustomDateEditor dateEditor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, dateEditor);
    }
    
    /**
     * Render the event edit page .
     */

    @RequestMapping(value = "events", method = RequestMethod.GET)
    public String showAllEvents(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        List<Event> events = database.getAllAvailableEvents();
        
        model.addAttribute("events", events);
        return "events";
    }

        
    /**
     * Deletes an event and all users associated with it.
     */
    @RequestMapping(value = "deleteEvent", method = RequestMethod.POST)
    public String deleteEvent(
            @RequestParam String eventId,
            HttpSession session,
            Model model
        ) throws SQLException
    {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        try {
        	database.deleteEvent(eventId);
        } catch(NoSuchEventException err) {
            throw new InvalidParameterFromGUIException();
        }

        model.addAttribute("events", database.getAllAvailableEvents());
        return "events";
    }
    
    /**
     * Render the event edit page.
     */
    @RequestMapping(value = "viewEditEvents", method = RequestMethod.GET)
    public String enterDataToEditEvet(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        model.addAttribute("events", database.getAllAvailableEvents());
        return "events";
    }
    
    @RequestMapping(value = "editEvent", method = RequestMethod.GET)
    public String enterDataToEditEvent(
            HttpSession session,
            Model model,
            @RequestParam String eventId
    ) throws SQLException {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Load existing data ---
        Event event = database.getEventById(eventId);
        
        // --- Show the edit page ---
        return showEditEventWithErrorMessage(model, transformEventData(event), "");
    }


    /**
     * A subroutine used to set up and then show the add event form. It
     * returns the string, so you can invoke it as "return showEditEventWithErrorMessage(...)".
     */
    public String showEditEventWithErrorMessage(Model model, CreateEventFormData formData, String errorMessage)
            throws SQLException
    {
    	model.addAttribute("allowedDates", cache.getAllowedDates());
        model.addAttribute("allowedTimes", cache.getAllowedTimes());
        model.addAttribute("formData", formData);
        model.addAttribute("errorMessage", errorMessage);
        return "editEvent";
    }


    @RequestMapping(value = "editEvent", method = RequestMethod.POST)
    public String doEditEvent(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") EventRegistrationFormData formData
        ) throws SQLException
    {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        try {
            database.modifyEvent(formData);
        } catch(NoSuchEventException err) {
            throw new InvalidParameterFromGUIException();
        } 

        // --- Successful; show the master event edit again ---
        model.addAttribute("events", database.getAllAvailableEvents());
        return "events";
    }


    
    /**
     * Transform school modal data
     */
    
    private EventRegistrationFormData transformEventData(Event event) {
    	EventRegistrationFormData formData = new EventRegistrationFormData();
        formData.setEventDate(event.getEventDate());
        formData.setEventTime(event.getEventTime());
        formData.setGrade(event.getGrade());
        formData.setNotes(event.getNotes());
        formData.setEventId(event.getEventId());
        
        return formData;
    }
       
}
