/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
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
@RequestMapping("/")
public class MainController {

    public static final String VIEW_MAIN = "main";

    public static final String MODEL_LOGGED_IN_USER = "loggedInUser";
    public static final String MODEL_SOCIAL_APP_URL = "socialAppUrl";
    private static final String IS_LOGGED_USER_SUPERADMIN = "isSuperAdmin";

    private String socialAppRootUrl;
    private String socialAppName;

    public void setSocialAppRootUrl(String socialAppRootUrl) {
        this.socialAppRootUrl = socialAppRootUrl;
    }

    @Required
    public void setSocialAppName(String socialAppName) {
        this.socialAppName = socialAppName;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView viewMain(HttpServletRequest request) {
        StringBuilder socialAppUrl;

        if (StringUtils.isNotEmpty(socialAppRootUrl)) {
            socialAppUrl = new StringBuilder(socialAppRootUrl).append("/").append(socialAppName);
        } else {
            socialAppUrl = HttpUtils.getBaseRequestUrl(request, false).append("/").append(socialAppName);
        }

        ModelAndView mav = new ModelAndView(VIEW_MAIN);
        Profile loggedUser=getLoggedInUser(request);

        mav.addObject(MODEL_LOGGED_IN_USER, loggedUser);
        mav.addObject(IS_LOGGED_USER_SUPERADMIN, isSuperAdmin(loggedUser));
        mav.addObject(MODEL_SOCIAL_APP_URL, socialAppUrl.toString());

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
