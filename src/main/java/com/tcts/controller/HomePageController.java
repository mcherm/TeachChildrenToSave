package com.tcts.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.tcts.S3Bucket.S3Util;
import com.tcts.datamodel.ApprovalStatus;
import com.tcts.datamodel.Document;
import com.tcts.formdata.SetBankSpecificFieldLabelFormData;
import com.tcts.util.EventUtil;
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

    final static String SHOW_DOCUMENTS_SETTING = "ShowDocuments";

    @Autowired
    private DatabaseFacade database;

    @Autowired
    private S3Util s3Util;

    /**
     * This renders the default home page for someone who is not logged in.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showDefaultHomePage(HttpServletRequest httpServletRequest, Model model) {
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
    @RequestMapping(value = "volunteerHome.htm", method = RequestMethod.GET)
    public String showVolunteerHomePage(HttpSession session, Model model) {
        SessionData sessionData = SessionData.fromSession(session);
        Volunteer volunteer = sessionData.getVolunteer();
        if (volunteer == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }

        // --- Get the bank ---
        Bank bank = database.getBankById(volunteer.getBankId());
        volunteer.setLinkedBank(bank);
        // --- Get list of events ---
        List<Event> events = database.getEventsByVolunteerWithTeacherAndSchool(volunteer.getUserId());

        // -- Get list of important documents --
        SortedSet<Document> documents = database.getDocuments();
        List<String> volunteerDocs = new ArrayList<String>();
        for (Document document : documents) {
            if (document.getShowToVolunteer()){
                volunteerDocs.add(document.getName());
            }
        }

        // --- Display the page ---
        model.addAttribute("bank", bank);
        model.addAttribute("events", events);
        model.addAttribute("showGradeColumn",EventUtil.hasMultipleGrades(database, events));
        model.addAttribute("showDeliveryMethodColumn",  EventUtil.hasMultipleDeliveryMethods(database, events));
        boolean volunteerSignupsOpen = EventRegistrationController.isVolunteerSignupsOpen(database);
        model.addAttribute("volunteerSignupsOpen", volunteerSignupsOpen);

        if (volunteerDocs.size() == 0){
            model.addAttribute("showDocuments", false);
        } else {
            model.addAttribute("showDocuments", getShowDocuments());
        }
        model.addAttribute("s3Util",s3Util);
        model.addAttribute("volunteerDocs", volunteerDocs);
        return "volunteerHome";
    }

    /**
     * Render the home page for a teacher.
     */
    @RequestMapping(value = "teacherHome.htm", method = RequestMethod.GET)
    public String showTeacherHomePage(HttpSession session, Model model) throws InconsistentDatabaseException {
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

        //Get list of important documents
        // -- Get list of important documents --
        SortedSet<Document> documents = database.getDocuments();
        List<String> teacherDocs = new ArrayList<String>();
        for (Document document : documents) {
            if (document.getShowToTeacher()){
                teacherDocs.add(document.getName());
            }
        }

        boolean eventCreationOpen = CreateEventController.isEventCreationOpen(database);
        model.addAttribute("events", events);
        model.addAttribute("showGradeColumn",EventUtil.hasMultipleGrades(database, events));
        model.addAttribute("showDeliveryMethodColumn", EventUtil.hasMultipleDeliveryMethods(database, events));
        model.addAttribute("eventCreationOpen", eventCreationOpen);
        if (teacherDocs.size() == 0){
            model.addAttribute("showDocuments", false);
        } else {
            model.addAttribute("showDocuments", getShowDocuments());
        }
        model.addAttribute("s3Util",s3Util);
        model.addAttribute("teacherDocs", teacherDocs);

        return "teacherHome";
    }

    /**
     * Render the home page for a bankAdmin.
     */
    @RequestMapping(value = "bankAdminHome.htm", method = RequestMethod.GET)
    public String showBankAdminHomePage(HttpSession session, Model model) {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        BankAdmin bankAdmin = sessionData.getBankAdmin();
        if (bankAdmin == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in bank admin.");
        }

        // --- Load the bank ---
        Bank bank = database.getBankById(bankAdmin.getBankId());
        bankAdmin.setLinkedBank(bank);

        // --- Obtain and sort the volunteers ---
        List<Volunteer> volunteers = database.getVolunteersByBank(bankAdmin.getBankId());
        List<Volunteer> newVolunteers = new ArrayList<>();
        List<Volunteer> normalVolunteers = new ArrayList<>(volunteers.size());
        List<Volunteer> suspendedVolunteers = new ArrayList<>();
        for (Volunteer volunteer : volunteers) {
            if (volunteer.getApprovalStatus() == ApprovalStatus.UNCHECKED) {
               newVolunteers.add (volunteer);
            } else if (volunteer.getApprovalStatus() == ApprovalStatus.CHECKED) {
                normalVolunteers.add(volunteer);
            } else {
                suspendedVolunteers.add(volunteer);
            }
        }

        // --- Set up a form in case they want to change the bank specific field label ---
        SetBankSpecificFieldLabelFormData formData = new SetBankSpecificFieldLabelFormData();
        formData.setBankId(bank.getBankId());
        formData.setBankSpecificFieldLabel(bank.getBankSpecificDataLabel());

        //Load the list of important documents shown to volunteer and bank admin
        SortedSet<Document> documents = database.getDocuments();
        List<String> volunteerDocs = new ArrayList<>();
        List<String> bankAdminDocs = new ArrayList<>();
        for (Document document : documents) {
            if (document.getShowToVolunteer()) {
                volunteerDocs.add(document.getName());
            } else if (document.getShowToBankAdmin()) {
                bankAdminDocs.add(document.getName());
            }
        }

        // --- Check if we should show the grade column ---
        List<String> allowedGrades = database.getAllowedGrades();

        // --- Check if we should show the delivery method column ---
        List<String> allowedDeliveryMethods = database.getAllowedDeliveryMethods();

        // --- Show homepage ---
        model.addAttribute("bank", bank);
        model.addAttribute("normalVolunteers", normalVolunteers);
        model.addAttribute("suspendedVolunteers", suspendedVolunteers);
        model.addAttribute("newVolunteers", newVolunteers);
        model.addAttribute("formData", formData);
        model.addAttribute("showDocuments", getShowDocuments());
        model.addAttribute("volunteerDocs",volunteerDocs);
        model.addAttribute("bankAdminDocs", bankAdminDocs);
        model.addAttribute("s3Util",s3Util);
        model.addAttribute("showGradeColumn", allowedGrades.size() >= 2);
        model.addAttribute("showDeliveryMethodColumn", allowedDeliveryMethods.size() >= 2);
        return "bankAdminHome";
    }


    /**
     * Render the home page for a site admin?.
     */
    @RequestMapping(value = "siteAdminHome.htm", method = RequestMethod.GET)
    public String showSiteAdminHomePage(HttpSession session) {
        SessionData sessionData = SessionData.fromSession(session);
        SiteAdmin siteAdmin = sessionData.getSiteAdmin();
        if (siteAdmin == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in site admin.");
        }
        return "siteAdminHome";
    }


    /**
     * Looks at the site setting and returns true if the documents should be shown, false if not.
     */
    private boolean getShowDocuments() {
        return database.getSiteSettings().get(SHOW_DOCUMENTS_SETTING).equalsIgnoreCase("yes");
    }
}
