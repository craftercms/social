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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.types.ObjectId;
import org.craftercms.profile.impl.domain.Profile;
import org.craftercms.social.domain.Action;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.services.PermissionService;
import org.craftercms.social.services.UGCService;
import org.craftercms.social.util.action.ActionConstants;
import org.craftercms.social.util.action.ActionEnum;
import org.craftercms.social.util.support.CrafterProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class PermissionServiceImpl implements PermissionService {
	
	@Autowired
	private UGCService uGCService;
	
	@Autowired
	private CrafterProfile crafterProfileService;
	
	public static final String SUPER_ADMIN = "SUPERADMIN";

	@Override
	public boolean allowed(ActionEnum action, UGC ugc, Profile profile) {
		if (ugc.getActions() == null) { 
			return false;
		}
		
		if (isSuperAdmin(profile.getRoles())) { 
			return true;
		}
		
		List<Action> actions = ugc.getActions();
		
		for (Action a: actions) {
			if (a.getName().equalsIgnoreCase(action.name().toLowerCase())) {
				return checkActionPermission(a, ugc, profile);
			}
		}
		return false;
	}
	
	@Override
	public boolean allowed(ActionEnum action, UGC ugc, String profileId) {
		return allowed(action, ugc, crafterProfileService.getProfile(profileId));
	}

	@Override
	public boolean allowed(ActionEnum action, ObjectId ugcId, String profileId) {
		return allowed(action, uGCService.findById(ugcId), crafterProfileService.getProfile(profileId));
	}

	@Override
	public Query getQuery(ActionEnum action, Profile p) {
		String[] roles = null;
		if (p.getRoles() != null) {
			roles = p.getRoles().toArray(new String[p.getRoles().size()]);
		}
		Query query = new Query();
		if (isSuperAdmin(roles)) {
			query.addCriteria(Criteria.where("actions.name").is(action.toString().toLowerCase()));
		} else if (roles!= null && roles.length > 0) {
			 query.addCriteria(Criteria.where("actions").elemMatch(
					Criteria.where("name").is("read")
						.and("roles").in(roles)));
		}
		return query;
	}
//	@Override
//	public Query getQuery(ActionEnum action, Profile p) {
//		String[] roles = {};
//		if (p.getRoles() != null) {
//			roles = p.getRoles().toArray(new String[p.getRoles().size()]);
//		}
//		Query query = new Query();
//		if (isSuperAdmin(roles)) {
//			query.addCriteria(Criteria.where("actions.name").is(action.toString().toLowerCase()));
//		} else if (roles!= null) {
//			 query.addCriteria(Criteria.where("actions").elemMatch(
//					Criteria.where("name").is(action.toString().toLowerCase())
//						.and("roles").in(roles)));
//		}
//		return query;
//	}
	
	public List<UGC> checkGrantedPermission(ActionEnum action, List<UGC> list, String profileId) {
		List<UGC> grantedList = new ArrayList<UGC>();
		UGC checkUGCPermissions;
		for (UGC current: list) {
			if (current.getParentId() != null) {
				checkUGCPermissions = getParent(list, current.getParentId().toString());
			} else {
				checkUGCPermissions = current;
			}
			if (allowed(action, checkUGCPermissions, profileId)) {
				grantedList.add(current);
			}
		}
		return grantedList;
	}

    @Override
    public boolean excludeProfileInfo(UGC ugc, ActionEnum actionName, List<String> roles) {
    	boolean exclude = true;
         List<Action> actions = ugc.getActions();
         Action action = new Action();
         action.setName(actionName.toString());
         if (actions != null && !actions.isEmpty()) {
        	 for (Action current: actions) {
        		 if (current.equals(action) && !excludeProfileInfo(current, roles)) {
    				 exclude = false;
    				 break;
        		 }
        	 }
         }
         return exclude;
    }
    
    private boolean excludeProfileInfo(Action currentAction, List<String> roles) {
    	boolean exclude = true;
    	if (roles == null) {
    		return exclude;
    	}
		 for (String r: roles) {
			 if (currentAction.getRoles().contains(r)) {
				 return false;
			 }
		 }
    	return exclude;
    }
	
	private boolean checkActionPermission(Action a, UGC ugc, Profile p) {
		List<String> rolesAllowed = a.getRoles();
		List<String> rolesProfile = p.getRoles();
		boolean found = false;
		for(String roleAllowed: rolesAllowed) {
			if (roleAllowed.equalsIgnoreCase(ActionConstants.OWNER) && ugc.getProfileId() != null && p.getId()!=null  //OWNER
					&& ugc.getProfileId().equalsIgnoreCase(p.getId())) {
				return true;
			}
			if (isInProfileRoles(roleAllowed, rolesProfile)) {
				found = true;
				break;
			}
		}
		return found;
	}

   private boolean isInProfileRoles(String roleAllowed,
			List<String> rolesProfile) {
		if (roleAllowed.equalsIgnoreCase(ActionConstants.ANONYMOUS)) {
			return true;
		}
		if (rolesProfile == null) {
			return false;
		}
		boolean isInProfile = false;
		for (String roleProfile: rolesProfile) {
			if (roleProfile.equalsIgnoreCase(roleAllowed)) {
				isInProfile = true;
				break;
			}
		}
		return isInProfile;
	}
	
	private UGC getParent(List<UGC> list, String parentId) {
		UGC found = null;
		for (UGC c: list) {
			if (c.getId().toString().equals(parentId) && c.getParentId() == null) {
				found = c; 
				break;
			} else if (c.getId().toString().equals(parentId)) {
				found = getParent(list, c.getParentId().toString());
			}
		}
		return found;
	}
	
	private boolean isSuperAdmin(List<String> roles) {
		boolean isSuperAdmin = false;
		for(String role: roles) {
			if (role.equalsIgnoreCase(SUPER_ADMIN)) {
				isSuperAdmin = true;
				break;
			}
		}
		return isSuperAdmin;
	}
	
	private boolean isSuperAdmin(String[] roles) {
		if (roles == null) {
			return false;
		}
		return isSuperAdmin(Arrays.asList(roles));
	}
}
