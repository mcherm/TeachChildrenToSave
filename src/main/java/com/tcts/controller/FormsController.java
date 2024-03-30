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
public class FormsController {

    @RequestMapping(value = "/forms.htm", method = RequestMethod.GET)
    public String aboutPage() {
        return "forms"; // which .jsp to display
    }

}
