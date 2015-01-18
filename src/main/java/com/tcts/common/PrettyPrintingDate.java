package com.tcts.common;

import java.text.SimpleDateFormat;
import java.sql.Date;

/**
 * A subclass of java.util.Date that overrides the toString() to print it out
 * in the format that we want to use for display of dates on this site.
 */
public class PrettyPrintingDate extends Date {
    private final static ThreadLocal<SimpleDateFormat> prettyFormatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("EEE, M/d/yyyy");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> parseableFormatter = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    public PrettyPrintingDate(Date date) {
        super(date.getTime());
    }

    /**
     * This is the method that exposes a pretty version of the date.
     */
    public String getPretty() {
        return prettyFormatter.get().format(this);
    }

    /**
     * This method exposes a parseable version of the date.
     */
    public String getParseable() {
        return parseableFormatter.get().format(this);
    }
}
