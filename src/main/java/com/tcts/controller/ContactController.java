package com.tcts.controller;

import com.tcts.common.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * This controller displays the "contact" page.
 */
@Controller
public class ContactController {

    @Autowired
    private Configuration configuration;

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public String aboutPage(Model model) {
        model.addAttribute("email", configuration.getProperty("email.from"));
        return "contact";
    }

}
