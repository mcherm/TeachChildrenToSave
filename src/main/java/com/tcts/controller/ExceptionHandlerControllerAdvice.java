package com.tcts.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {
	
	@ExceptionHandler(Exception.class)
	public ModelAndView exception(Exception e) {
		
		ModelAndView model = new ModelAndView("genericError");
		model.addObject("name", e.getClass().getSimpleName());
		model.addObject("message", " Unexpected error occurred.");
		return model;
	}

}
