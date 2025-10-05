package com.tcts.controller;

import java.sql.Date;
import java.util.List;

import com.tcts.formdata.AddAllowedValueFormData;
import jakarta.servlet.http.HttpSession;

import com.tcts.common.PrettyPrintingDate;
import com.tcts.exception.AllowedValueAlreadyInUseException;
import com.tcts.formdata.AddAllowedDateFormData;
import com.tcts.formdata.EditSiteSettingFormData;
import com.tcts.formdata.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.tcts.common.SessionData;
import com.tcts.database.DatabaseFacade;
import com.tcts.exception.InvalidParameterFromGUIException;
import com.tcts.exception.NoSuchAllowedValueException;
import com.tcts.exception.NotLoggedInException;


/**
 * This is a controller for editing (at least some of) the tables in
 * the database that the site admin is allowed to maintain.
 */
@Controller
public class AdminEditController {

    @Autowired
    private DatabaseFacade database;


    @RequestMapping(value = "listAllowedDates.htm", method = RequestMethod.GET)
    public String listAllowedDates(
            HttpSession session,
            Model model
    ) {
        ensureSiteAdminLoggedIn(session);
        return showListAllowedDatesPage(model);
    }


    @RequestMapping(value = "listAllowedValues.htm", method = RequestMethod.GET)
    public String listAllowedValues(
            @RequestParam("valueType") String valueType,
            HttpSession session,
            Model model
    ) {
        ensureSiteAdminLoggedIn(session);
        model.addAttribute("valueType", valueType);
        final List<String> allowedValues = switch (valueType) {
            case "Time" -> database.getAllowedTimes();
            case "Grade" -> database.getAllowedGrades();
            case "Delivery Method" -> database.getAllowedDeliveryMethods();
            default -> throw new InvalidParameterFromGUIException("Invalid value type: '" + valueType + "'.");
        };
        model.addAttribute("allowedValues", allowedValues);
        return "listAllowedValues";
    }


    /**
     * A site admin views the current settings
     */
    @RequestMapping(value = "viewSiteSettings.htm", method = RequestMethod.GET)
    public String viewSiteSettings(
            HttpSession session,
            Model model
    ) {
        ensureSiteAdminLoggedIn(session);
        return showViewSiteSettingsPage(model);
    }


    @RequestMapping(value = "addAllowedDate.htm", method = RequestMethod.GET)
    public String showAddAllowedDate(
            HttpSession session,
            Model model
    ) {
        ensureSiteAdminLoggedIn(session);
        AddAllowedDateFormData formData = new AddAllowedDateFormData();
        return showAddAllowedDatePageWithErrors(model, formData, null);
    }


    @RequestMapping(value = "addAllowedValue.htm", method = RequestMethod.GET)
    public String showAddAllowedValue(
            HttpSession session,
            Model model,
            @RequestParam("valueType") String valueType
    ) {
        ensureSiteAdminLoggedIn(session);
        final AddAllowedValueFormData formData = new AddAllowedValueFormData();
        formData.setValueType(valueType);
        return showAddAllowedValuePageWithErrors(model, formData, null);
    }


    @RequestMapping(value = "addAllowedDate.htm", method = RequestMethod.POST)
    public String doAddAllowedDate(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") AddAllowedDateFormData formData
    ) {
        ensureSiteAdminLoggedIn(session);

        // --- Validation Rules ---
        Errors errors = formData.validate();
        if (errors.hasErrors()) {
            return showAddAllowedDatePageWithErrors(model, formData, errors);
        }

        // --- Insert it ---
        try {
            database.insertNewAllowedDate(formData);
        } catch(AllowedValueAlreadyInUseException err) {
            return showAddAllowedDatePageWithErrors(model, formData,
                    new Errors("That date is already listed as an allowed date."));
        }

        // --- Return to the date list ---
        return "redirect:listAllowedDates.htm";
    }

    @RequestMapping(value = "editAllowedValues.htm", method = RequestMethod.GET)
    public String editAllowedValues(HttpSession session) {
        ensureSiteAdminLoggedIn(session);
        return "editAllowedValues";
    }


    @RequestMapping(value = "addAllowedValue.htm", method = RequestMethod.POST)
    public String doAddAllowedValue(
            HttpSession session,
            Model model,
            @ModelAttribute("formData") AddAllowedValueFormData formData
    ) {
        ensureSiteAdminLoggedIn(session);

        // --- Validation Rules ---
        Errors errors = formData.validate();
        if (errors.hasErrors()) {
            return showAddAllowedValuePageWithErrors(model, formData, errors);
        }

        // --- Insert it ---
        try {
            switch (formData.valueType) {
                case "Time": database.insertNewAllowedTime(formData.allowedValue, formData.valueToInsertBefore); break;
                case "Grade": database.insertNewAllowedGrade(formData.allowedValue, formData.valueToInsertBefore); break;
                case "Delivery Method": database.insertNewAllowedDeliveryMethod(formData.allowedValue, formData.valueToInsertBefore); break;
                default: throw new InvalidParameterFromGUIException("Invalid field type: '" + formData.valueType + "'.");
            }
        } catch(AllowedValueAlreadyInUseException err) {
            return showAddAllowedValuePageWithErrors(model, formData,
                    new Errors("That " + formData.valueType + " is already listed as an allowed " + formData.valueType + "."));
        } catch(NoSuchAllowedValueException err) {
            return showAddAllowedValuePageWithErrors(model, formData,
                    new Errors("The " + formData.valueType + " you inserted this before does not exist."));
        }

        // --- Return to the value list ---
        return "redirect:listAllowedValues.htm?valueType=" + formData.valueType;
    }


