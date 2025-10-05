package com.tcts.util;

import com.tcts.database.DatabaseFacade;
import com.tcts.datamodel.Event;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Utility methods for working with events.
 */
public class EventUtil {

    /**
     * Checks if a list of events combined with allowedEvents has 2 or more unique non-empty delivery methods.
     * Returns true if there are 2+ unique non-empty delivery methods, false otherwise.
     */
    public static boolean hasMultipleDeliveryMethods(DatabaseFacade database, List<Event> events) {
        List<String> allowedDeliveryMethods = database.getAllowedDeliveryMethods();
        Set<String> uniqueDeliveryMethods = new HashSet<>(allowedDeliveryMethods);
        for (Event event : events) {
            String deliveryMethod = event.getDeliveryMethod();
            if (deliveryMethod != null && !deliveryMethod.trim().isEmpty()) {
                uniqueDeliveryMethods.add(deliveryMethod);
            }
        }
        return uniqueDeliveryMethods.size() >= 2;
    }

    /**
     * Checks if a list of events combined with allowedGrades has 2 or more unique non-empty grades.
     * Returns true if there are 2+ unique non-empty grades, false otherwise.
     */
    public static boolean hasMultipleGrades(DatabaseFacade database, List<Event> events) {
        List<String> alllowedGrades = database.getAllowedGrades();
        Set<String> uniqueGrades = new HashSet<>(alllowedGrades);
        for (Event event : events) {
            String grade = event.getGrade();
            if (grade != null && !grade.trim().isEmpty()) {
                uniqueGrades.add(grade);
            }
        }
        return uniqueGrades.size() >= 2;
    }
}
