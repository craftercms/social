package org.craftercms.social.security;

import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.permissions.Permission;
import org.craftercms.security.exception.AccessDeniedException;
import org.craftercms.social.repositories.security.PermissionRepository;

import java.util.Set;

/**
 *
 */
public class SocialPermission implements Permission {

    private final String tenantId;
    private Set<String> profileRoles;
    private PermissionRepository repository;

    public SocialPermission(final Set<String> profileRoles, final PermissionRepository repository,
                            final String tenantId) {
        this.profileRoles = profileRoles;
        this.repository = repository;
        this.tenantId = tenantId;
    }

    @Override
    public boolean isAllowed(final String action) {
        try {
            // The super admin can do anything
            return profileRoles.contains("SOCIAL_SUPER_ADMIN") || repository.isAllowed(action, profileRoles, tenantId);
        } catch (MongoDataException e) {
            throw new AccessDeniedException("Unable to find Action", e);
        }
    }

}
