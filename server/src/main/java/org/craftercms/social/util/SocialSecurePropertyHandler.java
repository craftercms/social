package org.craftercms.social.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.craftercms.commons.jackson.mvc.SecurePropertyHandler;
import org.craftercms.social.security.SocialSecurityUtils;

/**
 *
 */
public class SocialSecurePropertyHandler implements SecurePropertyHandler {

    @Override
    public boolean suppressProperty(final String propertyName, final String[] roles) {
        List<String> currentRoles = SocialSecurityUtils.getSocialRoles();
        return CollectionUtils.containsAny(currentRoles, Arrays.asList(roles));
    }

}
