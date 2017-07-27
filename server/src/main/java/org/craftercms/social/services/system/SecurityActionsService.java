/*
 * Copyright (C) 2007-${year} Crafter Software Corporation.
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.services.system;

import java.util.List;

import org.craftercms.social.domain.social.system.SocialSecurityAction;
import org.craftercms.social.exceptions.SocialException;

/**
 * SecurityActions Service Definition
 */
public interface SecurityActionsService {

    /**
     * Gets All SecurityAction for the given context.
     * @param context the context.
     * @return All SecurityActions for the given context, empty Iterator if nothing is found
     */
    Iterable<SocialSecurityAction> get(final String context);

    /**
     * Updates the Roles for the given Action of the Context.
     * @param context context of the action.
     * @param actionName Action name to removeWatcher.
     * @param roles New roles to assign the action.
     * @return the updated SecurityAction, null if unable to find action for the given context.
     */
    SocialSecurityAction update(String context, String actionName, List<String> roles) throws SocialException;

    /**
     * Saves a new Action
     * @param action Action to be saved.
     */
    void save(SocialSecurityAction action) throws SocialException;
}
