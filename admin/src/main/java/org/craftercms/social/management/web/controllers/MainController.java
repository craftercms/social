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

import org.craftercms.profile.api.Profile;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

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

    private String socialAppUrl;

    @Required
    public void setSocialAppUrl(String socialAppUrl) {
        this.socialAppUrl = socialAppUrl;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView viewMain(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView(VIEW_MAIN);
        mav.addObject(MODEL_LOGGED_IN_USER, getLoggedInUser(request));
        mav.addObject(MODEL_SOCIAL_APP_URL, socialAppUrl);

        return mav;
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
