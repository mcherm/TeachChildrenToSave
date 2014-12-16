package com.tcts.controller;

import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.common.NotLoggedInException;
import com.tcts.common.SessionData;
import com.tcts.dao.DatabaseFacade;
import com.tcts.datamodel.User;
import com.tcts.model.EditPersonalDataFormData;

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
        model.addAttribute("errorMessage", "");
        return "editPersonalData";
    }

    /**
     * Actually change the values that were entered.
     */
    @RequestMapping(value="/editPersonalData", method=RequestMethod.POST)
    public String editPersonalData(HttpSession session, Model model,
                                   @ModelAttribute("formData") EditPersonalDataFormData formData
    ) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        // FIXME: must validate the fields
        User user = sessionData.getUser();
        if (user == null) {
            throw new NotLoggedInException();
        }
        User newUser = database.modifyUserPersonalFields(user.getUserId(), formData);
        sessionData.setUser(newUser);
        return "redirect:" + user.getUserType().getHomepage();
    }
}
