package com.tcts.common;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

/**
 * This instance will be created and auto-wired into places that
 * need to access the configuration properties.
 */
@Component
public class SitesConfig extends Properties {

    /**
     * Initialize it in the constructor.
     */
    public SitesConfig() {
        try {
            this.load(getClass().getClassLoader().getResourceAsStream("sites.properties"));
        } catch(NullPointerException err) {
            throw new RuntimeException("Cannot read sites file at startup.", err);
        } catch(IOException err) {
            throw new RuntimeException("Cannot read sites file at startup.", err);
        }
    }
}

