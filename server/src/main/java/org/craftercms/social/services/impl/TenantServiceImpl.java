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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.craftercms.social.domain.Tenant;
import org.craftercms.social.repositories.TenantRepository;
import org.craftercms.social.services.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TenantServiceImpl implements TenantService {

	@Autowired
	private TenantRepository tenantRepository;

	@Value("#{socialSettings['create.roles']}")
	private String createRoles;

	@Override
	public List<String> getRootCreateRoles(String tenantName) {
		Tenant tenant = this.tenantRepository
				.findTenantByTenantName(tenantName);
		if (tenant == null || tenant.getRoles() == null) {
			ArrayList<String> roles = new ArrayList<String>();
			String[] creates = this.createRoles.split(",");
			for (String role : creates) {
				roles.add(role.trim());
			}
			return roles;
		} else {
			return tenant.getRoles();
		}
	}

	@Override
	public Tenant setTenant(String tenantName, List<String> roles) {
		Tenant tenant = new Tenant();
		tenant.setTenantName(tenantName);
		tenant.setRoles(roles);
		tenantRepository.save(tenant);
		return tenant;
	}

	@Override
	public void setTenantRoles(String tenantName, List<String> roles) {
		Tenant t = this.tenantRepository.findTenantByTenantName(tenantName);
		this.tenantRepository.setRoles(t.getTenantName(), roles);
	}

	@Override
	public Tenant getTenantByName(String tenantName) {
		Tenant tenant = this.tenantRepository
				.findTenantByTenantName(tenantName);
		return tenant;
	}

	@Override
	public void deleteTenant(String tenantName) {
		Tenant t = this.tenantRepository.findTenantByTenantName(tenantName);
		if (t != null) {
			this.tenantRepository.delete(t);
		}
	}
}
