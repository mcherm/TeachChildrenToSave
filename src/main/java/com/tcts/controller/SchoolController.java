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
import com.tcts.dao2.DatabaseFacade;
import com.tcts.model2.School;

/**
 * This is a controller for the "home page" for users. It renders substantially
 * different information depending on the type of user who is logged in.
 */
@Controller

public class SchoolController {

    @Autowired
    private DatabaseFacade database;
    
    /**
     * Render the bank edit page .
     */
    @RequestMapping(value = "/school/schools", method = RequestMethod.GET)
    public String showSchool(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        List<School> schools = database.getAllSchools();
        
        model.addAttribute("schools", schools);
        return "schools";
    }
    
    @RequestMapping(value = "/school/delete", method = RequestMethod.GET)
    public String deleteSchool(@ModelAttribute(value="school") School school,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        
        database.deleteSchool(school.getSchoolId());
        
        model.addAttribute("schools", database.getAllSchools());
        return "schools";
    }
    
    @RequestMapping(value = "/school/show", method = RequestMethod.GET)
    public String getSchool(@ModelAttribute(value="school") School school,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
               
        model.addAttribute("school", database.getSchoolById(school.getSchoolId()));
        return "school";
    }
    
    @RequestMapping(value = "/school/update/{id}", method = RequestMethod.GET)
    public String getUpdatedSchool(@ModelAttribute(value="school") School school,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
                
        model.addAttribute("school", database.updateSchool(school));
        return "school";
    }

   
}
