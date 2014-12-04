package com.tcts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.manager.EventManager;
import com.tcts.manager.impl.EventManagerImpl;
import com.tcts.model.Event;

@Controller
public class EventRegistrationController extends AuthenticationController {
	
	@RequestMapping(value = "/event", method = RequestMethod.GET)
    public String newForm(Model model) {
        model.addAttribute("event", new Event());

        return "event";
    }
	
	   @RequestMapping(value = "/addEvent", method = RequestMethod.POST)
	   public String addEvent(@ModelAttribute("SpringWeb")Event event, 
	   ModelMap model) {
		   
		   EventManager eventManager = new EventManagerImpl();
		   eventManager.addEvent(event);
		   
	      model.addAttribute("eventID", event.getEventID());
	      model.addAttribute("schooldID", event.getSchoolID());
	      model.addAttribute("teacherUserID", event.getTeacherUserID());
	      model.addAttribute("volunteerUserID", event.getVolunteerUserID());
	      model.addAttribute("grade", event.getGrade());
	      model.addAttribute("subject", event.getSubject());
	      model.addAttribute("numStudents", event.getNumStudents());
	      model.addAttribute("eventDate", event.getEventDate());
	      model.addAttribute("eventTime", event.getEventTime());
	      model.addAttribute("eventNotes", event.getEventNotes());
	      model.addAttribute("volunteerAssigned", event.isVolunteerAssigned());
	      
	      return "event_confirm";
	   }
}
