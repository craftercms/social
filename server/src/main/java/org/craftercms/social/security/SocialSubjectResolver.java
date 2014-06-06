package org.craftercms.social.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;

import org.craftercms.commons.security.permissions.SubjectResolver;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.utils.SecurityUtils;

/**
 *
 */
public class SocialSubjectResolver implements SubjectResolver<Profile> {

    protected String anonymousRole;
    @Override
    public Profile getCurrentSubject() {
        Authentication currentAuth = SecurityUtils.getCurrentAuthentication();
        Profile profile;
        if(currentAuth==null){
            profile=new Profile();
            profile.setUsername(anonymousRole);
            profile.setRoles(new LinkedHashSet<>(Arrays.asList(anonymousRole)));
            profile.setAttributes(new HashMap<String, Object>());
        }else{
            profile=currentAuth.getProfile();
        }
      return profile;
    }

}
