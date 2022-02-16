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

package org.craftercms.social.services.system;

import java.util.List;
import java.util.Map;

import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.exceptions.SocialException;

/**
 *
 */
public interface ContextPreferencesService {

    Map findEmailPreference(final String contextId) throws SocialException;
    Map<String,Object> saveEmailConfig(final String contextId,final Map<String,Object> newConfiguration) throws
        SocialException;
    String getNotificationEmailTemplate(final String contextId,final String notificationType) throws SocialException;

    Map<String, Object> getContextPreferences(String contextId);

    boolean saveContextPreference(String contextId,Map<String,Object> preferences);

    boolean saveEmailTemplate(String context, String type, final String template) throws SocialException;

    String getEmailTemplate(String context, String emailTemplateType) throws  SocialException;

    boolean deleteContextPreference(String context, List<String> strings);

    Map<String,Object> getAllPreferences(String context);

    void saveAllContextPreferences(String contextId, Map<String, Object> newPreferences) throws SocialException;
}
