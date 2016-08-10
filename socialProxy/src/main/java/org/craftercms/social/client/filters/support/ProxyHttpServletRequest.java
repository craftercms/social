package org.craftercms.social.client.filters.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.cookie.SM;

public class ProxyHttpServletRequest extends HttpServletRequestWrapper {
    private final DelegateServletInputStream wrapServletInput;
    private List<Cookie> cookieList;
    private String cookieProxyFilter;

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
        wrapServletInput = new DelegateServletInputStream(new ByteArrayInputStream(out.toByteArray()));
        if (request.getCookies() != null && request.getCookies().length > 0) {
            cookieList = new ArrayList<Cookie>(Arrays.asList(request.getCookies()));
        } else {
            cookieList = new ArrayList<Cookie>();
        }
        this.cookieProxyFilter = cookieProxyFilter;
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
