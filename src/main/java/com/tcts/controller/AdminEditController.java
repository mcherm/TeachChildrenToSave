package com.tcts.controller;

import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NoSuchAllowedDateException;
import com.tcts.exception.NoSuchAllowedTimeException;
import com.tcts.exception.NotLoggedInException;
import com.tcts.formdata.EditAllowedDateTimeData;

/**
 * This is a controller for editing (at least some of) the tables in
 * the database that the site admin is allowed to maintain.
 */
@Controller
public class AdminEditController {

    @Autowired
    private DatabaseFacade database;


    @RequestMapping(value = "adminEditAllowedTimes", method = RequestMethod.GET)
    public String listAllowedTimes(
            HttpSession session,
            Model model
        ) throws SQLException
    {
        // --- Make sure it's the site admin that's logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        model.addAttribute("allowedTimes", database.getAllowedTimes());

        return "adminEditAllowedTimes";
    }
    
    @RequestMapping(value = "allowedTimeDelete", method = RequestMethod.GET)
    public String deleteAllowedTime(@RequestParam String time,
            HttpSession session,
            Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        try {
			database.deleteAllowedTime(time);
		} catch (NoSuchAllowedTimeException e) {
			throw new InvalidParameterFromGUIException();
		}
        
        model.addAttribute("allowedTimes", database.getAllowedTimes());

        return "adminEditAllowedTimes";
    }
    
    @RequestMapping(value = "editAllowedTime", method = RequestMethod.GET)
    public String enterDataToEditAllowedTime(
            HttpSession session,
            Model model,
            @RequestParam String time
    ) throws SQLException {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        
        EditAllowedDateTimeData formData = new EditAllowedDateTimeData();
        formData.setAllowedTime(time);
        // --- Show the edit page ---
        return showEditDateTimeWithErrorMessage(model, formData, "");
    }

    /**
     * Actually change the values that were entered.
     */
    @RequestMapping(value="editAllowedTime", method=RequestMethod.POST)
    public String doEditAllowedTime(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") EditAllowedDateTimeData formData
        ) throws SQLException
    {
        SessionData sessionData = SessionData.fromSession(session);
        // FIXME: must validate the fields
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        try {
           database.modifyAllowedTime(formData);
        } catch(NoSuchAllowedTimeException err) {
            return showEditDateTimeWithErrorMessage(model, formData, "Incorrect time value exists.");
        }
        
        model.addAttribute("allowedTimes", database.getAllowedTimes());

        return "adminEditAllowedTimes";
    }
    
    @RequestMapping(value = "adminEditAllowedDates", method = RequestMethod.GET)
    public String listAllowedDates(
            HttpSession session,
            Model model
        ) throws SQLException
    {
        // --- Make sure it's the site admin that's logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        model.addAttribute("allowedDates", database.getAllowedDates());
        return "adminEditAllowedDates";
    }
    
    @RequestMapping(value = "allowedDateDelete", method = RequestMethod.GET)
    public String deleteAllowedDate(@RequestParam String date,
            HttpSession session,
            Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        try {
			database.deleteAllowedDate(date);
		} catch (NoSuchAllowedDateException e) {
			throw new InvalidParameterFromGUIException();
		}
        
        model.addAttribute("allowedDates", database.getAllowedDates());
        return "adminEditAllowedDates";
    }
    
    @RequestMapping(value = "editAllowedDate", method = RequestMethod.GET)
    public String enterDataToEditAllowedDate(
            HttpSession session,
            Model model,
            @RequestParam String date
    ) throws SQLException {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        
        EditAllowedDateTimeData formData = new EditAllowedDateTimeData();
        formData.setAllowedDate(date);
        // --- Show the edit page ---
        return showEditDateTimeWithErrorMessage(model, formData, "");
    }

    /**
     * Actually change the values that were entered.
     */
    @RequestMapping(value="editAllowedDate", method=RequestMethod.POST)
    public String doEditAllowedDate(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") EditAllowedDateTimeData formData
        ) throws SQLException
    {
        SessionData sessionData = SessionData.fromSession(session);
        // FIXME: must validate the fields
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        try {
           database.modifyAllowedDate(formData);
        } catch(NoSuchAllowedDateException err) {
            return showEditDateTimeWithErrorMessage(model, formData, "Incorrect date value exists.");
        }
        
        model.addAttribute("allowedDates", database.getAllowedDates());
        return "adminEditAllowedDates";
    }
    
    /**
     * A subroutine used to set up and then show the add user form. It
     */
    
    public String showEditDateTimeWithErrorMessage(Model model, EditAllowedDateTimeData formData, String errorMessage)
            throws SQLException
    {
        model.addAttribute("formData", formData);
        model.addAttribute("errorMessage", errorMessage);
        return "editAllowedTime";
    }


}
