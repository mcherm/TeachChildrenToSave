package com.tcts.controller;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.tcts.exception.EmailAlreadyInUseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.common.SessionData;
import com.tcts.dao.DatabaseFacade;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.User;
import com.tcts.datamodel.Volunteer;
import com.tcts.model.EditPersonalDataFormData;

/**
 * This is a controller for the "home page" for users. It renders substantially
 * different information depending on the type of user who is logged in.
 */
@Controller

public class TeacherController {

    @Autowired
    private DatabaseFacade database;
    
    /**
     * Render the list of users .
     */
    @RequestMapping(value = "/teacher/teachers", method = RequestMethod.GET)
    public String showTeachersList(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        List<? super User> teachers = database.getUsersByType("T");
        
        model.addAttribute("teachers", teachers);
        return "teachers";
    }
    
    @RequestMapping(value = "/teacher/delete", method = RequestMethod.GET)
    public String deleteTeacher(@ModelAttribute(value="teacher") Teacher teacher,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        database.deleteVolunteer(teacher.getUserId());
        
       model.addAttribute("teachers", database.getUsersByType("T"));
       return "teachers";
    }
    
    @RequestMapping(value = "/teacher/show", method = RequestMethod.GET)
    public String getTeacher(@ModelAttribute(value="teacher") Teacher teacher,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        //teacher = (Teacher) database.getUserById(id);
        
        model.addAttribute("teacher", database.getUserById(teacher.getUserId()));
        return "teacher";
    }
    
    @RequestMapping(value = "/teacher/update", method = RequestMethod.POST)
    public String getUpdatedTeacher(@ModelAttribute(value="teacher") Teacher teacher,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        Volunteer volunteer = sessionData.getVolunteer(); // FIXME: Won't ever work, since it's a teacher, not a volunteer
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }
        
        EditPersonalDataFormData formData = new EditPersonalDataFormData();
		formData.setEmail(volunteer.getEmail());
		formData.setFirstName(volunteer.getFirstName());
		formData.setLastName(volunteer.getLastName());

        User newUser;
        try {
            newUser = database.modifyUserPersonalFields(teacher.getUserId(), formData);
        } catch(EmailAlreadyInUseException err) {
            // FIXME: Need to handle this by reporting it to the user, NOT by just throwing an exception.
            // FIXME: ...see EditPersonalDataController for an example of how to do this.
            throw new RuntimeException(err);
        }
        model.addAttribute("teacher", newUser);
        return "teacher";
    }
    
   

   
}
