package com.tcts.controller;

import java.sql.Date;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import com.tcts.common.PrettyPrintingDate;
import com.tcts.exception.AllowedDateAlreadyInUseException;
import com.tcts.exception.AllowedTimeAlreadyInUseException;
import com.tcts.formdata.AddAllowedDateFormData;
import com.tcts.formdata.AddAllowedTimeFormData;
import com.tcts.formdata.EditAllowedDateFormData;
import com.tcts.formdata.EditAllowedTimeFormData;
import com.tcts.formdata.Errors;
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


/**
 * This is a controller for editing (at least some of) the tables in
 * the database that the site admin is allowed to maintain.
 */
@Controller
public class AdminEditController {

    @Autowired
    private DatabaseFacade database;


    @RequestMapping(value = "listAllowedDates", method = RequestMethod.GET)
    public String listAllowedDates(
            HttpSession session,
            Model model
        ) throws SQLException
    {
        ensureSiteAdminLoggedIn(session);
        return showListAllowedDates(model);
    }


    @RequestMapping(value = "listAllowedTimes", method = RequestMethod.GET)
    public String listAllowedTimes(
            HttpSession session,
            Model model
        ) throws SQLException
    {
        ensureSiteAdminLoggedIn(session);
        return showListAllowedTimes(model);
    }


    @RequestMapping(value = "addAllowedDate", method = RequestMethod.GET)
    public String showAddAllowedDate(
            HttpSession session,
            Model model
        )
    {
        ensureSiteAdminLoggedIn(session);
        AddAllowedDateFormData formData = new AddAllowedDateFormData();
        return showAddAllowedDateWithErrors(model, formData, null);
    }


    @RequestMapping(value = "addAllowedTime", method = RequestMethod.GET)
    public String showAddAllowedTime(
            HttpSession session,
            Model model
        ) throws SQLException
    {
        ensureSiteAdminLoggedIn(session);
        AddAllowedTimeFormData formData = new AddAllowedTimeFormData();
        return showAddAllowedTimeWithErrors(model, formData, null);
    }


    @RequestMapping(value = "addAllowedDate", method = RequestMethod.POST)
    public String doAddAllowedDate(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") AddAllowedDateFormData formData
        ) throws SQLException
    {
        ensureSiteAdminLoggedIn(session);

        // --- Validation Rules ---
        Errors errors = formData.validate();
        if (errors.hasErrors()) {
            return showAddAllowedDateWithErrors(model, formData, errors);
        }

        // --- Insert it ---
        try {
            database.insertNewAllowedDate(formData);
        } catch(AllowedDateAlreadyInUseException err) {
            return showAddAllowedDateWithErrors(model, formData,
                    new Errors("That date is already listed as an allowed date."));
        }

        // --- Return to the date list ---
        return "redirect:listAllowedDates.htm";
    }


    @RequestMapping(value = "addAllowedTime", method = RequestMethod.POST)
    public String doAddAllowedTime(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") AddAllowedTimeFormData formData
        ) throws SQLException
    {
        ensureSiteAdminLoggedIn(session);

        // --- Validation Rules ---
        Errors errors = formData.validate();
        if (errors.hasErrors()) {
            return showAddAllowedTimeWithErrors(model, formData, errors);
        }

        // --- Insert it ---
        try {
            database.insertNewAllowedTime(formData);
        } catch(AllowedTimeAlreadyInUseException err) {
            return showAddAllowedTimeWithErrors(model, formData,
                    new Errors("That time is already listed as an allowed time."));
        } catch(NoSuchAllowedTimeException err) {
            return showAddAllowedTimeWithErrors(model, formData,
                    new Errors("The time you inserted this before does not exist."));
        }

        // --- Return to the time list ---
        return "redirect:listAllowedTimes.htm";
    }


    @RequestMapping(value = "deleteAllowedDate", method = RequestMethod.POST)
    public String deleteAllowedDate(
            @RequestParam String parseableDateStr,
            HttpSession session
        ) throws SQLException
    {
        ensureSiteAdminLoggedIn(session);

        Date date;
        try {
            date = Date.valueOf(parseableDateStr);
        } catch(IllegalArgumentException err) {
            throw new InvalidParameterFromGUIException();
        }
        try {
            database.deleteAllowedDate(new PrettyPrintingDate(date));
        } catch (NoSuchAllowedDateException e) {
            throw new InvalidParameterFromGUIException();
        }

        return "redirect:listAllowedDates.htm";
    }


    @RequestMapping(value = "deleteAllowedTime", method = RequestMethod.POST)
    public String deleteAllowedTime(
            @RequestParam String allowedTime,
            HttpSession session
        ) throws SQLException
    {
        ensureSiteAdminLoggedIn(session);

        try {
			database.deleteAllowedTime(allowedTime);
		} catch (NoSuchAllowedTimeException e) {
			throw new InvalidParameterFromGUIException();
		}

        return "redirect:listAllowedTimes.htm";
    }
    
    public String showListAllowedDates(Model model) throws SQLException {
        model.addAttribute("allowedDates", database.getAllowedDates());
        return "listAllowedDates";
    }

    public String showListAllowedTimes(Model model) throws SQLException {
        model.addAttribute("allowedTimes", database.getAllowedTimes());
        return "listAllowedTimes";
    }

    public String showAddAllowedDateWithErrors(Model model, AddAllowedDateFormData formData, Errors errors) {
        model.addAttribute("formData", formData);
        model.addAttribute("errors", errors);
        return "addAllowedDate";
    }

    public String showAddAllowedTimeWithErrors(
            Model model,
            AddAllowedTimeFormData formData,
            Errors errors
        ) throws SQLException
    {
        model.addAttribute("allowedTimes", database.getAllowedTimes());
        model.addAttribute("formData", formData);
        model.addAttribute("errors", errors);
        return "addAllowedTime";
    }

    /**
     * A subroutine used to display the edit-date form.
     */
    public String showEditAllowedDateWithErrors(Model model, EditAllowedDateFormData formData, Errors errors)
            throws SQLException
    {
        model.addAttribute("formData", formData);
        model.addAttribute("errors", errors);
        return "editAllowedTime";
    }

    /**
     * A subroutine used to set up and then show the add user form. It
     */
    public String showEditAllowedTimeWithErrors(Model model, EditAllowedTimeFormData formData, Errors errors)
            throws SQLException
    {
        model.addAttribute("formData", formData);
        model.addAttribute("errors", errors);
        return "editAllowedTime";
    }


    /** A subroutine called to ensure it's the site admin who is logged in. */
    private void ensureSiteAdminLoggedIn(HttpSession session) {
        // --- Make sure it's the site admin that's logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
    }

}
