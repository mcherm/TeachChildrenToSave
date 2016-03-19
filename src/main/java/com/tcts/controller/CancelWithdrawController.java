package com.tcts.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.tcts.email.EmailSender;
import com.tcts.exception.InconsistentDatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.User;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.AppConfigurationException;
import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NoSuchEventException;
import com.tcts.exception.NoVolunteerOnThatEventException;
import com.tcts.exception.NotLoggedInException;
import com.tcts.exception.NotOwnedByYouException;
import com.tcts.formdata.WithdrawFormData;
import com.tcts.email.EmailUtil;
import com.tcts.util.TemplateUtil;


/**
 * Used for a teacher to cancel a class or a volunteer to cancel their registration
 * for a course.
 */
@Controller
public class CancelWithdrawController {

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private DatabaseFacade database;
    
    @Autowired
    private TemplateUtil templateUtil;
    
    @Autowired
    private EmailUtil emailUtil;

    /**
     *   A volunteer is withdrawn from a class
     *   This method can be called by a volunteer or bankadmin to withdraw themselves from a class
     *   they signed up for or by a siteadmin in which case they are withdrawing the current volunteer from the class
     */
    @RequestMapping(value = "volunteerWithdraw", method = RequestMethod.GET)
    public String showVolunteerWithdraw(
            HttpSession session,
            Model model,
            @RequestParam(value = "eventId") String eventId
        ) throws SQLException
    {
        // --- Ensure logged in as a Volunteer ---
        SessionData sessionData = SessionData.fromSession(session);
        Volunteer loggedInVolunteer;
        if (sessionData.getVolunteer()!= null){
            loggedInVolunteer = sessionData.getVolunteer();
        } else if (sessionData.getBankAdmin()!=null){
            loggedInVolunteer = sessionData.getBankAdmin();
        } else if (sessionData.getSiteAdmin()!=null){
            loggedInVolunteer = null;
        } else {
            throw new NotLoggedInException();
        }

        // --- Ensure the event is valid and is listed for this volunteer ---
        if (eventId == null || eventId.length() == 0) {
            throw new InvalidParameterFromGUIException();
        }
        Event event = database.getEventById(eventId);
        if (event == null) {
            // No such event by that ID
            throw new InvalidParameterFromGUIException();
        }
        if ((sessionData.getVolunteer()!= null  || sessionData.getBankAdmin()!= null)
                && (!loggedInVolunteer.getUserId().equals(event.getVolunteerId()))) {
            throw new NotOwnedByYouException();
        }

        // --- Display the page ---
        WithdrawFormData formData = new WithdrawFormData();
        formData.setEventId(eventId);
        model.addAttribute("formData", formData);
        model.addAttribute("errorMessage", "");
        return "volunteerWithdraw";
    }


    /**
     * A volunteer withdraws themselves from a class they had previously
     * volunteered for.
     */
    @RequestMapping(value = "volunteerWithdraw", method = RequestMethod.POST)
    public String doVolunteerWithdraw(
            HttpSession session,
            HttpServletRequest request,
            @ModelAttribute WithdrawFormData formData
        ) throws SQLException
    {
        // --- Ensure logged in as a Volunteer ---
        SessionData sessionData = SessionData.fromSession(session);

        String eventId = formData.getEventId();
        if (eventId == null || eventId.length() == 0) {
            throw new InvalidParameterFromGUIException();
        }

        Event event = database.getEventById(eventId);
        if (event == null) {
            // No such event by that ID
            throw new InvalidParameterFromGUIException();
        }

        if ((sessionData.getVolunteer()!= null)) {
            //if a volunteer is logged make sure the volunteer owns the event she is withdrawing from
            if (!sessionData.getVolunteer().getUserId().equals(event.getVolunteerId())) {
                throw new NotOwnedByYouException();
            }
        } else if ((sessionData.getBankAdmin()!= null)) {
            //if a bankadmin is logged in make sure the bankadmin  owns the event she is withdrawing from
            if (!sessionData.getBankAdmin().getUserId().equals(event.getVolunteerId())) {
                throw new NotOwnedByYouException();
            }
       } else if (sessionData.getSiteAdmin() == null) { //siteadmin has permission to withdraw other volunteer's events and we don't do the "owned by you" check
            //if a bankadmin, volunteer, or siteadmin is not logged in
            throw new NotLoggedInException();
        }

        // --- Perform the withdraw ---
        withdrawFromAnEvent(event, request, formData.getWithdrawNotes());

        // --- Done ---
        return "redirect:" + sessionData.getUser().getUserType().getHomepage();   }


