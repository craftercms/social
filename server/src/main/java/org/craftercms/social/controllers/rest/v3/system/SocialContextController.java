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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.commons.collections.IterableUtils;
import org.craftercms.profile.api.Profile;
import org.craftercms.security.exception.AuthenticationRequiredException;
import org.craftercms.social.domain.social.system.SocialContext;
import org.craftercms.social.exceptions.SocialException;
import org.craftercms.social.security.SecurityActionNames;
import org.craftercms.social.security.SocialSecurityUtils;
import org.craftercms.social.services.system.ContextPreferencesService;
import org.craftercms.social.services.system.SocialContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.wordnik.swagger.annotations.Api;

/**
 *
 */
@RequestMapping("/api/3/system/context")
@Controller
@Api(value = "Handles Context Configuration", description = "Creates and associates Social " + "Context to profiles")
public class SocialContextController {

    @Autowired
    private SocialContextService socialContextService;
    @Autowired
    private ContextPreferencesService contextPreferencesService;


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
    public Profile addProfileToContext(@PathVariable("id") final String contextId, @PathVariable("profileId") final
    String profileId, @RequestParam final String roles) throws SocialException {
        if (roles.toUpperCase().contains(SecurityActionNames.ROLE_SOCIAL_SUPERADMIN)) {
            throw new IllegalArgumentException(SecurityActionNames.ROLE_SOCIAL_SUPERADMIN + " is not a valid role");
        }

        return socialContextService.addProfileToContext(profileId, contextId, StringUtils.split(roles, ','));
    }

    @RequestMapping(value = "/{id}/{profileId}/delete", method = {RequestMethod.DELETE,RequestMethod.POST})
    @ResponseBody
    public Profile removeProfileFromContext(@PathVariable("id") final String contextId, @PathVariable("profileId")
    final String profileId) throws SocialException {
        return socialContextService.removeProfileFromContext(contextId, profileId);
    }

    @RequestMapping(value = "/preferences/email", method = {RequestMethod.PUT, RequestMethod.POST})
    @ResponseBody
    public boolean saveEmailTemplate(@RequestParam(required = true) final String template, @RequestParam(required =
        true) final String type) throws SocialException {
        if (!checkIfUserIsAdmin()) {
            throw new AuthenticationRequiredException("User must be logged in and must be social admin or context "
                + "admin");
        }

        if (StringUtils.isBlank(template)) {
            throw new IllegalArgumentException("\"template\" param is cannot be blank");
        }
        if (StringUtils.isBlank(type) || !Arrays.asList("DAILY", "WEEKLY", "INSTANT", "APPROVEREMAIL",
            "APPROVER_RESULT_TEMPLATE").contains(type.toUpperCase())) {
            throw new IllegalArgumentException("\"type\" param can not be blank and must be on of the following " +
                "values DAILY,WEEKLY,INSTANT,APPROVEREMAIL APPROVER_RESULT_TEMPLATE");
        }
        return contextPreferencesService.saveEmailTemplate(SocialSecurityUtils.getContext(), type.toUpperCase(),
            template);
    }


