package com.tcts.controller;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.User;
import com.tcts.exception.NotLoggedInException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** A controller for the admin screen where passwords and password resets are managed. */
@Controller
public class ManagePasswordsController {

    @Autowired
    private DatabaseFacade database;

    @RequestMapping(value = "/managePasswords.htm", method = RequestMethod.GET)
    public String emailAnnouncement(HttpSession session, Model model, HttpServletRequest request) {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        List<User> allUsers = database.getAllUsers();
        List<User> resetableUsers = new ArrayList<>();
        for (User user : allUsers) {
            if (!user.getResetPasswordToken().isEmpty()) {
                resetableUsers.add(user);
            }
        }
        model.addAttribute("users", resetableUsers);
        Map<String,String> passwordResetUrls = new HashMap<>();
        for (User user : resetableUsers) {
            final String passwordResetUrl = ResetPasswordController.passwordResetUrl(request, user.getResetPasswordToken());
            passwordResetUrls.put(user.getUserId(), passwordResetUrl);
        }
        model.addAttribute("passwordResetUrls", passwordResetUrls);

        return "managePasswords";
    }

}
