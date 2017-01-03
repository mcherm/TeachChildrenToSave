package com.tcts.datamodel;

/**
 * Represents the information about displaying a single document. Represents
 * one row from the documents table.
 */
public class Document implements Comparable<Document> {
    private String name;
    private boolean showToTeacher;
    private boolean showToVolunteer;
    private boolean showToBankAdmin;

    /**
     * Constructor.
     */
    public Document(String name, boolean showToTeacher, boolean showToVolunteer, boolean showToBankAdmin) {
        this.name = name;
        this.showToTeacher = showToTeacher;
        this.showToVolunteer = showToVolunteer;
        this.showToBankAdmin = showToBankAdmin;
    }

    /**
     * We will sort them by document name.
     */
    @Override
    public int compareTo(Document document) {
        return (this.name.compareTo(document.name));
    }

    /**
     * We will enforce that two Document instances are considered "the same"
     * if the document names are equal.
     */
    @Override
    public boolean equals(Object document){
        if (!(document instanceof Document)) {
            return false;
        }

        if (this.name.equals(((Document)document).name)){
            return true;
        }
        return false;
    }

    /**
     * We will enforce that two Document instances are considered "the same"
     * if the document names are equal.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getShowToTeacher() {
        return showToTeacher;
    }

    public void setShowToTeacher(boolean showToTeacher) {
        this.showToTeacher = showToTeacher;
    }

    public boolean getShowToVolunteer() {
        return showToVolunteer;
    }

    public void setShowToVolunteer(boolean showToVolunteer) {
        this.showToVolunteer = showToVolunteer;
    }

    public boolean getShowToBankAdmin() {
        return showToBankAdmin;
    }

    public void setShowToBankAdmin(boolean showToBankAdmin) {
        this.showToBankAdmin = showToBankAdmin;
    }
}
