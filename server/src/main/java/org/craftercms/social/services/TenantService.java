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

import org.craftercms.social.domain.Action;
import org.craftercms.social.domain.Tenant;
import org.craftercms.social.exceptions.TenantException;

public interface TenantService {

    static final String MODERATE = "moderate";
    static final String CREATE = "create";

    List<String> getRootCreateRoles(String tenantName) throws TenantException;

    Tenant setTenant(String tenantName, List<String> roles) throws TenantException;

    Tenant getTenantByName(String tenantName) throws TenantException;

    void deleteTenant(String tenantName) throws TenantException;

    void setTenantRoles(String tenantName, List<String> roles) throws TenantException;

    List<String> getRootModeratorRoles(String tenantName) throws TenantException;

    void setTenantActions(String tenant, List<Action> actions) throws TenantException;


}
