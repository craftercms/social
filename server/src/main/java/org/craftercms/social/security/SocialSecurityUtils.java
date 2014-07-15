package org.craftercms.social.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.craftercms.commons.http.RequestContext;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.utils.SecurityUtils;
import org.craftercms.social.exceptions.ProfileConfigurationException;

import java.util.*;

/**
 */
public class SocialSecurityUtils {


    public static final String SOCIAL_TENANTS = "socialTenants";
    public static final String ANONYMOUS = "ANONYMOUS";
    public static final String SOCIAL_TENANT_ID = "id";
    public static final String SOCIAL_TENANT_ROLES = "roles";

    private SocialSecurityUtils() {
    }

    public static List<String> getRolesForSocialContext(){
        Profile profile=getCurrentProfile();
        if(profile.getUsername().equals(ANONYMOUS)){
            return Arrays.asList(ANONYMOUS);
        }
        List<String> list=getSocialTenantValue(profile,SOCIAL_TENANT_ROLES);
        if(list==null){
            list=new ArrayList<>();
        }
       list.addAll(profile.getRoles());
        return list;
    }

    public static String getTenant(){
        String tenant = RequestContext.getCurrent().getRequest().getParameter(SecurityUtils
            .TENANT_REQUEST_ATTRIBUTE_NAME);
        if (StringUtils.isBlank(tenant)) {
            throw new IllegalArgumentException("Parameter \"" + SecurityUtils.TENANT_REQUEST_ATTRIBUTE_NAME + " \" "
                + "is" + " " + "needed on the request");
        }
        return tenant;
    }

    private static <T> T getSocialTenantValue(final Profile profile, final String key){
        String tenant = getTenant();
        List<Map<String, Object>> socialTenants = (List<Map<String, Object>>)profile.getAttribute(SOCIAL_TENANTS);
        if (socialTenants == null || CollectionUtils.isEmpty(socialTenants)) {
            throw new ProfileConfigurationException("Current profile tenant is not complete, "
                + "" + "missing '" + SOCIAL_TENANTS + "'");
        }
        for (Map<String, Object> socialTenant : socialTenants) {
            String id = (String)socialTenant.get(SOCIAL_TENANT_ID);
            if (StringUtils.isBlank(id)) {
                throw new ProfileConfigurationException("Current profile tenant is not complete, "
                    + "" + "missing '" + SOCIAL_TENANT_ID + "'");
            }
            if (id.equals(tenant)) {
                return (T)socialTenant.get(key);
            }
        }
        if(profile.getRoles().contains(SecurityActionNames.ROLE_SOCIAL_SUPERADMIN)){
            return null;
        }else {
            throw new ProfileConfigurationException("Current Profile is not assign to the given tenant");
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
            //This is The Social Tenant 1
            profile.setTenant("");
            return profile;
        } else {
            return  currentAuth.getProfile();
        }
    }
}