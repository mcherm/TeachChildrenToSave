package com.tcts.controller;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.exception.NotLoggedInException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

/**
 * This is a controller for editing (at least some of) the tables in
 * the database that the site admin is allowed to maintain.
 */
@Controller
public class AdminEditController {

    @Autowired
    private DatabaseFacade database;


    @RequestMapping(value = "/adminEditAllowedTimes", method = RequestMethod.GET)
    public String listAllowedTimes(
            HttpSession session,
            Model model
        ) throws SQLException
    {
        // --- Make sure it's the site admin that's logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        model.addAttribute("allowedTimes", database.getAllowedTimes());

        return "adminEditAllowedTimes";
    }


}
