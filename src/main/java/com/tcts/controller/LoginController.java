package com.tcts.controller;

import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.User;
import com.tcts.exception.InconsistentDatabaseException;
import com.tcts.formdata.LoginFormData;
import com.tcts.util.SecurityUtil;

/**
 * The controller that handles user login for all users.
 */
@Controller
public class LoginController {

    @Autowired
    private DatabaseFacade database;
    
    

    @RequestMapping(value = "/getLoginPage", method = RequestMethod.GET)
    public String newLoginPage(Model model) {
    	model.addAttribute("formData", new LoginFormData());
        model.addAttribute("errorMessage", "");
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String isUserAuthenticated(
            @ModelAttribute("formData") LoginFormData formData,
            ModelMap model,
            HttpSession session
        ) throws SQLException, InconsistentDatabaseException
    {
        SessionData.ensureNoActiveSession(session);
        
        User potentialUser = database.getUserByEmail(formData.getEmail());

        if (potentialUser != null) {
            // verify the password
            if (potentialUser.getSalt() != null && potentialUser.getHashedPassword() != null) {
                String hashedPassword = SecurityUtil.getHashedPassword(formData.getPassword(), potentialUser.getSalt());
                if (hashedPassword.equals(potentialUser.getHashedPassword())) {
                    // --- Successful login ---
                    SessionData sessionData = SessionData.beginNewSession(session);
                    sessionData.setAuthenticated(true);
                    sessionData.setUser(potentialUser);
                    return "redirect:" + potentialUser.getUserType().getHomepage();
                }
            }
        }

        // --- Failed login ---
        model.addAttribute("formData", new LoginFormData());
        model.addAttribute("errorMessage", "Invalid user id or password.");
        return "login";
    }
}
