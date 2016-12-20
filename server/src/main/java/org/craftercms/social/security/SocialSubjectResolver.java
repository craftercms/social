package org.craftercms.social.security;

import org.craftercms.commons.security.permissions.SubjectResolver;
import org.craftercms.profile.api.Profile;

/**
 *
 */
public class SocialSubjectResolver implements SubjectResolver<Profile> {

    @Override
    public Profile getCurrentSubject() {
        return SocialSecurityUtils.getCurrentProfile();
    }

}
