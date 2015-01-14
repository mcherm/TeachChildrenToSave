package com.tcts.formdata;

/**
 * Data that will be entered on the teacher registration form.
 */
public class TeacherRegistrationFormData extends UserRegistrationFormData {
    private String schoolId;

    @Override
    protected void validationRules(Errors errors) throws RuntimeException {
        super.validationRules(errors);
        if (schoolId == null || schoolId.trim().length() == 0 || schoolId.equals("0")) {
            errors.addError("You must select a school.");
        }
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }
}
