package com.tcts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.tcts.database.DatabaseFacade;

/**
 * This is a controller for the "home page" for users. It renders substantially
 * different information depending on the type of user who is logged in.
 */
@Controller
public class AdminController {

    @Autowired
    private DatabaseFacade database;
    
    /**
     * Render the bank edit page .
     */
    /*@RequestMapping(value = "/adminEditBanks", method = RequestMethod.GET)
    public String showBanks(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        Volunteer volunteer = sessionData.getVolunteer();
        if (volunteer == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        List<Bank> bankList = database.getBankList();
        
        model.addAttribute("banks", bankList);
        return "banks";
    }
    
    @RequestMapping(value = "bankDelete/{id}", method = RequestMethod.GET)
    public String deleteBank(@RequestParam("id") int id,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        Volunteer volunteer = sessionData.getVolunteer();
        if (volunteer == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        List<Bank> bankList = database.getBankList();
        
        model.addAttribute("banks", bankList);
        return "banks";
    }
    
    @RequestMapping(value = "bank/{id}", method = RequestMethod.GET)
    public String getBank(@RequestParam("id") int id,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        Volunteer volunteer = sessionData.getVolunteer();
        if (volunteer == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        List<Bank> bankList = database.getBankList();
        
        model.addAttribute("banks", bankList);
        return "banks";
    }
    
    @RequestMapping(value = "bankEdit/{id}", method = RequestMethod.GET)
    public String getUpdatedBank(@RequestParam("id") int id,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        Volunteer volunteer = sessionData.getVolunteer();
        if (volunteer == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        List<Bank> bankList = database.getBankList();
        
        model.addAttribute("banks", bankList);
        return "banks";
    }

    *//**
     * Render the list of users .
     *//*
    @RequestMapping(value = "adminListTeachers", method = RequestMethod.GET)
    public String showTeachersList(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        Volunteer volunteer = sessionData.getVolunteer();
        if (volunteer == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        List<? super User> teachers = database.getUsersByType("T");
        
        model.addAttribute("teachers", teachers);
        return "teachers";
    }
    
    *//**
     * Render the list of users .
     *//*
    @RequestMapping(value = "adminListVolunteers", method = RequestMethod.GET)
    public String showVolunteersList(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        Volunteer volunteer = sessionData.getVolunteer();
        if (volunteer == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        List<? super User> teachers = database.getUsersByType("V");
        
        model.addAttribute("teachers", teachers);
        return "volunteers";
    }
    
    //
    *//**
     * Render the list of users .
     *//*
    @RequestMapping(value = "adminListClasses", method = RequestMethod.GET)
    public String showClasses(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        Volunteer volunteer = sessionData.getVolunteer();
        if (volunteer == null) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        List<? super User> teachers = database.getUsersByType("V");
        
        model.addAttribute("teachers", teachers);
        return "classes";
    }*/

}
