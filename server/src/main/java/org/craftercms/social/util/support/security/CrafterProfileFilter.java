/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.social.util.support.security;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.craftercms.profile.constants.ProfileConstants;
import org.craftercms.profile.domain.Profile;
import org.craftercms.profile.domain.Tenant;
import org.craftercms.social.util.support.CrafterProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.multipart.MultipartResolver;
import org.craftercms.social.util.support.security.crypto.SimpleDesCipher;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

public class CrafterProfileFilter extends GenericFilterBean {
    public static final String CRAFTER_SOCIAL_COOKIE_PATH = "/crafter-social";

    //@Autowired

    // not autowiring this because we want the cache version
    private CrafterProfile profile;

    @Value("#{socialSettings['security.token.tokenRequestParamKey']}")
    private String tokenRequestParamKey;

    @Value("#{socialSettings['security.tenant.tenantRequestParamKey']}")
    private String tenantRequestParamKey;

    @Value("#{socialSettings['security.cipher.key']}")
    private String cipherkey;

    @Value("#{socialSettings['security.cipher.cipherTokenCookieKey']}")
    private String cipherTokenCookieKey;

    @Value("#{socialSettings['security.cipher.expires']}")
    private int cipherTokenExpires;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;
    
    private static final String ERROR_ENCRYPTED_TOKEN = "Error creating encrypted token";
    private static final String ERROR_DESCYPTING_TOKEN = "Error decrypting token";

    /* index of properties in cookie */
    private static final int TOKEN = 0;
    private static final int PROFILE_ID = 1;
    private static final int DATE = 2;
    private static final int ROLES = 3;
    private static final int TENANT_NAME = 4;

    public static final String TOKEN_SEPARATOR = "|";

    private static final Logger log = LoggerFactory.getLogger(CrafterProfileFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String token = getParamFromRequest(httpRequest, tokenRequestParamKey);
        String tenantName = getParamFromRequest(httpRequest, tenantRequestParamKey);

        try {
            // get encrypted token cookie with the user profile information
            String encryptedToken = getCipherTokenCookie(httpRequest);

            // get the cipher
            SimpleDesCipher cipher = new SimpleDesCipher(cipherkey);

            // try to use the encryptedToken first
            if (encryptedToken != null && !encryptedToken.isEmpty()) {

                // decrypt the cookie and read values from it
                String[] profileValues = getProfileValues(encryptedToken, cipher);

                String profileToken = profileValues[TOKEN];

                /*  Validate the token.  If the simple token & cookie token don't match,
                 *  the user may have changed, so use the basic ticket in this case
                 */
                if (profileToken.equals(token) && profile.validateUserToken(profileToken)) {

                    authenticateWithCipherToken(chain, httpRequest, httpResponse, tenantName, cipher, profileValues, profileToken);

                } else {

                    // try authenticate with the simple token, is token in cipher is no longer valid
                    authenticateWithSimpleToken(chain, httpRequest, httpResponse, token, tenantName, cipher);
                }


            /*  if no encrypted token, look for regular token & start with that
             *  this will always happen before the encrypted token
             */
            } else {

                authenticateWithSimpleToken(chain, httpRequest, httpResponse, token, tenantName, cipher);
            }

        } catch (org.craftercms.social.exceptions.AuthenticationException authExc) {
            failRequest(httpRequest, httpResponse, new BadCredentialsException(authExc.getMessage()));

        }
    }

    /**
     *
     * @param chain
     * @param httpRequest
     * @param httpResponse
     * @param tenantName
     * @param cipher
     * @param profileValues
     * @param profileToken
     * @throws IOException
     * @throws ServletException
     * @throws org.craftercms.social.exceptions.AuthenticationException
     */
    private void authenticateWithCipherToken(FilterChain chain, HttpServletRequest httpRequest, HttpServletResponse httpResponse,
                 String tenantName, SimpleDesCipher cipher, String[] profileValues, String profileToken) throws IOException, ServletException, org.craftercms.social.exceptions.AuthenticationException {
        // validate tenant, exception thrown for failure
        validateTenant(httpRequest.getServerName(), tenantName, profileValues[TENANT_NAME], profileValues[PROFILE_ID]);

        Profile userProfile = null;

        // validate the date
        Date date = null;
        try {
            date = DateFormat.getInstance().parse(profileValues[DATE]);
        } catch (ParseException e) {
            String error = "Error parsing date: '" + profileValues[DATE] + "' ";
            log.error(error + e);
            throw new org.craftercms.social.exceptions.AuthenticationException(error, e);
        }

        // if expired, we need to get refresh the user profile
        if (date.before(new Date())) {
            userProfile = profile.getUserInformation(profileToken);

        } else {

            // create user profile from cookie values
            userProfile = new Profile();
            userProfile.setId(profileValues[PROFILE_ID]);
            String[] profileRoles = profileValues[ROLES].split(",");
            userProfile.setRoles(Arrays.asList(profileRoles));
            userProfile.setTenantName(profileValues[TENANT_NAME]);

        }

        // set profile in context
        SecurityContextHolder.getContext().setAuthentication(getCrafterAuthToken(userProfile));

        // generate the encrypted token and set in response
        httpResponse.addCookie(getCipherCookie(cipher, profileToken, userProfile));

        chain.doFilter(httpRequest, httpResponse);
    }

