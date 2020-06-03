/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
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

package org.craftercms.social.util.profile;

import java.util.List;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ProfileAggregatorImpl implements ProfileAggregator {

    private ProfileService profileService;
    private Ehcache cache;
    private Logger log = LoggerFactory.getLogger(ProfileAggregatorImpl.class);

    private String[] attributesToReturn;

    public void setAttributesToReturn(String[] attributesToReturn) {
        this.attributesToReturn = attributesToReturn;
    }

    @Override
    public void clearProfileCache(final List<String> profileIds) {
        for (String profileId : profileIds) {
            if (cache.get(profileId) != null) {
                try {
                    cache.remove(profileId, false);
                    log.debug("Profile {} deleted from cache");
                } catch (IllegalStateException ex) {
                    log.warn("Unable to remove profile " + profileId + " from cache ", ex);
                }
            }
        }
    }

    @Override
    public void clearProfileCache() {
        try {
            cache.removeAll();
        } catch (IllegalStateException ex) {
            log.warn("Unable to clear profile cache ", ex);
        }
    }

    @Override
    public Profile getProfile(final String profileId) {
        Profile profile;
        Element element = cache.get(profileId);
        if (element == null) {
            profile = getProfileFromServer(profileId);
            if (profile != null) {
                cache.put(new Element(profileId, profile));
            }
        } else {
            profile = (Profile)element.getObjectValue();
        }
        return profile;

    }

    private Profile getProfileFromServer(final String profileId) {
        try {
            Profile profile = profileService.getProfile(profileId, attributesToReturn);
            if(profile == null){
                return null;
            }

            Profile toReturn = new Profile();
            toReturn.setId(profile.getId());
            toReturn.setUsername(profile.getUsername());
            toReturn.setEmail(profile.getEmail());
            toReturn.setAttributes(profile.getAttributes());

            return toReturn;
        } catch (ProfileException ex) {
            log.error("Unable to get profile \"" + profileId + "\"from server ", ex);
            return null; // Can't do much about this.
        }
    }

    public void setProfileService(final ProfileService profileService) {
        this.profileService = profileService;
    }

    public void setCache(final Ehcache cache) {
        this.cache = cache;
    }

}
