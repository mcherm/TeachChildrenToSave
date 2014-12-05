package com.tcts.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.manager.impl.VolunteerManagerImpl;
import com.tcts.model.Volunteer;

@Controller
public class VolunteerRegistrationController extends AuthenticationController{

	/*@RequestMapping(value = "/volunteer", method = RequestMethod.GET)
	   public ModelAndView volunteer() {
		  model.addAttribute("user", new User());
	      return new ModelAndView("volunteer", "volunteer", new Volunteer());
	   }*/
	   
	
	
	@RequestMapping(value = "/volunteer", method = RequestMethod.GET)
    public String newForm(Model model) {
        model.addAttribute("volunteer", new Volunteer());

        return "volunteer";
    }
	
	   @RequestMapping(value = "/addVolunteer", method = RequestMethod.POST)
	   public String addStudent(@ModelAttribute("SpringWeb")Volunteer volunteer, 
	   ModelMap model) {
		   
		   VolunteerManagerImpl volunteerManager = new VolunteerManagerImpl();
		   volunteerManager.addVolunteer(volunteer);
		   
		   
	      model.addAttribute("firstName", volunteer.getFirstName());
	      model.addAttribute("lastName", volunteer.getLastName());
	      model.addAttribute("workPhoneNumber", volunteer.getWorkPhoneNumber());
	      model.addAttribute("mobileNumber", volunteer.getMobilePhoneNumber());
	      model.addAttribute("addressLine1", volunteer.getAddressLine1());
	      model.addAttribute("addressLine2", volunteer.getAddressLine2());
	      model.addAttribute("state", volunteer.getState());
	      model.addAttribute("city", volunteer.getCity());
	      model.addAttribute("zip", volunteer.getZipcode());
	      model.addAttribute("accessType", volunteer.getAccessType());
	      model.addAttribute("emailAddress", volunteer.getEmailAddress());
	      model.addAttribute("organization", volunteer.getOrganizatiom());
	      
	      return "volunteer_confirm";
	   }

	    
}
