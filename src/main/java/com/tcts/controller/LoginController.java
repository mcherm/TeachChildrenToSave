package com.tcts.controller;

import com.tcts.dao2.DatabaseFacade;
import com.tcts.dao2.InconsistentDatabaseException;
import com.tcts.model.SessionData;
import com.tcts.model2.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpSession;

import com.tcts.model.Login;
import java.sql.SQLException;

/**
 * The controller that handles user login for all users.
 */
@Controller
public class LoginController extends AuthenticationController {

    @Autowired
    private DatabaseFacade database;

    @RequestMapping(value = "/getLoginPage", method = RequestMethod.GET)
    public String newLoginPage(Model model) {
        model.addAttribute("login", new Login());
        model.addAttribute("errorMessage", "");
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String isUserAutherticated( @ModelAttribute("SpringWeb")Login login,
                                       ModelMap model,
                                       HttpSession session) throws SQLException, InconsistentDatabaseException {
		   
        User user = database.getUserById(login.getUserID().toString());
        if (user == null || !user.getPassword().equals(login.getPassword().toString())) {
            model.addAttribute("login", new Login());
            model.addAttribute("errorMessage", "Invalid user id or password.");
            return "login";
        }
        SessionData sessionData = SessionData.beginNewSession(session);
        sessionData.setUser(user);
        sessionData.setAuthenticated(true);

        return "redirect:" + user.getUserType().getHomepage();
    }
}
