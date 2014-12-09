package com.tcts.controller;

import com.tcts.dao2.DatabaseFacade;
import com.tcts.common.SessionData;
import com.tcts.dao2.InconsistentDatabaseException;
import com.tcts.model2.Bank;
import com.tcts.model2.BankAdmin;
import com.tcts.model2.Event;
import com.tcts.model2.School;
import com.tcts.model2.SiteAdmin;
import com.tcts.model2.Teacher;
import com.tcts.model2.Volunteer;
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
    public String showVolunteerHomePage(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        Volunteer volunteer = sessionData.getVolunteer();
        if (volunteer == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        List<Event> events = database.getEventsByVolunteer(volunteer.getUserId());
        for (Event event : events) {
            Teacher teacher = (Teacher) database.getUserById(event.getTeacherId());
            if (teacher == null) {
                throw new InconsistentDatabaseException("Event " + event.getEventId() + " has no valid teacher.");
            }
            event.setLinkedTeacher(teacher);
            String schoolId = teacher.getSchoolId();
            if (schoolId == null) {
                throw new InconsistentDatabaseException("Teacher " + event.getTeacherId() + " has no valid school.");
            }
            School school = database.getSchoolById(schoolId);
            teacher.setLinkedSchool(school);
        }
        model.addAttribute("events", events);
        return "volunteerHome";
    }

    /**
     * Render the home page for a teacher.
     */
    @RequestMapping(value = "teacherHome", method = RequestMethod.GET)
    public String showTeacherHomePage(HttpSession session, Model model) throws SQLException, InconsistentDatabaseException {
        SessionData sessionData = SessionData.fromSession(session);
        Teacher teacher = sessionData.getTeacher();
        if (teacher == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in teacher.");
        }
        List<Event> events = database.getEventsByTeacher(teacher.getUserId());
        for (Event event : events) {
            String volunteerId = event.getVolunteerId();
            if (volunteerId != null) {
                Volunteer volunteer = (Volunteer) database.getUserById(volunteerId);
                event.setLinkedVolunteer(volunteer);
                Bank bank = database.getBankById(volunteer.getBankId());
                if (bank == null) {
                    throw new InconsistentDatabaseException("Volunteer " + volunteerId + " has no bank.");
                }
                volunteer.setLinkedBank(bank);
            }
        }
        model.addAttribute("events", events);
        return "teacherHome";
    }

    /**
     * Render the home page for a volunteer.
     */
    @RequestMapping(value = "bankAdminHome", method = RequestMethod.GET)
    public String showBankAdminHomePage(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        BankAdmin bankAdmin = sessionData.getBankAdmin();
        if (bankAdmin == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in bank admin.");
        }
        List<Volunteer> volunteers = database.getVolunteersByBank(bankAdmin.getBankId());
        model.addAttribute("volunteers", volunteers);
        return "bankAdminHome";
    }

    /**
     * Render the home page for a teacher.
     */
    @RequestMapping(value = "siteAdminHome", method = RequestMethod.GET)
    public String showSiteAdminHomePage(HttpSession session) {
        SessionData sessionData = SessionData.fromSession(session);
        SiteAdmin siteAdmin = sessionData.getSiteAdmin();
        if (siteAdmin == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in site admin.");
        }
        return "siteAdminHome";
    }

}