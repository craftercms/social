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

package org.craftercms.social.repositories.system;

import java.util.List;
import java.util.Map;

import org.craftercms.commons.mongo.CrudRepository;
import org.craftercms.social.domain.system.ContextPreferences;
import org.craftercms.social.exceptions.SocialException;

/**
 *
 */
public interface ContextPreferencesRepository extends CrudRepository<ContextPreferences> {

    Map<String,String> getEmailPreference(String contextId) throws SocialException;

    Map<String,Object> saveEmailConfig(String contextId, Map<String, Object> emailPref) throws
        SocialException;

    String findNotificationTemplate(String contextId, String notificationType) throws SocialException;

    Map<String,Object> getContextPreferences(String contextId);

    Map<String, Object> getContextAllPreferences(String contextId);

    boolean setContextPreferences(Map<String,Object> preferences, String contextId);

    boolean saveEmailTemplate(String context, String type, String template) throws SocialException;

    boolean deleteContextPreferences(String context, List<String> preferences);

    void saveAllContextPreferences(String contextId, Map<String, Object> newPreferences)throws SocialException;
}
