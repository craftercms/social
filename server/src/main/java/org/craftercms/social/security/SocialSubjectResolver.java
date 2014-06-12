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

    @Override
    public Profile getCurrentSubject() {
        return SocialSecurityUtils.getCurrentProfile();
    }

}
