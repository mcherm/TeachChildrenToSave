package com.tcts.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amazonaws.services.s3.model.EmailAddressGrantee;
import com.tcts.common.SessionData;
import com.tcts.dao.DatabaseFacade;
import com.tcts.datamodel.User;
import com.tcts.exception.InconsistentDatabaseException;
import com.tcts.model.Login;
import com.tcts.util.EmailUtil;
import com.tcts.util.SecurityUtil;

/**
 * The controller that handles user login for all users.
 */
@Controller
public class LoginController extends AuthenticationController {

    @Autowired
    private DatabaseFacade database;
    
    

    @RequestMapping(value = "/getLoginPage", method = RequestMethod.GET)
    public String newLoginPage(Model model) {
        model.addAttribute("login", new Login());
        model.addAttribute("errorMessage", "");
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String isUserAuthenticated( @ModelAttribute("SpringWeb")Login login,
                                       ModelMap model,
                                       HttpSession session) throws SQLException, InconsistentDatabaseException {
        SessionData.ensureNoActiveSession(session);
        
        User user = database.getUserByLogin(login.getUserID().toString());
        
        byte[] bDigest = null;
        byte[] bSalt = null;
        byte[] proposedDigest = null;
        
        try {
			 bDigest = SecurityUtil.base64ToByte(user.getPassword());
			 bSalt = SecurityUtil.base64ToByte(user.getSalt());
			 proposedDigest = SecurityUtil.getHash(SecurityUtil.ITERATION_NUMBER, login.getPassword().toString(), bSalt);
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			model.addAttribute("login", new Login());
            model.addAttribute("errorMessage", "Invalid user id or password.");
            return "login";
		} catch(NoSuchAlgorithmException ex){
			ex.printStackTrace();
			model.addAttribute("login", new Login());
            model.addAttribute("errorMessage", "Invalid user id or password.");
            return "login";
		}
        
        boolean isValidUser = Arrays.equals(proposedDigest, bDigest) ;
         
        // Compute the new DIGEST
        
        if (login.getUserID() == null || login.getPassword() == null || user == null || !isValidUser) {
            // --- Failed login ---
            model.addAttribute("login", new Login());
            model.addAttribute("errorMessage", "Invalid user id or password.");
            return "login";
        } else {
            // --- Successful login ---
            SessionData sessionData = SessionData.beginNewSession(session);
            sessionData.setAuthenticated(true);
            sessionData.setUser(user);
            return "redirect:" + user.getUserType().getHomepage();
        }

    }
}
