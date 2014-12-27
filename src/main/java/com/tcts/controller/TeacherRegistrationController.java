package com.tcts.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.tcts.exception.AppConfigurationException;
import com.tcts.exception.EmailAlreadyInUseException;
import com.tcts.util.SecurityUtil;
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
import com.tcts.exception.NoSuchSchoolException;
import com.tcts.formdata.TeacherRegistrationFormData;
import com.tcts.util.EmailUtil;

/**
 * A controller for the flow where new teachers sign up.
 */
@Controller
public class TeacherRegistrationController {

    @Autowired
    private DatabaseFacade database;
    
    @Autowired
    private EmailUtil emailUtil;

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
        if (formData.getSchoolId() == null || formData.getSchoolId().trim().length() == 0 || formData.getSchoolId().equals("0")) {
            return showFormWithErrorMessage(model, "You must select a school.");
        }
        // FIXME: probably need more validation rules
        // --- Create object ---
        try {
            String salt = SecurityUtil.generateSalt();
            String hashedPassword = SecurityUtil.getHashedPassword(formData.getPassword(), salt);
            Teacher teacher = database.insertNewTeacher(formData, hashedPassword, salt);
            SessionData sessionData = SessionData.beginNewSession(session);
            sessionData.setUser(teacher);
            sessionData.setAuthenticated(true);
            try {
                emailUtil.sendEmail(teacher.getEmail());
            } catch(AppConfigurationException err) {
                // FIXME: Need to log or report this someplace more reliable.
                System.err.println("Could not send email for new teacher '" + formData.getEmail() + "'.");
            } catch(IOException err) {
                // FIXME: Need to log or report this someplace more reliable.
                System.err.println("Could not send email for new teacher '" + formData.getEmail() + "'.");
            }
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
