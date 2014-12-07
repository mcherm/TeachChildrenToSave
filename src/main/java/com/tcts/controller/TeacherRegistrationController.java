package com.tcts.controller;

import com.tcts.common.SessionData;
import com.tcts.dao2.DatabaseFacade;
import com.tcts.dao2.LoginAlreadyInUseException;
import com.tcts.dao2.NoSuchSchoolException;
import com.tcts.model.TeacherRegistrationFormData;
import com.tcts.model2.School;
import com.tcts.model2.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

/**
 * A controller for the flow where new teachers sign up.
 */
@Controller
public class TeacherRegistrationController {

    @Autowired
    private DatabaseFacade database;

    @RequestMapping(value="/registerTeacher", method=RequestMethod.GET)
    public String showRegisterTeacherPage(Model model) throws SQLException {
        model.addAttribute("teacherRegistrationFormData", new TeacherRegistrationFormData());
        return showFormWithErrorMessage(model, "");
    }

    /**
     * A subroutine used to set up and then show the register teacher form. It
     * returns the string, so you can invoke it as "return showFormWithErrorMessage(...)".
     */
    private String showFormWithErrorMessage(Model model, String errorMessage) throws SQLException {
        List<School> schools = database.getAllSchools(); // FIXME: It's expensive to retrieve it each time; should cache it for performance.
        model.addAttribute("schools", schools);
        model.addAttribute("errorMessage", errorMessage);
        return "registerTeacher";
    }

    @RequestMapping(value="/registerTeacher", method=RequestMethod.POST)
    public String createNewTeacher(
            HttpSession session,
            Model model,
            @ModelAttribute("teacherRegistrationFormData") TeacherRegistrationFormData formData
            ) throws SQLException
    {
        SessionData.ensureNoActiveSession(session);
        // --- Validation rules ---
        if (formData.getLogin() == null || formData.getLogin().trim().length()==0) {
            return showFormWithErrorMessage(model,
                    "You must select a valid login name. You may use your email address if you like.");
        }
        if (formData.getSchoolId() == null || formData.getSchoolId().trim().length() == 0 || formData.getSchoolId().equals("0")) {
            return showFormWithErrorMessage(model, "You must select a school.");
        }
        // FIXME: probably need more validation rules
        // --- Create object ---
        try {
            Teacher teacher = database.insertNewTeacher(formData);
            SessionData sessionData = SessionData.beginNewSession(session);
            sessionData.setUser(teacher);
            sessionData.setTeacher(teacher);
            sessionData.setAuthenticated(true);
            return "redirect:" + teacher.getUserType().getHomepage();
        } catch (NoSuchSchoolException e) {
            return showFormWithErrorMessage(model, "That is not a valid school.");
        } catch (LoginAlreadyInUseException e) {
            return showFormWithErrorMessage(model, "That login is already in use; please choose another.");
        }
    }
}
