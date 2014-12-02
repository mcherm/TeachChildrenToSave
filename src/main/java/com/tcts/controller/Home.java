package com.tcts.controller;
import org.springframework.stereotype.Controller;  
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.servlet.ModelAndView;  
  
@Controller  
public class Home {
	 String message = "Welcome to Teach Children To Save Website !";  
	  
	    @RequestMapping("/hello")  
	    public ModelAndView showMessage() {  
	        System.out.println("from controller");  
	        return new ModelAndView("Teach Children To Save", "message", message);  
	    }  
}
