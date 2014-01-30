package org.craftercms.social.util;

import org.apache.commons.collections.MapUtils;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.api.SecurityConstants;
import org.craftercms.security.authentication.AuthenticationToken;
import org.craftercms.social.domain.Subscriptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: alfonsovasquez
 * Date: 20/11/13
 * Time: 17:06
 * To change this template use File | Settings | File Templates.
 */
public class ProfileUtils {

    public static Profile getCurrentProfile() {
        RequestContext requestContext = RequestContext.getCurrent();
        if (requestContext != null) {
            AuthenticationToken authToken = requestContext.getAuthenticationToken();
            if (authToken != null) {
                return authToken.getProfile();
            }
        }

        return null;
    }

    public static String getCurrentProfileId() {
        Profile profile = getCurrentProfile();
        if (profile != null) {
            return profile.getId();
        } else {
            return null;
        }
    }

    public static Profile getAnonymousProfile() {
        Profile profile = new Profile();
        profile.setId(null);
        profile.setUserName(SecurityConstants.ANONYMOUS_USERNAME);
        profile.setPassword("");
        profile.setActive(true);

        return profile;
    }

}
