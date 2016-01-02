package com.tcts.controller;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.SiteAdmin;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NotLoggedInException;
import com.tcts.exception.NotOwnedByYouException;
import com.tcts.formdata.WithdrawFormData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.Map;

/**
 * Used by a SiteAdmin to view or change the site settings.
 */
@Controller
public class ViewEditSiteSettingsController {

    @Autowired
    private DatabaseFacade database;

    /**
     * A site admin views the current settings
     */
    @RequestMapping(value = "viewSiteSettings", method = RequestMethod.GET)
    public String viewSiteSettings(
            HttpSession session,
            Model model
    ) throws SQLException
    {
        // --- Ensure logged in as a SiteAdmin ---
        SessionData sessionData = SessionData.fromSession(session);
        SiteAdmin loggedInSiteAdmin = sessionData.getSiteAdmin();
        if (loggedInSiteAdmin == null) {
            throw new NotLoggedInException();
        }

        // --- Load the site settings ---
        Map<String, String> siteSettings = database.getSiteSettings();

        // --- Display the page ---
        model.addAttribute("siteSettings", siteSettings);
        return "viewSiteSettings";
    }

}
