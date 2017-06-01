/**
 * Copyright (c) www.bugull.com
 */
package com.loumt.spring;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public abstract class WebUtils {

    public static final String INCLUDE_REQUEST_URI_ATTRIBUTE = "javax.servlet.include.request_uri";
    public static final String INCLUDE_CONTEXT_PATH_ATTRIBUTE = "javax.servlet.include.context_path";
    public static final String INCLUDE_SERVLET_PATH_ATTRIBUTE = "javax.servlet.include.servlet_path";
    public static final String INCLUDE_PATH_INFO_ATTRIBUTE = "javax.servlet.include.path_info";
    public static final String INCLUDE_QUERY_STRING_ATTRIBUTE = "javax.servlet.include.query_string";
    public static final String FORWARD_REQUEST_URI_ATTRIBUTE = "javax.servlet.forward.request_uri";
    public static final String FORWARD_CONTEXT_PATH_ATTRIBUTE = "javax.servlet.forward.context_path";
    public static final String FORWARD_SERVLET_PATH_ATTRIBUTE = "javax.servlet.forward.servlet_path";
    public static final String FORWARD_PATH_INFO_ATTRIBUTE = "javax.servlet.forward.path_info";
    public static final String FORWARD_QUERY_STRING_ATTRIBUTE = "javax.servlet.forward.query_string";
    public static final String ERROR_STATUS_CODE_ATTRIBUTE = "javax.servlet.error.status_code";
    public static final String ERROR_EXCEPTION_TYPE_ATTRIBUTE = "javax.servlet.error.exception_type";
    public static final String ERROR_MESSAGE_ATTRIBUTE = "javax.servlet.error.message";
    public static final String ERROR_EXCEPTION_ATTRIBUTE = "javax.servlet.error.exception";
    public static final String ERROR_REQUEST_URI_ATTRIBUTE = "javax.servlet.error.request_uri";
    public static final String ERROR_SERVLET_NAME_ATTRIBUTE = "javax.servlet.error.servlet_name";
    public static final String CONTENT_TYPE_CHARSET_PREFIX = ";charset=";
    public static final String DEFAULT_CHARACTER_ENCODING = "ISO-8859-1";
    public static final String TEMP_DIR_CONTEXT_ATTRIBUTE = "javax.servlet.context.tempdir";
    public static final String HTML_ESCAPE_CONTEXT_PARAM = "defaultHtmlEscape";
    public static final String WEB_APP_ROOT_KEY_PARAM = "webAppRootKey";
    public static final String DEFAULT_WEB_APP_ROOT_KEY = "webapp.root";
    public static final String[] SUBMIT_IMAGE_SUFFIXES = new String[]{".x", ".y"};
    public static final String SESSION_MUTEX_ATTRIBUTE = WebUtils.class.getName() + ".MUTEX";

    public WebUtils() {
    }

    public static void setWebAppRootSystemProperty(ServletContext servletContext) throws IllegalStateException {
        Assert.notNull(servletContext, "ServletContext must not be null");
        String root = servletContext.getRealPath("/");
        if (root == null) {
            throw new IllegalStateException("Cannot set web app root system property when WAR file is not expanded");
        } else {
            String param = servletContext.getInitParameter("webAppRootKey");
            String key = param != null ? param : "webapp.root";
            String oldValue = System.getProperty(key);
            if (oldValue != null && !StringUtils.pathEquals(oldValue, root)) {
                throw new IllegalStateException("Web app root system property already set to different value: \'" + key + "\' = [" + oldValue + "] instead of [" + root + "] - " + "Choose unique values for the \'webAppRootKey\' context-param in your web.xml files!");
            } else {
                System.setProperty(key, root);
                servletContext.log("Set web app root system property: \'" + key + "\' = [" + root + "]");
            }
        }
    }

    public static void removeWebAppRootSystemProperty(ServletContext servletContext) {
        Assert.notNull(servletContext, "ServletContext must not be null");
        String param = servletContext.getInitParameter("webAppRootKey");
        String key = param != null ? param : "webapp.root";
        System.getProperties().remove(key);
    }

    public static boolean isDefaultHtmlEscape(ServletContext servletContext) {
        if (servletContext == null) {
            return false;
        } else {
            String param = servletContext.getInitParameter("defaultHtmlEscape");
            return Boolean.valueOf(param).booleanValue();
        }
    }

    public static Boolean getDefaultHtmlEscape(ServletContext servletContext) {
        if (servletContext == null) {
            return null;
        } else {
            Assert.notNull(servletContext, "ServletContext must not be null");
            String param = servletContext.getInitParameter("defaultHtmlEscape");
            return StringUtils.hasText(param) ? Boolean.valueOf(param) : null;
        }
    }

    public static File getTempDir(ServletContext servletContext) {
        Assert.notNull(servletContext, "ServletContext must not be null");
        return (File) servletContext.getAttribute("javax.servlet.context.tempdir");
    }

    public static String getRealPath(ServletContext servletContext, String path) throws FileNotFoundException {
        Assert.notNull(servletContext, "ServletContext must not be null");
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        String realPath = servletContext.getRealPath(path);
        if (realPath == null) {
            throw new FileNotFoundException("ServletContext resource [" + path + "] cannot be resolved to absolute file path - " + "web application archive not expanded?");
        } else {
            return realPath;
        }
    }

    public static String getSessionId(HttpServletRequest request) {
        Assert.notNull(request, "Request must not be null");
        HttpSession session = request.getSession(false);
        return session != null ? session.getId() : null;
    }

    public static Object getSessionAttribute(HttpServletRequest request, String name) {
        Assert.notNull(request, "Request must not be null");
        HttpSession session = request.getSession(false);
        return session != null ? session.getAttribute(name) : null;
    }

    public static Object getRequiredSessionAttribute(HttpServletRequest request, String name) throws IllegalStateException {
        Object attr = getSessionAttribute(request, name);
        if (attr == null) {
            throw new IllegalStateException("No session attribute \'" + name + "\' found");
        } else {
            return attr;
        }
    }

    public static void setSessionAttribute(HttpServletRequest request, String name, Object value) {
        Assert.notNull(request, "Request must not be null");
        if (value != null) {
            request.getSession().setAttribute(name, value);
        } else {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(name);
            }
        }

    }

    public static Object getOrCreateSessionAttribute(HttpSession session, String name, Class clazz) throws IllegalArgumentException {
        Assert.notNull(session, "Session must not be null");
        Object sessionObject = session.getAttribute(name);
        if (sessionObject == null) {
            try {
                sessionObject = clazz.newInstance();
            } catch (InstantiationException var5) {
                throw new IllegalArgumentException("Could not instantiate class [" + clazz.getName() + "] for session attribute \'" + name + "\': " + var5.getMessage());
            } catch (IllegalAccessException var6) {
                throw new IllegalArgumentException("Could not access default constructor of class [" + clazz.getName() + "] for session attribute \'" + name + "\': " + var6.getMessage());
            }

            session.setAttribute(name, sessionObject);
        }

        return sessionObject;
    }

    public static Object getSessionMutex(HttpSession session) {
        Assert.notNull(session, "Session must not be null");
        Object mutex = session.getAttribute(SESSION_MUTEX_ATTRIBUTE);
        if (mutex == null) {
            mutex = session;
        }

        return mutex;
    }


    public static boolean isIncludeRequest(ServletRequest request) {
        return request.getAttribute("javax.servlet.include.request_uri") != null;
    }

    public static void exposeForwardRequestAttributes(HttpServletRequest request) {
        exposeRequestAttributeIfNotPresent(request, "javax.servlet.forward.request_uri", request.getRequestURI());
        exposeRequestAttributeIfNotPresent(request, "javax.servlet.forward.context_path", request.getContextPath());
        exposeRequestAttributeIfNotPresent(request, "javax.servlet.forward.servlet_path", request.getServletPath());
        exposeRequestAttributeIfNotPresent(request, "javax.servlet.forward.path_info", request.getPathInfo());
        exposeRequestAttributeIfNotPresent(request, "javax.servlet.forward.query_string", request.getQueryString());
    }

    public static void exposeErrorRequestAttributes(HttpServletRequest request, Throwable ex, String servletName) {
        exposeRequestAttributeIfNotPresent(request, "javax.servlet.error.status_code", Integer.valueOf(200));
        exposeRequestAttributeIfNotPresent(request, "javax.servlet.error.exception_type", ex.getClass());
        exposeRequestAttributeIfNotPresent(request, "javax.servlet.error.message", ex.getMessage());
        exposeRequestAttributeIfNotPresent(request, "javax.servlet.error.exception", ex);
        exposeRequestAttributeIfNotPresent(request, "javax.servlet.error.request_uri", request.getRequestURI());
        exposeRequestAttributeIfNotPresent(request, "javax.servlet.error.servlet_name", servletName);
    }

    private static void exposeRequestAttributeIfNotPresent(ServletRequest request, String name, Object value) {
        if (request.getAttribute(name) == null) {
            request.setAttribute(name, value);
        }

    }

    public static void clearErrorRequestAttributes(HttpServletRequest request) {
        request.removeAttribute("javax.servlet.error.status_code");
        request.removeAttribute("javax.servlet.error.exception_type");
        request.removeAttribute("javax.servlet.error.message");
        request.removeAttribute("javax.servlet.error.exception");
        request.removeAttribute("javax.servlet.error.request_uri");
        request.removeAttribute("javax.servlet.error.servlet_name");
    }

    public static void exposeRequestAttributes(ServletRequest request, Map<String, ?> attributes) {
        Assert.notNull(request, "Request must not be null");
        Assert.notNull(attributes, "Attributes Map must not be null");
        Iterator var3 = attributes.entrySet().iterator();

        while (var3.hasNext()) {
            Map.Entry entry = (Map.Entry) var3.next();
            request.setAttribute((String) entry.getKey(), entry.getValue());
        }

    }

    public static Cookie getCookie(HttpServletRequest request, String name) {
        Assert.notNull(request, "Request must not be null");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Cookie[] var6 = cookies;
            int var5 = cookies.length;

            for (int var4 = 0; var4 < var5; ++var4) {
                Cookie cookie = var6[var4];
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }

        return null;
    }

    public static boolean hasSubmitParameter(ServletRequest request, String name) {
        Assert.notNull(request, "Request must not be null");
        if (request.getParameter(name) != null) {
            return true;
        } else {
            String[] var5 = SUBMIT_IMAGE_SUFFIXES;
            int var4 = SUBMIT_IMAGE_SUFFIXES.length;

            for (int var3 = 0; var3 < var4; ++var3) {
                String suffix = var5[var3];
                if (request.getParameter(name + suffix) != null) {
                    return true;
                }
            }

            return false;
        }
    }

    public static String findParameterValue(ServletRequest request, String name) {
        return findParameterValue(request.getParameterMap(), name);
    }

    public static String findParameterValue(Map<String, ?> parameters, String name) {
        Object value = parameters.get(name);
        if (value instanceof String[]) {
            String[] var10 = (String[]) value;
            return var10.length > 0 ? var10[0] : null;
        } else if (value != null) {
            return value.toString();
        } else {
            String prefix = name + "_";
            Iterator var5 = parameters.keySet().iterator();

            String paramName;
            do {
                if (!var5.hasNext()) {
                    return null;
                }

                paramName = (String) var5.next();
            } while (!paramName.startsWith(prefix));

            String[] var9 = SUBMIT_IMAGE_SUFFIXES;
            int var8 = SUBMIT_IMAGE_SUFFIXES.length;

            for (int var7 = 0; var7 < var8; ++var7) {
                String suffix = var9[var7];
                if (paramName.endsWith(suffix)) {
                    return paramName.substring(prefix.length(), paramName.length() - suffix.length());
                }
            }

            return paramName.substring(prefix.length());
        }
    }

    public static Map<String, Object> getParametersStartingWith(ServletRequest request, String prefix) {
        Assert.notNull(request, "Request must not be null");
        Enumeration paramNames = request.getParameterNames();
        TreeMap params = new TreeMap();
        if (prefix == null) {
            prefix = "";
        }

        while (paramNames != null && paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if ("".equals(prefix) || paramName.startsWith(prefix)) {
                String unprefixed = paramName.substring(prefix.length());
                String[] values = request.getParameterValues(paramName);
                if (values != null && values.length != 0) {
                    if (values.length > 1) {
                        params.put(unprefixed, values);
                    } else {
                        params.put(unprefixed, values[0]);
                    }
                }
            }
        }

        return params;
    }

    public static int getTargetPage(ServletRequest request, String paramPrefix, int currentPage) {
        Enumeration paramNames = request.getParameterNames();

        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            if (paramName.startsWith(paramPrefix)) {
                for (int i = 0; i < SUBMIT_IMAGE_SUFFIXES.length; ++i) {
                    String suffix = SUBMIT_IMAGE_SUFFIXES[i];
                    if (paramName.endsWith(suffix)) {
                        paramName = paramName.substring(0, paramName.length() - suffix.length());
                    }
                }

                return Integer.parseInt(paramName.substring(paramPrefix.length()));
            }
        }

        return currentPage;
    }

    public static String extractFilenameFromUrlPath(String urlPath) {
        String filename = extractFullFilenameFromUrlPath(urlPath);
        int dotIndex = filename.lastIndexOf(46);
        if (dotIndex != -1) {
            filename = filename.substring(0, dotIndex);
        }

        return filename;
    }

    public static String extractFullFilenameFromUrlPath(String urlPath) {
        int end = urlPath.indexOf(59);
        if (end == -1) {
            end = urlPath.indexOf(63);
            if (end == -1) {
                end = urlPath.length();
            }
        }

        int begin = urlPath.lastIndexOf(47, end) + 1;
        return urlPath.substring(begin, end);
    }
}