    @RequestMapping(value = "/preferences/email", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getSaveEmailTemplate(@RequestParam(required = true) final String type) throws
        SocialException {
        if (!checkIfUserIsAdmin()) {
            throw new AuthenticationRequiredException("User must be logged in and must be social admin or context "
                + "admin");
        }
        if (StringUtils.isBlank(type) || !Arrays.asList("DAILY", "WEEKLY", "INSTANT", "APPROVEREMAIL",
            "APPROVER_RESULT_TEMPLATE").contains(type.toUpperCase())) {
            throw new IllegalArgumentException("\"type\" param can not be blank and must be on of the following " +
                "values DAILY,WEEKLY,INSTANT,APPROVEREMAIL APPROVER_RESULT_TEMPLATE");
        }
        final Map<String, String> toReturn = new HashMap<String, String>();
        toReturn.put("template", contextPreferencesService.getEmailTemplate(SocialSecurityUtils.getContext(), type
            .toUpperCase()));
        return toReturn;
    }


    @RequestMapping(value = "/preferences/email/config", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmailConfiguration() throws SocialException {
        if (!checkIfUserIsAdmin()) {
            throw new AuthenticationRequiredException("User must be logged in and must be social admin or context "
                + "admin");
        }
        return contextPreferencesService.findEmailPreference(SocialSecurityUtils.getContext());
    }


    @RequestMapping(value = "/preferences/email/config", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public Map<String, Object> setEmailConfiguration(@RequestParam(required = true) final String host, @RequestParam
        (required = true) final String encoding, @RequestParam(required = true) final int port, @RequestParam
        (required = true) final boolean auth, @RequestParam(required = true) final String username, @RequestParam
        (required = true) final String password, @RequestParam(required = true) final boolean tls, @RequestParam
        (required = true) final String replyTo, @RequestParam(required = true) final String from, @RequestParam
        (required = true) final int priority, @RequestParam(required = true) final String subject) throws
        SocialException {
        if (!checkIfUserIsAdmin()) {
            throw new AuthenticationRequiredException("User must be logged in and must be social admin or context " +
                "admin");
        }
        HashMap<String, Object> emailPref = new HashMap<>(11);
        emailPref.put("host", host);
        emailPref.put("encoding", encoding);
        emailPref.put("port", port);
        emailPref.put("auth", auth);
        emailPref.put("username", username);
        emailPref.put("password", password);
        emailPref.put("tls", tls);
        emailPref.put("replyTo", replyTo);
        emailPref.put("from", from);
        emailPref.put("priority", priority);
        emailPref.put("subject", subject);
        return contextPreferencesService.saveEmailConfig(SocialSecurityUtils.getContext(), emailPref);
    }


    @RequestMapping(value = "/preferences", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getContextPreference() {
        if (!SocialSecurityUtils.getCurrentProfile().getUsername().equalsIgnoreCase(SocialSecurityUtils.ANONYMOUS)) {
            return contextPreferencesService.getContextPreferences(SocialSecurityUtils.getContext());
        }
        throw new AuthenticationRequiredException("User must be logged in");
    }

    @RequestMapping(value = "/updatePreference", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public boolean savePreferences(@RequestParam final Map<String, Object> preferences) {
        if (!SocialSecurityUtils.getCurrentProfile().getUsername().equalsIgnoreCase(SocialSecurityUtils.ANONYMOUS)) {
            if (SocialSecurityUtils.getCurrentProfile().hasRole(SecurityActionNames.ROLE_SOCIAL_ADMIN) ||
                SocialSecurityUtils.getCurrentProfile().hasRole(SecurityActionNames.ROLE_SOCIAL_SUPERADMIN)) {
                return contextPreferencesService.saveContextPreference(SocialSecurityUtils.getContext(), preferences);
            }
        }
        throw new AuthenticationRequiredException("User must be logged in and must be social admin or context admin");
    }

    @RequestMapping(value = "/deletePreferences", method = {RequestMethod.POST, RequestMethod.DELETE})
    @ResponseBody
    public boolean deletePreferences(@RequestParam final String preferences) {
        if (!SocialSecurityUtils.getCurrentProfile().getUsername().equalsIgnoreCase(SocialSecurityUtils.ANONYMOUS)) {
            if (SocialSecurityUtils.getCurrentProfile().hasRole(SecurityActionNames.ROLE_SOCIAL_ADMIN) ||
                SocialSecurityUtils.getCurrentProfile().hasRole(SecurityActionNames.ROLE_SOCIAL_SUPERADMIN)) {

                contextPreferencesService.deleteContextPreference(SocialSecurityUtils.getContext(), Arrays.asList
                    (preferences.split(",")));
            }
        }else {
            throw new AuthenticationRequiredException("User must be logged in and must be social admin or context admin");
        }
        return true;
    }


    private boolean checkIfUserIsAdmin() {
        return !SocialSecurityUtils.getCurrentProfile().getUsername().equalsIgnoreCase(SocialSecurityUtils.ANONYMOUS)
            && SocialSecurityUtils.getCurrentProfile().hasRole(SecurityActionNames.ROLE_SOCIAL_ADMIN) ||
            SocialSecurityUtils.getCurrentProfile().hasRole(SecurityActionNames.ROLE_SOCIAL_SUPERADMIN);
    }
}
