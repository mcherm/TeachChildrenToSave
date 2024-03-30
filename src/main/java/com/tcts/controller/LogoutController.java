package com.tcts.controller;

import com.tcts.common.SessionData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpSession;


/**
 * Very simple controller, just used to log out.
 */
@Controller
public class LogoutController {

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        SessionData.logout(session);
        return "redirect:/";
    }

}
