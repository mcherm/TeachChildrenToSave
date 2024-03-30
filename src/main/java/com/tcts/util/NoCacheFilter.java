package com.tcts.util;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * This is a servlet filter that adds the appropriate headers to the HTTP response
 * to let intermediate systems know not to cache the responses. These are added
 * to ALL responses -- the filter is only invoked for calls that go through Spring,
 * not for calls to get resources like images or css files, and we can't allow
 * caching for calls to "/" because we apparently forward through that for each
 * and every call we make.
 */
public class NoCacheFilter implements Filter {


    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        if (req instanceof HttpServletRequest && res instanceof  HttpServletResponse) {
            HttpServletRequest httpReq = (HttpServletRequest) req;
            HttpServletResponse httpRes = (HttpServletResponse) res;
            httpRes.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            httpRes.setHeader("Pragma", "no-cache");
            httpRes.setHeader("Expires", "0");
        }
        filterChain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
