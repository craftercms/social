/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
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
package org.craftercms.social.services.impl;

import java.util.List;
import java.util.Set;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.collections4.CollectionUtils;
import org.bson.types.ObjectId;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.Actions;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.exceptions.PermissionsException;
import org.craftercms.social.repositories.SecurityProfileRepository;
import org.craftercms.social.services.PermissionService;
import org.craftercms.social.services.UGCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * <p>This Class is intent to be use with Spring Security Annotations.</p>
 * <p><b>DO Not use this class with other with out reading the Javadoc properly,
 * Unexpected things can and will happen</b></p>
 */
public class PermissionServiceImpl implements PermissionService {

    private static final String SYSTEM_DEFAULT = "systemDefaultProfile";
    private SecurityProfileRepository securityProfileRepository;
    private UGCService ugcService;
    private Ehcache securityCache;

    private Logger log = LoggerFactory.getLogger(PermissionServiceImpl.class);


    @Override
    public boolean allowed(final String actionName, final List<String> roles) {
        Actions defaultProfile = getDefaultSecurityProfile(); //Never throws null
        if(defaultProfile.getActions().containsKey(actionName.toUpperCase())){
            return CollectionUtils.containsAny(defaultProfile.getActions().get(actionName.toUpperCase()),roles);
        }else{
            log.info("Security Profile {} (default) does not have action {}",defaultProfile,actionName);
            return false;
        }
    }

    @Override
    public boolean allowed(final String ugcId, final String actionName, final String profileId,
                           final List<String> roles) {
        UGC ugc = ugcService.findById(new ObjectId(ugcId));
        if (ugc == null) {
            return true;
        }
        Actions profile = getSecurityProfile(ugc.getSecurityProfile());
        if (profile.getActions().containsKey(actionName.toUpperCase())) {
            List<String> securityProfileRoles = profile.getActions().get(actionName.toUpperCase());
            //Makes Runtime special OWNER Role
            if(ugc.getProfileId().equals(profileId)){
                securityProfileRoles.add(Actions.OWNER_ROLE);
            }
            return CollectionUtils.containsAny(securityProfileRoles,roles);
        } else {
            log.info("Security Profile {} for ugc {} does not have action {}", profile.getId(), ugcId, actionName.toUpperCase());
            return false;
        }
    }

    @Override
    public boolean excludeProfileInfo(final UGC ugc, final String action, final List<String> roles) {
        throw new NotImplementedException();
    }


    @Override
    public Set<String> getActionsForUser(final List<String> profileRoles) {
        return null;
    }

    private Actions getSecurityProfile(String securityProfileId) {
        Element cacheElement = securityCache.get(securityProfileId);
        if (cacheElement == null) {
            return getSecurityProfileFromDB(securityProfileId);
        } else {
            return (Actions)cacheElement.getObjectValue();
        }
    }

    private Actions getSecurityProfileFromDB(final String securityProfileId) {
        try {
            Actions secProfile = securityProfileRepository.findById(securityProfileId);
            if (secProfile != null) {
                securityCache.put(new Element(securityProfileId, secProfile));
                return secProfile;
            }
            throw new PermissionsException("Unable to find Security Profile with Id " + securityProfileId);
        } catch (MongoDataException e) {
            log.error("Unable to get Security Profile with id" + securityProfileId, e);
            throw new PermissionsException("Unable to get Security Profile");
        }
    }

    private Actions getDefaultSecurityProfile(){
        Element cacheElement = securityCache.get(SYSTEM_DEFAULT);
        if (cacheElement == null) {
            return getDefaultSecurityProfileDB();
        } else {
            return (Actions)cacheElement.getObjectValue();
        }
    }

    public Actions getDefaultSecurityProfileDB() {
        try {
            Actions defaultProfile = securityProfileRepository.findDefault();
            if (defaultProfile != null) {
                securityCache.put(new Element(SYSTEM_DEFAULT, defaultProfile));
                return defaultProfile;
            }
            throw new PermissionsException("Unable to find Default Security Profile ");
        } catch (MongoDataException e) {
            log.error("\"Unable to find Default Security Profile", e);
            throw new PermissionsException("Unable to get Default Security Profile");
        }
    }
}