    @RequestMapping(value = "deleteAllowedDate.htm", method = RequestMethod.POST)
    public String deleteAllowedDate(
            @RequestParam("parseableDateStr") String parseableDateStr,
            HttpSession session
    ) {
        ensureSiteAdminLoggedIn(session);

        Date date;
        try {
            date = Date.valueOf(parseableDateStr);
        } catch(IllegalArgumentException err) {
            throw new InvalidParameterFromGUIException();
        }
        try {
            database.deleteAllowedDate(new PrettyPrintingDate(date));
        } catch (NoSuchAllowedValueException e) {
            throw new InvalidParameterFromGUIException();
        }

        return "redirect:listAllowedDates.htm";
    }


    @RequestMapping(value = "deleteAllowedValue.htm", method = RequestMethod.POST)
    public String deleteAllowedValue(
            @RequestParam("valueType") String valueType,
            @RequestParam("allowedValue") String allowedValue,
            HttpSession session
    ) {
        ensureSiteAdminLoggedIn(session);

        try {
            switch (valueType) {
                case "Time" -> database.deleteAllowedTime(allowedValue);
                case "Grade" -> database.deleteAllowedGrade(allowedValue);
                case "Delivery Method" -> database.deleteAllowedDeliveryMethod(allowedValue);
                default -> throw new InvalidParameterFromGUIException();
            }
        } catch (NoSuchAllowedValueException e) {
            throw new InvalidParameterFromGUIException();
        }

        return "redirect:listAllowedValues.htm?valueType=" + valueType;
    }

    @RequestMapping(value = "showEditSiteSetting.htm", method = RequestMethod.GET)
    public String showEditSiteSetting(
            @RequestParam("settingToEdit") String settingToEdit,
            HttpSession session,
            Model model
    ) {
        ensureSiteAdminLoggedIn(session);
        return showEditSiteSettingPage(model, settingToEdit);
    }

    @RequestMapping(value = "editSiteSetting.htm", method = RequestMethod.POST)
    public String editSiteSetting(
            @ModelAttribute("formData") EditSiteSettingFormData formData,
            HttpSession session
    ) {
        ensureSiteAdminLoggedIn(session);
        database.modifySiteSetting(formData.getSettingName(), formData.getSettingValue());
        return "redirect:viewSiteSettings.htm";
    }

    public String showListAllowedDatesPage(Model model) {
        model.addAttribute("allowedDates", database.getAllowedDates());
        return "listAllowedDates";
    }

    public String showViewSiteSettingsPage(Model model) {
        model.addAttribute("siteSettings", database.getSiteSettings());
        return "viewSiteSettings";
    }

    public String showAddAllowedDatePageWithErrors(Model model, AddAllowedDateFormData formData, Errors errors) {
        model.addAttribute("formData", formData);
        model.addAttribute("errors", errors);
        return "addAllowedDate";
    }

    public String showAddAllowedValuePageWithErrors(
            Model model,
            AddAllowedValueFormData formData,
            Errors errors
    ) {
        assert formData.valueType != null;
        final List<String> allowedValues = switch(formData.valueType) {
            case "Time" -> database.getAllowedTimes();
            case "Grade" -> database.getAllowedGrades();
            case "Delivery Method" -> database.getAllowedDeliveryMethods();
            default -> throw new IllegalStateException("Invalid allowedValue: " + formData.valueType);
        };
        model.addAttribute("valueType", formData.valueType);
        model.addAttribute("allowedValues", allowedValues);
        model.addAttribute("formData", formData);
        model.addAttribute("errors", errors);
        return "addAllowedValue";
    }

    /**
     * A subroutine that launches the page for editing a site setting.
     */
    public String showEditSiteSettingPage(Model model, String settingToEdit) {
        EditSiteSettingFormData formData = new EditSiteSettingFormData();
        formData.setSettingName(settingToEdit);
        formData.setSettingValue(database.getSiteSettings().get(settingToEdit));
        model.addAttribute("formData", formData);
        return "editSiteSetting";
    }


    /** A subroutine called to ensure it's the site admin who is logged in. */
    private void ensureSiteAdminLoggedIn(HttpSession session) {
        // --- Make sure it's the site admin that's logged in ---
        SessionData sessionData = SessionData.fromSession(session);
        if (sessionData.getSiteAdmin() == null) {
            throw new NotLoggedInException();
        }
    }

}
