package com.tcts.controller;

import jakarta.servlet.http.HttpSession;

import com.tcts.exception.EmailAlreadyInUseException;

import com.tcts.exception.NotOwnedByYouException;
import com.tcts.formdata.EditVolunteerPersonalDataFormData;
import com.tcts.formdata.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.exception.NotLoggedInException;
import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.User;
import com.tcts.datamodel.Volunteer;
import com.tcts.formdata.EditPersonalDataFormData;

/**
 * A controller for the screen where they are editing the
 */
@Controller
public class EditPersonalDataController {

    @Autowired
    private DatabaseFacade database;

    /**
     * Render the page for editing personal data of teachers and site admins.
     */
    @RequestMapping(value="/editPersonalData.htm", method=RequestMethod.GET)
    public String showEditPersonalDataPage(HttpSession session, Model model) {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getVolunteer() != null) {
            throw new RuntimeException("Volunteers should be edited with /editVolunteerPersonalData.");
        }
        User user = sessionData.getUser();
        EditPersonalDataFormData formData = new EditPersonalDataFormData();
        formData.setEmail(user.getEmail());
        formData.setFirstName(user.getFirstName());
        formData.setLastName(user.getLastName());
        formData.setPhoneNumber(user.getPhoneNumber());
        formData.setUserId(user.getUserId());
        model.addAttribute("formData", formData);

        return showFormWithErrors(model, sessionData, null);
    }

    /**
     * Render the page for editing personal data of volunteers and bank admins (the difference being
     * that they may have a bankSpecificData field to fill in).
     */
    @RequestMapping(value="/editVolunteerPersonalData.htm", method=RequestMethod.GET)
    public String showEditVolunteerPersonalDataPage(HttpSession session, Model model) {
        SessionData sessionData = SessionData.fromSession(session);
        Volunteer volunteer = sessionData.getVolunteer();
        EditVolunteerPersonalDataFormData formData = new EditVolunteerPersonalDataFormData();
        formData.setEmail(volunteer.getEmail());
        formData.setFirstName(volunteer.getFirstName());
        formData.setLastName(volunteer.getLastName());
        formData.setPhoneNumber(volunteer.getPhoneNumber());
        formData.setUserId(volunteer.getUserId());
        formData.setBankSpecificData(volunteer.getBankSpecificData());
        formData.setStreetAddress(volunteer.getStreetAddress());
        formData.setSuiteOrFloorNumber(volunteer.getSuiteOrFloorNumber());
        formData.setCity(volunteer.getCity());
        formData.setState(volunteer.getState());
        formData.setZip(volunteer.getZip());
        model.addAttribute("formData", formData);

        return showVolunteerFormWithErrors(model, sessionData, null);
    }

    /**
     * A subroutine used to set up and then show the editPersonalData form. It
     * returns the string, so you can invoke it as "return showFormWithErrors(...)".
     * This fills in all necessary fields EXCEPT for the formData.
     */
    public String showFormWithErrors(Model model, SessionData sessionData, Errors errors) {
        model.addAttribute("cancelURL", sessionData.getUser().getUserType().getHomepage());
        model.addAttribute("userId", sessionData.getUser().getUserId());
        model.addAttribute("errors", errors);
        return "editPersonalData";
    }

    /**
     * A subroutine used to set up and then show the editVolunteerPersonalData form. It
     * returns the string, so you can invoke it as "return showFormWithErrors(...)".
     * This fills in all necessary fields EXCEPT for the formData.
     */
    public String showVolunteerFormWithErrors(Model model, SessionData sessionData, Errors errors) {
        Volunteer volunteer = sessionData.getVolunteer();
        model.addAttribute("bankSpecificFieldLabel", volunteer.getLinkedBank().getBankSpecificDataLabel());
        model.addAttribute("cancelURL", sessionData.getUser().getUserType().getHomepage());
        model.addAttribute("userId", sessionData.getUser().getUserId());
        model.addAttribute("errors", errors);
        return "editVolunteerPersonalData";
    }

    /**
     * Actually change the values that were entered.
     */
    @RequestMapping(value="/editPersonalData.htm", method=RequestMethod.POST)
    public String editPersonalData(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") EditPersonalDataFormData formData
    ) {
        SessionData sessionData = SessionData.fromSession(session);
        User user = sessionData.getUser();
        if (user == null) {
            throw new NotLoggedInException();
        }
        if (!user.getUserId().equals(formData.getUserId())) {
            throw new NotOwnedByYouException();
        }

        // --- Validation Rules ---
        Errors errors = formData.validate();
        if (errors.hasErrors()) {
            return showFormWithErrors(model, sessionData, errors);
        }

        // --- Make the changes ---
        try {
            database.modifyUserPersonalFields(formData);
            User newUser = database.getUserById(user.getUserId());
            sessionData.setUser(newUser);
            return "redirect:" + user.getUserType().getHomepage();
        } catch(EmailAlreadyInUseException err) {
            return showFormWithErrors(model, sessionData, new Errors("That email is already in use by another user."));
        }
    }


    /*
     * Actually change the values that were entered, for volunteers.
     */
    @RequestMapping(value="/editVolunteerPersonalData.htm", method=RequestMethod.POST)
    public String editVolunteerPersonalData(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") EditVolunteerPersonalDataFormData formData
    ) {
        SessionData sessionData = SessionData.fromSession(session);
        User user = sessionData.getUser();
        if (user == null) {
            throw new NotLoggedInException();
        }
        if (!user.getUserId().equals(formData.getUserId())) {
            throw new NotOwnedByYouException();
        }

        // --- Validation Rules ---
        Errors errors = formData.validate();
        formData.validateAddressFilledIn(errors);

        if (errors.hasErrors()) {
            return showVolunteerFormWithErrors(model, sessionData, errors);
        }

        // --- Make the changes ---
        User newUser;
        try {
            database.modifyVolunteerPersonalFields(formData);
            sessionData.setUser(database.getUserById(formData.getUserId()));
            return "redirect:" + user.getUserType().getHomepage();
        } catch(EmailAlreadyInUseException err) {
            return showVolunteerFormWithErrors(model, sessionData, new Errors("That email is already in use by another user."));
        }
    }

}
