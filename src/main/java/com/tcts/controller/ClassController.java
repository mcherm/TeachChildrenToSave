package com.tcts.controller;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NoSuchEventException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Event;

/**
 * This is a controller for the "home page" for users. It renders substantially
 * different information depending on the type of user who is logged in.
 */
@Controller

public class ClassController {

    @Autowired
    private DatabaseFacade database;
    
    /**
     * Render the bank edit page .
     */
    @RequestMapping(value = "/class/classes", method = RequestMethod.GET)
    public String showClasses(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in.");
        }
        List<Event> eventList = database.getEvents();
        
        model.addAttribute("events", eventList);
        return "classes";
    }
    
    @RequestMapping(value = "/class/delete", method = RequestMethod.GET)
    public String deleteClass(@ModelAttribute(value="class") Event event,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in.");
        }
        try {
            database.deleteEvent(event.getEventId());
        } catch(NoSuchEventException err) {
            throw new InvalidParameterFromGUIException();
        }
        
        List<Event> eventList = database.getEvents();
        
        model.addAttribute("events", eventList);
        return "events";
    }
    
    @RequestMapping(value = "/class/show", method = RequestMethod.GET)
    public String getClass(@ModelAttribute(value="class") Event event,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        event = database.getEventById(event.getEventId());
        
        model.addAttribute("event", event);
        return "class";
    }
    
    @RequestMapping(value = "/class/update", method = RequestMethod.POST)
    public String getUpdatedBank(@ModelAttribute(value="class") Event event,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
       
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }

        // FIXME: it should now modify the event
        return "class";
    }

   
}
