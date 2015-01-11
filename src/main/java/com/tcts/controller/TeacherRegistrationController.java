package com.tcts.controller;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
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
    

    @RequestMapping(value="/registerTeacher", method=RequestMethod.GET)
    public String showRegisterTeacherPage(HttpSession session, Model model) throws SQLException {
        SessionData.ensureNoActiveSession(session);
        model.addAttribute("formData", new TeacherRegistrationFormData());
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
            @ModelAttribute("formData") TeacherRegistrationFormData formData
            ) throws SQLException
    {
        SessionData.ensureNoActiveSession(session);
        // --- Validation rules ---
        if (formData.getEmail() == null || formData.getEmail().trim().length()==0) {
            return showFormWithErrorMessage(model,
                    "You must provide a valid email.");
        }
        if (formData.getFirstName() == null || formData.getFirstName().trim().length()==0) {
            return showFormWithErrorMessage(model,
                    "You must provide a first name.");
        }
        if (formData.getLastName() == null || formData.getLastName().trim().length()==0) {
            return showFormWithErrorMessage(model,
                    "You must provide a last name.");
        }
        if (formData.getPassword() == null || formData.getPassword().trim().length()==0) {
            return showFormWithErrorMessage(model,
                    "You must select a password.");
        }
        if (formData.getSchoolId() == null || formData.getSchoolId().trim().length() == 0 || formData.getSchoolId().equals("0")) {
            return showFormWithErrorMessage(model, "You must select a school.");
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
            return showFormWithErrorMessage(model, "That is not a valid school.");
        } catch (EmailAlreadyInUseException e) {
            return showFormWithErrorMessage(model, "That email is already in use; please choose another.");
        } catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
        	e.printStackTrace();
        	return showFormWithErrorMessage(model, "There was an error registering your data.Please try after some time");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return showFormWithErrorMessage(model, "There was an error registering your data.Please try after some time");
		}
    }
}
