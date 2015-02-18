package com.tcts.formdata;

/**
 * Form fields for the screen where you can edit your personal data.
 */
public class EditPersonalDataFormData extends ValidatedFormData<RuntimeException> {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String userId;

    private boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    @Override
    public void validationRules(Errors errors) {
        if (isEmpty(email)) {
            errors.addError("You need to specify your email address.");
        }
        if (isEmpty(firstName)) {
            errors.addError("You need to provide a first name.");
        }
        if (isEmpty(lastName)) {
            errors.addError("You need to provide a last name.");
        }
        if (isEmpty(userId)) {
            errors.addError("The userId field is required.");
        }
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
    
}
