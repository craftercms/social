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

import org.bson.types.ObjectId;
import org.craftercms.commons.security.exception.PermissionException;
import org.craftercms.commons.security.permissions.Permission;
import org.craftercms.commons.security.permissions.PermissionResolver;
import org.craftercms.profile.api.Profile;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.domain.social.Flag;
import org.craftercms.social.exceptions.UGCException;
import org.craftercms.social.repositories.security.PermissionRepository;
import org.craftercms.social.services.ugc.UGCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class SocialPermissionResolver<T> implements PermissionResolver<Profile, T> {

    protected PermissionRepository permissionRepository;
    protected UGCService ugcService;
    private Logger log = LoggerFactory.getLogger(SocialPermissionResolver.class);

    @Override
    public Permission getGlobalPermission(final Profile subject) throws PermissionException {
        return getPermission(subject, null);
    }

    @Override
    public Permission getPermission(final Profile subject, final T object) throws PermissionException {
        if (object instanceof String) {
            String ugcId = (String)object;
            if (ObjectId.isValid(ugcId)) {
                try {
                    final UGC ugc = ugcService.read(ugcId, SocialSecurityUtils.getContext());
                    if (ugc != null) {
                        if (subject.getId().equals(ugc.getCreatedBy())) {
                            subject.getRoles().add("OWNER");
                        }
                    }
                } catch (UGCException e) {
                    log.error("Unable to find UGC with id " + ugcId, e);
                }
            }
        }else if(object instanceof Flag){
            Flag f = (Flag)object;
            if(f.getUserId().equalsIgnoreCase(subject.getId().toString())){
                subject.getRoles().add("OWNER");
            }
        }
        return new SocialPermission(SocialSecurityUtils.getSocialRoles(), permissionRepository,
            SocialSecurityUtils.getContext());
    }

    public void setPermissionRepository(final PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public void setUgcService(UGCService ugcService) {
        this.ugcService = ugcService;
    }

}
