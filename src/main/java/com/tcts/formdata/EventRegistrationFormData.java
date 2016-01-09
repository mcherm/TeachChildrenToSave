package com.tcts.formdata;

/**
 * A class containing the fields needed for a volunteer to sign up for an event.
 * FIXME: Why does this extend CreateEventFormData --- that seems quite wrong!!
 */
public class EventRegistrationFormData extends CreateEventFormData {
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
}
