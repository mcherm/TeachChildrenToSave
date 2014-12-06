package com.tcts.controller;

import com.tcts.dao2.DatabaseFacade;
import com.tcts.model.SessionData;
import com.tcts.model2.Event;
import com.tcts.model2.User;
import com.tcts.model2.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

/**
 * This is a controller for the "home page" for users. It renders substantially
 * different information depending on the type of user who is logged in.
 */
@Controller
public class UserHomePageController {

    @Autowired
    private DatabaseFacade database;

    /**
     * Render the home page for a volunteer.
     */
    @RequestMapping(value = "volunteerHome", method = RequestMethod.GET)
    public String showVolunteerHomePage(HttpSession session) {
        SessionData sessionData = SessionData.fromSession(session);
        User user = sessionData.getUser();
        if (user.getUserType() != UserType.VOLUNTEER) {
            throw new RuntimeException("Internal error; this should no occur.");
        }
        return "volunteerHome";
    }

    /**
     * Render the home page for a teacher.
     */
    @RequestMapping(value = "teacherHome", method = RequestMethod.GET)
    public String showTeacherHomePage(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        User user = sessionData.getUser();
        if (user.getUserType() != UserType.TEACHER) {
            throw new RuntimeException("Internal error; this should no occur.");
        }
        List<Event> events = database.getEventsByTeacher(sessionData.getUser().getUserId());
        model.addAttribute("events", events);
        return "teacherHome";
    }

    /**
     * Render the home page for a volunteer.
     */
    @RequestMapping(value = "bankAdminHome", method = RequestMethod.GET)
    public String showBankAdminHomePage(HttpSession session) {
        SessionData sessionData = SessionData.fromSession(session);
        User user = sessionData.getUser();
        if (user.getUserType() != UserType.VOLUNTEER) {
            throw new RuntimeException("Internal error; this should no occur.");
        }
        return "bankAdminHome";
    }

    /**
     * Render the home page for a teacher.
     */
    @RequestMapping(value = "siteAdminHome", method = RequestMethod.GET)
    public String showSiteAdminHomePage(HttpSession session) {
        SessionData sessionData = SessionData.fromSession(session);
        User user = sessionData.getUser();
        if (user.getUserType() != UserType.VOLUNTEER) {
            throw new RuntimeException("Internal error; this should no occur.");
        }
        return "siteAdminHome";
    }

}
