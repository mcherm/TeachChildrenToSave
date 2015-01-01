package com.tcts.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.User;
import com.tcts.exception.AppConfigurationException;
import com.tcts.exception.InconsistentDatabaseException;
import com.tcts.formdata.LoginFormData;
import com.tcts.util.EmailUtil;
import com.tcts.util.SecurityUtil;
import com.tcts.util.TemplateUtil;

/**
 * The controller that handles user login for all users.
 */
@Controller
public class ResetPasswordController {

    @Autowired
    private DatabaseFacade database;
    
    @Autowired
    EmailUtil emailUtil;
    
    @Autowired
    TemplateUtil templateUtil;
    
    @RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
    public String forGotPasswordPage(Model model) {
    	model.addAttribute("formData", new LoginFormData());
        model.addAttribute("errorMessage", "");
        return "forgotPassword";
    }

       
    @RequestMapping(value = "/sendPasswordResetEmail", method = RequestMethod.GET)
    public String showResetPassword(
            @ModelAttribute("formData") LoginFormData formData,
            ModelMap model,
            HttpSession session,
            @RequestParam(value = "token") String token
        )
    {
        session.setAttribute("resetPasswordToken", token);
        model.addAttribute("formData", new LoginFormData());
        model.addAttribute("token", token);
        model.addAttribute("errorMessage", "");
        return "resetPassword";
    }
    
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public String doResetPassword(
            @ModelAttribute("formData") LoginFormData formData,
            ModelMap model,
            HttpSession session,
            HttpServletRequest request,
            @RequestParam("token") String token
        ) throws SQLException, InconsistentDatabaseException, UnsupportedEncodingException
    {
        SessionData.ensureNoActiveSession(session);
        
        User potentialUser = database.getUserByEmail(formData.getEmail());
        
        if (formData.getPassword() == null || formData.getConfirmPassword() == null || (formData.getPassword() != null && formData.getConfirmPassword() != null && !formData.getConfirmPassword().equalsIgnoreCase(formData.getPassword()))) {
        	model.addAttribute("formData", new LoginFormData());
	        model.addAttribute("errorMessage", "Password and confirm password do not match.");
	        return "resetPassword";
        }

        if (potentialUser != null) {
        	    String decodedToken = URLDecoder.decode(token, "UTF-8").replace(" ", "+"); //FIXME space is getting replaced by + in url encoder sometimes
                String sentResetPaswordToken = potentialUser.getResetPasswordToken();
                
                if (decodedToken != null && sentResetPaswordToken != null && decodedToken.equalsIgnoreCase(sentResetPaswordToken)) {
                		String salt = SecurityUtil.generateSalt();
                        String hashedPassword = SecurityUtil.getHashedPassword(formData.getPassword(), salt);
                        database.updateUserCredential(potentialUser.getUserId(), hashedPassword, salt);
                        SessionData sessionData = SessionData.beginNewSession(session);
                        sessionData.setAuthenticated(true);
                        sessionData.setUser(potentialUser);
                        // FIXME: There's a minor bug here: the user being stored in the session has the wrong password. Shouldn't harm anything
                        return "redirect:" + potentialUser.getUserType().getHomepage();
                }
                else {
                	model.addAttribute("formData", new LoginFormData());
        	        model.addAttribute("errorMessage", "Reset password link is not valid.");
        	        return "resetPassword";
                }
                
        } else{
			        // User does not exists
			        model.addAttribute("formData", new LoginFormData());
			        model.addAttribute("errorMessage", "Email Id does not exists.");
			        return "forgotPassword";
        }
    }
    
    
    @RequestMapping(value = "/sendPasswordResetEmail", method = RequestMethod.POST)
    public String doSendPasswordResetEmail(
            @ModelAttribute("formData") LoginFormData formData,
            ModelMap model,
            HttpSession session,
            HttpServletRequest request
        ) throws SQLException, InconsistentDatabaseException, UnsupportedEncodingException
    {
        SessionData.ensureNoActiveSession(session);
        
        User potentialUser = database.getUserByEmail(formData.getEmail());

        if (potentialUser != null) {
                String randomToken = SecurityUtil.getHashedPassword(potentialUser.getSalt(),  UUID.randomUUID().toString());
                database.updateResetPasswordToken(potentialUser.getUserId(), randomToken);
                String url =  request.getRequestURL() + "?token=" + URLEncoder.encode(randomToken, "UTF-8").replace("+", "%20");
                
                try {
            		Map<String,Object> emailModel = new <String, Object>HashMap();
                	
                	emailModel.put("to", potentialUser.getEmail());
                	emailModel.put("subject", "Password reset link for teach children to save program");
                	
                	emailModel.put("passwordResetLink", url);
                	String emailContent = templateUtil.generateTemplate("passwordReset", emailModel);
                    emailUtil.sendEmail(emailContent, emailModel);
                } catch(AppConfigurationException err) {
                    // FIXME: Need to log or report this someplace more reliable.
                    System.err.println("Could not send email for new volunteer '" + potentialUser.getEmail() + "'.");
                } catch(IOException err) {
                    // FIXME: Need to log or report this someplace more reliable.
                    System.err.println("Could not send email for new volunteer '" + potentialUser.getEmail() + "'.");
                }
                
        } else {
			        // --- User does not exists
			        model.addAttribute("formData", new LoginFormData());
			        model.addAttribute("errorMessage", "Email Id does not exists.");
			        return "forgotPassword";
        }
       return "forgotPasswordResetEmailConfirm";
    }

    
}
