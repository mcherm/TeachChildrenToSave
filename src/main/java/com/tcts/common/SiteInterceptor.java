package com.tcts.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * A Spring MVC interceptor that runs before every request and sets the "site"
 * request attribute (e.g., "DE" or "FL") so that all JSPs can use ${site}
 * without each controller having to set it explicitly.
 */
public class SiteInterceptor implements HandlerInterceptor {

    @Autowired
    private SitesConfig sitesConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("site", sitesConfig.getSite(request));
        return true;
    }

}
