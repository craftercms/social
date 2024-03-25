package org.craftercms.social.client.filters.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.cookie.SM;

public class ProxyHttpServletRequest extends HttpServletRequestWrapper {
    private final DelegateServletInputStream wrapServletInput;
    private List<Cookie> cookieList;
    private String cookieProxyFilter;
    private int contentLength;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public ProxyHttpServletRequest(final HttpServletRequest request, final String cookieProxyFilter) throws
        IOException {
        super(request);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(super.getInputStream(), out);

        if(out.size() <= 0 &&
                (null != request.getHeader("content-type")) &&
                request.getHeader("content-type").toLowerCase().contains("application/x-www-form-urlencoded")){

            StringBuilder strBuilder = new StringBuilder();
            Enumeration<String> parameters = request.getParameterNames();
            while (parameters.hasMoreElements()){
                String parameter = parameters.nextElement();
                String value = request.getParameter(parameter);
                if(StringUtils.isNotBlank(value)){
                    strBuilder.append(URLEncoder.encode(parameter, "UTF-8")).append("=").append(URLEncoder.encode(request.getParameter(parameter), "UTF-8"));
                    if(parameters.hasMoreElements()){
                        strBuilder.append("&");
                    }
                }
            }
            strBuilder.trimToSize();

            wrapServletInput = new DelegateServletInputStream(IOUtils.toInputStream(strBuilder.toString()));
            contentLength = wrapServletInput.available();

        } else {
            contentLength = out.size();
            wrapServletInput = new DelegateServletInputStream(new ByteArrayInputStream(out.toByteArray()));
        }

        if (request.getCookies() != null && request.getCookies().length > 0) {
            cookieList = new ArrayList<Cookie>(Arrays.asList(request.getCookies()));
        } else {
            cookieList = new ArrayList<Cookie>();
        }
        this.cookieProxyFilter = cookieProxyFilter;
    }

    @Override
    public int getContentLength() {
        return contentLength;
    }

    @Override
    public Cookie[] getCookies() {
        return cookieList.toArray(new Cookie[cookieList.size()]);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return wrapServletInput;
    }


    @Override
    public Enumeration getHeaderNames() {
        Vector vector = new Vector();
        final Enumeration org = super.getHeaderNames();
        while (org.hasMoreElements()) {
            vector.add(org.nextElement());
        }
        if (!cookieList.isEmpty()) {
            vector.add(SM.COOKIE);
        }
        return vector.elements();
    }

    @Override
    public Enumeration getHeaders(final String name) {
        if (name.equalsIgnoreCase(org.apache.http.cookie.SM.COOKIE)) {
            Iterator<Cookie> cookieIterator = cookieList.iterator();
            StringBuffer buffer = new StringBuffer("");
            while (cookieIterator.hasNext()) {
                final Cookie cookie = cookieIterator.next();
                final String cookieName;

                if (!cookie.getName().startsWith(cookieProxyFilter)) {
                    cookieName = cookieProxyFilter + cookie.getName();
                } else {
                    cookieName = cookie.getName();
                }
                buffer.append(cookieName);
                buffer.append("=");
                buffer.append(cookie.getValue());
                if (cookieIterator.hasNext()) {
                    buffer.append("; ");
                }
            }
            final Vector tmp = new Vector();
            tmp.add(buffer.toString());
            return tmp.elements();
        } else {
            return super.getHeaders(name);
        }
    }

    public void addCookie(final Cookie cookie) {
        cookieList.add(cookie);
    }
}
