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

import org.craftercms.social.domain.Action;
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
	
	@Value("#{socialSettings['moderator.roles']}")
	private String moderatorRoles;

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
	public List<String> getActionCreateRoles(String tenantName) {
//		Tenant tenant = this.tenantRepository
//				.findTenantByTenantName(tenantName);
//		List<String> roles = new ArrayList<String>();
//		if (tenant == null || tenant.getActionRoles() == null) {
//			String[] creates = this.createRoles.split(",");
//			for (String role : roles) {
//				roles.add(role.trim());
//			}
//		} else {
//			for (Action action: tenant.getActionRoles()) {
//				if (action.equals("create")) {
//					roles.addAll(action.getRoles());
//					break;
//				}
//				
//			}
//		}
//		return roles;
		return getActionRoles(CREATE, tenantName,createRoles);
	}
	
	@Override
	public List<String> getModeratorRoles(String tenantName) {
//		Tenant tenant = this.tenantRepository
//				.findTenantByTenantName(tenantName);
//		if (tenant == null || tenant.getRoles() == null) {
//			ArrayList<String> roles = new ArrayList<String>();
//			String[] creates = this.moderatorRoles.split(",");
//			for (String role : creates) {
//				roles.add(role.trim());
//			}
//			return roles;
//		} else {
//			//TODO: get moderator tenant
//			return tenant.getRoles();
//		}
		return getActionRoles(MODERATE, tenantName,moderatorRoles);
	}
	
	private List<String> getActionRoles(String actionToFind, String tenantName, String defaultValue) {
		Tenant tenant = this.tenantRepository
				.findTenantByTenantName(tenantName);
		List<String> roles = new ArrayList<String>();
		if (tenant == null || tenant.getActions() == null) {
			String[] creates = defaultValue.split(",");
			for (String role : roles) {
				roles.add(role.trim());
			}
		} else {
			for (Action action: tenant.getActions()) {
				if (action.equals(actionToFind)) {
					roles.addAll(action.getRoles());
					break;
				}
				
			}
		}
		return roles;
	}
	
	@Override
	public List<String> getRootModeratorRoles(String tenantName) {
		Tenant tenant = this.tenantRepository
				.findTenantByTenantName(tenantName);
		if (tenant == null || tenant.getRoles() == null) {
			ArrayList<String> roles = new ArrayList<String>();
			String[] creates = this.moderatorRoles.split(",");
			for (String role : creates) {
				roles.add(role.trim());
			}
			return roles;
		} else {
			//TODO: get moderator tenant
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

	@Override
	public void setTenantActions(String tenant, List<Action> actions) {
		this.tenantRepository.setActions(tenant, actions);
		
	}
}
