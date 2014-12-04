package com.tcts.controller;

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
	      
	      return "index";
	   }
}
