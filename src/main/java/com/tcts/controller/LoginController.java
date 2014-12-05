package com.tcts.controller;

import com.tcts.model.UserType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.model.Login;

@Controller
public class LoginController extends AuthenticationController{

    @RequestMapping(value = "/getLoginPage", method = RequestMethod.GET)
    public String newLoginPage(Model model) {
        model.addAttribute("login", new Login());
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String isUserAutherticated( @ModelAttribute("SpringWeb")Login login,
                                       ModelMap model) {
		   
		   
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

        // FIXME: Here it should determine which type of user it is instead selecting one based on the user's first name
        String userId = login.getUserID().toString();
        UserType userType;
        if (userId == null || userId.length() == 0) {
            userType = UserType.VOLUNTEER;
        } else if (Character.toUpperCase(userId.charAt(0)) == 'T') {
            userType = UserType.TEACHER;
        } else if (Character.toUpperCase(userId.charAt(0)) == 'B') {
            userType = UserType.BANK_ADMIN;
        } else if (Character.toUpperCase(userId.charAt(0)) == 'S') {
            userType = UserType.SITE_ADMIN;
        } else {
            userType = UserType.VOLUNTEER;
        }
        return userType.getHomepage();
    }
}
