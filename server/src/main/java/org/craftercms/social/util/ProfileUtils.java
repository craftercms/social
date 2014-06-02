package org.craftercms.social.util;

import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.utils.SecurityUtils;

/**
 * Created with IntelliJ IDEA.
 * User: alfonsovasquez
 * Date: 20/11/13
 * Time: 17:06
 * To change this template use File | Settings | File Templates.
 */
public class ProfileUtils {

    public static Profile getCurrentProfile() {
        Authentication auth = SecurityUtils.getCurrentAuthentication();
        if (auth != null) {
            return auth.getProfile();
        }
        return null;
    }

    public static String getCurrentProfileId() {
        Profile profile = getCurrentProfile();
        if (profile != null) {
            return profile.getId().toString();
        } else {
            return null;
        }
    }

}
