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

import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.Action;
import org.craftercms.social.domain.Tenant;
import org.craftercms.social.exceptions.TenantException;
import org.craftercms.social.repositories.TenantRepository;
import org.craftercms.social.services.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TenantServiceImpl implements TenantService {

    private Logger log = LoggerFactory.getLogger(TenantServiceImpl.class);

    @Autowired
    private TenantRepository tenantRepository;

    @Value("#{socialSettings['create.roles']}")
    private String createRoles;

    @Value("#{socialSettings['moderator.roles']}")
    private String moderatorRoles;

    @Override
    public List<String> getRootCreateRoles(String tenantName) throws TenantException {
        try {
            Tenant tenant = this.tenantRepository.findTenantByTenantName(tenantName);
            if (tenant == null || tenant.getRoles() == null) {
                ArrayList<String> roles = new ArrayList<>();
                String[] creates = this.createRoles.split(",");
                for (String role : creates) {
                    roles.add(role.trim());
                }
                return roles;
            } else {
                return tenant.getRoles();
            }
        } catch (MongoDataException ex) {
            log.error("Unable to get Root Roles", ex);
            throw new TenantException("Unable to get Root Roles", ex);
        }
    }

    private List<String> getActionRoles(String actionToFind, String tenantName) throws TenantException {
        try {
            Tenant tenant = this.tenantRepository.findTenantByTenantName(tenantName);
            List<String> roles = new ArrayList<>();
            if (tenant == null || tenant.getActions() == null) {
                for (String role : roles) {
                    roles.add(role.trim());
                }
            } else {
                for (Action action : tenant.getActions()) {
                    if (action.equals(actionToFind)) {
                        roles.addAll(action.getRoles());
                        break;
                    }

                }
            }
            return roles;
        } catch (MongoDataException ex) {
            log.error("Unable to get action " + actionToFind + "roles for tenant " + tenantName, ex);
            throw new TenantException("Unable to get action Roles", ex);
        }
    }

    @Override
    public List<String> getRootModeratorRoles(String tenantName) throws TenantException {
        Tenant tenant;
        try {
            tenant = this.tenantRepository.findTenantByTenantName(tenantName);
        } catch (MongoDataException e) {
            log.error("Unable to find Tenant with name " + tenantName);
            throw new TenantException("Unable to find tenant by name ", e);
        }
        if (tenant == null || tenant.getRoles() == null) {
            ArrayList<String> roles = new ArrayList<>();
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
    public Tenant setTenant(String tenantName, List<String> roles) throws TenantException {
        Tenant tenant = new Tenant();
        tenant.setTenantName(tenantName);
        tenant.setRoles(roles);
        try {
            tenantRepository.save(tenant);
        } catch (MongoDataException e) {
            log.error("Unable to save tenant", e);
            throw new TenantException("Unable to save Tenant", e);
        }
        return tenant;
    }

    @Override
    public void setTenantRoles(String tenantName, List<String> roles) throws TenantException {
        try {
            Tenant t = this.tenantRepository.findTenantByTenantName(tenantName);
            this.tenantRepository.setRoles(t.getTenantName(), roles);
        } catch (MongoDataException ex) {
            log.error("Unable to set Roles " + roles + " to tenant " + tenantName, ex);
            throw new TenantException("Unable to set Roles to a Tenant", ex);
        }
    }

    @Override
    public Tenant getTenantByName(String tenantName) throws TenantException {
        try {
            return this.tenantRepository.findTenantByTenantName(tenantName);
        } catch (MongoDataException e) {
            log.error("Unable to get tenant " + tenantName, e);
            throw new TenantException("Unable to get tenant by name", e);
        }

    }

    @Override
    public void deleteTenant(String tenantName) throws TenantException {
        try {
            Tenant t = this.tenantRepository.findTenantByTenantName(tenantName);
            if (t != null) {
                this.tenantRepository.removeById(t.getId().toString());
            }
        } catch (MongoDataException ex) {
            log.error("Unable to delete tenant " + tenantName, ex);
            throw new TenantException("Unable to delete tenant", ex);
        }
    }

    @Override
    public void setTenantActions(String tenant, List<Action> actions) throws TenantException {
        try {
            this.tenantRepository.setActions(tenant, actions);
        } catch (MongoDataException e) {
            log.error("Unable to set Tenant " + tenant + " actions" + actions, e);
            throw new TenantException("Unable to set actions to a tenant", e);
        }

    }
}
