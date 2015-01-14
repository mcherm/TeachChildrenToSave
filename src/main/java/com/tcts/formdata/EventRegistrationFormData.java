package com.tcts.formdata;

/**
 * A class containing the fields needed for a volunteer to sign up for an event.
 * FIXME: Why does this extend CreateEventFormData --- that seems quite wrong!!
 */
public class EventRegistrationFormData extends CreateEventFormData {
    public String eventId;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
