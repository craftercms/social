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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.craftercms.social.domain.Tenant;
import org.craftercms.social.services.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/2/tenant/")
public class TenantRestController {
	
	@Autowired
	private TenantService tenantService;
	
	@RequestMapping(value = "set_tenant", method = RequestMethod.POST)
	@ModelAttribute
	public Tenant setTenant(HttpServletRequest request, 
			@RequestParam String tenant, 
			@RequestParam String[] roles,
            HttpServletResponse response) {

        return tenantService.setTenant(tenant,
                (roles != null ? Arrays.asList(roles) : null));
	}
	
	@RequestMapping(value = "set_tenant_roles", method = RequestMethod.POST)
	@ModelAttribute
	public void setTenantRoles(HttpServletRequest request, 
			@RequestParam String tenant, 
			@RequestParam String[] roles,
            HttpServletResponse response) {

        tenantService.setTenantRoles(tenant,
                (roles != null ? Arrays.asList(roles) : null));
	}
	
	@RequestMapping(value = "delete_tenant", method = RequestMethod.POST)
	@ModelAttribute
	public void deleteTenant(HttpServletRequest request, 
			@RequestParam String tenant, 
			HttpServletResponse response) {

        tenantService.deleteTenant(tenant);
	}
	
	@RequestMapping(value = "get_tenant", method = RequestMethod.GET)
	@ModelAttribute
	public Tenant getTenant(HttpServletRequest request, 
			@RequestParam String tenant, 
			HttpServletResponse response) {

        return tenantService.getTenantByName(tenant);
	}
	
	

}
