/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
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

package org.craftercms.social.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.utils.SecurityUtils;
import org.craftercms.social.exceptions.ProfileConfigurationException;
import org.craftercms.social.util.ProfileUtils;

/**
 */
public class SocialSecurityUtils {

    public static final String CONTEXT_PARAM = "context";
    public static final String SOCIAL_CONTEXTS_ATTRIBUTE = "socialContexts";
    public static final String ANONYMOUS = "ANONYMOUS";
    public static final String SOCIAL_CONTEXT_NAME = "name";
    public static final String SOCIAL_CONTEXT_ID = "id";
    public static final String SOCIAL_CONTEXT_ROLES = "roles";

    private SocialSecurityUtils() {
    }

    public static List<String> getSocialRoles() {
        Profile profile = getCurrentProfile();
        return getSocialRoles(profile);
    }


    public static List<String> getSocialRoles(final Profile profile) {
        return getSocialRoles(profile,getContext());
    }

    public static List<String> getSocialRoles(final Profile profile,final String socialContext) {
        if (profile.getUsername().equals(ANONYMOUS)) {
            return Arrays.asList(ANONYMOUS);
        }

        List<String> list = new ArrayList<>(getRolesForCurrentContext(socialContext,profile));
        if (list == null) {
            list = Collections.synchronizedList(new ArrayList<String>());
        }
        for (String role : profile.getRoles()) {
            if(!list.contains(role)) {
                list.add(role);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public static String getContext() {
        HttpServletRequest request = RequestContext.getCurrent().getRequest();
        String context = request.getParameter(CONTEXT_PARAM);

        if (StringUtils.isBlank(context)) {
            throw new IllegalArgumentException("Parameter '" + CONTEXT_PARAM + "' is missing from the request");
        }

        return context;
    }

    public static boolean isProfileModeratorOrAdmin(final Profile updateProfile, final String socialContext) {
        final List<String> roles = SocialSecurityUtils.getSocialRoles(updateProfile,socialContext);
        return roles.contains(SecurityActionNames.ROLE_SOCIAL_ADMIN) || roles.contains(SecurityActionNames
            .ROLE_SOCIAL_SUPERADMIN) || roles.contains("SOCIAL_MODERATOR");
    }


    public static List<String> getRolesForCurrentContext(final String contextId,final Profile profile) {

        List<Map<String, Object>> socialContexts = profile.getAttribute(SOCIAL_CONTEXTS_ATTRIBUTE);
        List<String> toReturn= new ArrayList<>();
        if (CollectionUtils.isNotEmpty(socialContexts)) {
            for (Map<String, Object> context : socialContexts) {
                String id = (String)context.get(SOCIAL_CONTEXT_ID);
                if (StringUtils.isBlank(id)) {
                    throw new ProfileConfigurationException("Social context missing '" + SOCIAL_CONTEXT_ID + "'");
                }
                if (id.equals(contextId)) {
                    toReturn.addAll((List<String>)context.get(SOCIAL_CONTEXT_ROLES));
                }
            }
            if (profile.hasAnyRole(SecurityActionNames.ROLE_SOCIAL_SUPERADMIN)) {
                return toReturn;
            } else {
                return toReturn;
            }
        } else if (profile.hasRole(ANONYMOUS)) {
            return toReturn;
        } else if (profile.hasRole(SecurityActionNames.ROLE_SOCIAL_SUPERADMIN)) {
            return toReturn;
        } else {
            throw new ProfileConfigurationException("Profile missing attribute '" + SOCIAL_CONTEXTS_ATTRIBUTE + "'");
        }
    }

    public static Profile getCurrentProfile() {
        Authentication currentAuth = SecurityUtils.getCurrentAuthentication();
        Profile profile;

        if (currentAuth == null) {
            return ProfileUtils.getAnonymousProfile();
        } else {
            return currentAuth.getProfile();
        }
    }

}