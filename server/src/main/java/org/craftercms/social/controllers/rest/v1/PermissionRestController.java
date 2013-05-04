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
package org.craftercms.social.controllers.rest.v1;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.craftercms.social.util.action.ActionEnum;
import org.craftercms.social.domain.Action;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.services.PermissionService;
import org.craftercms.social.services.TenantService;
import org.craftercms.social.util.support.CrafterProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/api/2/permission")
public class PermissionRestController {
	private final Logger log = LoggerFactory
			.getLogger(PermissionRestController.class);

	@Autowired
	CrafterProfile crafterProfileService;

	@Autowired
	PermissionService permissionService;
	
	@Autowired
	private TenantService tenantService;

	@RequestMapping(value = "/{ugcId}/{action}", method = RequestMethod.GET)
	@ModelAttribute
	public boolean isAllowed(@PathVariable String ugcId,
			@PathVariable String action) {
		log.debug(String.format("Is allowed id=%s action=%s", ugcId, action));
		try {
			return permissionService.allowed(ActionEnum.valueOf(action),
					new ObjectId(ugcId), getProfileId());
		} catch (Exception e) {
			return false;
		}
	}
	
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	@ModelAttribute
	public boolean isCreateAllowed(@RequestParam String tenant) {
		log.debug(String.format("Is allowed create ugc ", tenant));
		try {

			List<String> createRoles = this.tenantService.getRootCreateRoles(tenant);
			ArrayList<Action> actions = new ArrayList<Action>();
			Action createAction = new Action(ActionEnum.CREATE.toString(),createRoles);
			actions.add(createAction);
			UGC newUgc = new UGC();
			newUgc.setActions(actions);
			
			return permissionService.allowed(ActionEnum.CREATE, newUgc, getProfileId());
		} catch (Exception e) {
			return false;
		}
	}

	private String getProfileId() {
		return (String) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
	}

}
