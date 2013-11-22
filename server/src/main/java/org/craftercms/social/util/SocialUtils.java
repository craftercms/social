package org.craftercms.social.util;

import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.security.api.RequestContext;
import org.craftercms.security.authentication.AuthenticationToken;
import org.craftercms.social.domain.Subscriptions;

/**
 * Created with IntelliJ IDEA.
 * User: alfonsovasquez
 * Date: 20/11/13
 * Time: 17:06
 * To change this template use File | Settings | File Templates.
 */
public class SocialUtils {

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

    public static String getCurentProfileId() {
        Profile profile = getCurrentProfile();
        if (profile != null) {
            return profile.getId();
        } else {
            return null;
        }
    }

    public static final Subscriptions getSubscriptions(Profile profile) {
        return (Subscriptions) profile.getAttributes().get(SocialConstants.ATTRIBUTE_SUBSCRIPTIONS);
    }

    public static final void setSubscriptions(Profile profile, Subscriptions subscriptions) {
        profile.getAttributes().put(SocialConstants.ATTRIBUTE_SUBSCRIPTIONS, subscriptions);
    }

}