    /**
     * Build cipher auth cookie
     *
     * @param cipher
     * @param profileToken
     * @param userProfile
     * @return
     * @throws org.craftercms.social.exceptions.AuthenticationException
     */
    private Cookie getCipherCookie(SimpleDesCipher cipher, String profileToken, Profile userProfile) throws org.craftercms.social.exceptions.AuthenticationException {
        Cookie cipherAuth = new Cookie(cipherTokenCookieKey, generateEncryptedToken(cipher, profileToken, userProfile));
        cipherAuth.setMaxAge(60*60*8);
        cipherAuth.setPath(CRAFTER_SOCIAL_COOKIE_PATH);
        return cipherAuth;
    }

    /**
     *
     * @param chain
     * @param httpRequest
     * @param httpResponse
     * @param token
     * @param tenantName
     * @param cipher
     * @throws IOException
     * @throws ServletException
     * @throws org.craftercms.social.exceptions.AuthenticationException
     */
    private void authenticateWithSimpleToken(FilterChain chain, HttpServletRequest httpRequest, HttpServletResponse httpResponse,
                 String token, String tenantName, SimpleDesCipher cipher) throws IOException, ServletException, org.craftercms.social.exceptions.AuthenticationException {

        if (token != null && !token.isEmpty()) {
            if (profile.validateUserToken(token)) {

                final Profile userProfile = profile.getUserInformation(token);

                // validate tenant, exception thrown for failure
                validateTenant(httpRequest.getServerName(), tenantName, userProfile.getTenantName(), userProfile.getId());

                SecurityContextHolder.getContext().setAuthentication( getCrafterAuthToken(userProfile));

                // generate the encrypted token and set in response
                httpResponse.addCookie(getCipherCookie(cipher, token, userProfile));

                chain.doFilter(httpRequest, httpResponse);

            } else {
                profile.resetAppToken();
                failRequest(httpRequest, httpResponse,
                        new BadCredentialsException("Token is no longer valid"));
            }



        } else if (token.isEmpty()) { // ANONYMOUS support
            SecurityContextHolder.getContext().setAuthentication(
                    getCrafterAuthAnonymousToken());
            chain.doFilter(httpRequest, httpResponse);
        }
        else {

            failRequest(httpRequest, httpResponse,
                    new AuthenticationCredentialsNotFoundException(
                            "Need param is not on the request"));
        }
    }


    /**
     * Generate an encrypted token from the user profile details.
     *
     * @param cipher
     * @param profileToken
     * @param userProfile
     * @return
     */
    private String generateEncryptedToken(SimpleDesCipher cipher, String profileToken, Profile userProfile) throws org.craftercms.social.exceptions.AuthenticationException {

        // create new cookie
        byte[] encrypted = null;
        try {

            // increment the expires time
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.SECOND, cipherTokenExpires);

            encrypted = cipher.encrypt(new StringBuilder(profileToken).append(TOKEN_SEPARATOR)
                    .append(userProfile.getId()).append(TOKEN_SEPARATOR)
                    .append(DateFormat.getInstance().format(cal.getTime())).append(TOKEN_SEPARATOR)
                    .append(StringUtils.join(userProfile.getRoles().toArray(), ',')).append(TOKEN_SEPARATOR)
                    .append(userProfile.getTenantName()).append(TOKEN_SEPARATOR)
                    .append(UUID.randomUUID().toString()).toString().getBytes()) ;

        } catch (InvalidKeyException e) {
            log.error(ERROR_ENCRYPTED_TOKEN, e);
            throw new org.craftercms.social.exceptions.AuthenticationException(ERROR_ENCRYPTED_TOKEN, e);
        } catch (IllegalBlockSizeException e) {
            log.error(ERROR_ENCRYPTED_TOKEN, e);
            throw new org.craftercms.social.exceptions.AuthenticationException(ERROR_ENCRYPTED_TOKEN, e);
        } catch (BadPaddingException e) {
            log.error(ERROR_ENCRYPTED_TOKEN, e);
            throw new org.craftercms.social.exceptions.AuthenticationException(ERROR_ENCRYPTED_TOKEN, e);
        }

