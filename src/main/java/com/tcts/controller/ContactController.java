package com.tcts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * This controller displays the "contact" page. It also serves as a good
 * example of a completely minimal controller that does nothing but
 * display a screen.
 */
@Controller
public class ContactController {

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public String aboutPage() {
        return "contact"; // which .jsp to display
    }

}
