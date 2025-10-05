package com.tcts.controller;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.tcts.util.EventUtil;

import jakarta.servlet.http.HttpSession;

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

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.User;
import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NoSuchEventException;
import com.tcts.exception.NotLoggedInException;
import com.tcts.exception.NotOwnedByYouException;
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


    @InitBinder
    private void initBinder(WebDataBinder binder) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        CustomDateEditor dateEditor = new CustomDateEditor(dateFormat, true);
        binder.registerCustomEditor(Date.class, dateEditor);
                }

    /**
     * Deletes an event and all users associated with it.
     */
    @RequestMapping(value = "deleteEvent.htm", method = RequestMethod.POST)
    public String deleteEvent(
            @RequestParam("eventId") String eventId,
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

        return "redirect:" + "viewEditEvents.htm";
    }
    
    /**
     * Render the event edit page.
     */
    @RequestMapping(value = "viewEditEvents.htm", method = RequestMethod.GET)
    public String showAllEvents(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        java.util.List<Event> events = database.getAllEvents();
        model.addAttribute("events", events);
        model.addAttribute("calledByURL", "viewEditEvents.htm");
        java.util.List<String> allowedGrades = database.getAllowedGrades();
        model.addAttribute("allowedGrades", allowedGrades);
        model.addAttribute("showGradeColumn", EventUtil.hasMultipleGrades(database,events));
        java.util.List<String> allowedDeliveryMethods = database.getAllowedDeliveryMethods();
        model.addAttribute("allowedDeliveryMethods", allowedDeliveryMethods);
        model.addAttribute("showDeliveryMethodColumn", EventUtil.hasMultipleDeliveryMethods(database,events));
        return "sortedClasses";
    }
    
    @RequestMapping(value = "editEvent.htm", method = RequestMethod.GET)
    public String enterDataToEditEvent(
            HttpSession session,
            Model model,
            @RequestParam("eventId") String eventId
    ) throws SQLException {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        
        if (sessionData.getSiteAdmin() == null) {
        	if (sessionData.getTeacher() == null) {
        		throw new NotLoggedInException();
        	}
        }

        // --- Load existing data ---
        Event event = database.getEventById(eventId);
        if (event == null) {
            // No such event by that ID
            throw new InvalidParameterFromGUIException();
        }

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
    	model.addAttribute("allowedDates", database.getAllowedDates());
        model.addAttribute("allowedTimes", database.getAllowedTimes());
        List<String> allowedGrades = database.getAllowedGrades();
        model.addAttribute("allowedGrades", allowedGrades);
        model.addAttribute("showGradeColumn", allowedGrades.size() >= 2);
        List<String> allowedDeliveryMethods = database.getAllowedDeliveryMethods();
        model.addAttribute("allowedDeliveryMethods", allowedDeliveryMethods);
        model.addAttribute("showDeliveryMethodColumn", allowedDeliveryMethods.size() >= 2);
        model.addAttribute("formData", formData);
        model.addAttribute("errorMessage", errorMessage);
        return "editEvent";
    }


    @RequestMapping(value = "editEvent.htm", method = RequestMethod.POST)
    public String doEditEvent(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") EventRegistrationFormData formData
        ) throws SQLException
    {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
        	if (sessionData.getTeacher() == null) {
        		throw new NotLoggedInException();
        	}
        	else {
        		Event event = database.getEventById(formData.getEventId());
                if (event == null) {
                    // No such event by that ID
                    throw new InvalidParameterFromGUIException();
                }
                if (!sessionData.getTeacher().getUserId().equals(event.getTeacherId())) {
                    throw new NotOwnedByYouException();
                }

        	}
        }

        // --- Validate the event fields ---
        if (!database.getAllowedDates().contains(formData.getEventDate())) {
            return showEditEventWithErrorMessage(model, formData, "You must select a valid date.");
        }
        if (!database.getAllowedTimes().contains(formData.getEventTime())) {
            return showEditEventWithErrorMessage(model, formData, "You must select a time from the list.");
        }
        if (!database.getAllowedGrades().contains(formData.getGrade())) {
            return showEditEventWithErrorMessage(model, formData, "You must select a valid grade.");
        }
        if (!database.getAllowedDeliveryMethods().contains(formData.getDeliveryMethod())) {
            return showEditEventWithErrorMessage(model, formData, "You must select a valid delivery method.");
        }

        // --- Update the event ---
        try {
            database.modifyEvent(formData);
        } catch(NoSuchEventException err) {
            throw new InvalidParameterFromGUIException();
        } 

        if (sessionData.getTeacher() != null) {
        	 User user = sessionData.getTeacher();
        	 return "redirect:" + user.getUserType().getHomepage();
    	}
        else {
        	// --- Successful; show the master event edit again ---
            return showAllEvents(session, model);
        }
        
    }


    
    /**
     * Transform school modal data
     */
    
    private EventRegistrationFormData transformEventData(Event event) {
    	EventRegistrationFormData formData = new EventRegistrationFormData();
        formData.setEventDate(event.getEventDate());
        formData.setEventTime(event.getEventTime());
        formData.setGrade(event.getGrade());
        formData.setDeliveryMethod(event.getDeliveryMethod());
        formData.setNotes(event.getNotes());
        formData.setEventId(event.getEventId());
        formData.setNumberStudents(Integer.valueOf(event.getNumberStudents()==0?0:event.getNumberStudents()).toString());
        
        return formData;
    }
       
}
