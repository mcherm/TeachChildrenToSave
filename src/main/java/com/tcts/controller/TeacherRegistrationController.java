package com.tcts.controller;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import com.tcts.formdata.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.School;
import com.tcts.datamodel.Teacher;
import com.tcts.exception.EmailAlreadyInUseException;
import com.tcts.exception.NoSuchSchoolException;
import com.tcts.formdata.TeacherRegistrationFormData;
import com.tcts.util.SecurityUtil;

/**
 * A controller for the flow where new teachers sign up.
 */
@Controller
public class TeacherRegistrationController {

    @Autowired
    private DatabaseFacade database;
    

    @RequestMapping(value="/registerTeacher.htm", method=RequestMethod.GET)
    public String showRegisterTeacherPage(HttpSession session, Model model) throws SQLException {
        SessionData.ensureNoActiveSession(session);
        model.addAttribute("formData", new TeacherRegistrationFormData());
        return showFormWithErrors(model, null);
    }

    /**
     * A subroutine used to set up and then show the register teacher form. It
     * returns the string, so you can invoke it as "return showFormWithErrorMessage(...)".
     */
    private String showFormWithErrors(Model model, Errors errors) throws SQLException {
        List<School> schools = database.getAllSchools();
        model.addAttribute("schools", schools);
        model.addAttribute("errors", errors);
        return "registerTeacher";
    }

    @RequestMapping(value="/registerTeacher.htm", method=RequestMethod.POST)
    public String createNewTeacher(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") TeacherRegistrationFormData formData
            ) throws SQLException
    {
        SessionData.ensureNoActiveSession(session);

        // --- Validation rules ---
        Errors errors = formData.validate();
        if (errors.hasErrors()) {
            return showFormWithErrors(model, errors);
        }

        // --- Create object ---
        try {
            String salt = SecurityUtil.generateSalt();
            String hashedPassword = SecurityUtil.getHashedPassword(formData.getPassword().trim(), salt);
            Teacher teacher = database.insertNewTeacher(formData, hashedPassword, salt);
            SessionData sessionData = SessionData.beginNewSession(session);
            sessionData.setUser(teacher);
            sessionData.setAuthenticated(true);
            return "redirect:" + teacher.getUserType().getHomepage();
        } catch (NoSuchSchoolException e) {
            return showFormWithErrors(model,
                    new Errors("That is not a valid school."));
        } catch (EmailAlreadyInUseException e) {
            return showFormWithErrors(model,
                    new Errors("That email is already in use; please choose another."));
        } catch (NoSuchAlgorithmException e) {
        	return showFormWithErrors(model,
                    new Errors("There was an error registering your data.Please try after some time"));
		} catch (UnsupportedEncodingException e) {
			return showFormWithErrors(model,
                    new Errors("There was an error registering your data.Please try after some time"));
		}
    }
}
