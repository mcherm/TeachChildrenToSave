package com.tcts.common;

import java.text.ParseException;
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

    /**
     * As input, takes a date in yyyy-mm-dd format; returns the PrettyPrintingDate object.
     */
    public static PrettyPrintingDate fromParsableDate(String parsableDate) throws ParseException {
        return new PrettyPrintingDate(parseableFormatter.get().parse(parsableDate).getTime());
    }

    public static PrettyPrintingDate fromJavaUtilDate(java.util.Date date) {
        return new PrettyPrintingDate(date.getTime());
    }

    /**
     * Constructor used by the fromParsableDate() factory function.
     *
     * @param millisSinceTheEpoch the milliseconds since the epoch of the date
     */
    private PrettyPrintingDate(long millisSinceTheEpoch) {
        super(millisSinceTheEpoch);
    }

    /**
     * Constructor used by our JDBC driver.
     */
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
