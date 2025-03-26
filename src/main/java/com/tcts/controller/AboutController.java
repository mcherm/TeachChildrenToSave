package com.tcts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * This controller displays the "about" page. It also serves as a good
 * example of a completely minimal controller that does nothing but
 * display a screen.
 */
@Controller
public class AboutController {

    @RequestMapping(value = "/about.htm", method = RequestMethod.GET)
    public String aboutPage(Model model) {
        model.addAttribute("site", "DE"); // FIXME: Need to set this "correctly" (after deciding what that is)
        return "about"; // which .jsp to display
    }

}
