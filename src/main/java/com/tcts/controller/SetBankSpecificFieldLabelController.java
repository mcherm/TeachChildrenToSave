package com.tcts.controller;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NoSuchBankException;
import com.tcts.exception.NotLoggedInException;
import com.tcts.exception.NotOwnedByYouException;
import com.tcts.formdata.SetBankSpecificFieldLabelFormData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;

/**
 * A controller that handles the request to update the bank specific field label.
 */
@Controller
public class SetBankSpecificFieldLabelController {

    @Autowired
    private DatabaseFacade database;


    @RequestMapping(value="setBankSpecificFieldLabel.htm", method=RequestMethod.POST)
    public String setBankSpecificFieldLabel(
            HttpSession session,
            @ModelAttribute("formData") SetBankSpecificFieldLabelFormData formData
        ) throws SQLException
    {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getBankAdmin() == null) {
            throw new NotLoggedInException();
        }
        if (!sessionData.getBankAdmin().getBankId().equals(formData.getBankId())) {
            throw new NotOwnedByYouException();
        }

        try {
            database.setBankSpecificFieldLabel(formData);
        } catch(NoSuchBankException err) {
            throw new InvalidParameterFromGUIException();
        }

        return "redirect:bankAdminHome.htm";
    }
}
