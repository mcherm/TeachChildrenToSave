package com.tcts.formdata;


/**
 * A class containing the fields needed for a volunteer to sign up for an event.
 */
public class EventRegistrationFormData extends ValidatedFormData<RuntimeException> {
    public String eventId;
    public String volunteerId;

    public String getEventId() {
        return eventId;
    }
    public String getVolunteerId() {
        return volunteerId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    public void setVolunteerId(String volunteerId) {
        this.volunteerId =volunteerId;
    }


    @Override
    public void validationRules(Errors errors) {
        if ((eventId == null) || (eventId.isEmpty())) {
            errors.addError("An event id is required.");
        }
    }
}
