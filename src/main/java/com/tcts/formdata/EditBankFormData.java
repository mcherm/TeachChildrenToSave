package com.tcts.formdata;

import com.tcts.database.DatabaseField;
import com.tcts.datamodel.BankAdmin;
import com.tcts.exception.InvalidParameterFromGUIException;
import java.math.BigDecimal;
import java.util.List;


/**
 * The data fields needed to edit a bank.
 */
public class EditBankFormData extends ValidatedFormData<RuntimeException> {
    private String bankName;
    private String bankId;
    private String minLMIForCRA; // If valid, can be "" (meaning no setting) or a number 0..100.

    private static final BigDecimal ZERO = new BigDecimal(0);
    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    private boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    @Override
    public void validationRules(Errors errors) {
        if (isEmpty(bankName)) {
            errors.addError("Please provide a name for the bank.");
        }
        validateLength(bankName, DatabaseField.bank_name, errors);
        if (bankId == null || bankId.isEmpty()) {
            throw new InvalidParameterFromGUIException("The bankId should always be present.");
        }
        if (minLMIForCRA == null) {
            minLMIForCRA = "";
        }
        if (!minLMIForCRA.isEmpty()) {
            try {
                BigDecimal lmiCutoffDecimal = new BigDecimal(minLMIForCRA);
                if (lmiCutoffDecimal.compareTo(ZERO) < 0 || lmiCutoffDecimal.compareTo(ONE_HUNDRED) > 0) {
                    errors.addError("The LMI cutoff must be a number from 0 through 100.");
                }
            } catch(NumberFormatException err) {
                errors.addError("The LMI cutoff must be a number.");
            }
        }
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
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
