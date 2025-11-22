package com.tcts.common;

import com.tcts.exception.UnknownSiteException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;


/**
 * This instance will be created and auto-wired into places that
 * need to access the configuration properties.
 */
@Component
public class SitesConfig {
    final private Properties properties;

    /**
     * Initialize it in the constructor.
     */
    public SitesConfig() {
        final Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("sites.properties"));
        } catch(NullPointerException err) {
            throw new RuntimeException("Cannot read sites file at startup.", err);
        } catch(IOException err) {
            throw new RuntimeException("Cannot read sites file at startup.", err);
        }
        this.properties = properties;
    }

    /**
     * Given an HttpServletRequest, this returns a string indicating which site the
     * user is on (eg: "DE" or "FL"). If the server name is not recognized, then this
     * will throw an UnknownSiteException instead.
     *
     * @param request the current HTTP request
     * @return the site (eg: "DE" or "FL").
     * @throws UnknownSiteException if the server name is not recognized as being any particular site
     */
    public String getSite(HttpServletRequest request) throws UnknownSiteException {
        final String serverName = request.getServerName();
        final String site = this.properties.getProperty(serverName.toLowerCase(Locale.ENGLISH));
        if (site == null) {
            throw new UnknownSiteException(serverName);
        }
        return site;
    }

    /**
     * With no inputs, this returns a string indicating which site the user is on
     * (eg: "DE" or "FL"). If the server name is not recognized, then this will throw
     * an UnknownSiteException instead. The getSite() that takes an HttpServletRequest
     * is slightly faster if that value is available.
     *
     * @return the site (eg: "DE" or "FL").
     * @throws UnknownSiteException if the server name is not recognized as being any particular site
     */
    public String getSite() throws UnknownSiteException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null){
            throw new RuntimeException(
                    "Attempting to determine the site when there is not an active request. This cannot work because " +
                    "without a request we cannot determine which site to use.");
        }
        HttpServletRequest request = attributes.getRequest();
        return getSite(request);
    }
}