    /**
     * Subroutine that actually withdraws a volunteer from an event. Exposed
     * here so it can be shared from other locations.
     * <p>
    */
    public  void withdrawFromAnEvent(
            Event event,
            HttpServletRequest request,
            String withdrawNotes
    ) throws SQLException
    {
        // --- Make sure we have called it where it makes sense ---
        if (event.getVolunteerId() == null) {
            throw new NoVolunteerOnThatEventException();
        }

        // --- Make sure data is loaded ---
        if (event.getLinkedTeacher() == null) {
            Teacher linkedTeacher = (Teacher) database.getUserById((event.getTeacherId()));
            event.setLinkedTeacher(linkedTeacher);
        }

        // --- Save the info needed for emails ---
        String teacherEmail = event.getLinkedTeacher().getEmail();
        User teacher = database.getUserById(event.getVolunteerId());
        User volunteer= database.getUserById(event.getTeacherId());

        // --- Update the database ---
        try {
            database.volunteerForEvent(event.getEventId(), null);
        } catch(NoSuchEventException err) {
            throw new RuntimeException("Shouldn't happen; we just checked if it was there.");
        }

        // --- Send Emails ---
        // Send email to Teacher
        try {
            emailSender.sendVolunteerWithdrawEmailToTeacher(event, request, withdrawNotes, teacherEmail, teacher, volunteer);
        } catch(AppConfigurationException err) {
            // FIXME: Need to log or report this someplace more reliable.
            System.err.println("Could not send email for volunteer withdraw '" + teacherEmail + "'.");
        } catch(IOException err) {
            // FIXME: Need to log or report this someplace more reliable.
            System.err.println("Could not send email for volunteer withdraw '" + teacherEmail + "'.");
        }
    }


    /**
     * Display a confirmation page to allow a teacher to cancel a class.
     */
    @RequestMapping(value = "teacherCancel", method = RequestMethod.GET)
    public String showTeacherCancel(
            HttpSession session,
            Model model,
            @RequestParam(value = "eventId") String eventId
    ) throws SQLException
    {
        // --- Ensure logged in as a Teacher ---
        SessionData sessionData = SessionData.fromSession(session);
        Teacher loggedInTeacher = sessionData.getTeacher();
        if (loggedInTeacher == null) {
            throw new NotLoggedInException();
        }

        // --- Ensure the event is valid and is owned by this teacher ---
        if (eventId == null || eventId.length() == 0) {
            throw new InvalidParameterFromGUIException();
        }
        Event event = database.getEventById(eventId);
        if (event == null) {
            // No such event by that ID
            throw new InvalidParameterFromGUIException();
        }
        if (!loggedInTeacher.getUserId().equals(event.getTeacherId())) {
            throw new NotOwnedByYouException();
        }

        // --- Display the page ---
        WithdrawFormData formData = new WithdrawFormData();
        formData.setEventId(eventId);
        model.addAttribute("formData", formData);
        model.addAttribute("hasVolunteer", event.getVolunteerId() != null);
        model.addAttribute("errorMessage", "");
        return "teacherCancel";
    }


    /**
     * Cancel a teacher's class and sends the volunteer an email saying the class is cancelled.
     */
    @RequestMapping(value = "teacherCancel", method = RequestMethod.POST)
    public String doTeacherCancel(
            HttpSession session,
            HttpServletRequest request,
            @ModelAttribute WithdrawFormData formData
    ) throws SQLException
    {
        // --- Ensure logged in as a Teacher ---
        SessionData sessionData = SessionData.fromSession(session);
        Teacher loggedInTeacher = sessionData.getTeacher();
        if (loggedInTeacher == null) {
            throw new NotLoggedInException();
        }

        // --- Ensure the event is valid and is owned by this teacher ---
        String eventId = formData.getEventId();
        if (eventId == null || eventId.length() == 0) {
            throw new InvalidParameterFromGUIException();
        }
        Event event = database.getEventById(eventId);
        if (event == null) {
            // No such event by that ID
            throw new InvalidParameterFromGUIException();
        }
        if (!loggedInTeacher.getUserId().equals(event.getTeacherId())) {
            throw new NotOwnedByYouException();
        }

        // --- Update the database ---
        try {
            cancelEvent(request, loggedInTeacher, event);
        } catch(NoSuchEventException err) {
            throw new RuntimeException("Shouldn't happen; we just checked if it was there.");
        }



        // FIXME: Here we should send an email to the site admin

        // --- Done ---
        return "redirect:" + loggedInTeacher.getUserType().getHomepage();
    }

    /**
     * Subroutine that actually cancels an event. Exposed
     * here so it can be shared from other locations.  This subroutine deletes
     * the event from the database and if there is a volunteer signed up for it
     * sends an email to the volunteer
     * <p>
     */

    public void cancelEvent(
            HttpServletRequest request,
            Teacher teacher,
            Event event) throws SQLException, NoSuchEventException {

        // --- Send Emails ---

        if (event.getVolunteerId() != null) {
            Volunteer volunteer = (Volunteer) database.getUserById(event.getVolunteerId());
            if (volunteer == null) {
                throw new InconsistentDatabaseException("volunteer " + event.getVolunteerId() +  " is signed up for event but does not exist.");
            }
            emailSender.sendCancelEventEmailToVolunteer(volunteer, event, teacher, request);
        }
        database.deleteEvent(event.getEventId());
    }

}
