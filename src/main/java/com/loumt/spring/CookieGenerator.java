/**
 * Copyright (c) www.bugull.com
 */
package com.loumt.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * USED TO:
 * Log File:
 *
 * @author loumt(loumt@sanlogic.com)
 * @project Common-Util
 * @package com.loumt.spring
 * @date 2017/5/24/024
 */
public class CookieGenerator {

    public static final String DEFAULT_COOKIE_PATH = "/";
    /** @deprecated */
    @Deprecated
    public static final int DEFAULT_COOKIE_MAX_AGE = 2147483647;
    protected final Log logger = LogFactory.getLog(this.getClass());
    private String cookieName;
    private String cookieDomain;
    private String cookiePath = "/";
    private Integer cookieMaxAge = null;
    private boolean cookieSecure = false;

    public CookieGenerator() {
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getCookieName() {
        return this.cookieName;
    }

    public void setCookieDomain(String cookieDomain) {
        this.cookieDomain = cookieDomain;
    }

    public String getCookieDomain() {
        return this.cookieDomain;
    }

    public void setCookiePath(String cookiePath) {
        this.cookiePath = cookiePath;
    }

    public String getCookiePath() {
        return this.cookiePath;
    }

    public void setCookieMaxAge(Integer cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    public Integer getCookieMaxAge() {
        return this.cookieMaxAge;
    }

    public void setCookieSecure(boolean cookieSecure) {
        this.cookieSecure = cookieSecure;
    }

    public boolean isCookieSecure() {
        return this.cookieSecure;
    }

    public void addCookie(HttpServletResponse response, String cookieValue) {
        Cookie cookie = this.createCookie(cookieValue);
        Integer maxAge = this.getCookieMaxAge();
        if(maxAge != null) {
            cookie.setMaxAge(maxAge.intValue());
        }

        if(this.isCookieSecure()) {
            cookie.setSecure(true);
        }

        response.addCookie(cookie);
        if(this.logger.isDebugEnabled()) {
            this.logger.debug("Added cookie with name [" + this.getCookieName() + "] and value [" + cookieValue + "]");
        }

    }

    public void removeCookie(HttpServletResponse response) {
        Cookie cookie = this.createCookie("");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        if(this.logger.isDebugEnabled()) {
            this.logger.debug("Removed cookie with name [" + this.getCookieName() + "]");
        }

    }

    protected Cookie createCookie(String cookieValue) {
        Cookie cookie = new Cookie(this.getCookieName(), cookieValue);
        if(this.getCookieDomain() != null) {
            cookie.setDomain(this.getCookieDomain());
        }

        cookie.setPath(this.getCookiePath());
        return cookie;
    }


}
