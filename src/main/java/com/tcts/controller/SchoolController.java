package com.tcts.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.tcts.datamodel.Event;
import com.tcts.datamodel.Teacher;
import com.tcts.exception.NoSuchUserException;
import com.tcts.formdata.Errors;
import com.tcts.formdata.DeleteSchoolFormData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.School;
import com.tcts.exception.InconsistentDatabaseException;
import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NoSuchSchoolException;
import com.tcts.exception.NotLoggedInException;
import com.tcts.formdata.CreateSchoolFormData;
import com.tcts.formdata.EditSchoolFormData;


/**
 * This is a controller for the "home page" for users. It renders substantially
 * different information depending on the type of user who is logged in.
 */
@Controller
public class SchoolController {

    @Autowired
    private DatabaseFacade database;

    @Autowired CancelWithdrawController cancelWithdrawController;

    @Autowired TeacherController teacherController;
    
    /**
     * Render the bank edit page .
     */
    @RequestMapping(value = "schools", method = RequestMethod.GET)
    public String viewSchools(
            HttpSession session,
            Model model
        ) throws SQLException
    {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        return showListSchools(model);
    }

        
    /**
     * Show the page for creating a new school.
     */
    @RequestMapping(value = "addSchool", method = RequestMethod.GET)
    public String viewAddSchool(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") CreateSchoolFormData formData
    ) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Successful; show the master list again ---
        return showAddSchoolWithErrors(model, null);
    }


    @RequestMapping(value = "editSchool", method = RequestMethod.GET)
    public String viewEditSchool(
            HttpSession session,
            Model model,
            @RequestParam String schoolId
    ) throws SQLException {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Load existing data ---
        School school = database.getSchoolById(schoolId);
        
        // --- Show the edit page ---
        return showEditSchoolWithErrors(model, transformSchoolData(school), null);
    }


    @RequestMapping(value = "addSchool", method = RequestMethod.POST)
    public String doAddSchool(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") CreateSchoolFormData formData
    ) throws SQLException
    {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Validation Rules ---
        Errors errors = formData.validate();
        if (errors.hasErrors()) {
            return showAddSchoolWithErrors(model, errors);
        }

        // --- Do It ---
        try {
            database.insertNewSchool(formData);
        } catch(InconsistentDatabaseException err) {
            return showAddSchoolWithErrors(model,
                    new Errors("That school is already in use."));
        }

        // --- Successful; show the master school edit again ---
        return showListSchools(model);
    }


    @RequestMapping(value = "editSchool", method = RequestMethod.POST)
    public String doEditSchool(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") EditSchoolFormData formData
        ) throws SQLException
    {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Validation Rules ---
        Errors errors = formData.validate();
        if (errors.hasErrors()) {
            return showEditSchoolWithErrors(model, formData, errors);
        }

        try {
            database.modifySchool(formData);
        } catch(NoSuchSchoolException err) {
            throw new InvalidParameterFromGUIException();
        } 

        // --- Successful; show the master bank edit again ---
        return showListSchools(model);
    }


    @RequestMapping(value = "deleteSchool", method = RequestMethod.GET)
    public String deleteSchoolConfirm(
            @RequestParam String schoolId,
            HttpSession session,
            Model model
    ) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        Map<String,String> modifiedTeachers = new HashMap<String,String>();

        return showDeleteSchoolWithErrors(model,schoolId,modifiedTeachers, null);
    }


    /**
     * Populate the model and return the string to show the page for deleting a school.
     *
     * @param model the Model to populate
     * @param schoolId the ID of the school to be deleted
     * @param modifiedTeachers a map where the keys are teacherIds and the values are
     *                         either a schoolId or the special string "deleteme". The
     *                         value is used to pre-select an entry in the
     *                         dropdown list in the teacher table of the page which is used to determine
     *                         whether a teacher belonging to the school to be deleted is to be deleted or reassigned to a new school.
     *                         Any teacher not present in the map will have deleteme pre-selected.
     * @param errors the errors to display, or null
     * @return the string for the template to render
     * @throws SQLException
     */
    private String showDeleteSchoolWithErrors (Model model,
                                               String schoolId,
                                               Map<String,String> modifiedTeachers, // key  is teacherid, value is schoolid to move it to or deleteme as was passed in from the form
                                               Errors errors)
            throws SQLException
    {
        model.addAttribute ("school", database.getSchoolById(schoolId));

        List<School> schools = database.getAllSchools();
        model.addAttribute("schools", schools);

        List<Teacher> teachers = database.getTeachersBySchool(schoolId);
        // Change the initial school option in the select box on the DeleteSchool page to be the default value ("delete this teacher")
        // rather than the current school of the teacher
        for (Teacher teacher:teachers){
            teacher.setSchoolId(modifiedTeachers.get(teacher.getUserId()));
        }

        model.addAttribute("deleteSchoolFormData", new DeleteSchoolFormData(schoolId,teachers));
        model.addAttribute("teachers", teachers);
        model.addAttribute("errors",errors);
        return "deleteSchool";
    }


    /**
     * Deletes a bank and all users associated with it.
     */
    @RequestMapping(value = "deleteSchool", method = RequestMethod.POST)
    public String deleteSchool(
            @ModelAttribute("deleteSchoolFormData") DeleteSchoolFormData deleteSchoolFormData,
            HttpSession session,
            Model model,
            HttpServletRequest request
    ) throws SQLException, InvalidParameterFromGUIException
    {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        Errors errors = new Errors();

        if (deleteSchoolFormData.getTeachers() != null) {

            //VALIDATE FORM DATA
            //first check to see if any of the techers who have had their school modified have signed up events
            //You may not modify the school of a teacher with signed up events.
            // NOTE:  You CAN delete  a teacher with a signed up event.   This will cancel all classes and send notification
            //        emails to any volunteers signed up for the classes.
            for (Teacher teacher : deleteSchoolFormData.getTeachers()) {
                if (teacher.getSchoolId() == null || teacher.getSchoolId().trim().length() == 0 || teacher.getSchoolId().equals("0")) {
                    errors.addError("Invalid school data.");
                }
                if (teacher.getSchoolId().equals(deleteSchoolFormData.getSchoolIdToBeDeleted())) {
                    errors.addError("You can not change a teacher's new school to the school to be deleted.");
                } else if (!(teacher.getSchoolId().equals("deleteme"))) {
                    //check to see if any teachers marked to change school have signed up for events.. if so pass an error
                    List<Event> events = database.getEventsByTeacher(teacher.getUserId());
                    for (Event event : events) {
                        if (event.getVolunteerId() != null) {
                            Teacher fullTeacher = (Teacher) database.getUserById(teacher.getUserId());
                            errors.addError("You can not modify the school of teacher " + fullTeacher.getEmail() + " because they have a class with a volunteer. You must first delete the class which will send a cancellation email to the volunteer.");
                        }
                    }
                }
            }
            if (errors.hasErrors()) {
                Map<String, String> modifiedTeachers = new HashMap<String, String>();
                for (Teacher teacher : deleteSchoolFormData.getTeachers()) {
                    modifiedTeachers.put(teacher.getUserId(), teacher.getSchoolId());
                }
                return showDeleteSchoolWithErrors(model, deleteSchoolFormData.getSchoolIdToBeDeleted(), modifiedTeachers, errors);
            }

            //DELETE TEACHERS OR REASSIGN TO NEW SCHOOL
            for (Teacher teacher : deleteSchoolFormData.getTeachers()) {

                if (teacher.getSchoolId().equals("deleteme")) {
                    teacherController.deleteTeacherAndCancelEvents(teacher.getUserId(), request);
                } else {
                    try {
                        database.modifyTeacherSchool(teacher.getUserId(), teacher.getSchoolId());
                    } catch (NoSuchUserException ex) {
                        errors.addError("Invalid teacher.  Database may have been modified.  Retry request.");
                    } catch (NoSuchSchoolException ex) {
                        errors.addError("Invalid school.  Database may have been modified.  Retry request.");
                    }
                    if (errors.hasErrors()) {
                        Map<String, String> modifiedTeachers = new HashMap<String, String>();
                        for (Teacher teacher1 : deleteSchoolFormData.getTeachers()) {
                            modifiedTeachers.put(teacher1.getUserId(), teacher.getSchoolId());
                        }
                        return showDeleteSchoolWithErrors(model, deleteSchoolFormData.getSchoolIdToBeDeleted(), modifiedTeachers, errors);
                    }

                }
            }
        }
        // DELETE SCHOOL
       try {
           database.deleteSchool(deleteSchoolFormData.getSchoolIdToBeDeleted());
        } catch(NoSuchSchoolException err) {
           throw new InvalidParameterFromGUIException();
       }
        return showListSchools(model);
    }


    /**
     * A subroutine to set up for displaying the list of schools. It returns the
     * string for the next page so you can invoke it as "return showListOfSchools(...)".
     */
    private String showListSchools(Model model) throws SQLException {
        model.addAttribute("schools", database.getAllSchools());
        return "schools";
    }

    /**
     * A subroutine used to set up and then show the add bank form. It
     * returns the string, so you can invoke it as "return showAddBankWithErrorMessage(...)".
     */
    private String showEditSchoolWithErrors(Model model, EditSchoolFormData formData, Errors errors) {
        model.addAttribute("formData", formData);
        model.addAttribute("errors", errors);
        return "editSchool";
    }

    /**
     * A subroutine used to set up and then show the add school form. It
     * returns the string, so you can invoke it as "return showAddSchoolWithErrorMessage(...)".
     */
    private String showAddSchoolWithErrors(Model model, Errors errors) throws SQLException {
        model.addAttribute("errors", errors);
        return "addSchool";
    }

    /**
     * Transform school modal data
     */
    private EditSchoolFormData transformSchoolData(School school) {
    	EditSchoolFormData formData = new EditSchoolFormData();
        formData.setSchoolId(school.getSchoolId());
        formData.setCity(school.getCity());
        formData.setCounty(school.getCounty());
        formData.setDistrict(school.getSchoolDistrict());
        formData.setPhone(school.getPhone());
        formData.setSchoolAddress1(school.getAddressLine1());
        formData.setSchoolName(school.getName());
        formData.setState(school.getState());
        formData.setZip(school.getZip());
        formData.setLmiEligible(school.getLmiEligible() == null ? "" : school.getLmiEligible().toString());
        formData.setSLC(school.getSLC());
        
        return formData;
    }
       
}
