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
public class OpeningsController {

    @RequestMapping(value = "/openings", method = RequestMethod.GET)
    public String aboutPage() {
        return "openings"; // which .jsp to display
    }

}
