package com.tcts.controller;

import java.sql.SQLException;

import javax.servlet.http.HttpSession;

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
import com.tcts.datamodel.School;
import com.tcts.exception.InconsistentDatabaseException;
import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NoSuchSchoolException;
import com.tcts.exception.NotLoggedInException;
import com.tcts.formdata.CreateSchoolFormData;
import com.tcts.formdata.EditSchoolFormData;


/**
 * This is a controller for the "home page" for users. It renders substantially
 * different information depending on the type of user who is logged in.
 */
@Controller
public class SchoolController {

    @Autowired
    private DatabaseFacade database;
    
    /**
     * Render the bank edit page .
     */
    @RequestMapping(value = "schools", method = RequestMethod.GET)
    public String viewSchools(
            HttpSession session,
            Model model
        ) throws SQLException
    {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        return showListSchools(model);
    }

        
    /**
     * Show the page for creating a new school.
     */
    @RequestMapping(value = "addSchool", method = RequestMethod.GET)
    public String viewAddSchool(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") CreateSchoolFormData formData
    ) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Successful; show the master list again ---
        return showAddSchoolWithErrors(model, null);
    }


    @RequestMapping(value = "editSchool", method = RequestMethod.GET)
    public String viewEditSchool(
            HttpSession session,
            Model model,
            @RequestParam String schoolId
    ) throws SQLException {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Load existing data ---
        School school = database.getSchoolById(schoolId);
        
        // --- Show the edit page ---
        return showEditSchoolWithErrors(model, transformSchoolData(school), null);
    }


    @RequestMapping(value = "addSchool", method = RequestMethod.POST)
    public String doAddSchool(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") CreateSchoolFormData formData
    ) throws SQLException
    {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Validation Rules ---
        Errors errors = formData.validate();
        if (errors.hasErrors()) {
            return showAddSchoolWithErrors(model, errors);
        }

        // --- Do It ---
        try {
            database.insertNewSchool(formData);
        } catch(InconsistentDatabaseException err) {
            return showAddSchoolWithErrors(model,
                    new Errors("That school is already in use."));
        }

        // --- Successful; show the master school edit again ---
        return showListSchools(model);
    }


    @RequestMapping(value = "editSchool", method = RequestMethod.POST)
    public String doEditSchool(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") EditSchoolFormData formData
        ) throws SQLException
    {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Validation Rules ---
        Errors errors = formData.validate();
        if (errors.hasErrors()) {
            return showEditSchoolWithErrors(model, formData, errors);
        }

        try {
            database.modifySchool(formData);
        } catch(NoSuchSchoolException err) {
            throw new InvalidParameterFromGUIException();
        } 

        // --- Successful; show the master bank edit again ---
        return showListSchools(model);
    }

    /**
     * Deletes a bank and all users associated with it.
     */
    @RequestMapping(value = "deleteSchool", method = RequestMethod.POST)
    public String doDeleteSchool(
            @RequestParam String schoolId,
            HttpSession session,
            Model model
    ) throws SQLException
    {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        try {
            database.deleteSchool(schoolId);
        } catch(NoSuchSchoolException err) {
            throw new InvalidParameterFromGUIException();
        }

        return showListSchools(model);
    }

    /**
     * A subroutine to set up for displaying the list of schools. It returns the
     * string for the next page so you can invoke it as "return showListOfSchools(...)".
     */
    private String showListSchools(Model model) throws SQLException {
        model.addAttribute("schools", database.getAllSchools());
        return "schools";
    }

    /**
     * A subroutine used to set up and then show the add bank form. It
     * returns the string, so you can invoke it as "return showAddBankWithErrorMessage(...)".
     */
    private String showEditSchoolWithErrors(Model model, EditSchoolFormData formData, Errors errors) {
        model.addAttribute("formData", formData);
        model.addAttribute("errors", errors);
        return "editSchool";
    }

    /**
     * A subroutine used to set up and then show the add school form. It
     * returns the string, so you can invoke it as "return showAddSchoolWithErrorMessage(...)".
     */
    private String showAddSchoolWithErrors(Model model, Errors errors) throws SQLException {
        model.addAttribute("errors", errors);
        return "addSchool";
    }

    /**
     * Transform school modal data
     */
    private EditSchoolFormData transformSchoolData(School school) {
    	EditSchoolFormData formData = new EditSchoolFormData();
        formData.setSchoolId(school.getSchoolId());
        formData.setCity(school.getCity());
        formData.setCounty(school.getCounty());
        formData.setDistrict(school.getSchoolDistrict());
        formData.setPhone(school.getPhone());
        formData.setSchoolAddress1(school.getAddressLine1());
        formData.setSchoolName(school.getName());
        formData.setState(school.getState());
        formData.setZip(school.getZip());
        formData.setLmiEligible(Integer.toString(school.getLmiEligible()));
        formData.setSLC(school.getSLC());
        
        return formData;
    }
       
}
