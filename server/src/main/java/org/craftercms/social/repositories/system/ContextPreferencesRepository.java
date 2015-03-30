/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.repositories.system;

import java.util.Map;

import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.social.domain.system.ContextPreferences;
import org.craftercms.social.exceptions.SocialException;

/**
 *
 */
public interface ContextPreferencesRepository extends CrudRepository<ContextPreferences> {

    Map<String,String> findEmailPreference(String contextId) throws SocialException;

    String findNotificationTemplate(String contextId, String notificationType) throws SocialException;

    Map<String,Object> getContextPreferences(String contextId);

    boolean setContextPreferences(Map<String,Object> preferences,String contextId);

    boolean saveEmailTemplate(String context, String type, String template) throws SocialException;
}
