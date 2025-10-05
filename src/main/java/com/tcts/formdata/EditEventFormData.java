package com.tcts.formdata;


/**
 * A class containing the fields used when an admin edits an event. The admin can edit all
 * fields, including things about who is registered.
 */
public class EditEventFormData extends CreateEventFormData {
    public String eventId;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public void validationRules(Errors errors) {
        super.validationRules(errors);
        if ((eventId == null) || (eventId.isEmpty())) {
            errors.addError("An event id is required.");
        }
    }

}
