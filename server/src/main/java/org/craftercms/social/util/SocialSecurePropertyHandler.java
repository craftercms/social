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

package org.craftercms.social.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.jackson.mvc.SecurePropertyHandler;
import org.craftercms.profile.api.Profile;
import org.craftercms.social.domain.social.Flag;
import org.craftercms.social.security.SocialSecurityUtils;

/**
 *
 */
public class SocialSecurePropertyHandler implements SecurePropertyHandler {

    @Override
    public boolean suppressProperty(final Object propertyName, final String[] roles) {
        Profile p = SocialSecurityUtils.getCurrentProfile();
        List<String> currentRoles=new ArrayList<>();
        if(p!=null && !p.getUsername().equalsIgnoreCase(SocialSecurityUtils.ANONYMOUS)){
            if(propertyName instanceof Flag){
                final String userId = ((Flag)propertyName).getUserId();
                if(userId.equals(p.getId().toString())) {
                    currentRoles.add("OWNER");
                }
            }
        }
        currentRoles.addAll(SocialSecurityUtils.getSocialRoles());
        return CollectionUtils.containsAny(currentRoles, Arrays.asList(roles));
    }

}
