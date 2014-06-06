package org.craftercms.social.security;

import java.util.List;
import java.util.Map;

import org.craftercms.commons.security.exception.PermissionException;
import org.craftercms.commons.security.permissions.Permission;
import org.craftercms.commons.security.permissions.PermissionResolver;
import org.craftercms.profile.api.Profile;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.repositories.security.PermissionRepository;

/**
 */
public class SocialPermissionResolver<T extends UGC> implements PermissionResolver<Profile, T> {

    protected PermissionRepository permissionRepository;
    @Override
    public Permission getGlobalPermission(final Profile subject) throws PermissionException {
        return getPermission(subject, null);
    }

    @Override
    public Permission getPermission(final Profile subject, final T object) throws PermissionException {
        return new SocialPermission(subject.getRoles(),permissionRepository,subject.getTenant());
    }


    public void setPermissionRepository(final PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }
}
