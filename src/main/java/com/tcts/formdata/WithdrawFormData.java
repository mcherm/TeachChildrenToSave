package com.tcts.formdata;

/**
 * Contains the fields needed to withdraw from a course.
 */
public class WithdrawFormData {
    private String withdrawNotes;
    private String eventId;

    public String getWithdrawNotes() {
        return withdrawNotes;
    }

    public void setWithdrawNotes(String withdrawNotes) {
        this.withdrawNotes = withdrawNotes;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
