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
package org.craftercms.social.repositories;

import java.util.List;

import org.craftercms.commons.mongo.JongoRepository;
import org.craftercms.commons.mongo.MongoDataException;
import org.craftercms.social.domain.Action;
import org.craftercms.social.domain.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TenantRepositoryImpl extends JongoRepository<Tenant> implements TenantRepository {

    private Logger log = LoggerFactory.getLogger(TenantRepositoryImpl.class);

    /**
     * Creates a instance of a Jongo Repository.
     */
    public TenantRepositoryImpl() throws MongoDataException {
    }

    @Override
    public Tenant findTenantByTenantName(final String tenantName) throws MongoDataException {
        log.debug("Finding tenant by name {}", tenantName);
        String query = getQueryFor("social.tenant.byName");
        return findOne(query, tenantName);
    }


    @Override
    public void setRoles(String tenantName, List<String> roles) throws MongoDataException {
        log.debug("Updating Tenant {} roles with {}", tenantName, roles);
        Tenant tenant = findTenantByTenantName(tenantName);
        tenant.setRoles(roles);
        update(tenant.getId().toString(), tenant, false, false);
    }

    @Override
    public void setActions(String tenantName, List<Action> actions) throws MongoDataException {
        log.debug("Updating Tenant {} actions with  {}", tenantName, actions);
        Tenant tenant = findTenantByTenantName(tenantName);
        tenant.setActions(actions);
        update(tenant.getId().toString(), tenant, false, false);
    }

}
