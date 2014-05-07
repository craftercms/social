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

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;

import org.craftercms.social.domain.Tenant;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.services.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/2/tenant/")
public class TenantRestController {

    @Autowired
    private TenantService tenantService;

    @RequestMapping(value = "set_tenant", method = RequestMethod.POST)
    @ModelAttribute
    public Tenant setTenant(@RequestParam String tenant, @RequestParam String[] roles) throws SocialException {
        return tenantService.setTenant(tenant, (roles != null? Arrays.asList(roles): null));
    }

    @RequestMapping(value = "set_tenant_roles", method = RequestMethod.POST)
    @ModelAttribute
    public void setTenantRoles(@RequestParam String tenant, @RequestParam String[] roles) throws SocialException {
        tenantService.setTenantRoles(tenant, (roles != null? Arrays.asList(roles): null));
    }

    @RequestMapping(value = "set_tenant_actions", method = RequestMethod.POST)
    @ModelAttribute
    public void setTenantActions(HttpServletRequest request, @RequestParam String tenant) throws SocialException {
        throw new RuntimeException("To be change !!!");
        //List<Action> list = ActionUtil.getActions(request);
        //tenantService.setTenantActions(tenant, list);
    }

    @RequestMapping(value = "delete_tenant", method = RequestMethod.POST)
    @ModelAttribute
    public void deleteTenant(@RequestParam String tenant) throws SocialException {
        tenantService.deleteTenant(tenant);
    }

    @RequestMapping(value = "get_tenant", method = RequestMethod.GET)
    @ModelAttribute
    public Tenant getTenant(@RequestParam String tenant) throws SocialException {
        return tenantService.getTenantByName(tenant);
    }


}
