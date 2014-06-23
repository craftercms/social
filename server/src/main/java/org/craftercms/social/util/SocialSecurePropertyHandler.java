package org.craftercms.social.util;

import java.util.Arrays;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.craftercms.commons.jackson.mvc.SecurePropertyHandler;
import org.craftercms.social.security.SocialSecurityUtils;

/**
 * Created by cortiz on 6/19/14.
 */
public class SocialSecurePropertyHandler implements SecurePropertyHandler {


    @Override
    public boolean suppressProperty(final String propertyName, final String[] roles) {
        Set<String> currentRoles = SocialSecurityUtils.getCurrentProfile().getRoles();

        return CollectionUtils.containsAny(currentRoles, Arrays.asList(roles));
    }
}
