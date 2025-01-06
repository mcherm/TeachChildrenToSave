package com.tcts.controller;

import java.sql.SQLException;
import java.util.List;

import com.tcts.formdata.MarkAsBankAdminFormData;
import com.tcts.formdata.NewBankAdminFormData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.tcts.datamodel.BankAdmin;
import com.tcts.datamodel.Event;
import com.tcts.datamodel.UserType;
import com.tcts.datamodel.Volunteer;
import com.tcts.exception.BankHasVolunteersException;
import com.tcts.exception.EmailAlreadyInUseException;
import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NoSuchBankException;
import com.tcts.exception.NotLoggedInException;
import com.tcts.exception.VolunteerHasEventsException;
import com.tcts.formdata.CreateBankFormData;
import com.tcts.formdata.EditBankFormData;
import com.tcts.formdata.Errors;
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

    @Autowired
    CancelWithdrawController cancelWithdrawController;


    /**
     * Render the bank edit page.
     */
    @RequestMapping(value = "viewEditBanks.htm", method = RequestMethod.GET)
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
            BankAdmin bankAdmin = database.getBankAdminByBank(bank.getBankId()); // FIXME: Older version; eliminate
            bank.setLinkedBankAdmin(bankAdmin); // FIXME: Older version; eliminate
            List<BankAdmin> bankAdmins = database.getBankAdminsByBank(bank.getBankId());
            bank.setLinkedBankAdmins(bankAdmins);
        }

        model.addAttribute("banks", banks);
        return "banks";
    }


    /**
     * Displays the Delete Bank Confirmation Page
     */
    @RequestMapping(value = "deleteBank.htm", method = RequestMethod.GET)
    public String deleteBankConfirm(
            @RequestParam("bankId") String bankId,
            HttpSession session,
            Model model
        ) throws SQLException
    {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        Bank bank = database.getBankById(bankId);
        List<Volunteer> volunteers = database.getVolunteersByBank(bankId);
        model.addAttribute("volunteers", volunteers);
        model.addAttribute("bank", bank);
        return "deleteBank";

    }

    /**
     * Deletes a bank and all its bank admin and all of its volunteers and withdraws the volunteers from
     * any classes they were signed up for
     */
    @RequestMapping(value = "deleteBank.htm", method = RequestMethod.POST)
    public String deleteBank(
            @RequestParam("bankId") String bankId,
            HttpSession session,
            HttpServletRequest request
    ) throws SQLException, BankHasVolunteersException, VolunteerHasEventsException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
        List<Volunteer> volunteers = database.getVolunteersByBank(bankId);
        for (Volunteer volunteer : volunteers) {
            // --- First, unregister from any events ---
            List<Event> events = database.getEventsByVolunteer(volunteer.getUserId());
            for (Event event : events) {
                cancelWithdrawController.withdrawFromAnEvent(event, request, null);
            }
        }
        try {
            // delete bank, bank admin and all volunteers associated with the bank
            database.deleteBankandBankVolunteers(bankId);
        } catch(NoSuchBankException err) {
            throw new InvalidParameterFromGUIException();
        }
        return "redirect:viewEditBanks.htm";
    }


    @RequestMapping(value = "editBank.htm", method = RequestMethod.GET)
    public String enterDataToEditBank(
            HttpSession session,
            Model model,
            @RequestParam("bankId") String bankId
    ) throws SQLException {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null && sessionData.getBankAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Load existing data ---
        EditBankFormData formData = initializeNewEditBankFormData(bankId);

        // --- Show the edit page ---
        return showEditBankWithErrors(model, sessionData, formData, null);
    }

    /** Create an EditBankFormData loading everything from the DB (using the bankId). */
    private EditBankFormData initializeNewEditBankFormData(String bankId) throws SQLException {
        Bank bank = database.getBankById(bankId);
        EditBankFormData formData = new EditBankFormData();
        formData.setBankId(bankId);
        formData.setBankName(bank.getBankName());
        formData.setMinLMIForCRA(bank.getMinLMIForCRA() == null ? "" : bank.getMinLMIForCRA().toString());
        List<BankAdmin> bankAdmins = database.getBankAdminsByBank(bankId);
        formData.setBankAdmins(bankAdmins);
        return formData;
    }


    /**
     * A subroutine used to set up and then show the add bank form. It
     * returns the string, so you can invoke it as "return showEditBankWithErrors(...)".
     */
    public String showEditBankWithErrors(Model model, SessionData sessionData, EditBankFormData formData, Errors errors)
    {
        String cancelURL = bankEditCancelURL(sessionData);
        // only the site admin can change who is a bank admin and who is a mere volunteer
        boolean canEditAdmins = sessionData.getSiteAdmin() == null;

        model.addAttribute("cancelURL", cancelURL);
        model.addAttribute("canEditAdmins", canEditAdmins);
        model.addAttribute("formData", formData);
        model.addAttribute("errors", errors);
        return "editBank";
    }

    /** Subroutine to tell us where to go to if we cancel while editing a bank or its bank admins. */
    private static String bankEditCancelURL(SessionData sessionData) {
        String cancelURL;
        switch(sessionData.getUser().getUserType()) {
            case BANK_ADMIN:
                cancelURL = UserType.BANK_ADMIN.getHomepage();
                break;
            case SITE_ADMIN:
                cancelURL = "viewEditBanks.htm";
                break;
            default:
                throw new RuntimeException("We checked if they were logged in, so this should be impossible.");
        }
        return cancelURL;
    }


    @RequestMapping(value = "editBank.htm", method = RequestMethod.POST)
    public String getUpdatedBank(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") EditBankFormData formData
        ) throws SQLException
    {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null && sessionData.getBankAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Validation Rules ---
        Errors errors = formData.validate();
        if (errors.hasErrors()) {
            return showEditBankWithErrors(model, sessionData, formData, errors);
        }

        try {
            database.modifyBankAndBankAdmin(formData);
        } catch(NoSuchBankException err) {
            throw new InvalidParameterFromGUIException();
        } catch(EmailAlreadyInUseException err) {
            return showEditBankWithErrors(model, sessionData, formData,
                    new Errors("That email is already in use; please choose another."));
        }

        // --- Successful; show the master bank edit again ---
        switch(sessionData.getUser().getUserType()) {
            case SITE_ADMIN:
                return "redirect:viewEditBanks.htm";
            case BANK_ADMIN:
                return "redirect:" + UserType.BANK_ADMIN.getHomepage();
            default:
                throw new RuntimeException("Should not be able to get here.");
        }
    }


    /**
     * Show the page for creating a new bank and bank admin.
     */
    @RequestMapping(value = "addBank.htm", method = RequestMethod.GET)
    public String enterDataToAddBank(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") CreateBankFormData formData
    ) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Successful; show the edit page again ---
        return showAddBankWithErrors(model, null);
    }

    @RequestMapping(value = "addBank.htm", method = RequestMethod.POST)
    public String doAddBank(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") CreateBankFormData formData
        ) throws SQLException
    {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Validation Rules ---
        Errors errors = formData.validate();
        if (errors.hasErrors()) {
            return showAddBankWithErrors(model, errors);
        }

        try {
            database.insertNewBankAndAdmin(formData);
        } catch(EmailAlreadyInUseException err) {
            return showAddBankWithErrors(model,
                    new Errors("That email is already in use; please choose another."));
        }

        // --- Successful; show the master bank edit again ---
        return showForm(model);
    }


    /**
     * A subroutine used to set up and then show the add bank form. It
     * returns the string, so you can invoke it as "return showAddBankWithErrorMessage(...)".
     */
    private String showAddBankWithErrors(Model model, Errors errors) throws SQLException {
        model.addAttribute("errors", errors);
        return "addBank";
    }


    @RequestMapping(value = "newBankAdmin.htm", method = RequestMethod.GET)
    public String showNewBankAdmin(
            HttpSession session,
            Model model,
            @RequestParam("bankId") String bankId
    ) throws SQLException {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            // FIXME: Maybe I should all BankAdmins to add a new BankAdmin. But right now
            //   I am NOT allowing that -- only the site admin. (NOTE: Need to confirm
            //   that it is actually only avaliable to the Site Admin.)
            throw new NotLoggedInException();
        }

        // --- Prepare data ---
        Bank bank = database.getBankById(bankId);
        String bankName = bank.getBankName();
        NewBankAdminFormData formData = new NewBankAdminFormData();
        formData.setBankId(bankId);

        // --- Show the edit page ---
        return showNewBankAdminWithErrors(model, sessionData, bankName, formData, null);
    }


    @RequestMapping(value = "newBankAdmin.htm", method = RequestMethod.POST)
    public String doNewBankAdmin(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") NewBankAdminFormData formData
    ) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Validation Rules ---
        String bankId = formData.getBankId();
        Errors errors = formData.validate();
        if (errors.hasErrors()) {
            String bankName = database.getBankById(bankId).getBankName();
            return showNewBankAdminWithErrors(model, sessionData, bankName, formData, errors);
        }

        // --- Perform the updates ---
        try {
            database.insertNewBankAdmin(formData);
        } catch(EmailAlreadyInUseException err) {
            return showAddBankWithErrors(model,
                    new Errors("That email is already in use; please choose another."));
        }

        // --- Load existing data ---
        EditBankFormData editBankFormData = initializeNewEditBankFormData(bankId);

        // --- Successful; show the master bank edit again ---
        return showEditBankWithErrors(model, sessionData, editBankFormData, null);
    }


    /**
     * A subroutine used to set up and then show the new bank admin form. It
     * returns the string, so you can invoke it as "return showEditBankWithErrors(...)".
     */
    public String showNewBankAdminWithErrors(
            Model model,
            SessionData sessionData,
            String bankName,
            NewBankAdminFormData formData,
            Errors errors
    ) {
        String cancelURL = bankEditCancelURL(sessionData);

        model.addAttribute("bankName", bankName);
        model.addAttribute("cancelURL", cancelURL);
        model.addAttribute("formData", formData);
        model.addAttribute("errors", errors);
        return "newBankAdmin";
    }

    @RequestMapping(value = "markAsBankAdmin.htm", method = RequestMethod.GET)
    public String showMarkAsBankAdmin(
            HttpSession session,
            Model model,
            @RequestParam("bankId") String bankId
    ) throws SQLException {
        // --- Ensure logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            // FIXME: Maybe I should all BankAdmins to mark as a new BankAdmin. But right now
            //   I am NOT allowing that -- only the site admin. (NOTE: Need to confirm
            //   that it is actually only avaliable to the Site Admin.)
            throw new NotLoggedInException();
        }

        // --- Prepare data ---
        Bank bank = database.getBankById(bankId);
        String bankName = bank.getBankName();
        List<Volunteer> volunteers = database.getVolunteersByBank(bankId);
        // exclude the current bank admins, if any (they ARE volunteers, but we
        // don't want them for this purpose)
        volunteers.removeIf(x -> x.getUserType() == UserType.BANK_ADMIN);

        // --- Show the edit page ---
        String cancelURL = bankEditCancelURL(sessionData);
        model.addAttribute("bankName", bankName);
        model.addAttribute("bankId", bankId);
        model.addAttribute("cancelURL", cancelURL);
        model.addAttribute("volunteers", volunteers);
        return "markAsBankAdmin";
    }

    @RequestMapping(value = "markAsBankAdmin.htm", method = RequestMethod.POST)
    public String doMarkAsBankAdmin(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") MarkAsBankAdminFormData formData
    ) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Perform the updates ---
        database.setUserType(formData.getUserId(), UserType.BANK_ADMIN);

        // --- Load existing data ---
        EditBankFormData editBankFormData = initializeNewEditBankFormData(formData.getBankId());

        // --- Successful; show the master bank edit again ---
        return showEditBankWithErrors(model, sessionData, editBankFormData, null);
    }

    @RequestMapping(value = "markAsVolunteer.htm", method = RequestMethod.POST)
    public String doMarkAsVolunteer(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") MarkAsBankAdminFormData formData
    ) throws SQLException {
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }

        // --- Perform the updates ---
        database.setUserType(formData.getUserId(), UserType.VOLUNTEER);

        // --- Load existing data ---
        EditBankFormData editBankFormData = initializeNewEditBankFormData(formData.getBankId());

        // --- Successful; show the master bank edit again ---
        return showEditBankWithErrors(model, sessionData, editBankFormData, null);
    }
}
