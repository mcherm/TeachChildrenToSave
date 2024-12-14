package com.tcts.controller;

import com.tcts.common.SessionData;
import com.tcts.exception.NotLoggedInException;
import com.tcts.formdata.Errors;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.SQLException;

/** A controller for the admin screen where passwords and password resets are managed. */
@Controller
public class ManagePasswordsController {

    @RequestMapping(value = "/managePasswords.htm", method = RequestMethod.GET)
    public String emailAnnouncement(HttpSession session, Model model) {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        return "managePasswords";
    }

}
