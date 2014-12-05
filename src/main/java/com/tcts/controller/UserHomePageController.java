package com.tcts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * This is a controller for the "home page" for users. It renders substantially
 * different information depending on the type of user who is logged in.
 */
@Controller
public class UserHomePageController {

    /**
     * Render the home page for a volunteer.
     */
    @RequestMapping(value = "volunteerHome", method = RequestMethod.GET)
    public String showVolunteerHomePage() {
        return "volunteerHome";
    }

    /**
     * Render the home page for a teacher.
     */
    @RequestMapping(value = "teacherHome", method = RequestMethod.GET)
    public String showTeacherHomePage() {
        return "teacherHome";
    }

    /**
     * Render the home page for a volunteer.
     */
    @RequestMapping(value = "bankAdminHome", method = RequestMethod.GET)
    public String showBankAdminHomePage() {
        return "bankAdminHome";
    }

    /**
     * Render the home page for a teacher.
     */
    @RequestMapping(value = "siteAdminHome", method = RequestMethod.GET)
    public String showSiteAdminHomePage() {
        return "siteAdminHome";
    }

}
