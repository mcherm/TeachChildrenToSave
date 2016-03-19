package com.tcts.controller;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.tcts.datamodel.Event;
import com.tcts.datamodel.UserType;
import com.tcts.exception.InconsistentDatabaseException;
import com.tcts.exception.NoSuchEventException;
import com.tcts.exception.NotOwnedByYouException;
import com.tcts.exception.TeacherHasEventsException;
import com.tcts.formdata.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.User;
import com.tcts.exception.EmailAlreadyInUseException;
import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NoSuchUserException;
import com.tcts.exception.NotLoggedInException;
import com.tcts.formdata.EditPersonalDataFormData;
import com.tcts.controller.CancelWithdrawController;

/**
 * This controller gives the Site Admin ability to manage the teachers, showing the teachers list and giving them the ability to modify or delete them.
 */
@Controller

public class TeacherController {

    @Autowired
    private DatabaseFacade database;

    @Autowired
    private CancelWithdrawController cancelWithdrawController;
    
    /**
     * Render the list of users .
     */

    @RequestMapping(value = "/teachers", method = RequestMethod.GET)
    public String showTeachersList(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        List<Teacher> teachers = database.getTeacherWithSchoolData();
        
        model.addAttribute("teachers", teachers);
        return "teachers";
    }

    @RequestMapping(value = "teacherDelete", method = RequestMethod.POST)
    public String deleteTeacher(
            @RequestParam String teacherId,
            HttpSession session,
            Model model,
            HttpServletRequest request
            ) throws SQLException
    {
        SessionData sessionData = SessionData.fromSession(session);
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in volunteer.");
        }

        deleteTeacherAndCancelEvents(teacherId,request);

        model.addAttribute("teachers", database.getTeacherWithSchoolData());
       return "teachers";
    }

 /** This method properly deletes a Teacher from the Database and Cancels Any Events the Teacher has created.  Cancelling an
    event involves sending a cancel email to the volunteer if one is signed up*/

    public void deleteTeacherAndCancelEvents(String teacherId, HttpServletRequest request) throws SQLException {

        Teacher teacher = (Teacher) database.getUserById(teacherId);
        // --- First, delete any events ---
        List<Event> events = database.getEventsByTeacher(teacher.getUserId());
        for (Event event : events) {
            try {
                cancelWithdrawController.cancelEvent(request, teacher,event,"");
            } catch(NoSuchEventException err) {
                throw new RuntimeException("Event returned but cannot be deleted.");
            }
        }

        // --- Now delete the teacher ---
        try {
			database.deleteTeacher(teacher.getUserId());
		} catch (NoSuchUserException e) {
			throw new InvalidParameterFromGUIException();
        } catch (TeacherHasEventsException e) {
            throw new InconsistentDatabaseException("Deleted events for a teacher but some are still there.");
		}
    }

    @RequestMapping(value = "editTeacherData", method = RequestMethod.GET)
    public String enterDataToEditTeacher(
            HttpSession session,
            Model model,
            @RequestParam String userId
    ) throws SQLException {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Load existing data ---
        User user = database.getUserById(userId);
        
        // --- Show the edit page ---
        return showEditUserWithErrors(model, transformUserData(user), null);
    }

    /**
     * Actually change the values that were entered.
     */
    @RequestMapping(value="/editTeacherData", method=RequestMethod.POST)
    public String editTeacherData(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") EditPersonalDataFormData formData
        ) throws SQLException
    {
        SessionData sessionData = SessionData.fromSession(session);
        User user = sessionData.getUser();
        if (user == null) {
            throw new NotLoggedInException();
        }
        if (user.getUserType() != UserType.SITE_ADMIN) {
            throw new NotOwnedByYouException();
        }

        // --- Validation Rules ---
        Errors errors = formData.validate();
        if (errors.hasErrors()) {
            return showEditUserWithErrors(model, formData, errors);
        }

        try {
           database.modifyUserPersonalFields(formData);
        } catch(EmailAlreadyInUseException err) {
            return showEditUserWithErrors(model, formData, new Errors("That email is already in use by another user."));
        }
        
        List<Teacher> teachers = database.getTeacherWithSchoolData();
        
        model.addAttribute("teachers", teachers);
        return "teachers";
    }
    
    /**
     * A subroutine used to set up and then show the add user form. It
     * returns the string, so you can invoke it as "return showEditSchoolWithErrors(...)".
     */
    public String showEditUserWithErrors(Model model, EditPersonalDataFormData formData, Errors errors)
            throws SQLException
    {
        model.addAttribute("formData", formData);
        model.addAttribute("errors", errors);
        return "editTeacher";
    }
    
    /**
     * Transform user modal data
     */
    
    private EditPersonalDataFormData transformUserData(User user) {
    	EditPersonalDataFormData formData = new EditPersonalDataFormData();
        formData.setEmail(user.getEmail());
        formData.setFirstName(user.getFirstName());
        formData.setLastName(user.getLastName());
        formData.setPhoneNumber(user.getPhoneNumber());
        formData.setUserId(user.getUserId());
        return formData;
    }
    
}
