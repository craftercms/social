/*
 * Copyright (C) 2007-2014 Crafter Software Corporation.
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
package org.craftercms.social.management.web.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.http.HttpUtils;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the main view.
 *
 * @author avasquez
 */
@Controller
@RequestMapping("/contexts")
public class ContextController {

    public static final String VIEW_MAIN = "contexts";

    private static final String IS_LOGGED_USER_SUPERADMIN = "isSuperAdmin";

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView viewMain(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView(VIEW_MAIN);
        mav.addObject(IS_LOGGED_USER_SUPERADMIN, isSuperAdmin(getLoggedInUser(request)));
        return mav;
    }

    private boolean isSuperAdmin(final Profile loggedUser) {
        return loggedUser.getRoles().contains("SOCIAL_SUPERADMIN");
    }

    private Profile getLoggedInUser(HttpServletRequest request) {
        Authentication auth = SecurityUtils.getAuthentication(request);
        if (auth != null) {
            return auth.getProfile();
        } else {
            return null;
        }
    }



}
