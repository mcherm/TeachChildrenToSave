package com.tcts.controller;


import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.common.SessionData;
import com.tcts.dao.DatabaseFacade;
import com.tcts.datamodel.User;
import com.tcts.datamodel.Volunteer;

@Controller

public class VolunteerController extends AuthenticationController{

	@Autowired
    private DatabaseFacade database;
    
    /**
     * Render the list of users .
     */
    @RequestMapping(value = "/volunteer/list", method = RequestMethod.GET)
    public String showVolunteersList(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        Volunteer volunteer = sessionData.getVolunteer();
        if (volunteer == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        List<? super User> volunteers = database.getUsersByType("V");
        
        model.addAttribute("volunteers", volunteers);
        return "volunteers";
    }
    
    @RequestMapping(value = "/volunteer/delete", method = RequestMethod.GET)
    public String deleteVolunteer(@ModelAttribute(value="volunteer") Volunteer volunteer,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        database.deleteVolunteer(volunteer.getUserId());
        
        model.addAttribute("volunteers", database.getUsersByType("V"));
        return "volunteers";
    }
    
    @RequestMapping(value = "/volunteer/show", method = RequestMethod.GET)
    public String getVolunteer(@ModelAttribute(value="volunteer") Volunteer volunteer,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
                
        model.addAttribute("volunteer", database.getUserById(volunteer.getUserId()));
        return "volunteer";
    }
    
    @RequestMapping(value = "/volunteer/update/{id}", method = RequestMethod.POST)
    public String getUpdatedVolunteer(@ModelAttribute(value="volunteer") Volunteer volunteer,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        
        
        model.addAttribute("volunteer", database.updateVolunteer(volunteer));
        return "volunteer";
    }

	    
}
