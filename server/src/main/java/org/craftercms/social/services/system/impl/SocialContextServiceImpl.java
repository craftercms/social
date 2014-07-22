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

package org.craftercms.social.services.system.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.commons.security.permissions.annotations.HasPermission;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.social.domain.social.system.SocialContext;
import org.craftercms.social.domain.social.system.SocialSecurityAction;
import org.craftercms.social.exceptions.ProfileConfigurationException;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.repositories.SocialContextRepository;
import org.craftercms.social.security.SecurityActionNames;
import org.craftercms.social.security.SocialPermission;
import org.craftercms.social.security.SocialSecurityUtils;
import org.craftercms.social.services.system.SecurityActionsService;
import org.craftercms.social.services.system.SocialContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 */
public class SocialContextServiceImpl implements SocialContextService {

    private SocialContextRepository socialContextRepository;
    private ProfileService profileService;
    private SecurityActionsService securityActionsService;
    private Logger log = LoggerFactory.getLogger(SocialContextServiceImpl.class);

    @Override
    @HasPermission(type = SocialPermission.class, action = SecurityActionNames.SYSTEM_GET_ALL_CONTEXTS)
    public Iterable<SocialContext> getAllContexts() throws SocialException {
        try {
            return socialContextRepository.findAll();
        } catch (MongoDataException e) {
            log.error("Unable to find all Social Context", e);
            throw new SocialException("Unable to find social context", e);
        }
    }

    @Override
    @HasPermission(type = SocialPermission.class, action = SecurityActionNames.SYSTEM_CREATE_CONTEXT)
    public SocialContext createNewContext(final String contextName) throws SocialException {
        SocialContext context = new SocialContext(contextName);
        try {
            socialContextRepository.save(context);
            createDefaultActionsForContext(context,SecurityActionNames.TEMPLATE_CONTEXT_ACTIONS);
            return context;
        } catch (MongoDataException ex) {
            log.error("Unable to save new social Context", ex);
            throw new SocialException("Unable to save Social Context", ex);
        }
    }


    @HasPermission(type = SocialPermission.class, action = SecurityActionNames.SYSTEM_CREATE_CONTEXT)
    private void createDefaultActionsForContext(final SocialContext context,final String tenant) throws SocialException {
        final Iterable<SocialSecurityAction> actions = securityActionsService.get(tenant);
        for (SocialSecurityAction action : actions) {
            action.setId(null);
            action.setContextId(context.getId());
            securityActionsService.save(action);
        }
    }

    @Override
    @HasPermission(type = SocialPermission.class, action = SecurityActionNames.SYSTEM_ADD_PROFILE_CONTEXT)
    public Profile addProfileToContext(final String profileId, final String contextId, final String[] roles) throws
        SocialException {
        try {
            Profile p = profileService.getProfile(profileId, "socialTenants");
            if (p == null) {
                throw new ProfileConfigurationException("Given Profile \"" + profileId + "\" does not exist");
            }
            final HashMap<String, Object> attributesToUpdate = new HashMap<>();
            List<Map<String, Object>> socialContexts = (List<Map<String, Object>>)p.getAttribute(SocialSecurityUtils
                .SOCIAL_CONTEXT);
            SocialContext ctx = socialContextRepository.findById(contextId);
            if (ctx == null) {
                throw new ProfileConfigurationException("Given Context \"" + contextId + "\" does not exist");
            }
            if (CollectionUtils.isEmpty(socialContexts)) {
                Map<String, Object> socialContext = new HashMap<>();
                socialContext.put("name", ctx.getContextName());
                socialContext.put("id", ctx.getId());
                socialContext.put("roles", Arrays.asList(roles));
                socialContexts = Arrays.asList(socialContext);
            } else {
                boolean foundOne=false;
                for (Map<String, Object> socialContext : socialContexts) {
                    if (socialContext.containsValue(ctx.getId())) {
                        socialContext.put("roles", Arrays.asList(roles));
                        foundOne=true;
                        break;
                    }
                }
                if(!foundOne){
                    Map<String, Object> newCtx = new HashMap<>();
                    newCtx.put("name", ctx.getContextName());
                    newCtx.put("id", ctx.getId());
                    newCtx.put("roles", Arrays.asList(roles));
                    socialContexts.add(newCtx);
                }
            }
            attributesToUpdate.put(SocialSecurityUtils.SOCIAL_CONTEXT, socialContexts);
            return profileService.updateAttributes(profileId, attributesToUpdate, SocialSecurityUtils.SOCIAL_CONTEXT);
        } catch (ProfileException e) {
            log.error("Unable to find profile with given id " + profileId, e);
            throw new SocialException("Unable to find profile ", e);
        } catch (MongoDataException e) {
            log.error("Unable to look for SocialContext", e);
            throw new SocialException("Unable to find Context by id", e);
        }
    }

    @Override
    @HasPermission(type = SocialPermission.class, action = SecurityActionNames.SYSTEM_REMOVE_PROFILE_CONTEXT)
    public Profile removeProfileFromContext(final String contextId, final String profileId) throws SocialException {
        try {
            Profile p = profileService.getProfile(profileId, "socialTenants");
            if (p == null) {
                throw new ProfileConfigurationException("Given Profile \"" + profileId + "\" does not exist");
            }
            SocialContext ctx = socialContextRepository.findById(contextId);
            if (ctx == null) {
                throw new ProfileConfigurationException("Given Context \"" + contextId + "\" does not exist");
            }
            List<Map<String,Object>> updatedList=new ArrayList<>();
            final HashMap<String, Object> attributesToUpdate = new HashMap<>();
            List<Map<String, Object>> socialContexts = (List<Map<String, Object>>)p.getAttribute(SocialSecurityUtils
                .SOCIAL_CONTEXT);
            if (socialContexts == null) {
                    return p;
            }
            for (Map<String, Object> socialContext : socialContexts) {
                if(!socialContext.containsValue(ctx.getId())){
                    updatedList.add(socialContext);
                }
            }
            attributesToUpdate.put(SocialSecurityUtils.SOCIAL_CONTEXT, updatedList);
            return profileService.updateAttributes(profileId, attributesToUpdate, SocialSecurityUtils.SOCIAL_CONTEXT);
        }catch (ProfileException ex){
            log.error("Unable to find profile with given id " + profileId, ex);
            throw new SocialException("Unable to find profile ", ex);
        } catch (MongoDataException e) {
            log.error("Unable to look for SocialContext", e);
            throw new SocialException("Unable to find Context by id", e);
        }
    }

    public void setSocialContextRepositoryImpl(SocialContextRepository socialContextRepository) {
        this.socialContextRepository = socialContextRepository;
    }

    public void setSecurityActionsService(final SecurityActionsService securityActionsService) {
        this.securityActionsService = securityActionsService;
    }

    public void setProfileServiceRestClient(ProfileService profileService) {
        this.profileService = profileService;
    }
}
