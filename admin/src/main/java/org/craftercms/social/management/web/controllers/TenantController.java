/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.social.management.web.controllers;

import java.util.ArrayList;
import java.util.List;

import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.Tenant;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.TenantService;
import org.craftercms.security.utils.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * MVC Controller for displaying and modifying tenants.
 *
 * @author avasquez
 */
@Controller
@RequestMapping(TenantController.BASE_URL_TENANT)
public class TenantController {

    public static final String BASE_URL_TENANT = "/tenant";

    public static final String URL_GET_TENANT_NAMES = "/names";

    private TenantService tenantService;
    private List<String> allTenants;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @RequestMapping(value = URL_GET_TENANT_NAMES, method = RequestMethod.GET)
    @ResponseBody
    public List<String> getTenantNames() throws ProfileException {
        List<String> tenantNames = new ArrayList<>(1);
        final Profile profile = SecurityUtils.getCurrentProfile();

        if (profile != null) {
            if (profile.hasRole("PROFILE_SUPERADMIN")) {
                tenantNames = getAllTenants();
            } else {
                tenantNames.add(profile.getTenant());
            }
        }
        return tenantNames;
    }

    public List<String> getAllTenants() throws ProfileException {
        List<Tenant> tenants = tenantService.getAllTenants();
        List<String> tenantNames = new ArrayList<>(tenants.size());

        for (Tenant tenant : tenants) {
            tenantNames.add(tenant.getName());
        }

        return tenantNames;
    }
}
