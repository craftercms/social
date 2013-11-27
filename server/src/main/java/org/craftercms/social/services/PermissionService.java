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
package org.craftercms.social.services;

import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.util.action.ActionEnum;
import org.springframework.data.mongodb.core.query.Query;


public interface PermissionService {

	/* May not need both of these methods yet */
	
	/* For blog application
	 * 	1. on listing page, should validate against each of the children
	 *  2. on details page, should validate against the children recursively
	 * 
	 */
	
	/* for the action and object passed in, delete if the given profile is allowed to perform 
	 * action on the UGC
	 * 
	 * 
	 */
	boolean allowed(ActionEnum action, UGC ugc, Profile profile);
	
	
	boolean allowed(ActionEnum action, ObjectId ugcId, String profileId);	
	
	boolean allowed(ActionEnum action, UGC ugc, String profileId);
	
	/* used by findAll type query, to narrow the selection
	 * build sample query for this */
	Query getQuery(ActionEnum action, Profile profile);
	
	List<UGC> checkGrantedPermission(ActionEnum action, List<UGC> list, String profileId);

    boolean excludeProfileInfo(UGC ugc, ActionEnum action, List<String> roles);


    Set<String> getActionsForUser(List<String> profileRoles);
}
