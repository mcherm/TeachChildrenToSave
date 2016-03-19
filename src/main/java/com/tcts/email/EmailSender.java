package com.tcts.email;

import com.tcts.common.PrettyPrintingDate;
import com.tcts.datamodel.Teacher;
import com.tcts.datamodel.User;
import com.tcts.exception.AppConfigurationException;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.tcts.datamodel.Event;
import com.tcts.datamodel.Volunteer;
import com.tcts.util.TemplateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private TemplateUtil templateUtil;

    public void sendVolunteerWithdrawEmailToTeacher(
            Event event,
            HttpServletRequest request,
            String withdrawNotes,
            String teacherEmail,
            User teacher,
            User volunteer
    ) throws IOException {
        Map<String,Object> emailModel = new HashMap<String, Object>();

        String logoImage =  request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/img/logo-tcts.png";

        emailModel.put("logoImage", logoImage);
        emailModel.put("to", teacherEmail);
        emailModel.put("subject", "Your volunteer for " + new PrettyPrintingDate(new java.sql.Date(new Date().getTime()))  +" cancelled");
        String htmlTableDataHeader = "<table><tr> " +
                "<td style=\"background-color:#66CCFF\">Class Date</td>" +
                "<td style=\"background-color:#66CCFF\">Class Time</td>" +
                "<td style=\"background-color:#66CCFF\">Teacher</td>" +
                "<td style=\"background-color:#66CCFF\">Volunteer</td>" +
                "<td style=\"background-color:#66CCFF\">Grade</td>" +
                "<td style=\"background-color:#66CCFF\">Number of student</td>" +
                "<td style=\"background-color:#66CCFF\">Class Notes<td/></tr><tr>";
        String htmlTableDataValue =
                "<td>" + new PrettyPrintingDate(event.getEventDate())  + "</td>" +
                "<td>" + event.getEventTime()  + "</td>" +
                "<td>" + teacher.getFirstName() + " " + teacher.getLastName()  + "</td>" +
                "<td>" + volunteer.getFirstName() + " " + volunteer.getLastName()  + "</td>" +
                "<td>" + event.getGrade()  + "</td>" +
                "<td>" + event.getNumberStudents()  + "</td>" +
                "<td>" + event.getNotes()  + "</td></tr>";
        emailModel.put("class", htmlTableDataHeader + htmlTableDataValue);
        emailModel.put("withdrawNotes", withdrawNotes);
        String emailContent = templateUtil.generateTemplate("volunteerUnregisterEventToTeacher", emailModel);
        emailUtil.sendEmail(emailContent, emailModel);
    }


    public void sendCancelEventEmailToVolunteer(Volunteer volunteer, Event event, Teacher teacher, HttpServletRequest request) {
        try {

            Map<String,Object> emailModel = new HashMap<String, Object>();
            String logoImage =  request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tcts/img/logo-tcts.png";;

            emailModel.put("logoImage", logoImage);
            emailModel.put("to", volunteer.getEmail());
            emailModel.put("subject", "Your volunteer event has been canceled.");
            String htmlTableDataHeader = "<table><tr> " +
                    "<td style=\"background-color:#66CCFF\">Class Date</td>" +
                    "<td style=\"background-color:#66CCFF\">Class Time</td>" +
                    "<td style=\"background-color:#66CCFF\">Teacher</td>" +
                    "<td style=\"background-color:#66CCFF\">Volunteer</td>" +
                    "<td style=\"background-color:#66CCFF\">Grade</td>" +
                    "<td style=\"background-color:#66CCFF\">Number of student</td>" +
                    "<td style=\"background-color:#66CCFF\">Class Notes<td/></tr><tr>";
            String htmlTableDataValue =
                    "<td>" + new PrettyPrintingDate(event.getEventDate())  + "</td>" +
                    "<td>" + event.getEventTime()  + "</td>" +
                    "<td>" + teacher.getFirstName() + " " + teacher.getLastName()  + "</td>" +
                    "<td>" + volunteer.getFirstName() + " " + volunteer.getLastName()  + "</td>" +
                    "<td>" + event.getGrade()  + "</td>" +
                    "<td>" + event.getNumberStudents()  + "</td>" +
                    "<td>" + event.getNotes()  + "</td></tr>";

            emailModel.put("class", htmlTableDataHeader + htmlTableDataValue);
            String singupUrl =  request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/register.htm";
            emailModel.put("signupLink", singupUrl);

            String htmlTableDataHeaderForTeacher = "<br/><table><tr> " +
                    "<td style=\"background-color:#66CCFF\">Teacher Name</td>" +
                    "<td style=\"background-color:#66CCFF\">Teacher Email Id</td>" +
                    "<td style=\"background-color:#66CCFF\">Teacher Phone Number</td></tr>";

            String htmlTableDataValueForTeacher =
                    "<td>" + teacher.getFirstName() + " " + teacher.getLastName()  + "</td>" +
                    "<td>" + teacher.getEmail()  + "</td>" +
                    "<td>" + teacher.getPhoneNumber() +"</td></tr>";

            emailModel.put("teacher", htmlTableDataHeaderForTeacher + htmlTableDataValueForTeacher);
            String emailContent = templateUtil.generateTemplate("teacherCancelEventToVolunteer", emailModel);
            emailUtil.sendEmail(emailContent, emailModel);
        } catch(AppConfigurationException err) {
            // FIXME: Need to log or report this someplace more reliable.
            System.err.println("Could not send email for new volunteer '" + volunteer.getEmail() + "'.");
        } catch(IOException err) {
            // FIXME: Need to log or report this someplace more reliable.
            System.err.println("Could not send email for new volunteer '" + volunteer.getEmail() + "'.");
        }

    }

}
