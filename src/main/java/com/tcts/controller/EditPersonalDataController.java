package com.tcts.controller;

import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import com.tcts.exception.EmailAlreadyInUseException;
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
import com.tcts.formdata.EditPersonalDataFormData;

/**
 * A controller for the screen where they are editing the
 */
@Controller
public class EditPersonalDataController {

    @Autowired
    private DatabaseFacade database;

    /**
     * Render the page for editing personal data.
     */
    @RequestMapping(value="/editPersonalData", method=RequestMethod.GET)
    public String showEditPersonalDataPage(HttpSession session, Model model) {
        SessionData sessionData = SessionData.fromSession(session);
        User user = sessionData.getUser();
        EditPersonalDataFormData formData = new EditPersonalDataFormData();
        formData.setEmail(user.getEmail());
        formData.setFirstName(user.getFirstName());
        formData.setLastName(user.getLastName());
        formData.setPhoneNumber(user.getPhoneNumber());
        model.addAttribute("formData", formData);
        return showFormWithErrorMessage(model, "");
    }

    /**
     * A subroutine used to set up and then show the editPersonalData form. It
     * returns the string, so you can invoke it as "return showFormWithErrorMessage(...)".
     */
    public String showFormWithErrorMessage(Model model, String errorMessage) {
        model.addAttribute("errorMessage", errorMessage);
        return "editPersonalData";
    }

    /**
     * Actually change the values that were entered.
     */
    @RequestMapping(value="/editPersonalData", method=RequestMethod.POST)
    public String editPersonalData(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") EditPersonalDataFormData formData
        ) throws SQLException
    {
        SessionData sessionData = SessionData.fromSession(session);
        // FIXME: must validate the fields
        User user = sessionData.getUser();
        if (user == null) {
            throw new NotLoggedInException();
        }

        User newUser;
        try {
            newUser = database.modifyUserPersonalFields(user.getUserId(), formData);
            sessionData.setUser(newUser);
            return "redirect:" + user.getUserType().getHomepage();
        } catch(EmailAlreadyInUseException err) {
            return showFormWithErrorMessage(model, "That email is already in use by another user.");
        }
    }
}
