package com.tcts.formdata;

import com.tcts.exception.InvalidParameterFromGUIException;


/**
 * The data fields needed to edit a bank.
 */
public class EditBankFormData extends CreateBankFormData {
    private String bankId;
    private String minLMIForCRA; // If valid, can be "" (meaning no setting) or a number 0..100.

    @Override
    public void validationRules(Errors errors) {
        super.validationRules(errors);
        if (bankId == null || bankId.isEmpty()) {
            throw new InvalidParameterFromGUIException("The bankId should always be present.");
        }
        if (minLMIForCRA == null) {
            minLMIForCRA = "";
        }
        if (!minLMIForCRA.isEmpty()) {
            try {
                int lmiCutoffInt = Integer.parseInt(minLMIForCRA);
                if (lmiCutoffInt < 0 || lmiCutoffInt > 100) {
                    errors.addError("The LMI cutoff must be a number from 0 through 100.");
                }
            } catch(NumberFormatException err) {
                errors.addError("The LMI cutoff must be a number.");
            }
        }
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getMinLMIForCRA() {
        return minLMIForCRA;
    }

    public void setMinLMIForCRA(String minLMIForCRA) {
        this.minLMIForCRA = minLMIForCRA;
    }
}
