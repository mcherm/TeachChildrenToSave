package com.tcts.controller;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.ApprovalStatus;
import com.tcts.datamodel.Bank;
import com.tcts.datamodel.BankAdmin;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NotLoggedInException;
import com.tcts.email.EmailUtil;
import com.tcts.util.EventUtil;
import com.tcts.util.TemplateUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.sql.SQLException;
import java.util.List;
import java.util.SortedSet;

/**
 * Some pages (and fragments of pages) that are used by bank admins to manage the
 * volunteers from that bank.
 */
@Controller
public class BankAdminActionsController {

    @Autowired
    private DatabaseFacade database;

    @Autowired
    private TemplateUtil templateUtil;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private CancelWithdrawController cancelWithdrawController;


    /**
     * This generates, not a page, but a detail which is loaded dynamically. The detail
     * contains the particular classes that a volunteer is signed up for.
     */
    @RequestMapping(value = "bankAdminHomeDetail.htm", method = RequestMethod.POST)
    public String detailCoursesForAVolunteer(
            HttpSession session,
            Model model,
            @RequestParam("volunteerId") String volunteerId
        ) throws SQLException
    {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        BankAdmin bankAdmin = sessionData.getBankAdmin();
        if (bankAdmin == null) {
            throw new NotLoggedInException("Cannot navigate to this page unless you are a logged-in bank admin.");
        }

        // --- Load the bank ---
        Bank bank = database.getBankById(bankAdmin.getBankId());

        // --- Load the events ---
        // NOTE: We are not bothering to verify that this volunteer volunteers for this
        // particular bank. It was not deemed a security risk.
        List<Event> events = database.getEventsByVolunteerWithTeacherAndSchool(volunteerId);

        // --- Show the page (well, detail) ---
        model.addAttribute("bank", bank);
        model.addAttribute("events", events);
        model.addAttribute("showGradeColumn", EventUtil.hasMultipleGrades(database, events));
        model.addAttribute("showDeliveryMethodColumn", EventUtil.hasMultipleDeliveryMethods(database,events));
        return "bankAdminHomeDetail";
    }

    /**
     * Sets the given volunteer to CHECKED, which will REMOVE them from
     * all events they had volunteered for.
     */
    @RequestMapping(value = "approveVolunteer.htm", method = RequestMethod.POST)
    public String approveVolunteer(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam("volunteerId") String volunteerId
    ) throws SQLException
    {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        BankAdmin bankAdmin = sessionData.getBankAdmin();
        if (bankAdmin == null) {
            throw new NotLoggedInException("Cannot navigate to this page unless you are a logged-in bank admin.");
        }

        // --- Find that volunteer; make sure it's ours ---
        Volunteer volunteer = (Volunteer) database.getUserById(volunteerId);
        if (volunteer == null) {
            throw new InvalidParameterFromGUIException();
        }
        if (! volunteer.getBankId().equals(bankAdmin.getBankId())) {
            throw new InvalidParameterFromGUIException();
        }

        // --- Actually approve the person ---
        database.updateApprovalStatusById(volunteer.getUserId(), ApprovalStatus.CHECKED);

        // --- And navigate to home page ---
        return "redirect:" + bankAdmin.getUserType().getHomepage();
    }

    @RequestMapping(value = "unApproveVolunteer.htm", method = RequestMethod.POST)
    public String unApproveVolunteer(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam("volunteerId") String volunteerId
    ) throws SQLException
    {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        BankAdmin bankAdmin = sessionData.getBankAdmin();
        if (bankAdmin == null) {
            throw new NotLoggedInException("Cannot navigate to this page unless you are a logged-in bank admin.");
        }

        // --- Find that volunteer; make sure it's ours ---
        Volunteer volunteer = (Volunteer) database.getUserById(volunteerId);
        if (volunteer == null) {
            throw new InvalidParameterFromGUIException();
        }
        if (! volunteer.getBankId().equals(bankAdmin.getBankId())) {
            throw new InvalidParameterFromGUIException();
        }

        // --- We think this method is supposed Undo the Approval Status ie, put the user back in an UNCHECKED State ---
        database.updateApprovalStatusById(volunteer.getUserId(), ApprovalStatus.UNCHECKED);

        // --- And navigate to home page ---
        return "redirect:" + bankAdmin.getUserType().getHomepage();
    }

    /**
     * Sets the given volunteer to "suspended", which will REMOVE them from
     * all events they had volunteered for.
     */
    @RequestMapping(value = "suspendVolunteer.htm", method = RequestMethod.POST)
    public String suspendVolunteer(
            HttpSession session,
            HttpServletRequest request,
            @RequestParam("volunteerId") String volunteerId
        ) throws SQLException
    {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        BankAdmin bankAdmin = sessionData.getBankAdmin();
        if (bankAdmin == null) {
            throw new NotLoggedInException("Cannot navigate to this page unless you are a logged-in bank admin.");
        }

        // --- Find that volunteer; make sure it's ours ---
        Volunteer volunteer = (Volunteer) database.getUserById(volunteerId);
        if (volunteer == null) {
            throw new InvalidParameterFromGUIException();
        }
        if (! volunteer.getBankId().equals(bankAdmin.getBankId())) {
            throw new InvalidParameterFromGUIException();
        }

        // --- Resign from the events ---
        List<Event> events = database.getEventsByVolunteer(volunteerId);
        for (Event event : events) {
            cancelWithdrawController.withdrawFromAnEvent(event, request, null);
        }

        // --- Actually suspend the person ---
        database.updateApprovalStatusById(volunteer.getUserId(), ApprovalStatus.SUSPENDED);

        // --- And navigate to home page ---
        return "redirect:" + bankAdmin.getUserType().getHomepage();
    }


    /**
     * Sets the given volunteer to "suspended", which will REMOVE them from
     * all events they had volunteered for.
     */
    @RequestMapping(value = "reinstateVolunteer.htm", method = RequestMethod.POST)
    public String reinstateVolunteer(
            HttpSession session,
            @RequestParam("volunteerId") String volunteerId
        ) throws SQLException
    {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        BankAdmin bankAdmin = sessionData.getBankAdmin();
        if (bankAdmin == null) {
            throw new NotLoggedInException("Cannot navigate to this page unless you are a logged-in bank admin.");
        }

        // --- Find that volunteer; make sure it's ours ---
        Volunteer volunteer = (Volunteer) database.getUserById(volunteerId);
        if (volunteer == null) {
            throw new InvalidParameterFromGUIException();
        }
        if (! volunteer.getBankId().equals(bankAdmin.getBankId())) {
            throw new InvalidParameterFromGUIException();
        }

        // --- Actually restore the person ---
        database.updateApprovalStatusById(volunteer.getUserId(), ApprovalStatus.CHECKED);

        // --- And navigate to home page ---
        return "redirect:" + bankAdmin.getUserType().getHomepage();
    }
}
