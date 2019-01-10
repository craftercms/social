/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.permissions.Permission;
import org.craftercms.security.exception.AccessDeniedException;
import org.craftercms.social.repositories.security.PermissionRepository;

/**
 *
 */
public class SocialPermission implements Permission {

    private final String contextId;
    private List<String> profileRoles;
    private PermissionRepository repository;

    public SocialPermission(final List<String> profileRoles, final PermissionRepository repository,
                            final String contextId) {
        this.profileRoles = profileRoles;
        this.repository = repository;
        this.contextId = contextId;
    }

    @Override
    public boolean isAllowed(final String action) {
        try {
            ConcurrentSkipListSet coll = new ConcurrentSkipListSet();
            for (String profileRole : profileRoles) {
                if(!coll.contains(profileRole)) {
                    coll.add(profileRole);
                }
            }
            return repository.isAllowed(action,coll, contextId);
        } catch (MongoDataException e) {
            throw new AccessDeniedException("Unable to find Action", e);
        }
    }

}
