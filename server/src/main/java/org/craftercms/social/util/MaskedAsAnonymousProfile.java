package org.craftercms.social.util;

import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.security.api.SecurityConstants;

import java.util.Date;

/**
 * Profile that masks another profile as anonymous.
 *
 * @author avasquez
 */
public class MaskedAsAnonymousProfile extends Profile {

    private Profile actualProfile;

    public MaskedAsAnonymousProfile(Profile actualProfile) {
        setId(null);
        setUserName(SecurityConstants.ANONYMOUS_USERNAME);
        setPassword("");
        setActive(true);

        this.actualProfile = actualProfile;
    }

    public Profile getActualProfile() {
        return actualProfile;
    }

}
