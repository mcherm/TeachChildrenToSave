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

import com.tcts.common.SessionData;
import com.tcts.dao.DatabaseFacade;
import com.tcts.datamodel.Bank;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.LoginAlreadyInUseException;
import com.tcts.exception.NoSuchBankException;
import com.tcts.model.VolunteerRegistrationFormData;
import com.tcts.util.EmailUtil;

/**
 * A controller for the flow where new volunteers sign up.
 */
@Controller
public class VolunteerRegistrationController {

    @Autowired
    private DatabaseFacade database;

    @RequestMapping(value="/registerVolunteer", method=RequestMethod.GET)
    public String showRegisterVolunteerPage(HttpSession session, Model model) throws SQLException {
        SessionData.ensureNoActiveSession(session);
        model.addAttribute("formData", new VolunteerRegistrationFormData());
        return showFormWithErrorMessage(model, "");
    }

    /**
     * A subroutine used to set up and then show the register teacher form. It
     * returns the string, so you can invoke it as "return showFormWithErrorMessage(...)".
     */
    private String showFormWithErrorMessage(Model model, String errorMessage) throws SQLException {
        List<Bank> banks = database.getAllBanks(); // FIXME: It's expensive to retrieve it each time; should cache it for performance.
        model.addAttribute("banks", banks);
        model.addAttribute("errorMessage", errorMessage);
        return "registerVolunteer";
    }


    @RequestMapping(value="/registerVolunteer", method=RequestMethod.POST)
    public String createNewVolunteer(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") VolunteerRegistrationFormData formData
    ) throws SQLException
    {
        SessionData.ensureNoActiveSession(session);
        // --- Validation rules ---
        if (formData.getLogin() == null || formData.getLogin().trim().length()==0) {
            return showFormWithErrorMessage(model,
                    "You must select a valid login name. You may use your email address if you like.");
        }
        if (formData.getBankId() == null || formData.getBankId().trim().length() == 0 || formData.getBankId().equals("0")) {
            return showFormWithErrorMessage(model, "You must select a bank.");
        }
        // FIXME: probably need more validation rules
        // --- Create object ---
        try {
            Volunteer volunteer = database.insertNewVolunteer(formData);
            SessionData sessionData = SessionData.beginNewSession(session);
            sessionData.setUser(volunteer);
            sessionData.setAuthenticated(true);
            EmailUtil emailUtil = new EmailUtil();
            emailUtil.sendEmail(volunteer.getEmail());
            return "redirect:" + volunteer.getUserType().getHomepage();
        } catch (NoSuchBankException e) {
            return showFormWithErrorMessage(model, "That is not a valid bank.");
        } catch (LoginAlreadyInUseException e) {
            return showFormWithErrorMessage(model, "That login is already in use; please choose another.");
        } catch (Exception e) {
            return showFormWithErrorMessage(model, "That login is already in use; please choose another.");
        }
    }
}