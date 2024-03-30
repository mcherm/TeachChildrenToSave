package com.tcts.controller;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.SiteAdmin;
import com.tcts.datamodel.SiteStatistics;
import com.tcts.exception.NotLoggedInException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;

/**
 * This renders a page that displays certain program-wide statistics for the site
 * administrator.
 */
@Controller
public class AdminViewStatisticsController {

    @Autowired
    private DatabaseFacade database;

    /**
     * This generates, not a page, but a detail which is loaded dynamically. The detail
     * contains the particular classes that a volunteer is signed up for.
     */
    @RequestMapping(value = "adminViewStatistics.htm", method = RequestMethod.GET)
    public String detailCoursesForAVolunteer(
            HttpSession session,
            Model model
        ) throws SQLException
    {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        SiteAdmin siteAdmin = sessionData.getSiteAdmin();
        if (siteAdmin == null) {
            throw new NotLoggedInException("Cannot navigate to this page unless you are a logged-in site admin.");
        }

        // --- Collect the statistics ---
        SiteStatistics siteStatistics = database.getSiteStatistics();

        // --- Display the page ---
        model.addAttribute("siteStatistics", siteStatistics);
        return "adminViewStatistics";
    }


}
