package com.tcts.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.tcts.exception.FormDataConstructionException;
import com.tcts.formdata.EditEventFormData;
import com.tcts.formdata.EditEventFormDataStrings;
import com.tcts.formdata.Errors;
import com.tcts.util.EventUtil;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

/**
 * This is a controller for the "home page" for users. It renders substantially
 * different information depending on the type of user who is logged in.
 */
@Controller

public class EventController {

    @Autowired
    private DatabaseFacade database;


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
        return showEditEventWithErrorMessage(model, createFormDataToEditEvent(event), "");
    }


    /**
     * A subroutine used to set up and then show the add event form. It
     * returns the string, so you can invoke it as "return showEditEventWithErrorMessage(...)".
     */ // FIXME: This should be changed to accept an Errors object instead of a single string.
    public String showEditEventWithErrorMessage(Model model, CreateEventFormData formData, String errorMessage)
            throws SQLException
    {
    	model.addAttribute("allowedDates", database.getAllowedDates());
        model.addAttribute("allowedTimes", database.getAllowedTimes());
        List<String> allowedGrades = database.getAllowedGrades();
        if (!allowedGrades.contains(formData.getGrade())) {
            ArrayList<String> newAllowedGrades = new ArrayList<>();
            newAllowedGrades.add(formData.getGrade());
            newAllowedGrades.addAll(allowedGrades);
            allowedGrades = newAllowedGrades;
        }
        model.addAttribute("allowedGrades", allowedGrades);
        model.addAttribute("showGradeColumn", allowedGrades.size() >= 2);
        List<String> allowedDeliveryMethods = database.getAllowedDeliveryMethods();
        if (!allowedDeliveryMethods.contains(formData.getDeliveryMethod())) {
            ArrayList<String> newAllowedDeliveryMethods = new ArrayList<>();
            newAllowedDeliveryMethods.add(formData.getDeliveryMethod());
            newAllowedDeliveryMethods.addAll(allowedDeliveryMethods);
            allowedDeliveryMethods = newAllowedDeliveryMethods;
        }

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
            @ModelAttribute("formData") EditEventFormDataStrings formDataStrings
        ) throws SQLException
    {
        // --- Convert to the richer EditEventFormData object ---
        final EditEventFormData formData;
        try {
            formData = formDataStrings.asFormData();
        } catch(FormDataConstructionException err) {
            final Errors errors = err.getErrors();
            // We cannot re-display the page if we can't even parse the formData. And it will
            // only happen due to application error, not user error. So the error handling will
            // just be an exception.
            throw new RuntimeException("Cannot create EditEventFormData: " + errors);
        }

        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        final Event existingEvent = database.getEventById(formData.getEventId());
        if (sessionData.getSiteAdmin() == null) {
        	if (sessionData.getTeacher() == null) {
        		throw new NotLoggedInException();
        	}
        	else {
                if (existingEvent == null) {
                    // No such event by that ID
                    throw new InvalidParameterFromGUIException();
                }
                if (!sessionData.getTeacher().getUserId().equals(existingEvent.getTeacherId())) {
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
            if (formData.getGrade().equals(existingEvent.getGrade())) {
                // Acceptable -- they are leaving unchanged a now-obsolete value for grade
            } else {
                return showEditEventWithErrorMessage(model, formData, "You must select a valid grade.");
            }
        }
        if (!database.getAllowedDeliveryMethods().contains(formData.getDeliveryMethod())) {
            if (formData.getDeliveryMethod().equals(existingEvent.getDeliveryMethod())) {
                // Acceptable -- they are leaving unchanged a now-obsolete value for deliveryMethod
            } else {
                return showEditEventWithErrorMessage(model, formData, "You must select a valid delivery method.");
            }
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
     * Given an Event, populate the fields of the data object needed to edit it.
     */
    private EditEventFormData createFormDataToEditEvent(Event event) {
        EditEventFormData formData = new EditEventFormData();
        formData.setEventDate(event.getEventDate());
        formData.setEventTime(event.getEventTime());
        formData.setGrade(event.getGrade());
        formData.setDeliveryMethod(event.getDeliveryMethod());
        formData.setNotes(event.getNotes());
        formData.setEventId(event.getEventId());
        // FIXME: WHY are we doing the complex, messy thing we do below? Is it needed?
        formData.setNumberStudents(Integer.valueOf(
                event.getNumberStudents() == 0 ? 0 : event.getNumberStudents()
        ).toString());
        
        return formData;
    }
       
}
