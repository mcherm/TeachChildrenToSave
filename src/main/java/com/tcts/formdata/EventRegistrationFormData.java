package com.tcts.formdata;

/**
 * A class containing the fields needed for a volunteer to sign up for an event.
 */
public class EventRegistrationFormData extends CreateEventFormData{
    public String eventId;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
