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

import org.craftercms.social.services.PermissionService;
import org.craftercms.social.services.TenantService;
import org.craftercms.social.services.UGCService;
import org.craftercms.social.util.support.CrafterProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebSecurityExpressionRoot;
import org.springframework.stereotype.Component;

@Component(value = "ugcPermissionExpressionHandler")
public class UgcPermissionExpressionHandler extends
		DefaultWebSecurityExpressionHandler {
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private UGCService ugcService;

	@Autowired
	private TenantService tenantService;

	@Autowired
	private CrafterProfile crafterProfileService;

	public UgcPermissionExpressionHandler() {
		super();
	}

	protected SecurityExpressionRoot createSecurityExpressionRoot(
			Authentication authentication, FilterInvocation fi) {
		WebSecurityExpressionRoot expressionRoot = new UgcSecurityExpressionRoot(
				authentication, fi);
		((UgcSecurityExpressionRoot) expressionRoot)
				.setPermissionService(permissionService);
		((UgcSecurityExpressionRoot) expressionRoot).setUgcService(ugcService);
		((UgcSecurityExpressionRoot) expressionRoot)
				.setTenantService(tenantService);
		((UgcSecurityExpressionRoot) expressionRoot)
				.setCrafterProfileService(crafterProfileService);
		return expressionRoot;
	}

}