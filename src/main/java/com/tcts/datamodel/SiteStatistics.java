package com.tcts.datamodel;

import com.tcts.common.PrettyPrintingDate;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A collection of basic data about the site, for display on an admin screen.
 */
public class SiteStatistics {
    private int numEvents;
    private int numMatchedEvents;
    private int numUnmatchedEvents;
    private int num3rdGradeEvents;
    private int num4thGradeEvents;
    private int numVolunteers;
    private int numParticipatingTeachers;
    private int numParticipatingSchools;


    public int getNumEvents() {
        return numEvents;
    }

    public void setNumEvents(int numEvents) {
        this.numEvents = numEvents;
    }

    public int getNumMatchedEvents() {
        return numMatchedEvents;
    }

    public void setNumMatchedEvents(int numMatchedEvents) {
        this.numMatchedEvents = numMatchedEvents;
    }

    public int getNumUnmatchedEvents() {
        return numUnmatchedEvents;
    }

    public void setNumUnmatchedEvents(int numUnmatchedEvents) {
        this.numUnmatchedEvents = numUnmatchedEvents;
    }

    public int getNum3rdGradeEvents() {
        return num3rdGradeEvents;
    }

    public void setNum3rdGradeEvents(int num3rdGradeEvents) {
        this.num3rdGradeEvents = num3rdGradeEvents;
    }

    public int getNum4thGradeEvents() {
        return num4thGradeEvents;
    }

    public void setNum4thGradeEvents(int num4thGradeEvents) {
        this.num4thGradeEvents = num4thGradeEvents;
    }

    public int getNumVolunteers() {
        return numVolunteers;
    }

    public void setNumVolunteers(int numVolunteers) {
        this.numVolunteers = numVolunteers;
    }

    public int getNumParticipatingTeachers() {
        return numParticipatingTeachers;
    }

    public void setNumParticipatingTeachers(int numParticipatingTeachers) {
        this.numParticipatingTeachers = numParticipatingTeachers;
    }

    public int getNumParticipatingSchools() {
        return numParticipatingSchools;
    }

    public void setNumParticipatingSchools(int numParticipatingSchools) {
        this.numParticipatingSchools = numParticipatingSchools;
    }
}
