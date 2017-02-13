package com.tcts.util;

/**
 * A collection of functions for use in jsp pages that are intended for use in the Teach Children to Save
 * project.
 */
public class TCTSTaglib {

    /**
     * Escapes the string <code>input</code> for use in a Javascript string, assuming
     * that the result will be put through the normal &lt;c:out&gt; filter. Basically
     * just replaces newlines with spaces.
     */
    public static String forJavascriptString(String input) {
        String value = input.replace("\r\n", " ");
        value = value.replace("\n", " ");
        return value;
    }
}
