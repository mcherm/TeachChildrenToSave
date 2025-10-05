package com.tcts.formdata;

import com.tcts.exception.FormDataConstructionException;

/**
 * The class EditEventFormData contains a complex field: a PrettyPrintingDate. It
 * would be very complex to have Spring try to do binding to this field when reading in the
 * result from an HTML form. So this is a class with the same fields as EditEventFormData,
 * but they are all Strings; we will use this when binding the input from an HTML form and
 * then convert to an EditEventFormData. Also, EditEventFormData extends from
 * CreateEventFormData so we try to reuse CreateEventFormDataStrings.
 */
public class EditEventFormDataStrings extends CreateEventFormDataStrings {
    private String eventId;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public EditEventFormData asFormData() throws FormDataConstructionException {
        final EditEventFormData formData = new EditEventFormData();
        setCreateEventFormFields(formData);
        formData.setEventId(eventId);
        return formData;
    }
}