        return new String(Base64.encodeBase64(encrypted));
    }


    /**
     * Validate the tenant for this domain and user
     *
     * @param callerDomain
     * @param tenantName
     * @param userProfileTenantName
     * @return
     * @throws IOException
     * @throws ServletException
     */
    private void validateTenant(String callerDomain, String tenantName, String userProfileTenantName, String profileId) throws IOException, ServletException, org.craftercms.social.exceptions.AuthenticationException {
        final Tenant tenant = profile.getTenant(tenantName);
        if (tenant == null) {
        	String errorMessage = "Tenant: '" + tenantName + "' is not a valid entry in Tenant collection.";
            log.error(errorMessage);
            throw new org.craftercms.social.exceptions.AuthenticationException(errorMessage);
        }
        if (!tenant.getDomains().contains(callerDomain)) {
            String errorMessage = "Tenant: '" + tenantName + "' is not valid for domain: '" + callerDomain + "'";
            log.error(errorMessage);
            throw new org.craftercms.social.exceptions.AuthenticationException(errorMessage);
        } else if (!tenant.getTenantName().equals(userProfileTenantName)) {
            String errorMessage =  "Tenant: '" + tenantName + "' is not valid for user profile: '" + profileId + "'";
            log.error(errorMessage);
            throw new org.craftercms.social.exceptions.AuthenticationException(errorMessage);
        }
    }

    /**
     * Decrypt token and parse values from it
     *
     * @param encryptedToken
     * @param cipher
     * @return
     * @throws org.craftercms.social.exceptions.AuthenticationException
     */
    private String[] getProfileValues(String encryptedToken, SimpleDesCipher cipher) throws org.craftercms.social.exceptions.AuthenticationException {
        // get data from the cookie
        String decrypted = null;
        try {
            decrypted = new String(cipher.decrypt(Base64.decodeBase64(encryptedToken)));
        } catch (InvalidKeyException e) {
            log.error(ERROR_DESCYPTING_TOKEN, e);
            throw new org.craftercms.social.exceptions.AuthenticationException(ERROR_DESCYPTING_TOKEN, e);
        } catch (IllegalBlockSizeException e) {
            log.error(ERROR_DESCYPTING_TOKEN, e);
            throw new org.craftercms.social.exceptions.AuthenticationException(ERROR_DESCYPTING_TOKEN, e);
        } catch (BadPaddingException e) {
            log.error(ERROR_DESCYPTING_TOKEN, e);
            throw new org.craftercms.social.exceptions.AuthenticationException(ERROR_DESCYPTING_TOKEN, e);
        }

        return decrypted.split("[|]");
    }

    /**
     * Reads the encrypted token cookie.  Returns null if there is none.
     *
     * @param httpRequest
     * @return
     */
    private String getCipherTokenCookie(HttpServletRequest httpRequest) {
        String encryptedToken = null;
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cipherTokenCookieKey)) {
                    encryptedToken = cookie.getValue();
                    break;
                }
            }
        }
        return encryptedToken;
    }

    private Authentication getCrafterAuthToken(Profile userProfile) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        if (userProfile.getRoles() != null) {
            for (String r:userProfile.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(r.toUpperCase()));
            }
        }

        return new CrafterProfileAutenticationToken(authorities, userProfile);
    }

    private Authentication getCrafterAuthAnonymousToken() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("anonymous"));
        return new CrafterProfileAutenticationToken(authorities, ProfileConstants.ANONYMOUS);

    }

    private void failRequest(HttpServletRequest httpRequest,
                             HttpServletResponse httpResponse,
                             AuthenticationException authenticationException)
            throws IOException, ServletException {
        authenticationEntryPoint.commence(httpRequest, httpResponse,
                authenticationException);
    }

    private String getParamFromRequest(HttpServletRequest request, String paramKey) {
        String result = null;
        result = request.getParameter(paramKey);
        return result;
    }


    public class CrafterProfileAutenticationToken extends
            AbstractAuthenticationToken {

        private static final long serialVersionUID = 1142799805748917562L;
        private Profile profile;

        public CrafterProfileAutenticationToken(
                Collection<? extends GrantedAuthority> authorities,
                Profile profile) {
            super(authorities);
            this.profile = profile;
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return profile.getId();
        }

        @Override
        public String getName() {
            return profile.getUserName();
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }

        @Override
        public Object getDetails() {
            return profile;
        }
    }

    public void setProfile(CrafterProfile profile) {
        this.profile = profile;
    }
}
