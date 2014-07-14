package org.craftercms.social.security;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.permissions.Permission;
import org.craftercms.security.exception.AccessDeniedException;
import org.craftercms.social.repositories.security.PermissionRepository;

/**
 *
 */
public class SocialPermission implements Permission {

    private final String tenantId;
    private List<String> profileRoles;
    private PermissionRepository repository;

    public SocialPermission(final List<String> profileRoles, final PermissionRepository repository,
                            final String tenantId) {
        this.profileRoles = profileRoles;
        this.repository = repository;
        this.tenantId = tenantId;
    }

    @Override
    public boolean isAllowed(final String action) {
        try {
            return repository.isAllowed(action,new LinkedHashSet<>(profileRoles), tenantId);
        } catch (MongoDataException e) {
            throw new AccessDeniedException("Unable to find Action", e);
        }
    }

}
