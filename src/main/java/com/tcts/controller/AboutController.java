package com.tcts.controller;

import com.tcts.common.SitesConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private SitesConfig sitesConfig;

    @RequestMapping(value = "/about.htm", method = RequestMethod.GET)
    public String aboutPage(Model model, HttpServletRequest httpServletRequest) {
        String serverName = httpServletRequest.getServerName();
        String site = sitesConfig.getProperty(serverName);
        model.addAttribute("site", site);
        return "about"; // which .jsp to display
    }

}
