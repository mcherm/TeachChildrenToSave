package com.tcts.controller;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.common.SessionData;
import com.tcts.dao.DatabaseFacade;
import com.tcts.datamodel.Bank;

/**
 * This is a controller for the "home page" for users. It renders substantially
 * different information depending on the type of user who is logged in.
 */
@Controller
public class BankController {

    @Autowired
    private DatabaseFacade database;
    
    /**
     * Render the bank edit page .
     */
    @RequestMapping(value = "/bank/banks", method = RequestMethod.GET)
    public String showBanks(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in.");
        }
        List<Bank> bankList = database.getBankList();
        
        model.addAttribute("banks", bankList);
        return "banks";
    }
    
    @RequestMapping(value = "/bank/delete", method = RequestMethod.POST)
    public String deleteBank(@ModelAttribute(value="bank") Bank bank,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in.");
        }
        database.deleteBank(bank.getBankId());
        List<Bank> bankList = database.getBankList();
        
        model.addAttribute("banks", bankList);
        return "banks";
    }
    
    @RequestMapping(value = "/bank/show", method = RequestMethod.GET)
    public String getBank(@ModelAttribute(value="bank") Bank bank,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in.");
        }
        bank = database.getBankById(bank.getBankId());
        
        model.addAttribute("bank", bank);
        return "bank";
    }
    
    @RequestMapping(value = "bank/update", method = RequestMethod.POST)
    public String getUpdatedBank(@ModelAttribute(value="bank") Bank bank,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in.");
        }
        
        
        model.addAttribute("bank", database.updateBank(bank));
        return "bank";
    }
    
    @RequestMapping(value = "bank/add", method = RequestMethod.POST)
    public String addBank(@ModelAttribute(value="bank") Bank bank,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in.");
        }
        
        database.insertBank(bank);
        model.addAttribute("banks", database.getBankList());
        return "banks";
    }

   
}
