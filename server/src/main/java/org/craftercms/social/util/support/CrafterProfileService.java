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
package org.craftercms.social.util.support;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.profile.impl.domain.Tenant;

public interface CrafterProfileService {
	Profile authenticateAnonymous();
	String getAppToken();
	Profile getProfile(String profileId);
	Profile getProfile(String profileId, List<String> attributes);
	List<Profile> getProfilesByIds(List<String> profileIds);
	Tenant getTenant(String tenantName);
	void resetAppToken();
	boolean validateUserToken(String ticket);
	Profile getUserInformation(String ticket);
    void updateAttributes(String profileId, Map<String, Serializable> attributes);
    List<Profile> findProfilesBySubscriptions(String target);

}
