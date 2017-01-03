package com.tcts.formdata;


/**
 * This is form for entering the data to modify who on the site can access a single document.
 */
public class EditDocumentFormData extends ValidatedFormData<RuntimeException> {
    private String name;
    private boolean showToTeacher;
    private boolean showToVolunteer;
    private boolean showToBankAdmin;

    @Override
    protected void validationRules(Errors errors) throws RuntimeException {
        /* There is nothing to validate */
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShowToTeacher() {
        return showToTeacher;
    }

    public void setShowToTeacher(boolean showToTeacher) {
        this.showToTeacher = showToTeacher;
    }

    public boolean isShowToVolunteer() {
        return showToVolunteer;
    }

    public void setShowToVolunteer(boolean showToVolunteer) {
        this.showToVolunteer = showToVolunteer;
    }

    public boolean isShowToBankAdmin() {
        return showToBankAdmin;
    }

    public void setShowToBankAdmin(boolean showToBankAdmin) {
        this.showToBankAdmin = showToBankAdmin;
    }
}
