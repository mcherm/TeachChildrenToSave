package com.tcts.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {
	
	@ExceptionHandler(Throwable.class)
	public ModelAndView exception(Throwable err) {
        // --- First, make sure it goes into the logs ---
        err.printStackTrace();

        // --- Then, we want the fields available for the error screen ---
        String exceptionType = err.getClass().getSimpleName();
        String exceptionMessage = err.getMessage();
        if (exceptionMessage == null) {
            exceptionMessage = "";
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        err.printStackTrace(printWriter);
        printWriter.flush();
        String stackTrace = stringWriter.toString();

        // --- Finally, display it ---
		ModelAndView modelAndView = new ModelAndView("genericError");
        modelAndView.addObject("exceptionType", exceptionType);
        modelAndView.addObject("exceptionMessage", exceptionMessage);
        modelAndView.addObject("stackTrace", stackTrace);
		return modelAndView;
	}

}
