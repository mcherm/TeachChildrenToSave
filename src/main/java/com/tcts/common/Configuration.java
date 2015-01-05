package com.tcts.common;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

/**
 * This instance will be created and auto-wired into places that
 * need to access the configuration properties.
 */
@Component
public class Configuration extends Properties {

    /**
     * Initialize it in the constructor.
     */
    public Configuration() {
        try {
            this.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch(IOException err) {
            throw new RuntimeException("Cannot read properties file at startup.", err);
        }
    }
}
