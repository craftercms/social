/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;

import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.utils.SecurityUtils;
import org.craftercms.social.security.SocialSecurityUtils;

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

    public static Profile getAnonymousProfile() {
        Profile anonymous = new Profile();
        anonymous.setEmail(SocialSecurityUtils.ANONYMOUS);
        anonymous.setUsername(SocialSecurityUtils.ANONYMOUS.toLowerCase());
        anonymous.setRoles(new LinkedHashSet<>(Arrays.asList(SocialSecurityUtils.ANONYMOUS)));
        anonymous.setAttributes(new HashMap<String, Object>());
        anonymous.setAttribute("displayName",SocialSecurityUtils.ANONYMOUS.toLowerCase());
        anonymous.setAttribute("avatarLink","");
        anonymous.setAttribute("anonymized",true);
        anonymous.setTenant("");
        return anonymous;

    }
}
