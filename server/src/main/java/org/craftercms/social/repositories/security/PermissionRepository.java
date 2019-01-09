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

package org.craftercms.social.repositories.security;

import java.util.List;
import java.util.Set;

import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.social.system.SocialSecurityAction;

/**
 *
 */
public interface PermissionRepository {

    /**
     * Checks if the Actions is allow for a given set of Roles for the context.
     * @param action Actions to check.
     * @param profileRoles Roles to check action against.
     * @param context Context of the action.
     * @return True if any of the given roles can execute the action, false otherwise.
     * @throws MongoDataException If unable to check.
     */
    boolean isAllowed(String action, Set<String> profileRoles, String context) throws MongoDataException;

    /**
     * Returns all the actions for the context.
     * @param context Context of the action.
     * @return A list of all the security Actions. Empty if nothing is found.
     */
    Iterable<SocialSecurityAction> findActions(String context) throws MongoDataException;

    /**
     * Updates the SecurityAction based
     * @param context
     * @param actionName
     * @param roles
     * @return The updated SecurityAction, null if context/action does not exist.
     */
    SocialSecurityAction updateSecurityAction(final String context, final String actionName,
                                              final List<String> roles) throws MongoDataException;


    /**
     * Saves a new Action.
     * @param action New Action to save.
     * @throws MongoDataException If unable to save.
     */
    void save(SocialSecurityAction action) throws MongoDataException;
}
