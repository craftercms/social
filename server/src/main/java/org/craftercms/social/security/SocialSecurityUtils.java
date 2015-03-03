package org.craftercms.social.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
        if (profile.getUsername().equals(ANONYMOUS)) {
            return Arrays.asList(ANONYMOUS);
        }

        List<String> list = new ArrayList<>(getRolesForCurrentContext(profile));
        if (list == null) {
            list = Collections.synchronizedList(new ArrayList<String>());
        }

        list.addAll(profile.getRoles());

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

    private static List<String> getRolesForCurrentContext(final Profile profile) {
        String currentContext = getContext();
        List<Map<String, Object>> socialContexts = profile.getAttribute(SOCIAL_CONTEXTS_ATTRIBUTE);

        if (CollectionUtils.isNotEmpty(socialContexts)) {
            for (Map<String, Object> context : socialContexts) {
                String id = (String)context.get(SOCIAL_CONTEXT_ID);
                if (StringUtils.isBlank(id)) {
                    throw new ProfileConfigurationException("Social context missing '" + SOCIAL_CONTEXT_ID + "'");
                }
                if (id.equals(currentContext)) {
                    return (List<String>)context.get(SOCIAL_CONTEXT_ROLES);
                }
            }
            if (profile.hasRole(SecurityActionNames.ROLE_SOCIAL_SUPERADMIN)) {
                return null;
            } else {
                throw new ProfileConfigurationException("Current profile is not assign to the given currentContext");
            }
        } else if (profile.hasRole(ANONYMOUS)) {
            return null;
        } else if (profile.hasRole(SecurityActionNames.ROLE_SOCIAL_SUPERADMIN)) {
            return null;
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