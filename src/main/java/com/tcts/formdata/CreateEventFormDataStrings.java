package com.tcts.formdata;

import com.tcts.common.PrettyPrintingDate;
import com.tcts.exception.FormDataConstructionException;

import java.text.ParseException;

/**
 * The class CreateEventFormData contains a complex field: a PrettyPrintingDate. It would be
 * very complex to have Spring try to do binding to this field when reading in the result
 * from an HTML form. So this is a class with the same fields as CreateEventFormData, but
 * they are all Strings; we will use this when binding the input from an HTML form and
 * then convert to a CreateEventFormData.
 */
public class CreateEventFormDataStrings {
    private String eventDate;
    private String eventTime;
    private String grade;
    private String deliveryMethod;
    private String numberStudents;
    private String notes;
    private String teacherId;

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public void setNumberStudents(String numberStudents) {
        this.numberStudents = numberStudents;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    /**
     * Attempts to return the properly constructed CreateEventFormData. If there is an error
     * constructing the complex field, this will throw a FormDataConstructionException.
     *
     * @return the form data object
     * @throws FormDataConstructionException if the input is invalid in a way that prevents creating the form data.
     */
    public CreateEventFormData asFormData() throws FormDataConstructionException {
        final CreateEventFormData formData = new CreateEventFormData();
        setCreateEventFormFields(formData);
        return formData;
    }

    /**
     * This is the part of asFormData() that sets the fields; it was pulled into a separate method that
     * operate on any subclass of CreateFormData because we wanted to extend this class and reuse
     * the code.
     */
    protected <T extends CreateEventFormData> void setCreateEventFormFields(T formData) throws FormDataConstructionException {
        try {
            formData.setEventDate(PrettyPrintingDate.fromParsableDate(eventDate));
        } catch (ParseException e) {
            throw new FormDataConstructionException(new Errors("You must select a valid date."));
        }
        formData.setEventTime(eventTime);
        formData.setGrade(grade);
        formData.setDeliveryMethod(deliveryMethod);
        formData.setNumberStudents(numberStudents);
        formData.setNotes(notes);
        formData.setTeacherId(teacherId);
    }
}
