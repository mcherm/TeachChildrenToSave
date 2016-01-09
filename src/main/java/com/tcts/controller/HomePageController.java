package com.tcts.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.tcts.datamodel.ApprovalStatus;
import com.tcts.formdata.SetBankSpecificFieldLabelFormData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Bank;
import com.tcts.datamodel.BankAdmin;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.School;
import com.tcts.datamodel.SiteAdmin;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.InconsistentDatabaseException;

/**
 * This is a controller for the "home page" for users. It renders substantially
 * different information depending on the type of user who is logged in.
 */
@Controller
public class HomePageController {

    @Autowired
    private DatabaseFacade database;

    /**
     * This renders the default home page for someone who is not logged in.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showDefaultHomePage(HttpServletRequest httpServletRequest, Model model) throws SQLException {
        if (httpServletRequest.getRequestURI().endsWith("/")) {
            String currentYear = database.getSiteSettings().get("CurrentYear");
            String eventDatesOnHomepage = database.getSiteSettings().get("EventDatesOnHomepage");
            model.addAttribute("currentYear", currentYear);
            model.addAttribute("eventDatesOnHomepage", eventDatesOnHomepage);
            return "home";
        } else {
            return "redirect:/";
        }
    }

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

        // --- Get the bank ---
        Bank bank = database.getBankById(volunteer.getBankId());
        volunteer.setLinkedBank(bank);
        // --- Get list of events ---
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

        // --- Display the page ---
        model.addAttribute("bank", bank);
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
        boolean eventCreationOpen = CreateEventController.isEventCreationOpen(database);
        model.addAttribute("events", events);
        model.addAttribute("eventCreationOpen", eventCreationOpen);
        return "teacherHome";
    }

    /**
     * Render the home page for a volunteer.
     */
    @RequestMapping(value = "bankAdminHome", method = RequestMethod.GET)
    public String showBankAdminHomePage(HttpSession session, Model model) throws SQLException {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        BankAdmin bankAdmin = sessionData.getBankAdmin();
        if (bankAdmin == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in bank admin.");
        }

        // --- Load the bank ---
        Bank bank = database.getBankById(bankAdmin.getBankId());

        // --- Obtain and sort the volunteers ---
        List<Volunteer> volunteers = database.getVolunteersByBank(bankAdmin.getBankId());
        List<Volunteer> newVolunteers = new ArrayList<Volunteer>();
        List<Volunteer> normalVolunteers = new ArrayList<Volunteer>(volunteers.size());
        List<Volunteer> suspendedVolunteers = new ArrayList<Volunteer>();
        for (Volunteer volunteer : volunteers) {
            if (volunteer.getApprovalStatus() == ApprovalStatus.Unchecked) {
               newVolunteers.add (volunteer);
            } else if (volunteer.getApprovalStatus() == ApprovalStatus.Checked) {
                normalVolunteers.add(volunteer);
            } else {
                suspendedVolunteers.add(volunteer);
            }
        }

        // --- Set up a form in case they want to change the bank specific field label ---
        SetBankSpecificFieldLabelFormData formData = new SetBankSpecificFieldLabelFormData();
        formData.setBankId(bank.getBankId());
        formData.setBankSpecificFieldLabel(bank.getBankSpecificDataLabel());

        // --- Show homepage ---
        model.addAttribute("bank", bank);
        model.addAttribute("normalVolunteers", normalVolunteers);
        model.addAttribute("suspendedVolunteers", suspendedVolunteers);
        model.addAttribute("newVolunteers", newVolunteers);
        model.addAttribute("formData", formData);
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