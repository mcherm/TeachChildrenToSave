package com.tcts.controller;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.User;
import com.tcts.exception.NoSuchEventException;
import com.tcts.exception.NoSuchUserException;
import com.tcts.exception.NotLoggedInException;
import com.tcts.exception.TeacherHasEventsException;
import com.tcts.exception.VolunteerHasEventsException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;


/** A controller for the admin screen where passwords and password resets are managed. */
@Controller
public class ManageDatabaseController {

    @Autowired
    private DatabaseFacade database;

    @RequestMapping(value = "/manageDatabase.htm", method = RequestMethod.GET)
    public String manageDatabase(HttpSession session) {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        return "manageDatabase";
    }

    @RequestMapping(value = "deleteUserDataConfirm.htm", method = RequestMethod.GET)
    public String deleteUserDataConfirm(HttpSession session) {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        return "deleteUserDataConfirm";
    }

    @RequestMapping(value = "deleteUserDataConfirm.htm", method = RequestMethod.POST)
    public String deleteUserData(HttpSession session) {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Delete everything ---
        // NOTE: This is not a built-in for the database facade because there is no need for
        //   good performance.
        List<Event> events = database.getAllEvents();
        for (Event event : events) {
            try {
                database.deleteEvent(event.getEventId());
            } catch(NoSuchEventException err) {
                // just ignore this
            }
        }
        List<User> users = database.getAllUsers();
        for (User user : users) {
            try {
                switch(user.getUserType()) {
                    case SITE_ADMIN -> {} // Leave these in place
                    case TEACHER -> database.deleteTeacher(user.getUserId());
                    case VOLUNTEER -> database.deleteVolunteer(user.getUserId());
                    case BANK_ADMIN -> database.deleteVolunteer(user.getUserId());
                }
            } catch(NoSuchUserException err) {
                // just ignore this
            } catch(TeacherHasEventsException | VolunteerHasEventsException err) {
                // Shouldn't happen (unless someone is simultaneously creating events).
                // If it happens, just allow that user to not be deleted.
            }
        }

        // --- All done ---
        return "redirect:siteAdminHome.htm";
    }
}
