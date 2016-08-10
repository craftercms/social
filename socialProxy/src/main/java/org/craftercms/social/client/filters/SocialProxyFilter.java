package org.craftercms.social.client.filters;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.ContentType;
import org.craftercms.social.client.filters.support.ProfileNotFound;
import org.craftercms.social.client.filters.support.ProxyHttpServletRequest;
import org.mitre.dsmiley.httpproxy.ProxyServlet;

public abstract class SocialProxyFilter extends ProxyServlet implements Filter {

    public static final String CRAFTER_PROFILE_AUTH = "Crafter-Profile-AUTH";
    private ServletContext servletContext;
    private FilterConfig filterConfig;
    protected String proxyFilterPrefix;
    protected ProfileClient client;
    protected String cookieTicketName;

    public void init(final FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
        this.filterConfig = filterConfig;
    }

    public abstract Map<String, Object> getUserProfile(HttpServletRequest request, HttpServletResponse response);

    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest httpServletRequest = new ProxyHttpServletRequest((HttpServletRequest)request,
            getCookieNamePrefix());
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;

        try {
            checkIfUserIsAuth((ProxyHttpServletRequest)httpServletRequest, httpServletResponse);
            this.service(httpServletRequest, response);
            if (httpServletRequest.getAttribute(CRAFTER_PROFILE_AUTH) != null) {
                httpServletRequest.getInputStream().reset();
                final Map<String, Object> useInformation = getUserProfile(httpServletRequest, (HttpServletResponse)
                    response);
                if (useInformation != null) {
                    // User Should Exist and be Auth here, only possible  way to be here is user not in the context
                    doCrafterProfileAuth(useInformation, (ProxyHttpServletRequest)httpServletRequest,
                        (HttpServletResponse)response);
                    this.service(httpServletRequest, response);
                    httpServletRequest.setAttribute(CRAFTER_PROFILE_AUTH, null);
                } else {
                    httpServletResponse.sendError(401);
                }
            }
        } catch (Exception e) {
            if (!httpServletResponse.isCommitted()) {
                httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                httpServletResponse.setHeader("content-type", ContentType.APPLICATION_JSON.withCharset("utf-8")
                    .toString());
                response.getWriter().write("{\"Error\":\"Unable to create or retrieve user with given id\"," +
                    "\"ex\":\"" + e.toString() + "\"}");
            }
            e.printStackTrace();
        } finally {
            httpServletRequest.getInputStream().close();
        }
    }

    protected void checkIfUserIsAuth(final ProxyHttpServletRequest httpServletRequest, final HttpServletResponse
        httpServletResponse) throws Exception {
        final String authCookieValue = ticketCookieExist(httpServletRequest);
        if (StringUtils.isBlank(authCookieValue)) {
            final Map<String, Object> profile = getUserProfile(httpServletRequest, httpServletResponse);
            if (profile != null) {
                doCrafterProfileAuth(profile, httpServletRequest, httpServletResponse);
            }
        } else {
            validateAuthCookie(authCookieValue, httpServletRequest, httpServletResponse);
        }

    }

    protected void validateAuthCookie(final String authCookieValue, final ProxyHttpServletRequest httpServletRequest,
                                      final HttpServletResponse httpServletResponse) throws Exception {
        if (!client.isTicketValid(authCookieValue)) {
            final Map<String, Object> profile = getUserProfile(httpServletRequest, httpServletResponse);
            doCrafterProfileAuth(profile, httpServletRequest, httpServletResponse);
        }
    }

    protected String ticketCookieExist(final ProxyHttpServletRequest httpServletRequest) {
        for (Cookie cookie : httpServletRequest.getCookies()) {
            if (cookie.getName().equals(getCookieNamePrefix() + cookieTicketName)) {
                return cookie.getValue();
            }
        }
        return null;
    }


    protected Map<String, Object> doCrafterProfileAuth(final Map<String, Object> userInformation, final
    ProxyHttpServletRequest request, final HttpServletResponse response) throws Exception {
        Map<String, Object> profile = null;
        try {
            profile = client.getProfile((String)userInformation.get(ProfileClient.USER_USERNAME));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ProfileNotFound ex) {
            profile = client.createProfile(request, userInformation);
        }
        if (profile == null) {
            response.reset();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setHeader("content-type", ContentType.APPLICATION_JSON.withCharset("utf-8").toString());
            response.getWriter().write("{\"Error\":\"Unable to create or retrieve user with given id\"}");
            response.getWriter().flush();
            throw new Exception("Unable to create or retrieve user");
        }
        checkProfileAndCurrentContext((Map<String, Object>)userInformation.get(ProfileClient.PARAM_ATTRIBUTES),
            profile);
        Map<String, Object> ticket = client.auth((String)profile.get(ProfileClient.USER_PROFILE_ID));
        Cookie authTicket = new Cookie(getCookieNamePrefix() + "ticket", (String)ticket.get(ProfileClient
            .USER_PROFILE_ID));
        authTicket.setMaxAge(-1);// 1 day
        authTicket.setPath("/");
        authTicket.setComment("Auth for social, created by SocialProxyFilter");
        response.addCookie(authTicket);
        request.addCookie(authTicket);
            return profile;
    }

    private void checkProfileAndCurrentContext(final Map<String, Object> userParams, final Map<String, Object>
        profile) throws IOException {
        Map<String, Object> currentAttributes = (Map<String, Object>)profile.get(ProfileClient.PARAM_ATTRIBUTES);
        List<Map<String, Object>> currentSocialContexts = (List<Map<String, Object>>)currentAttributes.get
            ("socialContexts");

        Map<String, Object> givenSocialContext = ((List<Map<String, Object>>)userParams.get("socialContexts")).get(0);

        boolean foundContext = false;
        for (Map<String, Object> currentSocialContext : currentSocialContexts) {
            if (currentSocialContext.containsValue(givenSocialContext.get("id"))) {
                foundContext = true;
                break;
            }
        }
        if (!foundContext) {
            currentSocialContexts.add(givenSocialContext);
            currentAttributes.put("socialContexts", currentSocialContexts);
            client.updateAttributes((String)profile.get(ProfileClient.USER_PROFILE_ID),currentAttributes);
        }
    }

    protected boolean doResponseRedirectOrNotModifiedLogic(HttpServletRequest servletRequest, HttpServletResponse
        servletResponse, HttpResponse proxyResponse, int statusCode) throws ServletException, IOException {
        // Check if the proxy response is a redirect
        // The following code is adapted from org.tigris.noodle.filters.CheckForRedirect
        if (statusCode >= HttpServletResponse.SC_MULTIPLE_CHOICES /* 300 */ && statusCode < HttpServletResponse
            .SC_NOT_MODIFIED /* 304 */) {
            Header locationHeader = proxyResponse.getLastHeader(HttpHeaders.LOCATION);
            if (locationHeader == null) {
                throw new ServletException("Received status code: " + statusCode + " but no " + HttpHeaders.LOCATION
                    + " header was found in the response");
            }
            // Modify the redirect to go to this proxy servlet rather that the proxied host
            String locStr = rewriteUrlFromResponse(servletRequest, locationHeader.getValue());

            servletResponse.sendRedirect(locStr);
            return true;
        }
        // 304 needs special handling.  See:
        // http://www.ics.uci.edu/pub/ietf/http/rfc1945.html#Code304
        // We get a 304 whenever passed an 'If-Modified-Since'
        // header and the data on disk has not changed; server
        // responds w/ a 304 saying I'm not going to send the
        // body because the file has not changed.
        if (statusCode == HttpServletResponse.SC_NOT_MODIFIED) {
            servletResponse.setIntHeader(HttpHeaders.CONTENT_LENGTH, 0);
            servletResponse.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return true;
        }
        if (statusCode == HttpServletResponse.SC_FORBIDDEN || statusCode == HttpServletResponse.SC_UNAUTHORIZED ||
            (statusCode==500 && isUserInContextError(proxyResponse.getEntity()))) {
            servletRequest.setAttribute(CRAFTER_PROFILE_AUTH, true);
            return true;
        }

        return false;
    }


    private boolean isUserInContextError(final HttpEntity entity) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        entity.writeTo(out);
        return new String(out.toByteArray(), Charset.forName("UTF-8")).contains("Current profile is not assign to the"
            + " given currentContext");
    }


    public void setSocialUrl(final String socialUrl) {
        super.targetUri = socialUrl;

    }

    /**
     * Reads the request URI from {@code servletRequest} and rewrites it, considering targetUri.
     * It's used to make the new request.
     */
    protected String rewriteUrlFromRequest(HttpServletRequest servletRequest) {
        StringBuilder uri = new StringBuilder(500);
        uri.append(getTargetUri(servletRequest));
        // Handle the path given to the servlet
        if (servletRequest.getRequestURI() != null) {//ex: /my/path.html
            uri.append(ProxyServlet.encodeUriQuery(servletRequest.getRequestURI().replace(proxyFilterPrefix, "")));
        }
        // Handle the query string & fragment
        String queryString = servletRequest.getQueryString();//ex:(following '?'): name=value&foo=bar#fragment
        String fragment = null;
        //split off fragment from queryString, updating queryString if found
        if (queryString != null) {
            int fragIdx = queryString.indexOf('#');
            if (fragIdx >= 0) {
                fragment = queryString.substring(fragIdx + 1);
                queryString = queryString.substring(0, fragIdx);
            }
        }

        queryString = rewriteQueryStringFromRequest(servletRequest, queryString);
        if (queryString != null && queryString.length() > 0) {
            uri.append('?');
            uri.append(ProxyServlet.encodeUriQuery(queryString));
        }

        if (doSendUrlFragment && fragment != null) {
            uri.append('#');
            uri.append(ProxyServlet.encodeUriQuery(fragment));
        }
        return uri.toString();
    }

    protected void initTarget() {
        if (targetUri == null) {
            throw new IllegalArgumentException(ProxyServlet.P_TARGET_URI + " is required.");
        }
        //test it's valid
        try {
            targetUriObj = new URI(targetUri);
        } catch (Exception e) {
            throw new IllegalArgumentException("Trying to process targetUri init parameter: " + e, e);
        }
        targetHost = URIUtils.extractHost(targetUriObj);
    }


    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }


    /**
     * The string prefixing rewritten cookies.
     */
    protected String getCookieNamePrefix() {
        return "!Proxy!" + filterConfig.getFilterName();
    }

    /**
     * Reads a configuration parameter. By default it reads servlet init parameters but
     * it can be overridden.
     */
    protected String getConfigParam(String key) {
        return null;
    }

    public void setProxyFilterPrefix(final String proxyFilterPrefix) {
        this.proxyFilterPrefix = proxyFilterPrefix;
    }


    public void setClient(final ProfileClient client) {
        this.client = client;
    }

    public void setCookieTicketName(final String cookieTicketName) {
        this.cookieTicketName = cookieTicketName;
    }
}
