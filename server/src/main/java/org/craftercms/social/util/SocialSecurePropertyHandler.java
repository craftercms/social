package org.craftercms.social.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.jackson.mvc.SecurePropertyHandler;
import org.craftercms.profile.api.Profile;
import org.craftercms.social.domain.social.Flag;
import org.craftercms.social.security.SocialSecurityUtils;

/**
 *
 */
public class SocialSecurePropertyHandler implements SecurePropertyHandler {

    @Override
    public boolean suppressProperty(final Object propertyName, final String[] roles) {
        Profile p = SocialSecurityUtils.getCurrentProfile();
        List<String> currentRoles=new ArrayList<>();
        if(p!=null && !p.getUsername().equalsIgnoreCase(SocialSecurityUtils.ANONYMOUS)){
            if(propertyName instanceof Flag){
                final String userId = ((Flag)propertyName).getUserId();
                if(userId.equals(p.getId().toString())) {
                    currentRoles.add("OWNER");
                }
            }
        }
        currentRoles.addAll(SocialSecurityUtils.getSocialRoles());
        return CollectionUtils.containsAny(currentRoles, Arrays.asList(roles));
    }

}
