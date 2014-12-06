package com.tcts.controller;

import com.tcts.dao2.DatabaseFacade;
import com.tcts.dao2.InconsistentDatabaseException;
import com.tcts.dao2.MySQLDatabase;
import com.tcts.database.ConnectionFactory;
import com.tcts.model.SessionData;
import com.tcts.model2.User;
import com.tcts.model2.UserType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.model.Login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class LoginController extends AuthenticationController{

    @RequestMapping(value = "/getLoginPage", method = RequestMethod.GET)
    public String newLoginPage(Model model) {
        model.addAttribute("login", new Login());
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String isUserAutherticated( @ModelAttribute("SpringWeb")Login login,
                                       ModelMap model) throws SQLException, InconsistentDatabaseException {
		   
		   
		   /*VolunteerManagerImpl volunteerManager = new VolunteerManagerImpl();
		   volunteerManager.addVolunteer(volunteer);
		   
	      
	      model.addAttribute("emailAddress", volunteer.getEmailAddress());
	      model.addAttribute("organization", volunteer.getOrganizatiom());*/

        //LoginManager loginManager = new LoginManagerImpl();
	      
	     /* if (loginManager.isUserAuthenticated(login.getUserID(),  login.getPassword())) {
	    	  return "index";
	      }
	      else {
	    	  return "login";
	      }*/


        // FIXME: This should be injected, not created.
        DatabaseFacade databaseFacade = new MySQLDatabase();
        User user = databaseFacade.getUserById(login.getUserID().toString());
        if (user == null) {
            // FIXME: Should show bad user/pwd message
            throw new RuntimeException("bad user or pwd");
        }
        if (!user.getPassword().equals(login.getPassword().toString())) {
            // FIXME: Should show bad user/pwd message
            throw new RuntimeException("bad user or pwd");
        }
        SessionData sessionData = new SessionData();
        sessionData.setUser(user);
        sessionData.setAuthenticated(true);

        return user.getUserType().getHomepage();
    }
}
