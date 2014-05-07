package org.craftercms.social.ext.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.profile.api.Profile;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Simple way to check for security information.
 * Also create the security Context
 */
public class AuthenticationTokenProcessingFilter extends GenericFilterBean {

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
                         final FilterChain chain) throws IOException, ServletException {
        Map<String, String[]> parms = request.getParameterMap();
        if (parms.containsKey("token")) {
            String token = parms.get("token")[0]; // grab the first "token" parameter
            // validate the token
            if (!StringUtils.isWhitespace(token) && validatedToken(token)) {
                // determine the user based on the (already validated) token
                Profile profile = tokenToProfile(token);
                SecurityContextHolder.getContext().setAuthentication(authenticate(profile));
            }
        }
        // continue thru the filter chain
        chain.doFilter(request, response);
    }


    private boolean validatedToken(final String token) {
        return true;
    }

    private Profile tokenToProfile(final String token) {
        return null;
    }

    private Authentication authenticate(final Profile profile) {
        final List<GrantedAuthority> authorityList = new ArrayList<>();
        for (String s : profile.getRoles()) {
            authorityList.add(new SimpleGrantedAuthority(s));
        }
        return new CrafterSocialAuthentication(profile,authorityList);
    }


    class CrafterSocialAuthentication extends AbstractAuthenticationToken {
        private static final long serialVersionUID = -2241160708762193090L;
        private final Profile profile;

        /**
         * Creates a token with the supplied array of authorities.
         *
         * @param authorities the collection of <tt>GrantedAuthority</tt>s for the
         *                    principal represented by this authentication object.
         */
        public CrafterSocialAuthentication(final Profile profile,final Collection<? extends GrantedAuthority>
            authorities) {
            super(authorities);
            this.profile=profile;
        }

        @Override
        public Object getCredentials() {
            return "";
        }

        @Override
        public Object getPrincipal() {
            return profile;
        }
    }


}

