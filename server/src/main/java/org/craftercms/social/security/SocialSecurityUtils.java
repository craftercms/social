package org.craftercms.social.security;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.utils.SecurityUtils;
import org.craftercms.social.exceptions.ProfileConfigurationException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 */
public class SocialSecurityUtils {

    public static final String SOCIAL_CONTEXT = "socialContext";
    public static final String ANONYMOUS = "ANONYMOUS";
    public static final String SOCIAL_CONTEXT_ID = "id";
    public static final String SOCIAL_CONTEXT_ROLES = "roles";

    private SocialSecurityUtils() {
    }

    public static List<String> getRolesForSocialContext(){
        Profile profile = getCurrentProfile();
        if(profile.getUsername().equals(ANONYMOUS)){
            return Arrays.asList(ANONYMOUS);
        }
        List<String> list = getSocialContextValue(profile, SOCIAL_CONTEXT_ROLES);
        if(list == null){
            list = new ArrayList<>();
        }
        list.addAll(profile.getRoles());

        return list;
    }

    public static String getContext(){
        HttpServletRequest request = RequestContext.getCurrent().getRequest();
        String context = request.getParameter("context");

        if (StringUtils.isBlank(context)) {
            throw new IllegalArgumentException("Parameter '" + SecurityUtils.TENANT_REQUEST_ATTRIBUTE_NAME +
                    "' is needed on the request");
        }
        return context;
    }

    private static <T> T getSocialContextValue(final Profile profile, final String key){
        String tenant = getContext();
        List<Map<String, Object>> socialTenants = (List<Map<String, Object>>) profile.getAttribute(SOCIAL_CONTEXT);

        if (CollectionUtils.isNotEmpty(socialTenants)) {
            for (Map<String, Object> socialTenant : socialTenants) {
                String id = (String) socialTenant.get(SOCIAL_CONTEXT_ID);
                if (StringUtils.isBlank(id)) {
                    throw new ProfileConfigurationException("Social context missing '" + SOCIAL_CONTEXT_ID + "'");
                }
                if (id.equals(tenant)) {
                    return (T)socialTenant.get(key);
                }
            }
            if (profile.hasRole(SecurityActionNames.ROLE_SOCIAL_SUPERADMIN)){
                return null;
            }else {
                throw new ProfileConfigurationException("Current profile is not assign to the given tenant");
            }
        } else if (profile.hasRole(SecurityActionNames.ROLE_SOCIAL_SUPERADMIN)) {
            return null;
        } else {
            throw new ProfileConfigurationException("Profile missing attribute '" + SOCIAL_CONTEXT + "'");
        }
    }


    public static Profile getCurrentProfile() {
        Authentication currentAuth = SecurityUtils.getCurrentAuthentication();
        Profile profile;

        if (currentAuth == null) {
            profile = new Profile();
            profile.setUsername(ANONYMOUS);
            profile.setRoles(new LinkedHashSet<>(Arrays.asList(ANONYMOUS)));
            profile.setAttributes(new HashMap<String, Object>());
            profile.setTenant("");

            return profile;
        } else {
            return  currentAuth.getProfile();
        }
    }

}