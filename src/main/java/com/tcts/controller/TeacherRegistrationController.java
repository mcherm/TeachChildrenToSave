package com.tcts.controller;

import com.tcts.common.SessionData;
import com.tcts.dao2.DatabaseFacade;
import com.tcts.dao2.LoginAlreadyInUseException;
import com.tcts.dao2.NoSuchSchoolException;
import com.tcts.model.TeacherRegistrationFormData;
import com.tcts.model2.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

/**
 * A controller for the flow where new teachers sign up.
 */

@Controller
public class TeacherRegistrationController {

    @Autowired
    private DatabaseFacade database;

    @RequestMapping(value="/registerTeacher", method=RequestMethod.GET)
    public String showRegisterTeacherPage(Model model) {
        model.addAttribute("teacherRegistrationFormData", new TeacherRegistrationFormData());
        model.addAttribute("errorMessage", "");
        return "registerTeacher";
    }

    @RequestMapping(value="/registerTeacher", method=RequestMethod.POST)
    public String createNewTeacher(
            HttpSession session,
            @ModelAttribute("teacherRegistrationFormData") TeacherRegistrationFormData formData
            ) throws SQLException
    {
        // FIXME: Make sure we are NOT already logged in?
        // --- Validation rules ---
        // FIXME: need some validation
        // --- Create object ---
        try {
            Teacher teacher = database.insertNewTeacher(formData);
            SessionData sessionData = SessionData.beginNewSession(session);
            sessionData.setUser(teacher);
            sessionData.setTeacher(teacher);
            sessionData.setAuthenticated(true);
            return "redirect:" + teacher.getUserType().getHomepage();
        } catch (NoSuchSchoolException e) {
            // FIXME: Need to handle
        } catch (LoginAlreadyInUseException e) {
            // FIXME: Need to handle
        }
        return null; // FIXME: Need actual destination
    }
}
