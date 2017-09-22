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
package org.craftercms.social.repositories.system.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.craftercms.commons.i10n.I10nLogger;
import org.craftercms.commons.mongo.AbstractJongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.system.ContextPreferences;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.repositories.system.ContextPreferencesRepository;
import org.craftercms.social.util.LoggerFactory;

/**
 *
 */
public class ContextPreferencesRepositoryImpl extends AbstractJongoRepository<ContextPreferences> implements
    ContextPreferencesRepository {

    private I10nLogger logger = LoggerFactory.getLogger(ContextPreferencesRepositoryImpl.class);

    @Override
    public Map<String,String> getEmailPreference(final String contextId) throws SocialException {
        try {
            String query = getQueryFor("social.system.preferences.emailPreferencesByContextId");
            final HashMap tmp = getCollection().findOne(query, contextId).projection("{email:1,_id:0}").as(HashMap
                .class);
            if (tmp == null || !tmp.containsKey("email")) {
                throw new SocialException("Current context " + contextId + "is missing email configuration");
            }
            return (Map)tmp.get("email");
        } catch (MongoException ex) {
            throw new SocialException("Unable to read email preferences for " + contextId);
        }
    }



    @Override
    public Map<String,Object> saveEmailConfig(final String contextId, Map<String, Object> emailPref) throws
        SocialException {
        try {
                String fQuery=getQueryFor("social.system.preferences.emailPreferencesByContextId");
                String uQuery=getQueryFor("social.system.preferences.updateContextEmailPref");
            getCollection().update(fQuery, contextId).with(uQuery,emailPref.get("host"),
                emailPref.get("encoding"),
                emailPref.get("port"),
                emailPref.get("auth"),
                emailPref.get("username"),
                emailPref.get("password"),
                emailPref.get("tls"),
                emailPref.get("replyTo"),
                emailPref.get("from"),
                emailPref.get("priority"),
                emailPref.get("subject")
            );
            return emailPref;
        } catch (MongoException ex) {
            throw new SocialException("Unable to read email preferences for " + contextId);
        }
    }

    @Override
    public String findNotificationTemplate(final String contextId, final String notificationType) throws
        SocialException {
        try {
            String query = getQueryFor("social.system.preferences.notificationEmailByType");
            Map qResult = getCollection().findOne(query, contextId, notificationType.toUpperCase()).projection
                ("{\"templates.$\":1,_id:0}").as(Map.class);
            if (qResult == null) {
                return null;
            }
            final List templates = (List)qResult.get("templates");
            if (templates == null) {
                return null;
            }
            if (templates.isEmpty()) {
                throw new SocialException("No template for type" + notificationType + " has been define for context "
                    + "" + contextId);
            } else {
                if (templates.size() > 1) {
                    logger.warn("logging.system.notification.multipleTemplatesForType", notificationType, contextId);
                }
                return ((Map)templates.get(0)).get("template").toString();
            }
        } catch (MongoException ex) {
            throw new SocialException("Unable to get Notification Template for " + contextId + " of type" +
                notificationType);
        }
    }

    @Override
    public Map<String, Object> getContextPreferences(final String contextId) {
        try {
            final String byId = getQueryFor("social.system.preferences.emailPreferencesByContextId");
            return getCollection().findOne(byId, contextId).projection("{preferences:1,_id:0}").as(HashMap.class);
        }catch (MongoException ex){
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getContextAllPreferences(final String contextId) {
        try {
            final String byId = getQueryFor("social.system.preferences.emailPreferencesByContextId");
            return getCollection().findOne(byId, contextId).as(HashMap.class);
        }catch (MongoException ex){
            return new HashMap<>();
        }
    }

    @Override
    public boolean setContextPreferences(final Map<String, Object> preferences, final String contextId) {
        try {
            final String preferencesString = new ObjectMapper().writeValueAsString(preferences);
            final String byId=getQueryFor("social.system.preferences.emailPreferencesByContextId");
            getCollection().update(byId,contextId).with("{$set: "+preferencesString+"}");
            return true;
        } catch (MongoException | JsonProcessingException e) {
            logger.error("Unable to delete context Preferences",e);
            return false;
        }
    }

    @Override
    public boolean deleteContextPreferences(final String context, final List<String> preferences) {
        try {
            final String byId=getQueryFor("social.system.preferences.emailPreferencesByContextId");
            String toUnset="";
            final Iterator<String> iter = preferences.iterator();
            while (iter.hasNext()){
                final String key = iter.next();
                toUnset+="preferences."+key+":1";
                if(iter.hasNext()){
                    toUnset+=",";
                }
            }
            getCollection().update(byId,context).with("{$unset:{"+toUnset+"}}");
            return true;
        } catch (MongoException e) {
            logger.error("Unable to delete context Preferences",e);
            return false;
        }
    }

    public void saveEmailPreference(final String contextId, Map<String, Object> emailPreferences) throws
        SocialException {
        try {
            String query = getQueryFor("social.system.preferences.savePreferencesByContextId");
            String findQ = getQueryFor("social.system.preferences.emailPreferencesByContextId");
            getCollection().update(findQ, contextId).upsert().with(query, emailPreferences.get("host"),
                emailPreferences.get("encoding"), emailPreferences.get("port"), emailPreferences.get("auth"),
                emailPreferences.get("username"), emailPreferences.get("password"), emailPreferences.get("tls"),
                emailPreferences.get("replyTo"), emailPreferences.get("from"), emailPreferences.get("priority"),
                emailPreferences.get("subject"));
        } catch (MongoException ex) {
            throw new SocialException("Unable to read email preferences for " + contextId);
        }
    }

    @Override
    public void saveAllContextPreferences(final String contextId, final Map<String, Object> newPreferences) throws SocialException {
        try{
            getCollection().save(newPreferences);
        }catch (MongoException ex){
            throw new SocialException("Unable to save email Preferences");
        }
    }

    @Override
    public boolean saveEmailTemplate(final String context, final String type, final String template) throws SocialException {
        try{
            String findQ=getQueryFor("social.system.preferences.byContextAndTemplateType");
            String updateQ=getQueryFor("social.system.preferences.updateContextTemplateType");
            WriteResult r = getCollection().update(findQ, context, type.toUpperCase()).with(updateQ, template);

            return r.getN()==1;
        } catch (MongoException ex){
            throw new SocialException("Unable to update Email template",ex);
        }
    }

}