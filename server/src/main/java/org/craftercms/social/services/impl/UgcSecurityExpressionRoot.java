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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.craftercms.profile.domain.Profile;
import org.craftercms.social.domain.Action;
import org.craftercms.social.domain.UGC;
import org.craftercms.social.services.PermissionService;
import org.craftercms.social.services.TenantService;
import org.craftercms.social.services.UGCService;
import org.craftercms.social.util.action.ActionEnum;
import org.craftercms.social.util.support.CrafterProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;

public class UgcSecurityExpressionRoot extends WebSecurityExpressionRoot {

	private static final String ADMIN = "ADMIN";
	private static final String AUDITOR = "AUDITOR";

	private final Logger log = LoggerFactory
			.getLogger(UgcPermissionExpressionHandler.class);

	private PermissionService permissionService;

	private UGCService ugcService;

	private HttpServletResponse response;

	private TenantService tenantService;
	
	private static final String APPLICATION_JSON = "application/json";
	private static final String CONTENT_TYPE = "Content-Type";

	@Autowired
	private CrafterProfile crafterProfileService;

	public UgcSecurityExpressionRoot(Authentication a, FilterInvocation fi) {
		super(a, fi);
		response = fi.getResponse();

	}

	public boolean hasCreatePermission() {
		Map params = request.getParameterMap();
		String[] target = (String[]) params.get("target");
		String[] parentId = (String[]) params.get("parentId");
		UGC parent = null;
		if (target != null && target.length == 1
				&& (parentId == null || parentId.length == 0)) {
			String[] tenant = (String[]) params.get("tenant");
			List<String> createRoles = this.tenantService
					.getRootCreateRoles(tenant[0]);
			ArrayList<Action> actions = new ArrayList<Action>();
			Action createAction = new Action(ActionEnum.CREATE.toString(),
					createRoles);
			actions.add(createAction);
			parent = new UGC();
			parent.setActions(actions);

		} else {
			parent = this.ugcService.findById(new ObjectId(parentId[0]));
		}
		if (!permissionService.allowed(ActionEnum.CREATE, parent,
				getProfileId())) {
			log.error("Create UGC permission not granted", parent);
			response.setHeader(CONTENT_TYPE, APPLICATION_JSON);
			// Permission not granted
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
			return false;
		}
		return true;
	}

	public boolean hasUpdatePermission() {
		Map params = request.getParameterMap();
		String[] ugcId = (String[]) params.get("ugcId");
		if (ugcId == null || ugcId.length == 0) {
			return false;
		}
		if (!permissionService.allowed(ActionEnum.UPDATE,
				new ObjectId(ugcId[0]), getProfileId())) {
			log.error("UPDATE UGC permission not granted", ugcId);
			response.setHeader(CONTENT_TYPE, APPLICATION_JSON);
			// Permission not granted
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
			return false;
		}
		return true;
	}

	public boolean hasModeratorPermission() {
		// READ permissions are checked using Query method
		if (request.getMethod().toLowerCase().equals("get")) {
			return true;
		}
		String ugcId = getUgcIdFromModerationUri();
		if (ugcId == null || ugcId.length() == 0) {
			return true;
		}
		if (!permissionService.allowed(ActionEnum.MODERATE,
				new ObjectId(ugcId), getProfileId())) {
			log.error("MODERATOR permission not granted", ugcId);
			response.setHeader(CONTENT_TYPE, APPLICATION_JSON);
			// Permission not granted
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
			return false;
		}
		return true;
	}

	public boolean hasActOnPermission() {
		String ugcId = getUgcIdFromActOnUri();
		if (ugcId == null || ugcId.length() == 0) {
			return true;
		}
		if (!permissionService.allowed(ActionEnum.ACT_ON, new ObjectId(ugcId),
				getProfileId())) {
			log.error("ACT_ON permission not granted", ugcId);
			response.setHeader(CONTENT_TYPE, APPLICATION_JSON);
			// Permission not granted
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
			return false;
		}
		return true;
	}

	public boolean hasAdminRole() {
		boolean isAdmin = false;
		String id = getProfileId();
		Profile profile = this.crafterProfileService.getProfile(id);
		if (profile == null) {
			return false;
		}
		List<String> roles = profile.getRoles();
		if (roles == null) {
			return false;
		}
		for (String role : roles) {
			if (role.toUpperCase().endsWith(ADMIN)) {
				isAdmin = true;
				break;
			}
		}
		return isAdmin;
	}

	public boolean hasAuditorRole() {
		boolean isAudit = false;
		String id = getProfileId();
		Profile profile = this.crafterProfileService.getProfile(id);
		if (profile == null) {
			return false;
		}
		List<String> roles = profile.getRoles();
		if (roles == null) {
			return false;
		}
		for (String role : roles) {
			if (role.toUpperCase().endsWith(AUDITOR)) {
				isAudit = true;
				break;
			}
		}
		return isAudit;
	}

	private String getProfileId() {
		return (String) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public UGCService getUgcService() {
		return ugcService;
	}

	public void setUgcService(UGCService ugcService) {
		this.ugcService = ugcService;
	}

	public void setTenantService(TenantService tenantService) {
		this.tenantService = tenantService;
	}

	private String getUgcIdFromActOnUri() {
		String ugcId = request.getRequestURI().replaceAll(
				".*api/2/ugc/[^\\/]*/([^\\/\\.]*).*", "$1");
		if (ugcId.equals(request.getRequestURI())) {
			return null;
		}
		return ugcId;
	}

	private String getUgcIdFromModerationUri() {
		String interceptedUri = "moderation/";
		String endPath = request.getRequestURI().substring(
				request.getRequestURI().indexOf("moderation/")
						+ interceptedUri.length());
		if (endPath.contains("/status")) {
			return endPath.substring(0, endPath.indexOf("/status"));
		}
		return null;
	}

	public void setCrafterProfileService(CrafterProfile crafterProfileService) {
		this.crafterProfileService = crafterProfileService;
	}

}
