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

package org.craftercms.social.services.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.permissions.annotations.HasPermission;
import org.craftercms.social.domain.system.ContextPreferences;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.repositories.system.ContextPreferencesRepository;
import org.craftercms.social.security.SecurityActionNames;
import org.craftercms.social.security.SocialPermission;
import org.craftercms.social.services.system.ContextPreferencesService;
import org.craftercms.social.services.system.TenantConfigurationService;

/**
 *
 */
public class ContextPreferencesServiceImpl implements ContextPreferencesService{
    private ContextPreferencesRepository contextPreferencesRepository;
    private String invalidKeys;
    private TenantConfigurationService tenantConfigurationService;

    @Override
    public Map findEmailPreference(final String contextId) throws SocialException {
        return (Map)contextPreferencesRepository.getEmailPreference(contextId);
    }

    @Override
    public String getNotificationEmailTemplate(final String contextId, final String notificationType) throws SocialException {
        return contextPreferencesRepository.findNotificationTemplate(contextId,notificationType);

    }

    @Override
    public Map<String, Object> getContextPreferences(final String contextId){
        return contextPreferencesRepository.getContextPreferences(contextId);
    }
    @Override
    public boolean saveContextPreference(final String contextId, final Map<String, Object> preferences) {
        final HashMap<String, Object> cleanPref = new HashMap<>();
        for (String key : preferences.keySet()) {
            if (!invalidKeys.contains(key)){
                cleanPref.put("preferences."+key, preferences.get(key));
            }
        }
        final boolean result = contextPreferencesRepository.setContextPreferences(cleanPref, contextId);
        if(result){
            tenantConfigurationService.reloadTenant(contextId);
        }
        return result;
    }

    @Override
    public boolean deleteContextPreference(final String context, final List<String> preferences) {
        final boolean result = contextPreferencesRepository.deleteContextPreferences(context, preferences);
        if(result){
            tenantConfigurationService.reloadTenant(context);
        }
        return result;
    }



    @Override
    @HasPermission(type = SocialPermission.class, action = SecurityActionNames.CHANGE_NOTIFICATION_TEMPLATE)
    public boolean saveEmailTemplate(final String context, final String type, final String template) throws SocialException {
        return contextPreferencesRepository.saveEmailTemplate(context, type, template);
    }

    @Override
    @HasPermission(type = SocialPermission.class, action = SecurityActionNames.CHANGE_NOTIFICATION_TEMPLATE)
    public String getEmailTemplate(final String context, final String emailTemplateType) throws SocialException {
        return getNotificationEmailTemplate(context,emailTemplateType.toLowerCase());
    }

    public void setContextPreferencesRepository(final ContextPreferencesRepository contextPreferencesRepository) {
        this.contextPreferencesRepository = contextPreferencesRepository;
    }

    public void setInvalidKeys(final String invalidKeys) {
        this.invalidKeys = invalidKeys;
    }

    @Override
    public Map<String, Object> saveEmailConfig(final String contextId, final Map<String, Object> newConfiguration)
        throws SocialException {
        return contextPreferencesRepository.saveEmailConfig(contextId,newConfiguration);
    }


    public void setTenantConfigurationServiceImpl(TenantConfigurationService tenantConfigurationService) {
        this.tenantConfigurationService=tenantConfigurationService;
    }
}