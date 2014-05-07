package org.craftercms.social.ext.security;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;
import org.craftercms.profile.api.Profile;
import org.craftercms.social.services.PermissionService;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 */
public class SocialSecurityExpressionRoot extends SecurityExpressionRoot {

    private PermissionService permissionService;

    /**
     * Creates a new instance
     *
     * @param authentication the {@link org.springframework.security.core.Authentication} to use. Cannot be null.
     */
    public SocialSecurityExpressionRoot(final Authentication authentication,
                                        final PermissionService permissionService) {
        super(authentication);
        this.permissionService = permissionService;
    }



    public boolean ugcCan(final String action,final String ugcId) {
        Profile profile= (Profile)authentication.getPrincipal();
        return permissionService.allowed(ugcId,action,profile.getId().toString(),toRoleString());
    }


    protected List<String> toRoleString() {
        List<String> roles = new ArrayList<>(authentication.getAuthorities().size());
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            roles.add(grantedAuthority.getAuthority());
        }
        return ListUtils.unmodifiableList(roles);
    }
}
