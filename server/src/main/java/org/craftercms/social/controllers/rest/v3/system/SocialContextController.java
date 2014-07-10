/*
 * Copyright (C) 2007-${year} Crafter Software Corporation.
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.social.controllers.rest.v3.system;

import com.wordnik.swagger.annotations.Api;

import org.craftercms.profile.api.Profile;
import org.craftercms.social.domain.social.system.SocialContext;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.services.system.SocialContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 */
@RequestMapping("/api/3/system/context")
@Controller
@Api("Creates and associates Social Context to profiles")
public class SocialContextController {

    @Autowired
    private SocialContextService socialContextService;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    public Iterable<SocialContext> getAllContexts() throws SocialException {
        return socialContextService.getAllContexts();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public SocialContext create(@RequestParam final String contextName) throws SocialException {
        return socialContextService.createNewContext(contextName);
    }

    @RequestMapping(value = "/{id}/{profileId}", method = RequestMethod.POST)
    @ResponseBody
    public Profile addProfileToContext(@PathVariable("id") final String contextId,
                                       @PathVariable("profileId") final String profileId,
                                       @RequestParam final  String roles) throws SocialException {
        if (roles.toUpperCase().contains("SOCIAL_GOD")) {
            throw new IllegalArgumentException("SOCIAL_GOD is not a valid role");
        }
        return socialContextService.addProfileToContext(profileId, contextId, roles.split(","));
    }

    @RequestMapping(value = "/{id}/{profileId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Profile removeProfileFromContext(@PathVariable("id") final String contextId,
                                       @PathVariable("profileId") final String profileId) throws SocialException {

        return socialContextService.removeProfileFromContext(contextId,profileId);
    }



}
