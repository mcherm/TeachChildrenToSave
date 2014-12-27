package com.tcts.controller;




import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.tcts.exception.AppConfigurationException;
import com.tcts.exception.EmailAlreadyInUseException;
import com.tcts.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Bank;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.NoSuchBankException;
import com.tcts.formdata.VolunteerRegistrationFormData;
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
        if (formData.getEmail() == null || formData.getEmail().trim().length()==0) {
            return showFormWithErrorMessage(model,
                    "You must provide a valid email.");
        }
        if (formData.getBankId() == null || formData.getBankId().trim().length() == 0 || formData.getBankId().equals("0")) {
            return showFormWithErrorMessage(model, "You must select a bank.");
        }
        // FIXME: probably need more validation rules
        // --- Create object ---
        try {
            String salt = SecurityUtil.generateSalt();
            String hashedPassword = SecurityUtil.getHashedPassword(formData.getPassword(), salt);
            Volunteer volunteer = database.insertNewVolunteer(formData, hashedPassword, salt);
            SessionData sessionData = SessionData.beginNewSession(session);
            sessionData.setUser(volunteer);
            sessionData.setAuthenticated(true);
            EmailUtil emailUtil = new EmailUtil();
            try {
                emailUtil.sendEmail(volunteer.getEmail());
            } catch(AppConfigurationException err) {
                // FIXME: Need to log or report this someplace more reliable.
                System.err.println("Could not send email for new volunteer '" + volunteer.getEmail() + "'.");
            } catch(IOException err) {
                // FIXME: Need to log or report this someplace more reliable.
                System.err.println("Could not send email for new volunteer '" + volunteer.getEmail() + "'.");
            }
            return "redirect:" + volunteer.getUserType().getHomepage();
        } catch (NoSuchBankException e) {
            return showFormWithErrorMessage(model, "That is not a valid bank.");
        } catch (EmailAlreadyInUseException e) {
            return showFormWithErrorMessage(model, "That email is already in use; please choose another.");
        }
    }
}