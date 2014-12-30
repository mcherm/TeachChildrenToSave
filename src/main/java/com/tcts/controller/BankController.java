package com.tcts.controller;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.tcts.datamodel.BankAdmin;
import com.tcts.exception.EmailAlreadyInUseException;
import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NoSuchBankException;
import com.tcts.exception.NotLoggedInException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Bank;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This is a controller for the pages that are used by the site admin to edit the
 * list of banks (and bank admin users).
 */
@Controller
public class BankController {

    @Autowired
    private DatabaseFacade database;
    
    /**
     * Render the bank edit page.
     */
    @RequestMapping(value = "viewEditBanks", method = RequestMethod.GET)
    public String showBanks(HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        return showForm(model);
    }


    /**
     * A subroutine used to set up and then show the edit banks form. It
     * returns the string, so you can invoke it as "return showForm(...)".
     */
    public String showForm(Model model) throws SQLException {
        List<Bank> banks = database.getAllBanks();
        for (Bank bank : banks) {
            BankAdmin bankAdmin = (BankAdmin) database.getUserById(bank.getBankAdminId());
            bank.setLinkedBankAdmin(bankAdmin);
        }

        model.addAttribute("banks", banks);
        return "banks";
    }


    /**
     * Deletes a bank and all users associated with it.
     */
    @RequestMapping(value = "deleteBank", method = RequestMethod.POST)
    public String deleteBank(
            @RequestParam String bankId,
            HttpSession session,
            Model model
        ) throws SQLException
    {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        try {
            database.deleteBank(bankId);
        } catch(NoSuchBankException err) {
            throw new InvalidParameterFromGUIException();
        }

        return showForm(model);
    }


    // FIXME: This needs rewriting
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

    // FIXME: This needs rewriting
    @RequestMapping(value = "bank/update", method = RequestMethod.POST)
    public String getUpdatedBank(@ModelAttribute(value="bank") Bank bank,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in.");
        }

        Bank newBank;
        try {
            newBank = database.updateBank(bank);
        } catch(EmailAlreadyInUseException err) {
            // FIXME: Need to handle this by reporting it to the user, NOT by just throwing an exception.
            // FIXME: ...see EditPersonalDataController for an example of how to do this.
            throw new RuntimeException(err);
        }
        model.addAttribute("bank", newBank);
        return "bank";
    }

    // FIXME: This needs rewriting
    @RequestMapping(value = "bank/add", method = RequestMethod.POST)
    public String addBank(@ModelAttribute(value="bank") Bank bank,HttpSession session, Model model) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (!sessionData.isAuthenticated()) {
            throw new RuntimeException("Cannot navigate to this page unless you are a logged-in.");
        }

        try {
            database.insertBank(bank);
        } catch (EmailAlreadyInUseException e) {
            // FIXME: Should show a form message not just throw a runtime exception.
            throw new RuntimeException("That email is already in use; please choose another.");
        }

        model.addAttribute("banks", database.getAllBanks());
        return "banks";
    }

   
}
