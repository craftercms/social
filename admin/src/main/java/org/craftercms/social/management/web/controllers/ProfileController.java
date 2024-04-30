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

import java.util.List;
import java.util.regex.Pattern;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.profile.api.Profile;
import org.craftercms.profile.api.SortOrder;
import org.craftercms.profile.api.exceptions.ProfileException;
import org.craftercms.profile.api.services.ProfileService;
import org.craftercms.security.utils.SecurityUtils;
import org.craftercms.social.management.exceptions.InvalidRequestParameterException;
import org.craftercms.social.management.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * MVC Controller for profile.
 *
 * @author avasquez
 */
@Controller
@RequestMapping(ProfileController.BASE_URL_PROFILE)
public class ProfileController {

    public static final String PATH_VAR_ID = "id";

    public static final String BASE_URL_PROFILE = "/profile";

    public static final String URL_GET_PROFILE_COUNT = "/count";
    public static final String URL_FIND_PROFILES = "/find";
    public static final String URL_GET_PROFILE = "/{" + PATH_VAR_ID + "}";

    public static final String PARAM_TENANT_NAME = "tenantName";
    public static final String PARAM_QUERY = "query";
    public static final String PARAM_SORT_BY = "sortBy";
    public static final String PARAM_SORT_ORDER = "sortOrder";
    public static final String PARAM_START = "start";
    public static final String PARAM_COUNT = "count";

    public static final Pattern QUERY_PATTERN = Pattern.compile("\\w+");
    public static final String FINAL_QUERY_FORMAT = "{username: {$regex: '.*%s.*', $options: 'i'}}";

    private ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @RequestMapping(value = URL_GET_PROFILE_COUNT, method = RequestMethod.GET)
    @ResponseBody
    public long getProfileCount(@RequestParam(value = PARAM_TENANT_NAME, required = false) String tenantName,
                                @RequestParam(value = PARAM_QUERY, required = false) String query,
                                HttpServletRequest request) throws ProfileException {
        if (StringUtils.isEmpty(tenantName)) {
            tenantName = SecurityUtils.getProfile(request).getTenant();
        }

        if (StringUtils.isNotEmpty(query)) {
            if (QUERY_PATTERN.matcher(query).matches()) {
                query = String.format(FINAL_QUERY_FORMAT, query);

                return profileService.getProfileCountByQuery(tenantName, query);
            } else {
                throw new InvalidRequestParameterException(
                        "Parameter '" + PARAM_QUERY + "' must match regex " + QUERY_PATTERN.pattern());
            }
        } else {
            return profileService.getProfileCount(tenantName);
        }
    }

    @RequestMapping(value = URL_FIND_PROFILES, method = RequestMethod.GET)
    @ResponseBody
    public List<Profile> getProfileList(@RequestParam(value = PARAM_TENANT_NAME, required = false) String tenantName,
                                        @RequestParam(value = PARAM_QUERY, required = false) String query,
                                        @RequestParam(value = PARAM_SORT_BY, required = false) String sortBy,
                                        @RequestParam(value = PARAM_SORT_ORDER, required = false) SortOrder sortOrder,
                                        @RequestParam(value = PARAM_START, required = false) Integer start,
                                        @RequestParam(value = PARAM_COUNT, required = false) Integer limit,
                                        HttpServletRequest request) throws ProfileException {
        if (StringUtils.isEmpty(tenantName)) {
            tenantName = SecurityUtils.getProfile(request).getTenant();
        }

        if (StringUtils.isNotEmpty(query)) {
            if (QUERY_PATTERN.matcher(query).matches()) {
                query = String.format(FINAL_QUERY_FORMAT, query);

                return profileService.getProfilesByQuery(tenantName, query, sortBy, sortOrder, start, limit);
            } else {
                throw new InvalidRequestParameterException(
                        "Parameter '" + PARAM_QUERY + "' must match regex " + QUERY_PATTERN.pattern());
            }
        } else {
            return profileService.getProfileRange(tenantName, sortBy, sortOrder, start, limit);
        }
    }

    @RequestMapping(value = URL_GET_PROFILE, method = RequestMethod.GET)
    @ResponseBody
    public Profile getProfile(@PathVariable(PATH_VAR_ID) String id) throws ProfileException {
        Profile profile = profileService.getProfile(id);
        if (profile != null) {
            return profile;
        } else {
            throw new ResourceNotFoundException("No profile found for ID '" + id + "'");
        }
    }

}
