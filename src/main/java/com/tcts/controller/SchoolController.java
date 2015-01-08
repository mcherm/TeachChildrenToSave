package com.tcts.controller;

import java.sql.SQLException;
import java.util.List;

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
    public String showSchool(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        List<School> schools = database.getAllSchools();
        
        model.addAttribute("schools", schools);
        return "schools";
    }

        
    /**
     * Deletes a bank and all users associated with it.
     */
    @RequestMapping(value = "deleteSchool", method = RequestMethod.POST)
    public String deleteSchool(
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

        model.addAttribute("schools", database.getAllSchools());
        return "schools";
    }
    
    /**
     * Render the school edit page.
     */
    @RequestMapping(value = "viewEditSchools", method = RequestMethod.GET)
    public String enterDataToEditSchool(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        model.addAttribute("schools", database.getAllSchools());
        return "schools";
    }
    
    @RequestMapping(value = "editSchool", method = RequestMethod.GET)
    public String enterDataToEditBank(
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
        return showEditSchoolWithErrorMessage(model, transformSchoolData(school), "");
    }


    /**
     * A subroutine used to set up and then show the add bank form. It
     * returns the string, so you can invoke it as "return showAddBankWithErrorMessage(...)".
     */
    public String showEditSchoolWithErrorMessage(Model model, EditSchoolFormData formData, String errorMessage)
            throws SQLException
    {
        model.addAttribute("formData", formData);
        model.addAttribute("errorMessage", errorMessage);
        return "editSchool";
    }


    @RequestMapping(value = "editSchool", method = RequestMethod.POST)
    public String getUpdatedSchool(
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

        try {
            database.modifySchool(formData);
        } catch(NoSuchSchoolException err) {
            throw new InvalidParameterFromGUIException();
        } 

        // --- Successful; show the master bank edit again ---
        model.addAttribute("schools", database.getAllSchools());
        return "schools";
    }

    /**
     * Show the page for creating a new school.
     */
    @RequestMapping(value = "addSchool", method = RequestMethod.GET)
    public String enterDataToAddSchool(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") CreateSchoolFormData formData
    ) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Successful; show the edit page again ---
        return showAddSchoolWithErrorMessage(model, "");
    }

    @RequestMapping(value = "addSchool", method = RequestMethod.POST)
    public String doAddBank(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") CreateSchoolFormData formData
    ) throws SQLException {
    	SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        try {
            database.insertNewSchool(formData);
        } catch(InconsistentDatabaseException err) {
            return showAddSchoolWithErrorMessage(model,
                    "That school is already in use.");
        }

        // --- Successful; show the master school edit again ---
  
        model.addAttribute("schools", database.getAllSchools());
        return "schools";
    }

        
    /**
     * A subroutine used to set up and then show the add school form. It
     * returns the string, so you can invoke it as "return showAddSchoolWithErrorMessage(...)".
     */
    private String showAddSchoolWithErrorMessage(Model model, String errorMessage) throws SQLException {
        model.addAttribute("errorMessage", errorMessage);
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
