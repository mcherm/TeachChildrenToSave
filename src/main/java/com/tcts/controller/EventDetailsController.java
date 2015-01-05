package com.tcts.controller;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Bank;
import com.tcts.datamodel.BankAdmin;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.School;
import com.tcts.datamodel.SiteAdmin;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.User;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NotLoggedInException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;


/**
 * A page displaying a nice summary of a single event. Intended for viewing by
 * BOTH teachers AND volunteers (or really any logged-in user). Because of that
 * is has a somewhat fancy "go back to previous page" functionality.
 */
@Controller
public class EventDetailsController {

    @Autowired
    private DatabaseFacade database;

    /**
     * Render the page.
     */
    @RequestMapping(value = "eventDetails", method = RequestMethod.POST)
    public String showEventDetail(
            HttpSession session,
            Model model,
            @RequestParam("eventId") String eventId,
            @RequestParam("doneURL") String doneURL
        ) throws SQLException
    {
        SessionData sessionData = SessionData.fromSession(session);

        // --- Find the event ---
        if (eventId == null || eventId.length() == 0) {
            throw new InvalidParameterFromGUIException();
        }
        Event event = database.getEventById(eventId);
        if (event == null) {
            // No such event by that ID
            throw new InvalidParameterFromGUIException();
        }

        // --- Make sure that the user has the right to see it ---
        User user = sessionData.getUser();
        if (user == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in user.");
        } else if (user instanceof Volunteer) {
            if (event.getVolunteerId() != null && !user.getUserId().equals(event.getVolunteerId())) {
                throw new NotLoggedInException();
            }
        } else if (user instanceof Teacher) {
            if (!user.getUserId().equals(event.getTeacherId())) {
                throw new NotLoggedInException();
            }
        } else if (user instanceof BankAdmin || user instanceof SiteAdmin) {
            // these roles allowed to see details on ALL events
        } else {
            throw new RuntimeException("Unknown type for user: " + user.getClass().toString());
        }

        // --- Load all data linked to the event ---
        if (event.getVolunteerId() != null) {
            Volunteer linkedVolunteer = (Volunteer) database.getUserById(event.getVolunteerId());
            event.setLinkedVolunteer(linkedVolunteer);
            Bank linkedBank = database.getBankById(linkedVolunteer.getBankId());
            linkedVolunteer.setLinkedBank(linkedBank);
        }
        Teacher linkedTeacher = (Teacher) database.getUserById(event.getTeacherId());
        event.setLinkedTeacher(linkedTeacher);
        School linkedSchool = database.getSchoolById(linkedTeacher.getSchoolId());
        linkedTeacher.setLinkedSchool(linkedSchool);

        // --- Display the page ---
        model.addAttribute("event", event);
        model.addAttribute("doneURL", doneURL);
        return "eventDetails";
    }

}
