package com.tcts.datamodel;

import com.tcts.common.PrettyPrintingDate;
import java.util.Map;

/**
 * A collection of basic data about the site, for display on an admin screen.
 */
public class SiteStatistics {
    private final int numEvents;
    private final int numMatchedEvents;
    private final int numUnmatchedEvents;
    private final int num3rdGradeEvents; // FIXME: Invalid now that the list of grades is flexible
    private final int num4thGradeEvents; // FIXME: Invalid now that the list of grades is flexible
    private final int numInPersonEvents; // FIXME: Invalid now that the list of delivery methods is flexible
    private final int numVirtualEvents; // FIXME: Invalid now that the list of delivery methods is flexible
    private final int numVolunteers;
    private final int numParticipatingTeachers;
    private final int numParticipatingSchools;
    private final Map<PrettyPrintingDate, Integer> numEventsByEventDate;
    private final Map<String, Integer> numEventsByGrade;
    private final Map<String, Integer> numEventsByDeliveryMethod;
    private final Map<String, Integer> numEventsByEventTime;

    /**
     * Constructor that sets all the fields.
     */
    public SiteStatistics(int numEvents, int numMatchedEvents, int numUnmatchedEvents, int num3rdGradeEvents, int num4thGradeEvents, int numInPersonEvents, int numVirtualEvents, int numVolunteers, int numParticipatingTeachers, int numParticipatingSchools, Map<PrettyPrintingDate, Integer> numEventsByEventDate, Map<String, Integer> numEventsByGrade, Map<String, Integer> numEventsByDeliveryMethod, Map<String, Integer> numEventsByEventTime) {
        this.numEvents = numEvents;
        this.numMatchedEvents = numMatchedEvents;
        this.numUnmatchedEvents = numUnmatchedEvents;
        this.num3rdGradeEvents = num3rdGradeEvents;
        this.num4thGradeEvents = num4thGradeEvents;
        this.numInPersonEvents = numInPersonEvents;
        this.numVirtualEvents = numVirtualEvents;
        this.numVolunteers = numVolunteers;
        this.numParticipatingTeachers = numParticipatingTeachers;
        this.numParticipatingSchools = numParticipatingSchools;
        this.numEventsByEventDate = numEventsByEventDate;
        this.numEventsByGrade = numEventsByGrade;
        this.numEventsByDeliveryMethod = numEventsByDeliveryMethod;
        this.numEventsByEventTime = numEventsByEventTime;
    }

    public int getNumEvents() {
        return numEvents;
    }

    public int getNumMatchedEvents() {
        return numMatchedEvents;
    }

    public int getNumUnmatchedEvents() {
        return numUnmatchedEvents;
    }

    public int getNum3rdGradeEvents() {
        return num3rdGradeEvents;
    }

    public int getNum4thGradeEvents() {
        return num4thGradeEvents;
    }

    public int getNumInPersonEvents() {
        return numInPersonEvents;
    }

    public int getNumVirtualEvents() {
        return numVirtualEvents;
    }

    public int getNumVolunteers() {
        return numVolunteers;
    }

    public int getNumParticipatingTeachers() {
        return numParticipatingTeachers;
    }

    public int getNumParticipatingSchools() {
        return numParticipatingSchools;
    }

    public Map<PrettyPrintingDate, Integer> getNumEventsByEventDate() {
        return numEventsByEventDate;
    }

    public Map<String, Integer> getNumEventsByGrade() {
        return numEventsByGrade;
    }

    public Map<String, Integer> getNumEventsByDeliveryMethod() {
        return numEventsByDeliveryMethod;
    }

    public Map<String, Integer> getNumEventsByEventTime() {
        return numEventsByEventTime;
    }
}
